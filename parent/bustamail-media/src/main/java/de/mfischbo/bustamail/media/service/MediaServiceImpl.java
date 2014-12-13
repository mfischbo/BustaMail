package de.mfischbo.bustamail.media.service;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import org.apache.tika.Tika;
import org.bson.types.ObjectId;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.mfischbo.bustamail.common.service.BaseService;
import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.media.domain.Directory;
import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.media.domain.MediaImage;
import de.mfischbo.bustamail.media.repository.DirectoryRepository;
import de.mfischbo.bustamail.media.repository.MediaImageRepository;
import de.mfischbo.bustamail.media.repository.MediaRepository;
import de.mfischbo.bustamail.security.domain.OrgUnit;
import de.mfischbo.bustamail.security.dto.OrgUnitDTO;
import de.mfischbo.bustamail.security.event.OrgUnitCreatedEvent;
import de.mfischbo.bustamail.security.service.SecurityService;

@Service
public class MediaServiceImpl extends BaseService implements MediaService, ApplicationListener<OrgUnitCreatedEvent> {

	public static final UUID GLOBAL_ROOT_DIRECTORY_ID = UUID.fromString("15f812fb-c25c-45ab-ab5b-1fdb7d2dce33");
	
	public static final String UI_DOCUMENT_ROOT_KEY = "de.mfischbo.bustamail.ui.documentRoot";
	public static final String UI_MEDIA_DIRECTORY_KEY = "de.mfischbo.bustamail.ui.mediadir";
	
	@Autowired
	private MediaRepository			mRepo;
	
	@Autowired
	private MediaImageRepository	miRepo;
	
	@Autowired
	private DirectoryRepository		dRepo;
	
	@Autowired
	private Tika					tika;
	
	@Autowired
	private	SecurityService			secService;
	
	@Autowired
	private Authentication			auth;
	
	@Autowired
	private Environment				env;
	
	private File					mediaDir;
	
	@PostConstruct
	public void init() {
		String dirname = env.getProperty(UI_DOCUMENT_ROOT_KEY) + env.getProperty(UI_MEDIA_DIRECTORY_KEY);
		this.mediaDir = new File(dirname);
		if (!this.mediaDir.exists())
			this.mediaDir.mkdirs();
	}
	
	@Override
	public Page<Media> getAllMedias(Pageable page) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Media getMediaById(ObjectId id) throws EntityNotFoundException {
		Media m = mRepo.findOne(id);
		checkOnNull(m);
		return m;
	}
	
	
	@Override
	public void flushToDisk() throws Exception {
		List<MediaImage> miList = miRepo.findAll();
		for (MediaImage i : miList)
			writeToDisk(i, true);
	}
	

	@Override
	public Media createMedia(Media media) {
		
		String mimetype = tika.detect(media.getData()).toLowerCase();
		if (mimetype.equals("text/plain")) {
			if (media.getExtension().equalsIgnoreCase("css"))
				mimetype = "text/css";
			if (media.getExtension().equalsIgnoreCase("js"))
				mimetype = "text/javascript";
		}
		media.setMimetype(mimetype);
		
		if (mimetype.startsWith("image")) {
			return processImage(media);
		} else {
			Media retval = mRepo.save(media);
			try {
				writeToDisk(retval, false);
			} catch (IOException ex) {
				log.error("Unable to write resource file to disk. Cause: " + ex.getMessage());
			}
			return retval;
		}
	}
	
	@Override
	public Media createCopy(Media media, String filename) {
		Media m2 = new Media();
		m2.setData(media.getData());
		m2.setDescription(media.getDescription());
		m2.setDirectory(media.getDirectory());
		m2.setMimetype(media.getMimetype());
		m2.setOwner(media.getOwner());
	
		if (filename != null && !filename.isEmpty()) 
			m2.setName(filename);
		else
			m2.setName(media.getName());
		
		return createMedia(m2);
	}
	
	
	@Override
	@Transactional
	public MediaImage createMediaImage(MediaImage image) {
		String mimetype = tika.detect(image.getData()).toLowerCase();
		image.setMimetype(mimetype);
		image = processImage(image);
	
		try {
			writeToDisk(image, true);
		} catch (Exception ex) {
			log.error("Failed writing media images to disk. Cause: " + ex.getMessage());
		}
		
		return image;
	}
	
