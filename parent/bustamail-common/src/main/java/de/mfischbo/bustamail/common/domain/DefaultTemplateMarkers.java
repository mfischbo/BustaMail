package de.mfischbo.bustamail.common.domain;

import java.util.Arrays;
import java.util.List;

public class DefaultTemplateMarkers {

	private static final String	  disableLinkTrackClass = "bm-disable-linktrack";
	
	private static final String[] templateClassMarkers = {
		"bm-fragment", 			// marks a template row
		"bm-element", 			// marks a template element
		"bm-on-replace",		// marks a node that replaces the content of an element
		"bm-changeable",		// marks an image that is changeable by the user
		"bm-fragment-hovered",	// marks a hovered fragment
		"bm-fragment-focused",	// marks a fragment that is selected
		"bm-element-hovered",	// marks an element that is hovered
		"bm-element-focused",	// marks an element that is focused
		"bm-hyperlink",			// marks a managed hyperlink A tag
		disableLinkTrackClass,	// marks an element with href attribute not to use link tracking
	}; 
	
	private static final String[] templateAttributeMarkers = {
		"data-widget-id",		// contains the id of the widget the HTML element represents
		"data-instance-id",		// the UUID of the instance the element represents
		"data-link-id",			// internal reference id for managed hyperlinks
		"contenteditable"		// The contenteditable attribute from the editor
	};
	
	
	public static List<String> getTemplateClassMarkers() {
		return Arrays.asList(DefaultTemplateMarkers.templateClassMarkers);
	}
	
	public static List<String> getTemplateAttributeMarkers() {
		return Arrays.asList(DefaultTemplateMarkers.templateAttributeMarkers);
	}
	
	public static String getDiableLinkTrackClass() {
		return DefaultTemplateMarkers.disableLinkTrackClass;
	}
}
