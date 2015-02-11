package de.mfischbo.bustamail.mailer.util;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.types.ObjectId;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.slf4j.LoggerFactory;

/**
 * Class that holds methods to modify the HTML source of a document
 * @author M. Fischboeck
 *
 */
public class HTMLSourceProcessor {

	public static Document replaceSpanCells(Document doc, URL baseUrl, String blankPixelUrl) {

		final String url = baseUrl.toString() + blankPixelUrl;
		
		doc.select("td").forEach(new Consumer<Element>() {
			@Override
			public void accept(Element t) {
				
				// check if the td element is empty
				if (HTMLSourceProcessor.isSpanCell(t)) {
					String width = t.attr("width");
					String height = t.attr("height");
					
					Element n = new Element(Tag.valueOf("img"), t.baseUri());
					if (width.trim().length() > 0)
						n.attr("width", width);
					if (height.trim().length() > 0)
						n.attr("height", height);
					
					n.attr("src", url);
					n.attr("border", "0");
					t.empty();
					t.appendChild(n);
				}
			}
		});
		return doc;
	}
	
	public static Document attachOpeningPixel(Document doc, URL baseUrl) {
		StringBuilder b = new StringBuilder("<img src=\"");
		b.append(baseUrl.toString())
		.append("/public/t/o.png?m=${")
		.append(MailingSerializer.KEY_MAILING_ID)
		.append("}&s=${")
		.append(MailingSerializer.KEY_SUBSCRIBER_ID)
		.append("}\" width=\"0\" height=\"0\">");
		doc.select("body").append(b.toString());
		return doc;
	}

	public static Map<ObjectId, List<Integer>> getStaticResourceIds(Document doc) {
		Map<ObjectId, List<Integer>> retval = new HashMap<ObjectId, List<Integer>>();
		
		doc.select("*[src]").forEach(e -> {
			String[] val = e.attr("src").split("/");
			ObjectId key = new ObjectId(val[val.length -1]);
			
			// check if a width is set
			Integer w = -1;
			if (e.attr("width") != null) {
				String ws = e.attr("width");
				ws = ws.replaceAll("\\D", "");
				w = Integer.parseInt(ws);
			}
			
			if (retval.containsKey(key)) {
				List<Integer> sizes = retval.get(key);
				if (!sizes.contains(w))
					sizes.add(w);
			} else {
				List<Integer> vals = new LinkedList<Integer>();
				vals.add(w);
				retval.put(key, vals);
			}
		});
		return retval;
	}
	
	
	public static Document replaceSourceURLs(Document doc, URL baseUrl, final Map<ObjectId, ObjectId> resourceMap, String disableTrackingClass) {
		
		doc.select("*[src]").forEach(e -> {
			String[] target = e.attr("src").split("/");
			try {
				// skip the blank.gif
				if (target[target.length-1].equals("blank.gif"))
					return;
				
				ObjectId key = new ObjectId(target[target.length-1]);
				e.attr("src", baseUrl + "/" + resourceMap.get(key).toString());
			} catch (Exception ex) {
				LoggerFactory.getLogger(HTMLSourceProcessor.class)
					.error("Unable to set image for source {}. Cause: {}", target[target.length-1], ex.getMessage());
			}
		});
		return doc;
	}
	
	public static Document createTrackingUrls(Document doc, URL baseUrl, String disableTrackingClass) {
		doc.select("*[href]").forEach(new Consumer<Element>() {
			@Override
			public void accept(Element t) {
				if (!t.hasClass(disableTrackingClass)) {
					String target = t.attr("href");
					String finalUrl = HTMLSourceProcessor.createTrackingUrl(baseUrl, target);
					t.attr("href", finalUrl);
				}
			}
		});
		return doc;
	}
	
