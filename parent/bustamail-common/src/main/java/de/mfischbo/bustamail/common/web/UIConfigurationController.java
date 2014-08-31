package de.mfischbo.bustamail.common.web;

import javax.inject.Inject;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.mfischbo.bustamail.common.dto.UIConfigurationDTO;

@RestController
@RequestMapping("/api/configuration")
public class UIConfigurationController extends BaseApiController {

	@Inject
	Environment	env;
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public UIConfigurationDTO getConfiguration() {
		UIConfigurationDTO conf = new UIConfigurationDTO();
	
		conf.setUiURL(env.getProperty("de.mfischbo.bustamail.ui.url"));
		conf.setSearchDelay(Integer.parseInt(env.getProperty("de.mfischbo.bustamail.ui.searchDelay")));
		conf.setPreviewURL(env.getProperty("de.mfischbo.bustamail.env.previewUrl"));
		conf.setLiveURL(env.getProperty("de.mfischbo.bustamail.env.liveUrl"));
		return conf;
	}
}
