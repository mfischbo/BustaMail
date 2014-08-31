package de.mfischbo.bustamail.template.web;

import java.io.ByteArrayInputStream;
import java.util.UUID;
import java.util.zip.ZipInputStream;

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

import de.mfischbo.bustamail.common.web.BaseApiController;
import de.mfischbo.bustamail.media.domain.MediaImage;
import de.mfischbo.bustamail.media.dto.MediaImageDTO;
import de.mfischbo.bustamail.template.domain.TemplatePack;
import de.mfischbo.bustamail.template.dto.TemplatePackDTO;
import de.mfischbo.bustamail.template.dto.TemplatePackIndexDTO;
import de.mfischbo.bustamail.template.service.TemplateService;

@RestController
@RequestMapping("/api/templates")
public class RestTemplatePackController extends BaseApiController {

	@Inject
	private TemplateService		service;

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
		return asDTO(service.cloneTemplatePack(o), TemplatePackDTO.class);
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
		service.exportTemplatePack(t, response.getOutputStream());
	}
	
	@RequestMapping(value = "/{owner}/packs/upload", method = RequestMethod.POST)
	public TemplatePackIndexDTO uploadTemplatePack(@PathVariable("owner") UUID owner, MultipartFile file) throws Exception {
		ByteArrayInputStream bin = new ByteArrayInputStream(file.getBytes());
		ZipInputStream zipin = new ZipInputStream(bin);
		return asDTO(service.importTemplatePack(owner, zipin), TemplatePackIndexDTO.class);
	}
}
