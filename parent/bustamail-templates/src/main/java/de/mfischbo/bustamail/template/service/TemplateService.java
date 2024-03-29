package de.mfischbo.bustamail.template.service;

import java.io.OutputStream;
import java.util.zip.ZipInputStream;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import de.mfischbo.bustamail.exception.BustaMailException;
import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.template.domain.TemplatePack;

public interface TemplateService {

	@PreAuthorize("hasPermission(#owner, 'Templates.USE_TEMPLATES')")
	public Page<TemplatePack>		getAllTemplatePacks(ObjectId owner, Pageable page);

	@PostAuthorize("hasPermission(returnObject.owner, 'Templates.USE_TEMPLATES')")
	public TemplatePack				getTemplatePackById(ObjectId id) throws EntityNotFoundException;

	@PostAuthorize("hasPermission(returnObject.owner, 'Templates.USE_TEMPLATES')")
	public TemplatePack				getTemplatePackContainingTemplateById(ObjectId template) throws EntityNotFoundException;
	
	@PreAuthorize("hasPermission(#pack.owner, 'Templates.MANAGE_TEMPLATES')")
	public TemplatePack				createTemplatePack(TemplatePack pack) throws Exception;
	
	@PreAuthorize("hasPermission(#pack.owner, 'Templates.MANAGE_TEMPLATES')")
	public TemplatePack				updateTemplatePack(@P("pack") TemplatePack pack) throws EntityNotFoundException;
	
	@PreAuthorize("hasPermission(#pack.owner, 'Templates.MANAGE_TEMPLATES')")
	public void						deleteTemplatePack(TemplatePack pack);
	
	@PreAuthorize("hasPermission(#pack.owner, 'Templates.MANAGE_TEMPLATES')")
	public void						exportAsZip(ObjectId owner, TemplatePack tp);
	
	@PreAuthorize("hasPermission(#pack.owner, 'Templates.MANAGE_TEMPLATES')")
	public Media					createTemplatePackImage(TemplatePack pack, Media image) throws Exception;

	@PreAuthorize("hasPermission(#owner, 'Templates.MANAGE_TEMPLATES')")
	public TemplatePack				importTemplatePack(ObjectId owner, ZipInputStream stream) throws Exception;
	
	@PreAuthorize("hasPermission(#pack.owner, 'Templates.MANAGE_TEMPLATES')")
	public void						exportTemplatePack(TemplatePack pack, OutputStream stream) throws BustaMailException;

	@PreAuthorize("hasPermission(#pack.owner, 'Templates.MANAGE_TEMPLATES')")
	public TemplatePack				cloneTemplatePack(TemplatePack  pack) throws Exception;
	
	@PreAuthorize("hasPermission(#pack.owner, 'Templates.MANAGE_TEMPLATES')")
	public Media					createTemplateResource(TemplatePack pack, ObjectId templateId, Media m, String type) throws Exception;

	@PreAuthorize("hasPermission(#pack.owner, 'Templates.MANAGE_TEMPLATES')")
	public void						deleteTemplateResource(TemplatePack pack, ObjectId templateId, ObjectId resourceId, String type) throws Exception;
}