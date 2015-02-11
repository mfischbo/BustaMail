package de.mfischbo.bustamail.mailer.dto;

import javax.mail.internet.InternetAddress;

import org.bson.types.ObjectId;

public interface IMailableMail {

	public ObjectId getId();
	public String getSubject();
	public String getSenderName();
	public InternetAddress getSender();
	public InternetAddress getReplyTo();
}
