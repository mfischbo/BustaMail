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
import de.mfischbo.bustamail.media.domain.MediaImage;
import de.mfischbo.bustamail.template.domain.Template;
import de.mfischbo.bustamail.template.domain.TemplatePack;

public interface TemplateService {

	@PreAuthorize("hasPermission(#owner, 'Templates.USE_TEMPLATES')")
	public Page<TemplatePack>		getAllTemplatePacks(ObjectId owner, Pageable page);

	@PostAuthorize("hasPermission(returnObject.owner, 'Templates.USE_TEMPLATES')")
	public TemplatePack				getTemplatePackById(ObjectId id) throws EntityNotFoundException;

	@PreAuthorize("hasPermission(#pack.owner, 'Templates.MANAGE_TEMPLATES')")
	public TemplatePack				createTemplatePack(TemplatePack pack) throws EntityNotFoundException;
	
	@PreAuthorize("hasPermission(#pack.owner, 'Templates.MANAGE_TEMPLATES')")
	public TemplatePack				updateTemplatePack(@P("pack") TemplatePack pack) throws EntityNotFoundException;
	
	@PreAuthorize("hasPermission(#pack.owner, 'Templates.MANAGE_TEMPLATES')")
	public void						deleteTemplatePack(TemplatePack pack);
	public void						exportAsZip(ObjectId owner, TemplatePack tp);
	public MediaImage				createTemplatePackImage(TemplatePack pack, MediaImage image);

	public TemplatePack				importTemplatePack(ObjectId owner, ZipInputStream stream) throws BustaMailException;
	public void						exportTemplatePack(TemplatePack pack, OutputStream stream) throws BustaMailException;
	public TemplatePack				cloneTemplatePack(TemplatePack  pack) throws EntityNotFoundException;
	
	
	
	@PostAuthorize("hasPermission(returnObject.templatePack.owner, 'Templates.USE_TEMPLATES')")
	public Template					getTemplateById(ObjectId template) throws EntityNotFoundException;

	/*
	@PreAuthorize("hasPermission(#template.templatePack.owner, 'Templates.MANAGE_TEMPLATES')")
	public Template					createTemplate(TemplatePack pack, Template template);
	public Template					updateTemplate(Template template);
	public void						deleteTemplate(Template template);
	public MediaImage				createTemplateImage(Template template, MediaImage image);
	public Media					createTemplateResource(Template template, Media media);
	
	public Widget					getWidgetById(ObjectId id) throws EntityNotFoundException;
	public Widget					createWidget(TemplatePack tp, Template t, Widget w);
	public Widget					updateWidget(TemplatePack tp, Template t, Widget w);
	public void						deleteWidget(TemplatePack tp, Template t, Widget w);
	*/
}
