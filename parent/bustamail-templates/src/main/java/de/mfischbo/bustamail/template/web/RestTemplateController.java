package de.mfischbo.bustamail.template.web;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.function.BiConsumer;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import de.mfischbo.bustamail.common.web.BaseApiController;
import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.media.domain.MediaImage;
import de.mfischbo.bustamail.media.dto.MediaDTO;
import de.mfischbo.bustamail.media.dto.MediaImageDTO;
import de.mfischbo.bustamail.media.service.MediaService;
import de.mfischbo.bustamail.template.domain.Template;
import de.mfischbo.bustamail.template.domain.TemplatePack;
import de.mfischbo.bustamail.template.domain.Widget;
import de.mfischbo.bustamail.template.dto.TemplateDTO;
import de.mfischbo.bustamail.template.dto.WidgetDTO;
import de.mfischbo.bustamail.template.service.TemplateService;

@RestController
@RequestMapping("/api/templates")
public class RestTemplateController extends BaseApiController {

	@Autowired
	private TemplateService			service;
	
	@Autowired
	private MediaService			mediaService;
	
	
	@RequestMapping(value = "/packs/{id}/templates", method = RequestMethod.POST)
	public TemplateDTO createTemplate(@PathVariable("id") ObjectId tpackId, @RequestBody TemplateDTO dto) throws Exception {
		
		Template t = new Template();
		t.setDescription(dto.getDescription());
		t.setName(dto.getName());
		t.setSource(dto.getSource());
		t.setHtmlHead(dto.getHtmlHead());
		t.setSettings(dto.getSettings());
		
		TemplatePack tp = service.getTemplatePackById(tpackId);
		checkOnNull(tp);
		t.setTemplatePack(tp);
		
		return asDTO(service.createTemplate(tp, t), TemplateDTO.class);
	}
	
	@RequestMapping(value = "/packs/{id}/templates/{tid}/clone", method = RequestMethod.PUT)
	public TemplateDTO cloneTemplate(@PathVariable("id") ObjectId tpackId, @PathVariable("tid") ObjectId templateId) throws Exception {
		
		Template o = service.getTemplateById(templateId);
		Template t = new Template();
		t.setName("Copy of " + o.getName());
		t.setDescription(o.getDescription());
		t.setSource(o.getSource());
		t.setHtmlHead(o.getHtmlHead());
		t.setTemplatePack(o.getTemplatePack());
		t.setWidgets(new LinkedList<Widget>());
		
		// copy widgets
		for (Widget wo : o.getWidgets()) {
			Widget wn = new Widget();
			wn.setDescription(wo.getDescription());
			wn.setName(wo.getName());
			wn.setSource(wo.getSource());
			t.getWidgets().add(wn);
		}
		
		// copy images
		t.setImages(new LinkedList<MediaImage>());
		for (MediaImage mo : o.getImages()) {
			MediaImage m = new MediaImage();
			m.setAwtColorSpace(mo.getAwtColorSpace());
			m.setData(mo.getData());
			m.setDescription(mo.getDescription());
			m.setMimetype(mo.getMimetype());
			m.setName(mo.getName());
			m.setOwner(mo.getOwner());
			t.getImages().add(m);
		}
		
		// copy resources
		t.setResources(new LinkedList<Media>());
		for (Media mo : o.getResources()) {
			Media m = new Media();
			m.setData(mo.getData());
			m.setDescription(mo.getDescription());
			m.setName(mo.getName());
			m.setMimetype(mo.getMimetype());
			m.setOwner(mo.getOwner());
			t.getResources().add(m);
		}
		
		//copy settings
		t.setSettings(new LinkedHashMap<String, String>());
		o.getSettings().forEach(new BiConsumer<String, String>() { // <3 java 8!
			@Override
			public void accept(String k, String v) {
				t.getSettings().put(k,v);
			}
		});
		
		return asDTO(service.createTemplate(o.getTemplatePack(), t), TemplateDTO.class);
	}
	
	@RequestMapping(value = "/packs/{pid}/templates/{tid}", method = RequestMethod.GET)
	public TemplateDTO getTemplateById(@PathVariable("pid") ObjectId packId, @PathVariable("tid") ObjectId templateId) throws Exception {
		TemplatePack tp = service.getTemplatePackById(packId);
		for (Template t : tp.getTemplates()) {
			if (t.getId().equals(templateId)) return asDTO(t, TemplateDTO.class);
		}
		return null;
	}
	