	public static Document replaceStaticLink(Document doc, URL baseUrl, String disableTrackingClass) {
		doc.select("a[rel=static-link]").forEach( e -> {
			String target = baseUrl.toString() + "/index.html";
			e.attr("href", target);
			e.addClass(disableTrackingClass);
		});
		return doc;
	}
	
	
	/**
	 * Replaces all occurences of a[rel=optin-link] tags with the configured optin target urls.
	 * @param doc The document to be processed
	 * @param mode
	 * @param target
	 * @param baseUrl
	 * @param disableLinkTrackClass
	 * @return
	 */
	public static Document replaceOptinLink(Document doc, String mode, String target, URL baseUrl, String disableLinkTrackClass) {
		doc.select("a[rel=optin-link]").forEach( e-> {
			
			String dstURL = "";
			try {
				if (mode.equals("INTERNAL")) {
					String fwdURL = URLEncoder.encode(target, "UTF-8");
					dstURL = baseUrl.toString() + "/public/subscriptions/${" + MailingSerializer.KEY_SUBSCRIBER_ID + "}"
							+ "/activate?tx=${mailingData.transactionId}&t=" + fwdURL;
				} else if (mode.equals("EXTERNAL")) {
					if (target.indexOf("?") > 0) {
						dstURL = target + "&tx=${mailingData.transactionId}&s=${mailingData.subscriberId}";
					} else {
						dstURL = target + "?tx=${mailingData.transactionId}&s=${mailingData.subscriberId}";
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			e.attr("href", dstURL);
			e.addClass(disableLinkTrackClass);
		});
		return doc;
	}
	

	private static String createTrackingUrl(URL base, String target) {
		String retval = base.toString() + "/public/t/c.png?m=${"+ MailingSerializer.KEY_MAILING_ID +"}&s=${"+ MailingSerializer.KEY_SUBSCRIBER_ID+"}&t=";
		try {
			return retval + URLEncoder.encode(target, "UTF8"); 
		} catch (UnsupportedEncodingException ex) {
			return retval + target;
		}
	}
	
	public static Document removeAttributes(Document doc, List<String> attributes) {
		for (String a : attributes) {
			doc.select("*["+a+"]").forEach(new Consumer<Element>() {
				@Override
				public void accept(Element t) {
					t.removeAttr(a);
				}
			});
		}
		return doc;
	}

	public static Document removeClasses(Document doc, List<String> classes) {
		for (String c: classes) {
			doc.select("." + c).stream().forEach(new Consumer<Element>() {
				@Override
				public void accept(Element t) {
					t.removeClass(c);
				}
			});
		}
		return doc;
	}
	
	public static Document cleanUp(Document doc) {
	
		// remove all empty class attributes
		doc.select("*[class]").forEach(new Consumer<Element>() {
			@Override
			public void accept(Element t) {
				if (t.attr("class").trim().isEmpty())
					t.removeAttr("class");
			}
		});
		
		// replace rgb() styles with hex color strings
		Pattern p = Pattern.compile("rgb\\s?\\([0-9,\\s]*\\)");
		doc.select("*[style]").forEach(new Consumer<Element>() {
			@Override
			public void accept(Element t) {
				String style = t.attr("style");
				Matcher m = p.matcher(style);
				while (m.find()) {
					String match = style.substring(m.start(), m.end());
					style = m.replaceFirst(rgb2Hex(match));
				}
				t.attr("style", style);
			}
		});
		
		return doc;
	}
	
	private static String rgb2Hex(String rgb) {
		
		rgb = rgb.substring(rgb.indexOf("(") + 1);
		rgb = rgb.substring(0, rgb.indexOf(")"));
		String[] n = rgb.split(",");
		String retval = "#";
		for (String d : n) {
			Integer dec = Integer.parseInt(d.trim());
			String s = Integer.toHexString(dec);
			if (s.length() == 1)
				retval += "0" + s;
			else
				retval += s;
		}
		return retval;
	}
	
	private static boolean isSpanCell(Element e ) {
	
		// check that the cell contains no other child elements
		if (e.getAllElements().size() > 1)
			return false;
		
		if (!(e.hasAttr("width") || e.hasAttr("height")))
			return false;
		
		String innerHTML = e.html();
		innerHTML = innerHTML.replaceAll("&nbsp;", "");
		
		if (innerHTML.trim().length() > 0)
			return false;
		return true;
	}
	
	

}
