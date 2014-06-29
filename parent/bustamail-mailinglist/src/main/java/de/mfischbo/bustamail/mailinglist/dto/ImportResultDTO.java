package de.mfischbo.bustamail.mailinglist.dto;

public class ImportResultDTO {

	private int 		linesImported;
	private int			contactsUpdated;
	private int			contactsCreated;
	
	
	public int getLinesImported() {
		return linesImported;
	}
	public void setLinesImported(int linesImported) {
		this.linesImported = linesImported;
	}
	public int getContactsUpdated() {
		return contactsUpdated;
	}
	public void setContactsUpdated(int contactsUpdated) {
		this.contactsUpdated = contactsUpdated;
	}
	public int getContactsCreated() {
		return contactsCreated;
	}
	public void setContactsCreated(int contactsCreated) {
		this.contactsCreated = contactsCreated;
	}
}
