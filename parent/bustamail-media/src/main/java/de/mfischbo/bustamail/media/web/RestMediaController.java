package de.mfischbo.bustamail.media.web;

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

@Controller
@RequestMapping("/api/media")
public class RestMediaController extends BaseApiController {

	@Autowired
	private MediaService			service;


	@RequestMapping(value = "", method = RequestMethod.POST)
	public Media createMedia(@RequestBody Media dto, MultipartFile file) throws Exception {
	
		Media m = new Media();
		m.setData(file.getBytes());
		m.setName(dto.getName());
		m.setDescription(dto.getDescription());
		return service.createMedia(m);
	}


	@RequestMapping(value = "/{id}/content", method = RequestMethod.GET)
	public String getMediaContent(@PathVariable("id") ObjectId mediaId) throws Exception {
		Media m = service.getMediaById(mediaId);
		return new String(m.getData());
	}
	
	@RequestMapping(value = "/{id}/content", method = RequestMethod.PATCH)
	public void updateMediaContent(@PathVariable("id") ObjectId mediaId, @RequestBody String data) throws Exception {
		Media m = service.getMediaById(mediaId);
		m.setData(data.getBytes());
		service.updateMedia(m);
	}
}
