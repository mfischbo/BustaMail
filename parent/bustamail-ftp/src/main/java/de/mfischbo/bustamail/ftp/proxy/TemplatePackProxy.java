package de.mfischbo.bustamail.ftp.proxy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mfischbo.bustamail.ftp.domain.BaseFtpFile;
import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.media.service.MediaService;
import de.mfischbo.bustamail.template.domain.Template;
import de.mfischbo.bustamail.template.domain.TemplatePack;
import de.mfischbo.bustamail.template.domain.Widget;
import de.mfischbo.bustamail.template.service.TemplateService;

public class TemplatePackProxy implements ISourceProxy {

	private TemplateService		service;
	private MediaService		mService;
	private TemplatePack		pack;
	private Logger				log = LoggerFactory.getLogger(getClass());
	private ByteArrayOutputStream outStream;
	
	public TemplatePackProxy(TemplateService service, TemplatePack pack, MediaService mService) {
		this.service = service;
		this.pack    = pack;
		this.mService = mService;
	}
	
	@Override
	public InputStream getInputStream(BaseFtpFile file) {
		Template t = selectTemplate(file);
		if (t == null)
			return null;
		if (file.getName().equals("index.html"))
			return new ByteArrayInputStream(t.getSource().getBytes());
		if (file.getName().equals("head.html"))
			return new ByteArrayInputStream(t.getHtmlHead().getBytes());
		for (Widget w : t.getWidgets()) {
			if ((w.getName() + ".html").equals(file.getName())) {
				return new ByteArrayInputStream(w.getSource().getBytes());
			}
		}
		log.warn("Unable to get input stream for file " + file.getAbsolutePath());
		return null;
	}

	@Override
	public OutputStream getOutputStream(BaseFtpFile file) {
		this.outStream = new ByteArrayOutputStream();
		return this.outStream;
	}
	
	private Template selectTemplate(BaseFtpFile file) {
		String[] parts = file.getAbsolutePath().split("/");
		int idx = 0;
		for (idx=0; idx < parts.length; idx++) {
			if (parts[idx].equals(pack.getName())) {
				idx++;
				break;
			}
		}
		String tName = parts[idx];
		for (Template t : pack.getTemplates()) {
			if (t.getName().equals(tName))
				return t;
		}
		return null;
	}

	@Override
	public boolean persist(BaseFtpFile file) {
	
		String fName = file.getName();
		Template tmpl = selectTemplate(file);
		if (tmpl == null) 
			return false;
		
		if (fName.equals("index.html") || fName.equals("head.html")) {
			if (fName.equals("head.html")) {
				tmpl.setHtmlHead(new String(this.outStream.toByteArray()));
				file.setSize(tmpl.getHtmlHead().getBytes().length);
			}
			if (fName.equals("index.html")) {
				tmpl.setSource(new String(this.outStream.toByteArray()));
				file.setSize(tmpl.getSource().getBytes().length);
			}
		}
		
		// persist widgets
		if (file.getParent().getName().equals("widgets")) {
			if (file.isPersistent()) {
				for (Widget w : tmpl.getWidgets()) {
					if (w.getId().equals(file.getId())) {
						w.setSource(new String(this.outStream.toByteArray()));
						file.setSize(w.getSource().getBytes().length);
						break;
					}
				}
			} else {
				Widget w = new Widget();
				w.setName(file.getName());
				w.setSource(new String(this.outStream.toByteArray()));
				tmpl.getWidgets().add(w);
				file.setId(w.getId());
				file.setSize(w.getSource().getBytes().length);
			}
		}
		
		// persist images and resource files
		try {
			List<Media> target = null;
			if (file.getParent().getName().equals("images")) 
				target = tmpl.getImages();
			if (file.getParent().getName().equals("files"))
				target = tmpl.getResources();
		
			if (target != null) {
				if (file.isPersistent()) {
					for (Media m : target) {
						if (m.getId().equals(file.getId())) {
							m.setData(this.outStream);
							m = mService.updateMedia(m);
							file.setSize(m.getSize());
						}
					}
				} else {
					Media m = new Media();
					m.setName(file.getName());
					m.setData(this.outStream);
					m.setDateCreated(DateTime.now());
					m.setDateModified(DateTime.now());
					m.setOwner(pack.getOwner());
					m = mService.createMedia(m);
					file.setId(m.getId());
					file.setSize(m.getSize());
					target.add(m);
				}
			}
		} catch (Exception ex) {
			return false;
		}
		try {
			service.updateTemplatePack(pack);
			file.setPersistent(true);
			this.outStream = null;
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}	
		return false;
	}
}
