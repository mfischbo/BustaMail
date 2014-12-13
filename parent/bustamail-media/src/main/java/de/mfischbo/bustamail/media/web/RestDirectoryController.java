package de.mfischbo.bustamail.media.web;

import java.util.List;

import javax.inject.Inject;

import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.mfischbo.bustamail.common.web.BaseApiController;
import de.mfischbo.bustamail.media.domain.Directory;
import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.media.service.MediaService;

/**
 * RESTful controller handling directories
 * @author M. Fischboeck 
 *
 */
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
	public List<Media> getDirectoryById(@PathVariable("id") ObjectId directoryId) throws Exception {
		Directory d = service.getDirectoryById(directoryId);
		return service.getFilesByDirectory(d);
	}
	

	/**
	 * Creates a new directory in the directory given the id
	 * @param parentDirectoryId The id of the parent directory
	 * @param dir The directory to be created
	 * @return The persisted instance
	 * @throws Exception On any error
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	public Directory createDirectory(@PathVariable("id") ObjectId parentDirectoryId, @RequestBody Directory dir) throws Exception {
		Directory d = service.getDirectoryById(parentDirectoryId);
		return service.createDirectory(d.getOwner(), d, dir);
	}
	
	/**
	 * Updates the given directory
	 * @param directoryId The id of the directory to be updated
	 * @param dto The data of the update
	 * @return The persisted instance
	 * @throws Exception On any error
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
	public Directory updateDirectory(@PathVariable("id") ObjectId directoryId, @RequestBody Directory dto) throws Exception {
		Directory d = service.getDirectoryById(directoryId);
		d.setName(dto.getName());
		d.setDescription(dto.getDescription());
		return service.updateDirectory(d);
	}

	/**
	 * Deletes the given directory
	 * @param directoryId The id of the directory
	 * @throws Exception On any error
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deleteDirectory(@PathVariable("id") ObjectId directoryId) throws Exception {
		Directory d = service.getDirectoryById(directoryId);
		service.deleteDirectory(d);
	}
}
