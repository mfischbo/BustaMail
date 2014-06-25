package de.mfischbo.bustamail.mailing.service;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import de.mfischbo.bustamail.mailing.dto.HyperlinkDTO;

public class HyperlinkStatusHandler implements ResponseErrorHandler {

	private HyperlinkDTO	t;
	
	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		HttpStatus s = response.getStatusCode();
		if (s.is1xxInformational() || s.is2xxSuccessful() || s.is3xxRedirection())
			t.setSuccess(true);
		else
			t.setSuccess(false);
		return t.isSuccess();
	}

	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		t.setHttpStatus(response.getStatusCode().ordinal());
	}
	
	public void setHyperlinkDTO(HyperlinkDTO t) {
		this.t = t;
	}
}
