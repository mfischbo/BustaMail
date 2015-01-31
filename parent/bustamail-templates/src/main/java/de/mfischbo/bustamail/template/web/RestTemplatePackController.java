package de.mfischbo.bustamail.template.web;

import java.io.ByteArrayInputStream;
import java.util.zip.ZipInputStream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mfischbo.bustamail.common.web.BaseApiController;
import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.template.domain.Template;
import de.mfischbo.bustamail.template.domain.TemplatePack;
import de.mfischbo.bustamail.template.service.TemplateService;
import de.mfischbo.bustamail.views.TemplatePackDetailView;

@RestController
@RequestMapping("/api/templates")
public class RestTemplatePackController extends BaseApiController {

	@Inject
	private TemplateService		service;

	@Autowired
	private ObjectMapper		mapper;
	
	@RequestMapping(value = "/templates/{id}", method = RequestMethod.GET)
	public Template getTemplateById(@PathVariable("id") ObjectId templateId) throws Exception {
		TemplatePack tp = service.getTemplatePackContainingTemplateById(templateId);
		for (Template t : tp.getTemplates()) {
			if (t.getId().equals(templateId))
				return t;
		}
		throw new EntityNotFoundException("No template found for id : " + templateId);
	}
	

	/**
	 * Returns all template packs visible to the given org unit
	 * @param owner The org unit
	 * @param page The page parameters
	 * @return A page of template packs
	 */
	@RequestMapping(value = "/{owner}/packs", method = RequestMethod.GET)
	@JsonView(Object.class)
	public Page<TemplatePack> getAllTemplatePacks(@PathVariable("owner") ObjectId owner, 
			@PageableDefault(page=0, size=30, sort="name") Pageable page) throws Exception {
		Page<TemplatePack> retval = service.getAllTemplatePacks(owner, page);
		return retval;
	}

	/**
	 * Returns the template pack for the given id
	 * @param id The id of the template pack to be returned
	 * @return The template pack
	 * @throws Exception If there is no such template pack
	 */
	@RequestMapping(value = "/packs/{id}", method = RequestMethod.GET)
	@JsonView(TemplatePackDetailView.class)
	public TemplatePack getTemplatePackById(@PathVariable("id") ObjectId id) throws Exception {
		return service.getTemplatePackById(id);
	}

	/**
	 * Creates a new template pack
	 * @param pack The template Pack to be created
	 * @return The persisted instance
	 * @throws Exception If something went wrong
	 */
	@RequestMapping(value = "/packs", method = RequestMethod.POST)
	public TemplatePack createTemplatePack(@RequestBody TemplatePack pack) throws Exception {
		return service.createTemplatePack(pack);
	}

	/**
	 * Updates the template pack by it's id
	 * @param id The id of the template pack to be updated
	 * @param pack The pack
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/packs/{id}", method = RequestMethod.PATCH)
	public TemplatePack updateTemplatePack(@PathVariable("id") ObjectId id, 
			@RequestBody TemplatePack pack) throws Exception {
		TemplatePack ident = service.getTemplatePackById(id);
		checkOnNull(ident);
		return service.updateTemplatePack(pack);
	}

	/**
	 * Deletes the template pack given it's id
	 * @param id The id of the template pack to be deleted
	 * @throws Exception
	 */
	@RequestMapping(value = "/packs/{id}", method = RequestMethod.DELETE) 
	public void deleteTemplatePack(@PathVariable("id") ObjectId id) throws Exception {
		TemplatePack t = service.getTemplatePackById(id);
		service.deleteTemplatePack(t);
	}
	
	
	
	@RequestMapping(value = "/packs/{id}/themeImage", method = RequestMethod.POST)
	public Media createThemeImage(@PathVariable("id") ObjectId packId, MultipartFile file) throws Exception {
		TemplatePack tp = service.getTemplatePackById(packId);
	
		Media im = new Media();
		im.setOwner(tp.getOwner());
		im.setName("Theme for " + tp.getName());
		im.setData(file.getInputStream());
		return service.createTemplatePackImage(tp, im);
	}
	
	@RequestMapping(value = "/packs/{id}/clone", method = RequestMethod.PUT)
	public TemplatePack cloneTemplatePack(@PathVariable("id") ObjectId id) throws Exception {
		TemplatePack o = service.getTemplatePackById(id);
		return service.cloneTemplatePack(o);
	}
	

	@RequestMapping(value = "/packs/{id}/download", method = RequestMethod.GET)
	public void downloadTemplatePack(@PathVariable("id") ObjectId id, HttpServletResponse response) throws Exception {

		TemplatePack t = service.getTemplatePackById(id);
		checkOnNull(t);
		response.setHeader("Content-Type", "application/octet-stream");
		response.setHeader("Content-Disposition", "attachment;filename=" + t.getName() + ".zip");
		response.setHeader("Content-Transfer-Encoding", "binary");
		service.exportTemplatePack(t, response.getOutputStream());
	}
	
	@RequestMapping(value = "/{owner}/packs/upload", method = RequestMethod.POST)
	public TemplatePack uploadTemplatePack(@PathVariable("owner") ObjectId owner, MultipartFile file) throws Exception {
		ByteArrayInputStream bin = new ByteArrayInputStream(file.getBytes());
		ZipInputStream zipin = new ZipInputStream(bin);
		return service.importTemplatePack(owner, zipin);
	}
}
