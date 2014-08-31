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
import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.mfischbo.bustamail.common.service.BaseService;
import de.mfischbo.bustamail.exception.BustaMailException;
import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.media.domain.MediaImage;
import de.mfischbo.bustamail.media.service.MediaService;
import de.mfischbo.bustamail.security.service.PermissionRegistry;
import de.mfischbo.bustamail.template.domain.Template;
import de.mfischbo.bustamail.template.domain.TemplatePack;
import de.mfischbo.bustamail.template.domain.Widget;
import de.mfischbo.bustamail.template.dto.TemplatePackDTO;
import de.mfischbo.bustamail.template.repository.TemplatePackRepository;
import de.mfischbo.bustamail.template.repository.TemplateRepositiory;
import de.mfischbo.bustamail.template.repository.WidgetRepository;

@Service
public class TemplateServiceImpl extends BaseService implements TemplateService {

	@Inject
	private TemplatePackRepository	tpRepo;
	
	@Inject
	private TemplateRepositiory		templateRepo;
	
	@Inject
	private WidgetRepository		widgetRepo;
	
	@Inject
	private MediaService			mediaService;
	
	@Inject
	private ObjectMapper			jacksonMapper;
	
	public TemplateServiceImpl() {
		TemplateModulePermissionProvider tmpp = new TemplateModulePermissionProvider();
		PermissionRegistry.registerPermissions(tmpp.getModulePermissions());
	}
	
	@Override
	@PreAuthorize("hasPermission(#owner, 'Templates.USE_TEMPLATES')")
	public Page<TemplatePack> getAllTemplatePacks(UUID owner, Pageable page) {
		return tpRepo.findAllByOwner(owner, page);
	}

	@Override
	@PostAuthorize("hasPermission(returnObject.owner, 'Templates.USE_TEMPLATES')")
	public TemplatePack getTemplatePackById(UUID id)
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
	@PreAuthorize("hasPermission(#pack.owner, 'Templates.MANAGE_TEMPLATES')")
	public TemplatePack createTemplatePack(@P("pack")TemplatePack pack) throws EntityNotFoundException {
		
		// create media images for each template first
		if (pack.getTemplates() != null) {
			for (Template t : pack.getTemplates()) {
				if (t.getImages() != null) {
					List<MediaImage> persisted = new ArrayList<MediaImage>(t.getImages().size());
					for (MediaImage i : t.getImages()) {
						i.setOwner(pack.getOwner());
						persisted.add(mediaService.createMediaImage(i));
					}
					t.setImages(persisted);
				}
			}
		}
		return tpRepo.saveAndFlush(pack);
	}
	
