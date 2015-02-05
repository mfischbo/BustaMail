package de.mfischbo.bustamail.stats.dto;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.DateTime;

public class MailingStats {

	public class ClickEntry {
		String targetUrl = "";
		long   clickAmount = 0;
		long   uniqueClickAmount = 0;
		
		public ClickEntry(String targetUrl, long clickAmount, long uniqueClickAmount) {
			this.targetUrl = targetUrl;
			this.clickAmount = clickAmount;
			this.uniqueClickAmount = uniqueClickAmount;
		}

		public String getTargetUrl() {
			return targetUrl;
		}

		public long getClickAmount() {
			return clickAmount;
		}

		public long getUniqueClickAmount() {
			return uniqueClickAmount;
		}
	}
	
	/*
	 * Start and end time of the mailing. 
	 * Values are calculated from the actual time the mailer sent the first and the last mail
	 */
	private DateTime	startedAt;
	private DateTime	finishedAt;

	/*
	 * Amount of mails that have been sent successfully or failed. 
	 * The value is based on how the mailer decides upon the success of a sent mailing.
	 * The values represent the amount of serialized mailings in the .success and .failed directories
	 */
	private long		mailsSentSuccess;
	private long		mailsSentFailure;

	/*
	 * recipientAmount:	The number of subscribers when the mailing has been published
	 * actualRecipientAmount: The number of mailings the mailer tried to send
	 * If both values differ the mailer has had an unexpected shutdown or some other issue.
	 */
	private long		recipientAmount;
	private long		actualRecipientAmount;
	
	
	/*
	 * The rate of successfully sent mails based on the recipientAmount and the amount
	 * of successfully sent mails
	 */
	private BigDecimal	sendingSuccessRate;
	

	/*
	 * The amount of times the mailing has been opened by a client.
	 * openingAmount : Openings over all (each opening counts, even from the same recipient)
	 * uniqueOpeningAmount : Openings counted once for each subscriber
	 * openingRate   : The rate of openings based on the uniqueOpenings and the recipientAmount
	 */
	private long		openingAmount;
	private BigDecimal	openingRate;
	private long		uniqueOpeningAmount;

	/*
	 * The amount of clicks in the mailing
	 * clickAmount : Each click counted
	 * uniqueClickAmount : Clicks are grouped by recipient and targetUrl
	 */
	private long		clickAmount;
	private long		uniqueClickAmount;

	/*
	 * The amount of mails the mailer sent per minute. Based on the start/finish time and the amount of actual recipients 
	 */
	private double 		mailsPerMinute;

	/*
	 * Detailed list of clicks
	 */
	private List<ClickEntry>	clickDetails;

	public DateTime getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(DateTime startedAt) {
		this.startedAt = startedAt;
	}

	public DateTime getFinishedAt() {
		return finishedAt;
	}

	public void setFinishedAt(DateTime finishedAt) {
		this.finishedAt = finishedAt;
	}

	public long getMailsSentSuccess() {
		return mailsSentSuccess;
	}

	public void setMailsSentSuccess(long mailsSentSuccess) {
		this.mailsSentSuccess = mailsSentSuccess;
	}

	public long getMailsSentFailure() {
		return mailsSentFailure;
	}

	public void setMailsSentFailure(long mailsSentFailure) {
		this.mailsSentFailure = mailsSentFailure;
	}

	public long getRecipientAmount() {
		return recipientAmount;
	}

	public void setRecipientAmount(long recipientAmount) {
		this.recipientAmount = recipientAmount;
	}

	public long getOpeningAmount() {
		return openingAmount;
	}

	public void setOpeningAmount(long openingAmount) {
		this.openingAmount = openingAmount;
	}

	public long getUniqueOpeningAmount() {
		return uniqueOpeningAmount;
	}

	public void setUniqueOpeningAmount(long uniqueOpeningAmount) {
		this.uniqueOpeningAmount = uniqueOpeningAmount;
	}

	public BigDecimal getOpeningRate() {
		return openingRate;
	}

	public void setOpeningRate(BigDecimal openingRate) {
		this.openingRate = openingRate;
	}

	public long getClickAmount() {
		return clickAmount;
	}

	public void setClickAmount(long clickAmount) {
		this.clickAmount = clickAmount;
	}

	public long getUniqueClickAmount() {
		return uniqueClickAmount;
	}

	public void setUniqueClickAmount(long uniqueClickAmount) {
		this.uniqueClickAmount = uniqueClickAmount;
	}

	public List<ClickEntry> getClickDetails() {
		return clickDetails;
	}

	public void setClickDetails(List<ClickEntry> clickDetails) {
		this.clickDetails = clickDetails;
	}

	public double getMailsPerMinute() {
		return mailsPerMinute;
	}

	public void setMailsPerMinute(double mailsPerMinute) {
		this.mailsPerMinute = mailsPerMinute;
	}

	public BigDecimal getSendingSuccessRate() {
		return sendingSuccessRate;
	}

	public void setSendingSuccessRate(BigDecimal sendingSuccessRate) {
		this.sendingSuccessRate = sendingSuccessRate;
	}

	public long getActualRecipientAmount() {
		return actualRecipientAmount;
	}

	public void setActualRecipientAmount(long actualRecipientAmount) {
		this.actualRecipientAmount = actualRecipientAmount;
	}
}