	private void writeToDisk(Media image, boolean includeVariants) throws IOException {
		File outputDir = this.mediaDir;
	
		File f = new File(outputDir.getAbsolutePath() + "/" + image.getId());
		FileOutputStream fout = new FileOutputStream(f);
		fout.write(image.getData());
		fout.flush();
		fout.close();
	
		if (includeVariants && image instanceof MediaImage) {
			for (MediaImage i : ((MediaImage) image).getVariants()) {
				File fv = new File(outputDir.getAbsolutePath() + "/" + i.getId());
				fout = new FileOutputStream(fv);
				fout.write(image.getData());
				fout.flush();
				fout.close();
			}
		}
	}
	
	
	private MediaImage processImage(Media m) {
		
		MediaImage mi = new MediaImage();
		mi.setOwner(m.getOwner());
		mi.setDirectory(m.getDirectory());
		mi.setData(m.getData());
		mi.setName(m.getName());
		mi.setDescription(m.getDescription());
		mi.setMimetype(m.getMimetype());
		mi.setVariants(new LinkedList<MediaImage>());
	
		ByteArrayInputStream bai = new ByteArrayInputStream(mi.getData());
		BufferedImage bim = null;
		try {
			bim = ImageIO.read(bai);
		} catch (Exception ex) {
			
		}
		if (bim != null) {
			mi.setWidth(bim.getWidth());
			mi.setHeight(bim.getHeight());
		
			ColorSpace cs = bim.getColorModel().getColorSpace();
			mi.setAwtColorSpace(cs.getType());
		}
		
		// create smaller variants
		int sizes[] = {1024, 512, 128, 64};
		for (int s : sizes) {
			if (bim.getWidth() > s) {
				BufferedImage res = Scalr.resize(bim, s);
				MediaImage imn = new MediaImage();
				imn.setAwtColorSpace(mi.getAwtColorSpace());
				imn.setDescription(mi.getDescription());
				imn.setHeight(res.getHeight());
				imn.setWidth(res.getWidth());
				imn.setMimetype(mi.getMimetype());
				imn.setName(mi.getName());
				imn.setOwner(mi.getOwner());
				imn.setParent(mi);
			
				byte[] data = getImageData(res, imn.getMimetype());
				if (data != null)
					imn.setData(data);
				mi.getVariants().add(imn);
			}
		}
		return miRepo.save(mi);
	}
	
	
	private byte[] getImageData(BufferedImage im, String mimetype) {
		String ext = "png";
		if (mimetype.equals("image/jpeg"))
			ext = "jpeg";
		if (mimetype.equals("image/jpg"))
			ext = "jpg";
		if (mimetype.equals("image/png"))
			ext = "png";
		if (mimetype.equals("image/tiff"))
			ext = "tiff";
		if (mimetype.equals("image/gif"))
			ext = "gif";
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ImageIO.write(im, ext, bos);
			bos.flush();
			return bos.toByteArray();
		} catch (Exception ex) {
			
		}
		return null;
	}

	@Override
	public Media updateMedia(Media media) throws EntityNotFoundException {
		if (media instanceof Media) {
			media = mRepo.save(media);
			try {
				writeToDisk(media, false);
			} catch (Exception ex) {
				log.error("Failed writing media contents to disk. Cause: " + ex.getMessage());
			}
		}
		return media;
	}

	@Override
	public void deleteMedia(Media media) throws EntityNotFoundException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Directory> getDirectoryRoots() {
		Set<OrgUnitDTO> units = secService.getTopLevelUnits();
		
		// NOTE: This is my first line of java 8 code!
		Set<ObjectId> ids = units.stream().map(OrgUnitDTO::getId).collect(Collectors.toSet());
		return dRepo.findByOwnerAndParent(ids, null);
	}

	@Override
	public Directory getDirectoryById(ObjectId directory)
			throws EntityNotFoundException {
		Directory d = dRepo.findOne(directory);
		checkOnNull(d);
		return d;
	}

	@Override
	public Directory createDirectory(ObjectId owner, Directory directory) {
		directory.setOwner(owner);
		return dRepo.save(directory);
	}

	@Override
	public Directory updateDirectory(Directory directory) {
		return dRepo.save(directory);
	}

	@Override
	public void deleteDirectory(Directory directory) {
		dRepo.delete(directory);
	}

	@Override
	public void onApplicationEvent(OrgUnitCreatedEvent event) {
		OrgUnit ou = event.getOrgUnit();
		Directory d = new Directory();
		d.setName(ou.getName());
		d.setOwner(ou.getId());
		dRepo.save(d);
	}
}
