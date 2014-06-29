package de.mfischbo.bustamail.mailinglist.service;

import de.mfischbo.bustamail.common.domain.Gender;

public class ImportUtility {

	public static Gender bestMatchingResult(Gender g1, Gender g2) {
		if (g1 == null) return g2;
		if (g1 == Gender.N && g2 != Gender.N) return g2;
		return g1;
	}
	
	public static String bestMatchingResult(String s1, String s2) {
		if (s1 == null) return s2;
		if (s1.trim().isEmpty()) return s2;
		return s1;
	}
}
