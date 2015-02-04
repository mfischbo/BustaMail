package de.mfischbo.bustamail.stats.dto;

import java.util.List;

import org.joda.time.DateTime;

public class MailingStats {

	public class ClickEntry {
		String targetUrl = "";
		long   clickAmount = 0;
		long   uniqueClickAmount = 0;
		
		public ClickEntry(String url, long clickAmnt, long uClickAmnt) {
			this.targetUrl = url;
			this.clickAmount = clickAmnt;
			this.uniqueClickAmount = uClickAmnt;
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
	
	private DateTime	startedAt;
	private DateTime	finishedAt;
	
	private long		mailsSentSuccess;
	private long		mailsSentRetry;
	private long		mailsSentFailure;
	
	private long		recipientAmount;
	
	private long		openingAmount;
	private long		openingRate;
	private long		uniqueOpeningAmount;
	private long		uniqueOpeningRate;
	
	private long		clickAmount;
	private long		clickRate;
	private long		uniqueClickAmount;
	private long		uniqueClickRate;
	
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

	public long getMailsSentRetry() {
		return mailsSentRetry;
	}

	public void setMailsSentRetry(long mailsSentRetry) {
		this.mailsSentRetry = mailsSentRetry;
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

	public long getOpeningRate() {
		return openingRate;
	}

	public void setOpeningRate(long openingRate) {
		this.openingRate = openingRate;
	}

	public long getUniqueOpeningAmount() {
		return uniqueOpeningAmount;
	}

	public void setUniqueOpeningAmount(long uniqueOpeningAmount) {
		this.uniqueOpeningAmount = uniqueOpeningAmount;
	}

	public long getUniqueOpeningRate() {
		return uniqueOpeningRate;
	}

	public void setUniqueOpeningRate(long uniqueOpeningRate) {
		this.uniqueOpeningRate = uniqueOpeningRate;
	}

	public long getClickAmount() {
		return clickAmount;
	}

	public void setClickAmount(long clickAmount) {
		this.clickAmount = clickAmount;
	}

	public long getClickRate() {
		return clickRate;
	}

	public void setClickRate(long clickRate) {
		this.clickRate = clickRate;
	}

	public long getUniqueClickAmount() {
		return uniqueClickAmount;
	}

	public void setUniqueClickAmount(long uniqueClickAmount) {
		this.uniqueClickAmount = uniqueClickAmount;
	}

	public long getUniqueClickRate() {
		return uniqueClickRate;
	}

	public void setUniqueClickRate(long uniqueClickRate) {
		this.uniqueClickRate = uniqueClickRate;
	}

	public List<ClickEntry> getClickDetails() {
		return clickDetails;
	}

	public void setClickDetails(List<ClickEntry> clickDetails) {
		this.clickDetails = clickDetails;
	}
}
