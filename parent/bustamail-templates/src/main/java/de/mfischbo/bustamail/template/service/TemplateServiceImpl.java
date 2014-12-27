package de.mfischbo.bustamail.template.service;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
import org.springframework.security.access.prepost.PreAuthorize;
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
	

	@Override
	public Page<TemplatePack> getAllTemplatePacks(ObjectId owner, Pageable page) {
		return tpRepo.findAllByOwner(owner, page);
	}

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
	
	@Override
	public void exportTemplatePack(TemplatePack pack, OutputStream stream) throws BustaMailException {
	
		try {
			// map the pack to a dto
			
			ZipOutputStream zip = new ZipOutputStream(stream);
			zip.putNextEntry(new ZipEntry("manifest.json"));
			zip.write(jacksonMapper.writeValueAsBytes(pack));
			
			zip.putNextEntry(new ZipEntry(pack.getThemeImage().getId().toString()));
			zip.write(pack.getThemeImage().getData());
	
			for (Template tm : pack.getTemplates()) {
				for (Media i : tm.getImages()) {
					zip.putNextEntry(new ZipEntry(i.getId().toString()));
					zip.write(i.getData());
				}
				
				for (Media r : tm.getResources()) {
					zip.putNextEntry(new ZipEntry(r.getId().toString()));
					zip.write(r.getData());
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
	
	
	@Override
	@PreAuthorize("hasPermission(#owner, 'Templates.MANAGE_TEMPLATES')")
	public TemplatePack importTemplatePack(ObjectId owner, ZipInputStream stream) throws Exception {
	
		// read all entries into a data structure
		TemplatePack tin = null;
		Map<UUID, byte[]> files = new HashMap<>();
			
		try {
			ZipEntry e = null;
			while ((e = stream.getNextEntry()) != null) {
				// the manifest file
				if (e.getName().equals("manifest.json")) {
					tin = jacksonMapper.readValue(StreamUtils.copyToByteArray(stream), TemplatePack.class);
					//tin = jacksonMapper.readValue(stream, TemplatePack.class);
				} else {
					// other resoures go to the map
					UUID id = UUID.fromString(e.getName());
					byte[] data = StreamUtils.copyToByteArray(stream);
					files.put(id, data);
				}
			}
		} catch (Exception ex) {
			throw new BustaMailException("Unable to read zip file");
		}
		
		if (tin == null)
			throw new BustaMailException("Unable to read Template Pack from provided file");
	
		TemplatePack nPack = new TemplatePack();
		nPack.setDescription(tin.getName());
		nPack.setName(tin.getName());
		nPack.setOwner(owner);
		nPack.setTemplates(new ArrayList<Template>(tin.getTemplates().size()));
	
		// perist the pack
		nPack = tpRepo.save(nPack);
	
		for (Template t : tin.getTemplates()) {
			Template nt = new Template();
			nt.setDescription(t.getDescription());
			nt.setHtmlHead(t.getHtmlHead());
			nt.setName(t.getName());
			nt.setSource(t.getSource());
			//nt.setTemplatePack(nPack);
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
			
			// checkpoint for a single template
			//nt = templateRepo.save(nt);
			
			// copy all images and resources for this template
			for (Media image : t.getImages()) {
				Media mi = new Media();
				mi.setName(image.getName());
				mi.setOwner(owner);
				mi.setDescription(image.getDescription());
				mi = mediaService.createMedia(mi);
				nt.getImages().add(mi);
			}
			
			for (Media res : t.getResources()) {
				Media m = new Media();
				m.setName(res.getName());
				m.setDescription(res.getDescription());
				m.setOwner(owner);
				m = mediaService.createMedia(m);
				nt.getResources().add(m);
			}
			//templateRepo.save(nt);
		}
		return tin;
	}
	
	
	@Override
	@PreAuthorize("hasPermission(#pack.owner, 'Templates.MANAGE_TEMPLATES')")
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
	

	@Override
	public TemplatePack updateTemplatePack(TemplatePack pack)
			throws EntityNotFoundException {
		return tpRepo.save(pack);
	}

	@Override
	public void deleteTemplatePack(TemplatePack pack) {
		tpRepo.delete(pack);
	}
	
	@Override
	public void exportAsZip(ObjectId owner, TemplatePack pack) {
		
	}

	@Override
	@PreAuthorize("hasPermission(#pack.owner, 'Templates.MANAGE_TEMPLATES')")
	public Media createTemplatePackImage(@P("pack") TemplatePack pack, Media image) throws Exception {
		image.setOwner(pack.getOwner());
		Media retval = mediaService.createMedia(image);
		pack.setThemeImage(retval);
		tpRepo.save(pack);
		return retval;
	}

	@Override
	public TemplatePack getTemplatePackContainingTemplateById(ObjectId templateId) throws EntityNotFoundException {
		TemplatePack tp = tpRepo.findByTemplateWithId(templateId);
		checkOnNull(tp);
		return tp;
	}
}
