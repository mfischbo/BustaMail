package de.mfischbo.bustamail.media.web;

import java.util.List;

import javax.inject.Inject;

import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import de.mfischbo.bustamail.common.web.BaseApiController;
import de.mfischbo.bustamail.media.domain.Directory;
import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.media.domain.MediaImage;
import de.mfischbo.bustamail.media.service.MediaService;

@RestController
@RequestMapping("/api/media/directory")
public class RestDirectoryController extends BaseApiController {

	@Inject
	private MediaService		service;
	
	/**
	 * Gets all directory roots the logged in user might see
	 * @return The list of directory roots
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public List<Directory> getDirectoryRoots() {
		return service.getDirectoryRoots();
	}

	/**
	 * Returns the directory with the specified id
	 * @param directoryId The id of the directory
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public List<MediaImage> getDirectoryById(@PathVariable("id") ObjectId directoryId) throws Exception {
		Directory d = service.getDirectoryById(directoryId);
		return service.getFilesByDirectory(d);
	}
	
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public Directory createDirectory(@PathVariable("id") ObjectId parentDirectoryId, @RequestBody Directory dir) throws Exception {
		Directory d = service.getDirectoryById(parentDirectoryId);
		return service.createDirectory(d.getOwner(), d, dir);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	public Media createFile(@PathVariable("id") ObjectId directoryId, MultipartFile file) throws Exception {
		Directory d = service.getDirectoryById(directoryId);
		Media m = new Media();
		m.setData(file.getBytes());
		m.setDirectory(d);
		m.setName(file.getOriginalFilename());
		m.setOwner(d.getOwner());
		return service.createMedia(m);
	}

	/**
	 * Updates the given directory
	 * @param directoryId
	 * @param dto
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
	public Directory updateDirectory(@PathVariable("id") ObjectId directoryId, @RequestBody Directory dto) throws Exception {
		Directory d = service.getDirectoryById(directoryId);
		d.setName(dto.getName());
		d.setDescription(dto.getDescription());
		return service.updateDirectory(d);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deleteDirectory(@PathVariable("id") ObjectId directoryId) throws Exception {
		Directory d = service.getDirectoryById(directoryId);
		service.deleteDirectory(d);
	}
}
