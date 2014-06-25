package de.mfischbo.bustamail.template.web;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.mfischbo.bustamail.common.web.BaseApiController;
import de.mfischbo.bustamail.media.domain.MediaImage;
import de.mfischbo.bustamail.media.dto.MediaImageDTO;
import de.mfischbo.bustamail.template.domain.Template;
import de.mfischbo.bustamail.template.domain.TemplatePack;
import de.mfischbo.bustamail.template.domain.Widget;
import de.mfischbo.bustamail.template.dto.TemplatePackDTO;
import de.mfischbo.bustamail.template.dto.TemplatePackIndexDTO;
import de.mfischbo.bustamail.template.service.TemplateService;

@RestController
@RequestMapping("/api/templates")
public class RestTemplatePackController extends BaseApiController {

	@Inject
	private TemplateService		service;

	@Inject
	private ObjectMapper		jacksonMapper;
	
	@RequestMapping(value = "/{owner}/packs", method = RequestMethod.GET)
	public Page<TemplatePackIndexDTO> getAllTemplatePacks(@PathVariable("owner") UUID owner, @PageableDefault(page =0, size=30, sort="name") Pageable page) {
		return asDTO(service.getAllTemplatePacks(owner, page), TemplatePackIndexDTO.class, page);
	}

	@Transactional
	@RequestMapping(value = "/packs/{id}", method = RequestMethod.GET)
	public TemplatePackDTO getTemplatePackById(@PathVariable("id") UUID id) throws Exception {
		return asDTO(service.getTemplatePackById(id), TemplatePackDTO.class);
	}
	
	@RequestMapping(value = "/packs/{id}/themeImage", method = RequestMethod.POST)
	public MediaImageDTO createThemeImage(@PathVariable("id") UUID packId, MultipartFile file) throws Exception {
		TemplatePack tp = service.getTemplatePackById(packId);
	
		MediaImage im = new MediaImage();
		im.setData(file.getBytes());
		im.setOwner(tp.getOwner());
		im.setName("Theme for " + tp.getName());
		return asDTO(service.createTemplatePackImage(tp, im), MediaImageDTO.class);
	}
	
	@RequestMapping(value = "/packs/{id}/clone", method = RequestMethod.PUT)
	public TemplatePackDTO cloneTemplatePack(@PathVariable("id") UUID id) throws Exception {
		TemplatePack o = service.getTemplatePackById(id);
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
		return asDTO(service.createTemplatePack(n), TemplatePackDTO.class);
	}
	
	@RequestMapping(value = "/packs", method = RequestMethod.POST)
	public TemplatePackDTO createTemplatePack(@RequestBody TemplatePackIndexDTO dto) throws Exception {
		
		TemplatePack t = new TemplatePack();
		t.setDescription(dto.getDescription());
		t.setName(dto.getName());
		t.setOwner(dto.getOwner());
		return asDTO(service.createTemplatePack(t), TemplatePackDTO.class);
	}
	
	@RequestMapping(value = "/packs/{id}", method = RequestMethod.PATCH)
	public TemplatePackDTO updateTemplatePack(@PathVariable("id") UUID id, @RequestBody TemplatePackDTO dto) throws Exception {
		
		TemplatePack t = service.getTemplatePackById(id);
		t.setDescription(dto.getDescription());
		t.setName(dto.getName());
		t.setOwner(dto.getOwner());
		return asDTO(service.updateTemplatePack(t), TemplatePackDTO.class);
	}
	
	@RequestMapping(value = "/packs/{id}", method = RequestMethod.DELETE) 
	public void deleteTemplatePack(@PathVariable("id") UUID id) throws Exception {
		TemplatePack t = service.getTemplatePackById(id);
		service.deleteTemplatePack(t);
	}
	
	@RequestMapping(value = "/packs/{id}/download", method = RequestMethod.GET)
	public void downloadTemplatePack(@PathVariable("id") UUID id, HttpServletResponse response) throws Exception {
		TemplatePack t = service.getTemplatePackById(id);
		checkOnNull(t);
	
		response.setHeader("Content-Type", "application/octet-stream");
		response.setHeader("Content-Disposition", "attachment;filename=" + t.getName() + ".zip");
		response.setHeader("Content-Transfer-Encoding", "binary");
		
		// map the pack to a dto
		TemplatePackDTO d = asDTO(t, TemplatePackDTO.class);
	
		ZipOutputStream zip = new ZipOutputStream(response.getOutputStream());
		zip.putNextEntry(new ZipEntry("manifest.json"));
		zip.write(jacksonMapper.writeValueAsBytes(d));
		
		zip.putNextEntry(new ZipEntry(t.getThemeImage().getId().toString()));
		zip.write(t.getThemeImage().getData());

		for (Template tm : t.getTemplates()) {
			for (MediaImage i : tm.getImages()) {
				zip.putNextEntry(new ZipEntry(i.getId().toString()));
				zip.write(i.getData());
			}
		}
		zip.flush();
		zip.close();
		response.getOutputStream().flush();
		response.getOutputStream().close();
	}
	
	@RequestMapping(value = "/packs/upload", method = RequestMethod.POST)
	public void uploadTemplatePack(MultipartFile file) throws Exception {
		
	}
}
