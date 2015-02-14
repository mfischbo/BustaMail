package de.mfischbo.bustamail.media.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.media.domain.Directory;
import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.security.domain.OrgUnit;

public interface MediaService {

	@PostAuthorize("hasPermission(returnObject.owner, 'Media.USE_MEDIA')")
	Media			getMediaById(ObjectId id) throws EntityNotFoundException;

	@PostAuthorize("hasPermission(returnObject.owner, 'Media.USE_MEDIA')")
	Media			getMediaById(ObjectId id, int preferedSize) throws EntityNotFoundException;
	
	@PreAuthorize("hasPermission(#m.owner, 'Media.USE_MEDIA')")
	void			getContent(Media m, OutputStream stream) throws Exception;
	
	@PreAuthorize("hasPermission(#m.owner, 'Media.USE_MEDIA')")
	void 			getContent(Media m, int preferedSize, OutputStream stream) throws Exception;
	
	@PreAuthorize("hasPermission(#m.owner, 'Media.USE_MEDIA')")
	InputStream		getContent(Media m);
	
	@PreAuthorize("hasPermission(#d.owner, 'Media.USE_MEDIA')")
	List<Media> 	getFilesByDirectory(@P("d") Directory d);

	@PreAuthorize("hasPermission(#m.owner, 'Media.MANAGE_MEDIA')")
	Media			createMedia(@P("m") Media media) throws Exception;
	
	@PreAuthorize("hasPermission(#m.owner, 'Media.MANAGE_MEDIA')")
	Media			createMedia(@P("m") Media media, Directory directory) throws Exception;
	
	@PreAuthorize("hasPermission(#m.owner, 'Media.MANAGE_MEDIA')")
	Media			createCopy(@P("m") Media media, String filename) throws Exception;
	
	@PreAuthorize("hasPermission(#m.owner, 'Media.MANAGE_MEDIA')")
	Media			updateMedia(@P("m") Media media) throws EntityNotFoundException;
	
	@PreAuthorize("hasPermission(#m.owner, 'Media.MANAGE_MEDIA')")
	void			deleteMedia(@P("m") Media media) throws EntityNotFoundException;
	
	
	
	
	
	// ------------------------	//
	// 	Directories			   //
	// -----------------------//
	
	List<Directory> getDirectoryRoots();

	@PostAuthorize("hasPermission(returnObject.owner, 'Media.USE_MEDIA')")
	Directory		getRootDirectoryByOrgUnit(OrgUnit unit);
	
	@PostAuthorize("hasPermission(returnObject.owner, 'Media.USE_MEDIA')")
	Directory		getDirectoryById(ObjectId directory) throws EntityNotFoundException;
	
	@PreAuthorize("hasPermission(#d.owner, 'Media.MANAGE_MEDIA')")
	List<Directory> getChildDirectories(@P("d") Directory parent);
	
	@PreAuthorize("hasPermission(#owner, 'Media.MANAGE_MEDIA')")
	Directory		createDirectory(ObjectId owner, Directory parent, Directory directory);
	
	@PreAuthorize("hasPermission(#d.owner, 'Media.MANAGE_MEDIA')")
	Directory		updateDirectory(Directory directory);
	
	@PreAuthorize("hasPermission(#d.owner, 'Media.MANAGE_MEDIA')")
	void			deleteDirectory(Directory directory);
}
