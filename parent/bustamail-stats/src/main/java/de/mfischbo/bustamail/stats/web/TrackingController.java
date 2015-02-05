package de.mfischbo.bustamail.stats.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.mfischbo.bustamail.stats.domain.StatsEntry;
import de.mfischbo.bustamail.stats.domain.StatsEntry.RecordType;
import de.mfischbo.bustamail.stats.service.StatsService;

@Controller
@RequestMapping(value = "/api/public/t")
public class TrackingController {

	@Inject
	private StatsService		service;
	
	@RequestMapping("o.png")
	public void createOpeningTrack(
			@RequestParam(value = "m", required = false, defaultValue = "") String mailingId,
			@RequestParam(value = "s", required = false, defaultValue = "") String subscriptionId,
			HttpServletRequest  request,
			HttpServletResponse response) {
	
		ObjectId mId = null;
		ObjectId sId = null;
		try {
			if (mailingId != null && mailingId.trim().length() > 0)
				mId = new ObjectId(mailingId);
			if (subscriptionId != null && subscriptionId.trim().length() > 0)
				sId = new ObjectId(subscriptionId);
			
			if (mId == null || sId == null)
				return;
			
			StatsEntry e = new StatsEntry();
			e.setMailingId(mId);
			e.setSubscriptionId(sId);
			e.setType(RecordType.OPEN);
			e.setSourceIP(request.getRemoteAddr());
			service.createStatsEntry(e);
		} catch (Exception ex) {
			
		}
	}
	
	@RequestMapping("t.png")
	public void createClickTrack(
			@RequestParam(value = "m", required = false, defaultValue = "") String mailingId,
			@RequestParam(value = "s", required = false, defaultValue = "") String subscriptionId,
			@RequestParam(value = "t", required = false, defaultValue = "") String targetUrl,
			HttpServletRequest  request,
			HttpServletResponse response) {

		try {
			ObjectId mId = null;
			ObjectId sId = null;
			if (mailingId != null && mailingId.trim().length() > 0)
				mId = new ObjectId(mailingId);
			if (subscriptionId != null && subscriptionId.trim().length() > 0)
				sId = new ObjectId(subscriptionId);
			
			StatsEntry e = new StatsEntry();
			e.setMailingId(mId);
			e.setSubscriptionId(sId);
			e.setTargetUrl(URLDecoder.decode(targetUrl, "UTF-8"));
			e.setType(RecordType.CLICK);
			e.setSourceIP(request.getRemoteAddr());
			service.createStatsEntry(e);
		} catch (Exception ex) {
			
		}
		
		try {
			String target = URLDecoder.decode(targetUrl, "UTF-8");
			response.setHeader("Location", target);
		} catch (UnsupportedEncodingException ex) {
			response.setHeader("Location", targetUrl);
		}
		response.setStatus(HttpStatus.MOVED_PERMANENTLY.value());
	}
}
