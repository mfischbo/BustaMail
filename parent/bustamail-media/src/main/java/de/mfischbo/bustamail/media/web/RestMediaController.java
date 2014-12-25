package de.mfischbo.bustamail.media.web;

import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import de.mfischbo.bustamail.common.web.BaseApiController;
import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.media.service.MediaService;

/**
 * RESTful controller for handling media files
 * @author M. Fischboeck
 */
@Controller
@RequestMapping("/api/media")
public class RestMediaController extends BaseApiController {

	@Autowired
	private MediaService			service;

	/**
	 * Creates a new media file
	 * @param dto The media to be created
	 * @param file The file content
	 * @return The persisted instance
	 * @throws Exception On any error
	 */
	@RequestMapping(value = "", method = RequestMethod.POST)
	public Media createMedia(@RequestBody Media m, MultipartFile file) throws Exception {
		m.setData(file.getInputStream());
		return service.createMedia(m);
	}

	/**
	 * Returns the content of a media file given it's id
	 * @param mediaId The id of the media file
	 * @param response The HttpServletResponse the output is written to
	 * @throws Exception On any error
	 */
	@RequestMapping(value = "/{id}/content", method = RequestMethod.GET)
	public void getMediaContent(@PathVariable("id") ObjectId mediaId, HttpServletResponse response) throws Exception {
		Media m = service.getMediaById(mediaId);
		service.getContent(m, response.getOutputStream());
	}

	/**
	 * Deletes a media file given it's id.
	 * If the media file is an image all variants will be removed as well.
	 * @param mediaId The id of the file to be deleted
	 * @throws Exception On any error
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deleteMedia(@PathVariable("id") ObjectId mediaId) throws Exception {
		Media m = service.getMediaById(mediaId);
		service.deleteMedia(m);
	}
}
