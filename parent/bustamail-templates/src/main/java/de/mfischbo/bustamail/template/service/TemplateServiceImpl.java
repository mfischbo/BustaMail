package de.mfischbo.bustamail.template.service;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.mfischbo.bustamail.common.service.BaseService;
import de.mfischbo.bustamail.exception.BustaMailException;
import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.media.service.MediaService;
import de.mfischbo.bustamail.template.domain.Template;
import de.mfischbo.bustamail.template.domain.TemplatePack;
import de.mfischbo.bustamail.template.domain.Widget;
import de.mfischbo.bustamail.template.repository.TemplatePackRepository;

@Service
public class TemplateServiceImpl extends BaseService implements TemplateService {

	@Inject
	private TemplatePackRepository	tpRepo;
	
	@Inject
	private MediaService			mediaService;
	
	@Inject
	private ObjectMapper			jacksonMapper;
	

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.template.service.TemplateService#getAllTemplatePacks(org.bson.types.ObjectId, org.springframework.data.domain.Pageable)
	 */
	@Override
	public Page<TemplatePack> getAllTemplatePacks(ObjectId owner, Pageable page) {
		return tpRepo.findAllByOwner(owner, page);
	}

	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.template.service.TemplateService#getTemplatePackById(org.bson.types.ObjectId)
	 */
	@Override
	public TemplatePack getTemplatePackById(ObjectId id)
			throws EntityNotFoundException {
		TemplatePack tp = tpRepo.findOne(id);
		checkOnNull(tp);
		tp.getTemplates().forEach(new Consumer<Template>() {
			@Override
			public void accept(Template t) {
				log.debug("Fetching " + t.getSettings().size() + " setting entries for template : " + t.getName());
			}
		});
		return tp;
	}

	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.template.service.TemplateService#createTemplatePack(de.mfischbo.bustamail.template.domain.TemplatePack)
	 */
	@Override
	public TemplatePack createTemplatePack(TemplatePack pack) throws Exception {
		
		// create media images for each template first
		if (pack.getTemplates() != null) {
			for (Template t : pack.getTemplates()) {
				if (t.getImages() != null) {
					List<Media> persisted = new ArrayList<Media>(t.getImages().size());
					for (Media i : t.getImages()) {
						i.setOwner(pack.getOwner());
						persisted.add(mediaService.createMedia(i));
					}
					t.setImages(persisted);
				}
			}
		}
		return tpRepo.save(pack);
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.template.service.TemplateService#exportTemplatePack(de.mfischbo.bustamail.template.domain.TemplatePack, java.io.OutputStream)
	 */
	@Override
	public void exportTemplatePack(TemplatePack pack, OutputStream stream) throws BustaMailException {
	
		try {
			ZipOutputStream zip = new ZipOutputStream(stream);
			zip.putNextEntry(new ZipEntry("manifest.json"));
			zip.write(jacksonMapper.writeValueAsBytes(pack));
			
			if (pack.getThemeImage() != null) {
				zip.putNextEntry(new ZipEntry(pack.getThemeImage().getId().toString()));
				byte[] data = StreamUtils.copyToByteArray(mediaService.getContent(pack.getThemeImage()));
				zip.write(data);
			}
			
			for (Template tm : pack.getTemplates()) {
				for (Media i : tm.getImages()) {
					zip.putNextEntry(new ZipEntry(i.getId().toString()));
					zip.write(StreamUtils.copyToByteArray(mediaService.getContent(i)));
				}
				
				for (Media r : tm.getResources()) {
					zip.putNextEntry(new ZipEntry(r.getId().toString()));
					zip.write(StreamUtils.copyToByteArray(mediaService.getContent(r)));
				}
			}
			zip.flush();
			zip.close();
			stream.flush();
			stream.close();
		} catch (Exception ex) {
			log.error("Error exporting template pack. Cause: " + ex.getMessage());
			ex.printStackTrace();
			throw new BustaMailException(ex.getMessage());
		}
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.template.service.TemplateService#importTemplatePack(org.bson.types.ObjectId, java.util.zip.ZipInputStream)
	 */
	@Override
	public TemplatePack importTemplatePack(ObjectId owner, ZipInputStream stream) throws Exception {
	
		// read all entries into a data structure
		TemplatePack tin = null;
		Map<ObjectId, byte[]> files = new HashMap<>();
			
		try {
			ZipEntry e = null;
			while ((e = stream.getNextEntry()) != null) {
				
				// the manifest file
				if (e.getName().equals("manifest.json")) {
					tin = jacksonMapper.readValue(StreamUtils.copyToByteArray(stream), TemplatePack.class);
				} else {
			
					// other resoures go to the map
					ObjectId id = new ObjectId(e.getName());
					byte[] data = StreamUtils.copyToByteArray(stream);
					files.put(id, data);
				}
			}
		} catch (Exception ex) {
			throw new BustaMailException("Unable to read zip file");
		}
		
		if (tin == null)
			throw new BustaMailException("Unable to read Template Pack from provided file");
	
		
		// create the pack itself
		TemplatePack nPack = new TemplatePack();
		nPack.setDescription(tin.getDescription());
		nPack.setName(tin.getName());
		nPack.setOwner(owner);
		nPack.setTemplates(new ArrayList<Template>(tin.getTemplates().size()));
		
		if (nPack.getThemeImage() != null) {
			Media m = new Media();
			m.setOwner(nPack.getOwner());
			m.setName("Theme for " + nPack.getName());
			m.setData(files.get(tin.getId()));
			m = mediaService.createMedia(m);
			nPack.setThemeImage(m);
		}

		for (Template t : tin.getTemplates()) {
			Template nt = new Template();
			nt.setDescription(t.getDescription());
			nt.setHtmlHead(t.getHtmlHead());
			nt.setName(t.getName());
			nt.setSource(t.getSource());
			nt.setWidgets(new LinkedList<>());
			nt.setImages(new LinkedList<>());
			nt.setResources(new LinkedList<>());
			for (Widget w : t.getWidgets()) {
				Widget nw = new Widget();
				nw.setDescription(w.getDescription());
				nw.setName(w.getName());
				nw.setSource(w.getSource());
				nt.getWidgets().add(nw);
			}
			nPack.getTemplates().add(nt);
			
			// copy all images and resources for this template
			for (Media image : t.getImages()) {
				Media mi = new Media();
				mi.setName(image.getName());
				mi.setOwner(owner);
				mi.setDescription(image.getDescription());
				mi.setData(files.get(image.getId()));
				mi = mediaService.createMedia(mi);
				nt.getImages().add(mi);
			}
			
			for (Media res : t.getResources()) {
				Media m = new Media();
				m.setName(res.getName());
				m.setDescription(res.getDescription());
				m.setOwner(owner);
				m.setData(files.get(res.getId()));
				m = mediaService.createMedia(m);
				nt.getResources().add(m);
			}
		}

		// perist the pack
		return tpRepo.save(nPack);
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.template.service.TemplateService#cloneTemplatePack(de.mfischbo.bustamail.template.domain.TemplatePack)
	 */
	@Override
	public TemplatePack cloneTemplatePack(@P("pack") TemplatePack o) throws Exception {
		TemplatePack n = new TemplatePack();
		n.setDescription(o.getDescription());
		n.setName("Copy of " + o.getName());
		n.setOwner(o.getOwner());
		n.setTemplates(new LinkedList<Template>());
		
		for (Template to : o.getTemplates()) {
			Template tn = new Template();
			tn.setDescription(to.getDescription());
			tn.setName(to.getName());
			tn.setSource(to.getSource());
			tn.setWidgets(new LinkedList<Widget>());
			//tn.setTemplatePack(n);
			
			for (Widget wo : to.getWidgets()) {
				Widget wn = new Widget();
				wn.setDescription(wo.getDescription());
				wn.setName(wo.getName());
				wn.setSource(wo.getSource());
				tn.getWidgets().add(wn);
			}
			
			tn.setImages(new LinkedList<Media>());
			for (Media mo : to.getImages()) {
				Media m = new Media();
				m.setColorspace(mo.getColorspace());
				m.setDescription(mo.getDescription());
				m.setName(mo.getName());
				m.setOwner(mo.getOwner());
				tn.getImages().add(m);
			}
			
			tn.setSettings(new LinkedHashMap<String, String>());
			to.getSettings().forEach(new BiConsumer<String, String>() {
				@Override
				public void accept(String t, String u) {
					tn.getSettings().put(t, u);
				}
			});
			n.getTemplates().add(tn);
		}
		return createTemplatePack(n);
	}
	

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.template.service.TemplateService#updateTemplatePack(de.mfischbo.bustamail.template.domain.TemplatePack)
	 */
	@Override
	public TemplatePack updateTemplatePack(TemplatePack pack)
			throws EntityNotFoundException {
		return tpRepo.save(pack);
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.template.service.TemplateService#deleteTemplatePack(de.mfischbo.bustamail.template.domain.TemplatePack)
	 */
	@Override
	public void deleteTemplatePack(TemplatePack pack) {
		tpRepo.delete(pack);
	}

	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.template.service.TemplateService#exportAsZip(org.bson.types.ObjectId, de.mfischbo.bustamail.template.domain.TemplatePack)
	 */
	@Override
	public void exportAsZip(ObjectId owner, TemplatePack pack) {
		
	}

	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.template.service.TemplateService#createTemplatePackImage(de.mfischbo.bustamail.template.domain.TemplatePack, de.mfischbo.bustamail.media.domain.Media)
	 */
	@Override
	public Media createTemplatePackImage(@P("pack") TemplatePack pack, Media image) throws Exception {
		image.setOwner(pack.getOwner());
		Media retval = mediaService.createMedia(image);
		pack.setThemeImage(retval);
		tpRepo.save(pack);
		return retval;
	}
	

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.template.service.TemplateService#createTemplateResource(de.mfischbo.bustamail.template.domain.TemplatePack, org.bson.types.ObjectId, de.mfischbo.bustamail.media.domain.Media, java.lang.String)
	 */
	@Override
	public Media createTemplateResource(TemplatePack pack , ObjectId templateId, Media media, String type) throws Exception {
		media.setOwner(pack.getOwner());
		for (Template t : pack.getTemplates()) {
			if (t.getId().equals(templateId)) {
				Media m = mediaService.createMedia(media);
				
				if (type.equals("resource"))
					t.getResources().add(m);
				if (type.equals("image"))
					t.getImages().add(m);
				tpRepo.save(pack);
				return m;
			}
		}
		throw new BustaMailException("No template found for id " + templateId);
	}
	

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.template.service.TemplateService#deleteTemplateResource(de.mfischbo.bustamail.template.domain.TemplatePack, org.bson.types.ObjectId, org.bson.types.ObjectId, java.lang.String)
	 */
	@Override
	public void deleteTemplateResource(TemplatePack pack, ObjectId templateId, ObjectId resourceId, String type) throws Exception {
		for (Template t : pack.getTemplates()) {
			if (t.getId().equals(templateId)) {
			
				List<Media> media = null;
				if (type.equals("resource")) 
					media = t.getResources();
				if (type.equals("image"))
					media = t.getImages();
				
				if (media == null)
					return;
				
				Iterator<Media> mit = media.iterator();
				while (mit.hasNext()) {
					Media m = mit.next();
					if (m.getId().equals(resourceId)) {
						mit.remove();
						mediaService.deleteMedia(m);
						tpRepo.save(pack);
					}
				}
			}
		}
	}
	

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.template.service.TemplateService#getTemplatePackContainingTemplateById(org.bson.types.ObjectId)
	 */
	@Override
	public TemplatePack getTemplatePackContainingTemplateById(ObjectId templateId) throws EntityNotFoundException {
		TemplatePack tp = tpRepo.findByTemplateWithId(templateId);
		checkOnNull(tp);
		return tp;
	}
}
