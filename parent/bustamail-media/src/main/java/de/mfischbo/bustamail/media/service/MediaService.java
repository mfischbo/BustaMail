package de.mfischbo.bustamail.media.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.media.domain.Directory;
import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.media.domain.MediaImage;

public interface MediaService {

	public Page<Media>		getAllMedias(Pageable page);
	
	void			flushToDisk() throws Exception;

	@PostAuthorize("hasPermission(returnObject.owner, 'Media.USE_MEDIA')")
	Media			getMediaById(UUID id) throws EntityNotFoundException;
	
	@PreAuthorize("hasPermission(#m.owner, 'Media.MANAGE_MEDIA')")
	Media			createMedia(@P("m") Media media) throws EntityNotFoundException;
	
	@PreAuthorize("hasPermission(#m.owner, 'Media.MANAGE_MEDIA')")
	MediaImage		createMediaImage(@P("m") MediaImage media);
	
	@PreAuthorize("hasPermission(#m.owner, 'Media.MANAGE_MEDIA')")
	Media			updateMedia(@P("m") Media media) throws EntityNotFoundException;
	
	@PreAuthorize("hasPermission(#m.owner, 'Media.MANAGE_MEDIA')")
	void				deleteMedia(@P("m") Media media) throws EntityNotFoundException;
	
	
	
	// ------------------------	//
	// 	Directories			   //
	// -----------------------//
	
	List<Directory> getDirectoryRoots();
	
	@PostAuthorize("hasPermission(returnObject.owner, 'Media.USE_MEDIA')")
	Directory		getDirectoryById(UUID directory) throws EntityNotFoundException;
	
	@PreAuthorize("hasPermission(#owner, 'Media.MANAGE_MEDIA')")
	Directory		createDirectory(UUID owner, Directory directory);
	
	@PreAuthorize("hasPermission(#d.owner, 'Media.MANAGE_MEDIA')")
	Directory		updateDirectory(@P("d") Directory directory);
	
	@PreAuthorize("hasPermission(#d.owner, 'Media.MANAGE_MEDIA')")
	void			deleteDirectory(@P("d") Directory directory);
}
