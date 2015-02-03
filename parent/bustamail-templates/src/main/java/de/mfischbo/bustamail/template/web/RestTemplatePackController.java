package de.mfischbo.bustamail.template.web;

import java.io.ByteArrayInputStream;
import java.util.zip.ZipInputStream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import de.mfischbo.bustamail.common.web.BaseApiController;
import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.template.domain.Template;
import de.mfischbo.bustamail.template.domain.TemplatePack;
import de.mfischbo.bustamail.template.service.TemplateService;

@RestController
@RequestMapping(value = "/api/templatepacks")
public class RestTemplatePackController extends BaseApiController {

	@Inject
	private TemplateService		service;
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public Page<TemplatePack> getTemplatePacks(@RequestParam("owner") ObjectId owner, @PageableDefault(size=30) Pageable page) {
		return service.getAllTemplatePacks(owner, page);
	}
	
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public TemplatePack getTemplatePackById(@PathVariable("id") ObjectId id) throws Exception {
		return service.getTemplatePackById(id);
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	public TemplatePack createTemplatePack(@RequestBody TemplatePack pack) throws Exception {
		return service.createTemplatePack(pack);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
	public TemplatePack updateTemplatePack(@RequestBody TemplatePack pack) throws Exception {
		return service.updateTemplatePack(pack);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public TemplatePack cloneTemplatePack(@PathVariable("id") ObjectId id) throws Exception {
		TemplatePack o = service.getTemplatePackById(id);
		return service.cloneTemplatePack(o);
	}

	@RequestMapping(value = "/{id}/download", method = RequestMethod.GET)
	public void downloadTemplatePack(@PathVariable("id") ObjectId id, HttpServletResponse response) throws Exception {
		TemplatePack t = service.getTemplatePackById(id);
		checkOnNull(t);
		response.setHeader("Content-Type", "application/octet-stream");
		response.setHeader("Content-Disposition", "attachment;filename=" + t.getName() + ".zip");
		response.setHeader("Content-Transfer-Encoding", "binary");
		service.exportTemplatePack(t, response.getOutputStream());
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public TemplatePack uploadTemplatePack(@RequestParam(value = "owner", required = true) ObjectId owner, MultipartFile file) throws Exception {
		ByteArrayInputStream bin = new ByteArrayInputStream(file.getBytes());
		ZipInputStream zipin = new ZipInputStream(bin);
		return service.importTemplatePack(owner, zipin);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deleteTemplatePack(@PathVariable("id") ObjectId id) throws Exception {
		TemplatePack pack = service.getTemplatePackById(id);
		service.deleteTemplatePack(pack);
	}
	
	@RequestMapping(value = "/{id}/templates/{tid}", method = RequestMethod.GET)
	public Template getTemplateById(@PathVariable("id") ObjectId packId, @PathVariable("tid") ObjectId templateId) throws Exception {
		TemplatePack pack = service.getTemplatePackById(packId);
		return pack.getTemplates().stream()
				.filter(t -> t.getId().equals(templateId))
				.findFirst()
				.get();
	}
	
	@RequestMapping(value = "/{id}/image", method = RequestMethod.POST)
	public Media createThemeImage(@PathVariable("id") ObjectId id, MultipartFile file) throws Exception {
		TemplatePack pack = service.getTemplatePackById(id);
		Media m = new Media();
		m.setOwner(pack.getOwner());
		m.setName("Theme for " + pack.getName());
		m.setData(file.getInputStream());
		return service.createTemplatePackImage(pack, m);
	}
	
	@RequestMapping(value = "/{id}/templates/{tid}/resources", method = RequestMethod.POST)
	public Media createTemplateStaticResource(@PathVariable("id") ObjectId tpId, 
			@PathVariable("tid") ObjectId tId,
			MultipartFile file,
			@RequestParam(value = "type", required = true) String type) throws Exception {
		TemplatePack pack = service.getTemplatePackById(tpId);
		Media m = new Media();
		m.setOwner(pack.getOwner());
		m.setName(file.getOriginalFilename());
		m.setData(file.getInputStream());
		return service.createTemplateResource(pack, tId, m, type);
	}
	
	
	@RequestMapping(value = "/{id}/templates/{tid}/resources/{rid}", method = RequestMethod.DELETE)
	public void deleteTemplateResource(@PathVariable("id") ObjectId packId,
			@PathVariable("tid") ObjectId templateId, 
			@PathVariable("rid") ObjectId resourceId,
			@RequestParam(value = "type", required=true) String type) throws Exception {
		TemplatePack pack = service.getTemplatePackById(packId);
		service.deleteTemplateResource(pack, templateId, resourceId, type);
	}
}