package de.mfischbo.bustamail.common.dto;

public class UIConfigurationDTO {

	private String uiURL;
	private int    searchDelay;
	private String previewURL;
	private String liveURL;

	public String getUiURL() {
		return uiURL;
	}

	public void setUiURL(String uiURL) {
		this.uiURL = uiURL;
	}

	public int getSearchDelay() {
		return searchDelay;
	}

	public void setSearchDelay(int searchDelay) {
		this.searchDelay = searchDelay;
	}

	public String getPreviewURL() {
		return previewURL;
	}

	public void setPreviewURL(String previewURL) {
		this.previewURL = previewURL;
	}

	public String getLiveURL() {
		return liveURL;
	}

	public void setLiveURL(String liveURL) {
		this.liveURL = liveURL;
	}
}
