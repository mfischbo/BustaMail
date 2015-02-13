package de.mfischbo.bustamail.media.util;

import org.bson.types.ObjectId;

import com.mongodb.gridfs.GridFSDBFile;

import de.mfischbo.bustamail.media.domain.Media;

public class MediaUtils {

	public static Media convertFile(GridFSDBFile file) {
		Media m = new Media((ObjectId) file.getId(), file.getFilename(), file);
		return m;
	}

	public static int getBestMatchingSize(int preferedSize) {
		// find the upper and lower boundary sizes to be returned
		int s = 1024;
		if (preferedSize < 1024 && preferedSize > 512) s = 1024;
		if (preferedSize <= 512 && preferedSize > 128) s = 512;
		if (preferedSize <= 128 && preferedSize > 64 ) s = 128;
		if (preferedSize <= 64  && preferedSize > 0  ) s = 64;
		return s;
	}
	
}
