package de.mfischbo.bustamail.publicapi.web;

import java.net.URLDecoder;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.mfischbo.bustamail.publicapi.service.PublicAPIService;

@Controller
@RequestMapping("/api/public/subscriber")
public class PublicRestSubscriberActivationController {

	@Inject PublicAPIService		service;
	
	@RequestMapping(value = "/{id}/activate", method = RequestMethod.GET)
	public void activateSubscriber(@PathVariable("id") ObjectId id,
			@RequestParam(value = "tx", required = true) UUID transactionId,
			@RequestParam(value = "t", required  = false) String forwardTarget,
			HttpServletResponse response) {
		service.activateSubscriptions(transactionId);
		if (forwardTarget != null && !forwardTarget.isEmpty()) {
			try {
				String url = URLDecoder.decode(forwardTarget, "UTF-8");
				if (url.indexOf("?") > 0) {
					url += "&s=" + id;
				} else {
					url += "?s=" + id;
				}
				response.setHeader("Location", url);
			} catch (Exception ex) {
				if (forwardTarget.indexOf("?") > 0) {
					forwardTarget += "&s=" + id;
				} else {
					forwardTarget += "?s=" + id;
				}
				response.setHeader("Location", forwardTarget);
			}
			response.setStatus(HttpStatus.MOVED_PERMANENTLY.value());
		}
	}
}
