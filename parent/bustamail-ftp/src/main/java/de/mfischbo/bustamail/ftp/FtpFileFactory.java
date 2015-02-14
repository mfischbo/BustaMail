package de.mfischbo.bustamail.ftp;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.ftp.domain.BaseFtpDirectory;
import de.mfischbo.bustamail.ftp.domain.BaseFtpFile;
import de.mfischbo.bustamail.ftp.proxy.MediaProxy;
import de.mfischbo.bustamail.ftp.proxy.TemplatePackProxy;
import de.mfischbo.bustamail.media.domain.Directory;
import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.media.service.MediaService;
import de.mfischbo.bustamail.security.domain.OrgUnit;
import de.mfischbo.bustamail.template.domain.TemplatePack;
import de.mfischbo.bustamail.template.service.TemplateService;


public class FtpFileFactory {

	public static BaseFtpDirectory populateRootDirectory(BaseFtpDirectory dir, Collection<OrgUnit> units) {
		if (dir.isInitialized()) return dir;
		
		units.forEach(u -> {
			BaseFtpDirectory d = new BaseFtpDirectory(u.getName(), dir, u);
			d.setDateModified(u.getDateModified());
			d.setPersistend(true);
			dir.getSubdirs().add(d);
		});
		dir.setPersistend(true);
		dir.setInitialized(true);
		return dir;
	}
	
	public static BaseFtpDirectory populateIntermediateDirs(BaseFtpDirectory cwd) {
		if (cwd.isInitialized()) return cwd;
		
		BaseFtpDirectory media = new BaseFtpDirectory("media", cwd, cwd.getOwner());
		media.setPersistend(true);
		BaseFtpDirectory templates = new BaseFtpDirectory("templates", cwd, cwd.getOwner());
		templates.setPersistend(true);
		cwd.getSubdirs().add(media);
		cwd.getSubdirs().add(templates);
		cwd.setInitialized(true);
		return cwd;
	}
	
	public static BaseFtpDirectory populateMediaDirectory(BaseFtpDirectory dir, OrgUnit currentUnit, MediaService mService) {
		if (dir.isInitialized()) return dir;
		String[] dirNames = dir.getAbsolutePath().split("/");
		String   dirName  = dirNames[dirNames.length -1];
	
		Directory base = null;
		if (dirName.equals("media")) {
			base = mService.getRootDirectoryByOrgUnit(currentUnit);
		} else {
			try {
				base = mService.getDirectoryById(dir.getId());
			} catch (EntityNotFoundException ex) {
				ex.printStackTrace();
			}
		}
		
		// load child directories and files
		List<Directory> children = mService.getChildDirectories(base);
		List<Media>		files    = mService.getFilesByDirectory(base);
		children.forEach(c -> {
			BaseFtpDirectory nd = new BaseFtpDirectory(c.getName(), dir, currentUnit);
			nd.setId(c.getId());
			nd.setDateModified(c.getDateModified());
			nd.setPersistend(true);
			dir.getSubdirs().add(nd);
		});
		
		final MediaProxy proxy = new MediaProxy(mService);
		files.forEach(f -> {
			BaseFtpFile nf = new BaseFtpFile(f.getName(), dir, currentUnit, proxy);
			nf.setId(f.getId());
			nf.setDateCreated(f.getDateCreated());
			nf.setDateModified(f.getDateModified());
			nf.setPersistent(true);
			nf.setSize(f.getSize());
			dir.getSubFiles().add(nf);
		});
		dir.setInitialized(true);
		return dir;
	}
	
	public static BaseFtpDirectory populateTemplatesDirectory(BaseFtpDirectory dir, OrgUnit currentUnit, TemplateService service) {
		if (dir.isInitialized()) return dir;
		
		Page<TemplatePack> packs = service.getAllTemplatePacks(
				currentUnit.getId(), new PageRequest(0, Integer.MAX_VALUE, Sort.Direction.ASC, "name"));
		packs.getContent().forEach(p -> {
			BaseFtpDirectory nd = new BaseFtpDirectory(p.getName(), dir, currentUnit);
			nd.setId(p.getId());
			nd.setPersistend(true);
			dir.getSubdirs().add(nd);
		});
		dir.setInitialized(true);
		return dir;
	}
	
