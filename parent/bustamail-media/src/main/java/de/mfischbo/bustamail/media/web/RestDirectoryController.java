package de.mfischbo.bustamail.media.web;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import de.mfischbo.bustamail.common.web.BaseApiController;
import de.mfischbo.bustamail.media.domain.Directory;
import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.media.dto.DirectoryDTO;
import de.mfischbo.bustamail.media.dto.MediaDTO;
import de.mfischbo.bustamail.media.dto.MediaImageDTO;
import de.mfischbo.bustamail.media.service.MediaService;

@RestController
@RequestMapping("/api/media/directory")
public class RestDirectoryController extends BaseApiController {

	@Autowired
	private MediaService		service;
	
	/**
	 * Gets all directory roots the logged in user might see
	 * @return The list of directory roots
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public List<DirectoryDTO> getDirectoryRoots() {
		return asDTO(service.getDirectoryRoots(), DirectoryDTO.class);
	}

	/**
	 * Returns the directory with the specified id
	 * @param directoryId The id of the directory
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public List<MediaImageDTO> getDirectoryById(@PathVariable("id") ObjectId directoryId) throws Exception {
		Directory d = service.getDirectoryById(directoryId);
		return asDTO(d.getFiles(), MediaImageDTO.class);
		//return asDTO(service.getDirectoryById(directoryId), DirectoryDTO.class);
	}
	
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public DirectoryDTO createDirectory(@PathVariable("id") ObjectId parentDirectoryId, @RequestBody DirectoryDTO dto) throws Exception {
		Directory d = service.getDirectoryById(parentDirectoryId);
		Directory n = new Directory();
		n.setName(dto.getName());
		n.setDescription(dto.getDescription());
		n.setParent(d);
		n.setOwner(d.getOwner());
		return asDTO(service.createDirectory(d.getOwner(), n), DirectoryDTO.class);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	public MediaDTO createFile(@PathVariable("id") ObjectId directoryId, MultipartFile file) throws Exception {
		Directory d = service.getDirectoryById(directoryId);
		Media m = new Media();
		m.setData(file.getBytes());
		m.setDirectory(d);
		m.setName(file.getOriginalFilename());
		m.setOwner(d.getOwner());
		return asDTO(service.createMedia(m), MediaDTO.class);
	}

	/**
	 * Updates the given directory
	 * @param directoryId
	 * @param dto
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
	public DirectoryDTO updateDirectory(@PathVariable("id") ObjectId directoryId, @RequestBody DirectoryDTO dto) throws Exception {
		Directory d = service.getDirectoryById(directoryId);
		d.setName(dto.getName());
		d.setDescription(dto.getDescription());
		return asDTO(service.updateDirectory(d), DirectoryDTO.class);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deleteDirectory(@PathVariable("id") ObjectId directoryId) throws Exception {
		Directory d = service.getDirectoryById(directoryId);
		service.deleteDirectory(d);
	}
}
