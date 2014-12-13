package de.mfischbo.bustamail.media.service;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.inject.Inject;

import org.apache.tika.Tika;
import org.bson.types.ObjectId;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;

import de.mfischbo.bustamail.common.service.BaseService;
import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.media.domain.Directory;
import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.media.repository.DirectoryRepository;
import de.mfischbo.bustamail.security.domain.OrgUnit;
import de.mfischbo.bustamail.security.event.OrgUnitCreatedEvent;
import de.mfischbo.bustamail.security.service.SecurityService;

@Service
public class MediaServiceImpl extends BaseService implements MediaService, ApplicationListener<OrgUnitCreatedEvent> {

	public static final String UI_DOCUMENT_ROOT_KEY = "de.mfischbo.bustamail.ui.documentRoot";
	public static final String UI_MEDIA_DIRECTORY_KEY = "de.mfischbo.bustamail.ui.mediadir";
	
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
	
	@Inject
	private GridFsTemplate			gridTemplate;
	
	private File					mediaDir;
	
	@PostConstruct
	public void init() {
		String dirname = env.getProperty(UI_DOCUMENT_ROOT_KEY) + env.getProperty(UI_MEDIA_DIRECTORY_KEY);
		this.mediaDir = new File(dirname);
		if (!this.mediaDir.exists())
			this.mediaDir.mkdirs();
	}
	

	@Override
	public Media getMediaById(ObjectId id) throws EntityNotFoundException {
		GridFSDBFile file = gridTemplate.findOne(Query.query(Criteria.where("_id").is(id)));
		return convertFile(file);
	}
	
	@Override
	public void getContent(Media m, OutputStream stream) throws IOException {
		GridFSDBFile f = gridTemplate.findOne(Query.query(Criteria.where("_id").is(m.getId())));
		if (f != null) {
			StreamUtils.copy(f.getInputStream(), stream);
		}
	}
	
	@Override
	public InputStream getContent(Media m) {
		GridFSDBFile f = gridTemplate.findOne(Query.query(Criteria.where("_id").is(m.getId())));
		if (f != null) {
			return f.getInputStream();
		}
		return null;
	}
	
	
	private Media convertFile(GridFSDBFile file) {
		Media m = new Media();
		m.setId((ObjectId) file.getId());
		m.setDescription((String) file.getMetaData().get("description"));
		m.setMimetype((String) file.getMetaData().get("mimetype"));
		m.setName(file.getFilename());
		m.setOwner((ObjectId) file.getMetaData().get("owner"));
		
		Directory d = dRepo.findOne((ObjectId) file.getMetaData().get("directory"));
		m.setDirectory(d);
		return m;
	}
	
	@Override
	public List<Media> getFilesByDirectory(Directory dir) {
		
		List<GridFSDBFile> files = gridTemplate.find(
				Query.query(Criteria.where("metadata.directory.$id").is(dir.getId())));
		
		List<Media>   retval = new ArrayList<>(files.size());
		for (GridFSDBFile f : files) {
			retval.add(convertFile(f));
		}
		return retval;
	}
	
	@Override
	public Media createMedia(Media media, InputStream data) throws IOException {
		
		String mimetype = tika.detect(data).toLowerCase();
		if (mimetype.equals("text/plain")) {
			if (media.getExtension().equalsIgnoreCase("css"))
				mimetype = "text/css";
			if (media.getExtension().equalsIgnoreCase("js"))
				mimetype = "text/javascript";
		}
		media.setMimetype(mimetype);
		
		if (mimetype.startsWith("image")) {
			return processImage(media, data);
		} else {
			GridFSFile file = gridTemplate.store(data, media.getName(), media.getMetaData());
			media.setId((ObjectId) file.getId());
			return media;
		}
	}
	


	private Media processImage(Media m, InputStream data) {
	
		BufferedImage bim = null;
		try {
			bim = ImageIO.read(data);
		} catch (Exception ex) {
			
		}
		if (bim != null) {
			m.setWidth(bim.getWidth());
			m.setHeight(bim.getHeight());
		
			ColorSpace cs = bim.getColorModel().getColorSpace();
			m.setColorspace(cs.getType());
		}
		
		// create smaller variants
		int sizes[] = {1024, 512, 128, 64};
		for (int s : sizes) {
			if (bim.getWidth() > s) {
				BufferedImage res = Scalr.resize(bim, s);
				Media imn = new Media();
				imn.setColorspace(m.getColorspace());
				imn.setDescription(m.getDescription());
				imn.setHeight(res.getHeight());
				imn.setWidth(res.getWidth());
				imn.setMimetype(m.getMimetype());
				imn.setName(m.getName());
				imn.setOwner(m.getOwner());
			
				byte[] thd = getImageData(res, imn.getMimetype());
				GridFSFile f = gridTemplate.store(new ByteArrayInputStream(thd), imn.getName(), imn.getMetaData());
				m.getVariants().add((ObjectId) f.getId());
			}
		}
		gridTemplate.store(new ByteArrayInputStream(getImageData(bim, m.getMimetype())),
				m.getName(), m.getMetaData());
		return m;
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
		GridFSDBFile f = gridTemplate.findOne(Query.query(Criteria.where("_id").is(media.getId())));
		f.setMetaData(media.getMetaData());
		f.save();
		return media;
	}

	@Override
	public Media createCopy(Media media, String filename) throws IOException {
		Media m2 = new Media();
		m2.setDescription(media.getDescription());
		m2.setDirectory(media.getDirectory());
		m2.setMimetype(media.getMimetype());
		m2.setOwner(media.getOwner());
	
		if (filename != null && !filename.isEmpty()) 
			m2.setName(filename);
		else
			m2.setName(media.getName());
	
		
		GridFSDBFile f = gridTemplate.findOne(Query.query(Criteria.where("_id").is(media.getId())));
		return createMedia(m2, f.getInputStream());
	}
	
	
	@Override
	public void deleteMedia(Media media) throws EntityNotFoundException {
		gridTemplate.delete(Query.query(Criteria.where("_id").is(media.getId())));
		for (ObjectId v : media.getVariants()) {
			gridTemplate.delete(Query.query(Criteria.where("_id").is(v)));
		}
	}

	@Override
	public List<Directory> getDirectoryRoots() {
		Set<OrgUnit> units = secService.getOrgUnitsByCurrentUser();
		
		// NOTE: This is my first line of java 8 code!
		Set<ObjectId> ids = units.stream().map(OrgUnit::getId).collect(Collectors.toSet());
		return dRepo.findByOwner(ids);
	}

	@Override
	public Directory getDirectoryById(ObjectId directory)
			throws EntityNotFoundException {
		Directory d = dRepo.findOne(directory);
		checkOnNull(d);
		return d;
	}

	@Override
	public Directory createDirectory(ObjectId owner, Directory parent, Directory directory) {
		directory.setOwner(owner);
		parent.getChildren().add(directory);
		return dRepo.save(parent);
	}

	@Override
	public Directory updateDirectory(Directory directory) {
		return dRepo.save(directory);
	}

	@Override
	public void deleteDirectory(Directory directory) {
		dRepo.delete(directory);
		gridTemplate.delete(Query.query(Criteria.where("metadata.directory").is(directory.getId())));
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
