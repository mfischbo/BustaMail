package de.mfischbo.bustamail.mailinglist.service;

import java.util.List;

import de.mfischbo.bustamail.mailinglist.dto.SubscriptionImportDTO;

public class ParsedImport {

	long					dateCreated		= System.currentTimeMillis();
	SubscriptionImportDTO	settings;
	List<List<String>>		data;
}
