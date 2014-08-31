package de.mfischbo.bustamail.template.service;

import java.io.OutputStream;
import java.util.UUID;
import java.util.zip.ZipInputStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import de.mfischbo.bustamail.exception.BustaMailException;
import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.media.domain.MediaImage;
import de.mfischbo.bustamail.template.domain.Template;
import de.mfischbo.bustamail.template.domain.TemplatePack;
import de.mfischbo.bustamail.template.domain.Widget;

public interface TemplateService {

	public Page<TemplatePack>		getAllTemplatePacks(UUID owner, Pageable page);
	public TemplatePack				getTemplatePackById(UUID id) throws EntityNotFoundException;
	public TemplatePack				createTemplatePack(TemplatePack pack) throws EntityNotFoundException;
	public TemplatePack				importTemplatePack(UUID owner, ZipInputStream stream) throws BustaMailException;
	public void						exportTemplatePack(TemplatePack pack, OutputStream stream) throws BustaMailException;
	public TemplatePack				cloneTemplatePack(TemplatePack  pack) throws EntityNotFoundException;
	public TemplatePack				updateTemplatePack(TemplatePack pack) throws EntityNotFoundException;
	public void						deleteTemplatePack(TemplatePack pack) throws EntityNotFoundException;
	public void						exportAsZip(UUID owner, TemplatePack tp);
	public MediaImage				createTemplatePackImage(TemplatePack pack, MediaImage image);

	public Template					getTemplateById(UUID templateId) throws EntityNotFoundException;
	public Template					createTemplate(Template template);
	public Template					updateTemplate(Template template);
	public void						deleteTemplate(Template template);
	public MediaImage				createTemplateImage(Template template, MediaImage image);
	public Media					createTemplateResource(Template template, Media media);
	
	public Widget					getWidgetById(UUID id) throws EntityNotFoundException;
	public Widget					createWidget(Widget w);
	public Widget					updateWidget(Widget w);
	public void						deleteWidget(Widget w);
}