	@RequestMapping(value = "/packs/{pid}/templates/{tid}", method = RequestMethod.PATCH)
	public TemplateDTO updateTemplate(@PathVariable("pid") ObjectId tpackId, @PathVariable("tid") ObjectId templateId,
			@RequestBody TemplateDTO dto) throws Exception {
		
		Template t = service.getTemplateById(templateId);
		t.setDescription(dto.getDescription());
		t.setName(dto.getName());
		t.setSource(dto.getSource());
		t.setHtmlHead(dto.getHtmlHead());
		t.setSettings(dto.getSettings());
		return asDTO(service.updateTemplate(t), TemplateDTO.class);
	}

	
	@RequestMapping(value = "/packs/{pid}/templates/{tid}", method = RequestMethod.DELETE)
	public void deleteTemplate(@PathVariable("pid") ObjectId tpackId, @PathVariable("tid") ObjectId templateId) throws Exception {
		Template t = service.getTemplateById(templateId);
		service.deleteTemplate(t);
	}
	
	@RequestMapping(value = "/templates/{tid}/widgets", method = RequestMethod.POST)
	public WidgetDTO createWidget(@PathVariable("tid") ObjectId templateId, @RequestBody WidgetDTO dto) throws Exception {
		
		Widget w = new Widget();
		w.setDescription(dto.getDescription());
		w.setName(dto.getName());
		w.setSource(dto.getSource());
		
		Template t = service.getTemplateById(templateId);
		w.setTemplate(t);
		return asDTO(service.createWidget(w), WidgetDTO.class);
	}
	
	@RequestMapping(value = "/templates/{tid}/widgets/{id}", method = RequestMethod.PATCH)
	public WidgetDTO updateWidget(@PathVariable("tid") ObjectId templateId, @PathVariable("id") ObjectId widgetId, @RequestBody WidgetDTO dto) throws Exception {
		
		Widget w = service.getWidgetById(widgetId);
		w.setDescription(dto.getDescription());
		w.setName(dto.getName());
		w.setSource(dto.getSource());
		return asDTO(service.updateWidget(w), WidgetDTO.class);
	}

	@RequestMapping(value = "/templates/{tid}/widgets/{wid}", method = RequestMethod.DELETE)
	public void deleteWidget(@PathVariable("tid") ObjectId templateId, @PathVariable("wid") ObjectId widgetId) throws Exception {
		Widget w = service.getWidgetById(widgetId);
		service.deleteWidget(w);
	}
	
	@RequestMapping(value ="/templates/{tid}/images", method = RequestMethod.POST)
	public MediaImageDTO createImage(@PathVariable("tid") ObjectId templateId, MultipartFile file) throws Exception {
		log.debug("Handling file upload from original filename" + file.getOriginalFilename());

		MediaImage m = new MediaImage();
		m.setData(file.getBytes());
		m.setName(file.getOriginalFilename());
		
		Template t = service.getTemplateById(templateId);
		
		return asDTO(service.createTemplateImage(t, m), MediaImageDTO.class);
	}
	
	@RequestMapping(value = "/templates/{tid}/images/{id}", method = RequestMethod.DELETE)
	public void deleteTemplateImage(@PathVariable("tid") ObjectId templateId, @PathVariable("id") ObjectId imageId) throws Exception {
		Template t = service.getTemplateById(templateId);
		ListIterator<MediaImage> it = t.getImages().listIterator();
		while (it.hasNext()) {
			if (it.next().getId().equals(imageId)) {
				it.remove();
				break;
			}
		}
		service.updateTemplate(t);
	}
	
	@RequestMapping(value = "/templates/{tid}/resources", method = RequestMethod.POST)
	public MediaDTO createResource(@PathVariable("tid") ObjectId template, MultipartFile file) throws Exception {
		Media m = new Media();
		m.setData(file.getBytes());
		m.setName(file.getOriginalFilename());
		Template t = service.getTemplateById(template);
		return asDTO(service.createTemplateResource(t, m), MediaDTO.class);
	}
	
	@RequestMapping(value = "/templates/{tid}/resources/{id}", method = RequestMethod.DELETE)
	public void deleteResource(@PathVariable("tid") ObjectId template, @PathVariable("id") ObjectId resourceId) throws Exception {
		Template t = service.getTemplateById(template);
		ListIterator<Media> it = t.getResources().listIterator();
		while (it.hasNext()) {
			if (it.next().getId().equals(resourceId)) {
				it.remove();
				break;
			}
		}
		service.updateTemplate(t);
	}
}
