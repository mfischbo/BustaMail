package de.mfischbo.bustamail.media.web;

import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.mfischbo.bustamail.common.web.BaseApiController;
import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.media.service.MediaService;

/**
 * RESTful controller for handling media files
 * @author M. Fischboeck
 */
@Controller
@RequestMapping("/api/files")
public class RestMediaController extends BaseApiController {

	@Autowired
	private MediaService			service;

	/**
	 * Returns the content of a media file given it's id
	 * @param mediaId The id of the media file
	 * @param response The HttpServletResponse the output is written to
	 * @throws Exception On any error
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public void getMediaContent(@PathVariable("id") ObjectId mediaId,
			@RequestParam(value = "w", required = false, defaultValue = "1024") Integer width, 
			HttpServletResponse response) throws Exception {
		
		Media m = service.getMediaById(mediaId);
		response.setHeader("Content-Type", m.getMimetype());
		if (m.getMimetype().startsWith("image"))
			service.getContent(m, width, response.getOutputStream());
		else
			service.getContent(m, response.getOutputStream());
	}
}