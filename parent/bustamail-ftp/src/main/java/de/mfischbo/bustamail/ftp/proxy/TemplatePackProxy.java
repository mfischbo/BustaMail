package de.mfischbo.bustamail.ftp.proxy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mfischbo.bustamail.ftp.domain.BaseFtpFile;
import de.mfischbo.bustamail.template.domain.Template;
import de.mfischbo.bustamail.template.domain.TemplatePack;
import de.mfischbo.bustamail.template.domain.Widget;
import de.mfischbo.bustamail.template.service.TemplateService;

public class TemplatePackProxy implements ISourceProxy {

	private TemplateService		service;
	private TemplatePack		pack;
	private Logger				log = LoggerFactory.getLogger(getClass());
	
	public TemplatePackProxy(TemplateService service, TemplatePack pack) {
		this.service = service;
		this.pack    = pack;
	}
	
	@Override
	public InputStream getInputStream(BaseFtpFile file) {
		Template t = selectTemplate(file);
		if (t == null)
			return null;
		if (file.getName().equals("index.html"))
			return new ByteArrayInputStream(t.getSource().getBytes());
		if (file.getName().equals("head.html"))
			return new ByteArrayInputStream(t.getHtmlHead().getBytes());
		for (Widget w : t.getWidgets()) {
			if ((w.getName() + ".html").equals(file.getName())) {
				return new ByteArrayInputStream(w.getSource().getBytes());
			}
		}
		log.warn("Unable to get input stream for file " + file.getAbsolutePath());
		return null;
	}

	@Override
	public OutputStream getOutputStream(BaseFtpFile file) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private Template selectTemplate(BaseFtpFile file) {
		String[] parts = file.getAbsolutePath().split("/");
		int idx = 0;
		for (idx=0; idx < parts.length; idx++) {
			if (parts[idx].equals(pack.getName())) {
				idx++;
				break;
			}
		}
		String tName = parts[idx];
		for (Template t : pack.getTemplates()) {
			if (t.getName().equals(tName))
				return t;
		}
		return null;
	}

}