	public static BaseFtpDirectory populateTemplateDirectory(BaseFtpDirectory dir, OrgUnit currentUnit, TemplateService service, MediaService mService) {
		if (dir.isInitialized()) return dir;
		try {
			TemplatePack pack = service.getTemplatePackById(dir.getId());
			final TemplatePackProxy proxy = new TemplatePackProxy(service, pack, mService);
			final MediaProxy		mProx = new MediaProxy(mService);
			
			// create subfolders for each template
			pack.getTemplates().forEach( t -> {
				BaseFtpDirectory d = new BaseFtpDirectory(t.getName(), dir, currentUnit);
				d.setId(t.getId());
				d.setPersistend(true);
			
				// head html snippet
				BaseFtpFile headHtml = new BaseFtpFile("head.html", d, currentUnit, proxy);
				headHtml.setPersistent(true);
				if (t.getHtmlHead() != null)
					headHtml.setSize(t.getHtmlHead().getBytes().length);
				d.getSubFiles().add(headHtml);
				
				// actual template file
				BaseFtpFile indexHtml = new BaseFtpFile("index.html", d, currentUnit, proxy);
				indexHtml.setPersistent(true);
				if (t.getSource() != null)
					indexHtml.setSize(t.getSource().getBytes().length);
				d.getSubFiles().add(indexHtml);
				
				// images directory
				BaseFtpDirectory imgDir = new BaseFtpDirectory("images", d, currentUnit);
				t.getImages().forEach(i -> {
					try {
						Media mImg = mService.getMediaById(i.getId());
						BaseFtpFile img = new BaseFtpFile(mImg.getName(), imgDir, currentUnit, mProx);
						img.setId(i.getId());
						img.setDateCreated(mImg.getDateCreated());
						img.setDateModified(mImg.getDateModified());
						img.setPersistent(true);
						img.setSize(mImg.getSize());
						imgDir.getSubFiles().add(img);
					} catch (EntityNotFoundException ex) {
						ex.printStackTrace();
					}
				});
				imgDir.setPersistend(true);
				imgDir.setInitialized(true);
				d.getSubdirs().add(imgDir);
				
				
				// resources directory
				BaseFtpDirectory resDir = new BaseFtpDirectory("files", d, currentUnit);
				resDir.setPersistend(true);
				t.getResources().forEach(r -> {
					try {
						Media mRes = mService.getMediaById(r.getId());
						BaseFtpFile res = new BaseFtpFile(r.getName(), resDir, currentUnit, mProx);
						res.setId(r.getId());
						res.setDateCreated(mRes.getDateCreated());
						res.setDateModified(mRes.getDateModified());
						res.setPersistent(true);
						res.setSize(mRes.getSize());
						resDir.getSubFiles().add(res);
					} catch (EntityNotFoundException ex) { ex.printStackTrace(); }
				});
				resDir.setInitialized(true);
				d.getSubdirs().add(resDir);
				
				// widgets directory
				BaseFtpDirectory widDir = new BaseFtpDirectory("widgets", d, currentUnit);
				t.getWidgets().forEach( w -> {
					BaseFtpFile wf = new BaseFtpFile(w.getName() + ".html", widDir, currentUnit, proxy);
					wf.setPersistent(true);
					wf.setId(w.getId());
					if (w.getSource() != null)
						wf.setSize(w.getSource().getBytes().length);
					widDir.getSubFiles().add(wf);
				});
				widDir.setInitialized(true);
				widDir.setPersistend(true);
				d.getSubdirs().add(widDir);
				
				d.setInitialized(true);
				
				dir.getSubdirs().add(d);
				dir.setInitialized(true);
				dir.setPersistend(true);
			});
		} catch (Exception ex) {
			return null;
		}
		return dir;
	}
}