	@Override
	public void exportTemplatePack(TemplatePack pack, OutputStream stream) throws BustaMailException {
	
		try {
			// map the pack to a dto
			TemplatePackDTO d = asDTO(pack, TemplatePackDTO.class);
	
			ZipOutputStream zip = new ZipOutputStream(stream);
			zip.putNextEntry(new ZipEntry("manifest.json"));
			zip.write(jacksonMapper.writeValueAsBytes(d));
			
			zip.putNextEntry(new ZipEntry(pack.getThemeImage().getId().toString()));
			zip.write(pack.getThemeImage().getData());
	
			for (Template tm : pack.getTemplates()) {
				for (MediaImage i : tm.getImages()) {
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
	public TemplatePack importTemplatePack(UUID owner, ZipInputStream stream) throws BustaMailException {
	
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
		nPack = tpRepo.saveAndFlush(nPack);
	
		for (Template t : tin.getTemplates()) {
			Template nt = new Template();
			nt.setDescription(t.getDescription());
			nt.setHtmlHead(t.getHtmlHead());
			nt.setName(t.getName());
			nt.setSource(t.getSource());
			nt.setTemplatePack(nPack);
			nt.setWidgets(new LinkedList<>());
			nt.setImages(new LinkedList<>());
			nt.setResources(new LinkedList<>());
			for (Widget w : t.getWidgets()) {
				Widget nw = new Widget();
				nw.setDescription(w.getDescription());
				nw.setName(w.getName());
				nw.setSource(w.getSource());
				nw.setTemplate(nt);
				nt.getWidgets().add(nw);
			}
			
			// checkpoint for a single template
			nt = templateRepo.save(nt);
			
			// copy all images and resources for this template
			for (MediaImage image : t.getImages()) {
				MediaImage mi = new MediaImage();
				mi.setName(image.getName());
				mi.setData(files.get(image.getId()));
				mi.setOwner(owner);
				mi.setDescription(image.getDescription());
				mi = mediaService.createMediaImage(mi);
				nt.getImages().add(mi);
			}
			
			for (Media res : t.getResources()) {
				Media m = new Media();
				m.setName(res.getName());
				m.setDescription(res.getDescription());
				m.setOwner(owner);
				m.setData(files.get(m.getId()));
				m = mediaService.createMedia(m);
				nt.getResources().add(m);
			}
			templateRepo.saveAndFlush(nt);
		}
		return tin;
	}
	
	
	@Override
	@PreAuthorize("hasPermission(#pack.owner, 'Templates.MANAGE_TEMPLATES')")
	public TemplatePack cloneTemplatePack(@P("pack") TemplatePack o) throws EntityNotFoundException {
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
			tn.setTemplatePack(n);
			
			for (Widget wo : to.getWidgets()) {
				Widget wn = new Widget();
				wn.setDescription(wo.getDescription());
				wn.setName(wo.getName());
				wn.setSource(wo.getSource());
				wn.setTemplate(tn);
				tn.getWidgets().add(wn);
			}
			
			tn.setImages(new LinkedList<MediaImage>());
			for (MediaImage mo : to.getImages()) {
				MediaImage m = new MediaImage();
				m.setAwtColorSpace(mo.getAwtColorSpace());
				m.setData(mo.getData());
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
	@PreAuthorize("hasPermission(#pack.owner, 'Templates.MANAGE_TEMPLATES')")
	public TemplatePack updateTemplatePack(@P("pack") TemplatePack pack)
			throws EntityNotFoundException {
		return tpRepo.saveAndFlush(pack);
	}

	@Override
	@PreAuthorize("hasPermission(#pack.owner, 'Templates.MANAGE_TEMPLATES')")
	public void deleteTemplatePack(@P("pack") TemplatePack pack) throws EntityNotFoundException {
		tpRepo.delete(pack);
	}
	
	@Override
	public void exportAsZip(UUID owner, TemplatePack pack) {
		
	}
	
	@Override
	@PostAuthorize("hasPermission(returnObject.templatePack.owner, 'Templates.USE_TEMPLATES')")
	public Template getTemplateById(UUID templateId) throws EntityNotFoundException {
		Template t = templateRepo.findOne(templateId);
		checkOnNull(t);
		return t;
	}
	
	@Override
	@PreAuthorize("hasPermission(#template.templatePack.owner, 'Templates.MANAGE_TEMPLATES')")
	public Template createTemplate(@P("template") Template template) {
	
		// copy media resources
		if (template.getImages() != null) {
			List<MediaImage> persisted = new ArrayList<MediaImage>();
			for (MediaImage i : template.getImages()) {
				i.setOwner(template.getTemplatePack().getOwner());
				persisted.add(mediaService.createMediaImage(i));
			}
			template.setImages(persisted);
		}
		return templateRepo.saveAndFlush(template);
	}
	
	@Override
	@PreAuthorize("hasPermission(#template.templatePack.owner, 'Templates.MANAGE_TEMPLATES')")
	public Template updateTemplate(@P("template") Template template) {
		return templateRepo.saveAndFlush(template);
	}
	
	@Override
	@PreAuthorize("hasPermission(#template.templatePack.owner, 'Templates.MANAGE_TEMPLATES')")
	public void deleteTemplate(Template template) {
		templateRepo.delete(template);
	}
	
	@Override
	@Transactional
	@PreAuthorize("hasPermission(#template.templatePack.owner, 'Templates.MANAGE_TEMPLATES')")
	public MediaImage createTemplateImage(Template template, MediaImage image) {
		
		image.setOwner(template.getTemplatePack().getOwner());
		MediaImage retval = mediaService.createMediaImage(image);
		
		if (template.getImages() == null)
			template.setImages(new LinkedList<MediaImage>());
		template.getImages().add(retval);
		templateRepo.saveAndFlush(template);
		return retval;
	}
	
	@Override
	@Transactional
	@PreAuthorize("hasPermission(#template.templatePack.owner, 'Templates.MANAGE_TEMPLATES')")
	public Media createTemplateResource(Template template, Media resource) {
		
		resource.setOwner(template.getTemplatePack().getOwner());
		Media retval = mediaService.createMedia(resource);
		if (template.getResources() == null)
			template.setResources(new LinkedList<Media>());
		template.getResources().add(retval);
		templateRepo.saveAndFlush(template);
		return retval;
	}
	
	
	@Override
	@Transactional
	@PreAuthorize("hasPermission(#pack.owner, 'Templates.MANAGE_TEMPLATES')")
	public MediaImage createTemplatePackImage(@P("pack") TemplatePack pack, MediaImage image) {
		image.setOwner(pack.getOwner());
		MediaImage retval = mediaService.createMediaImage(image);
		pack.setThemeImage(retval);
		tpRepo.saveAndFlush(pack);
		return retval;
	}
	

	@Override
	@PostAuthorize("hasPermission(returnObject.template.templatePack.owner, 'Templates.USE_TEMPLATES')")
	public Widget getWidgetById(UUID widget) throws EntityNotFoundException {
		Widget w = widgetRepo.findOne(widget);
		checkOnNull(w);
		return w;
	}
	
	@Override
	@PreAuthorize("hasPermission(#widget.template.templatePack.owner, 'Templates.MANAGE_TEMPLATES')")
	public Widget createWidget(@P("widget") Widget w) {
		return widgetRepo.saveAndFlush(w);
	}

	@Override
	@PreAuthorize("hasPermission(#widget.template.templatePack.owner, 'Templates.MANAGE_TEMPLATES')")
	public Widget updateWidget(@P("widget") Widget w) {
		return widgetRepo.saveAndFlush(w);
	}

	@Override
	@PreAuthorize("hasPermission(#widget.template.templatePack.owner, 'Templates.MANAGE_TEMPLATES')")
	public void deleteWidget(@P("widget") Widget w) {
		widgetRepo.delete(w);
	}
}
