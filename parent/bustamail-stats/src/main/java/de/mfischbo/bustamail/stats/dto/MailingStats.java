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
	
	private DateTime	startedAt;
	private DateTime	finishedAt;
	
	private long		mailsSentSuccess;
	private long		mailsSentFailure;
	
	private long		recipientAmount;
	
	private long		openingAmount;
	private BigDecimal	openingRate;
	private long		uniqueOpeningAmount;
	
	private long		clickAmount;
	private long		uniqueClickAmount;
	
	private BigDecimal	mailsPerMinute;
	
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

	public BigDecimal getMailsPerMinute() {
		return mailsPerMinute;
	}

	public void setMailsPerMinute(BigDecimal mailsPerMinute) {
		this.mailsPerMinute = mailsPerMinute;
	}
}
