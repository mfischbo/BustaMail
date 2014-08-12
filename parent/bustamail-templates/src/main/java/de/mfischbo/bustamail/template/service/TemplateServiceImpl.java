package de.mfischbo.bustamail.template.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import de.mfischbo.bustamail.common.service.BaseService;
import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.media.domain.MediaImage;
import de.mfischbo.bustamail.media.service.MediaService;
import de.mfischbo.bustamail.security.service.PermissionRegistry;
import de.mfischbo.bustamail.template.domain.Template;
import de.mfischbo.bustamail.template.domain.TemplatePack;
import de.mfischbo.bustamail.template.domain.Widget;
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
