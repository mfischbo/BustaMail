package de.mfischbo.bustamail.mailer.util;

import java.net.URL;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

public class HTMLSourceProcessor {

	public static Document replaceSpanCells(Document doc, URL baseUrl, String blankPixelUrl) {

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
					
					n.attr("src", blankPixelUrl);
					n.attr("border", "0");
					t.empty();
					t.appendChild(n);
				}
			}
		});
		return doc;
	}

	
	public static Document replaceSourceURLs(Document doc, URL baseUrl, String disableTrackingClass) {
		doc.select("*[src]").forEach(new Consumer<Element>() {
			@Override
			public void accept(Element t) {
				String target = t.attr("src");
				String finalUrl = HTMLSourceProcessor.createAbsoluteUrl(baseUrl, target);
				t.attr("src", finalUrl);
			}
		});
		return doc;
	}
	
	public static Document createTrackingUrls(Document doc, URL baseUrl, String disableTrackingClass) {
		doc.select("*[href]").forEach(new Consumer<Element>() {
			@Override
			public void accept(Element t) {
				if (!t.hasClass(disableTrackingClass)) {
					String target = t.attr("src");
					String finalUrl = HTMLSourceProcessor.createTrackingUrl(baseUrl, target);
					t.attr("href", finalUrl);
				}
			}
		});
		return doc;
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
	
	private static String createAbsoluteUrl(URL base, String target) {
		String retval = base.toExternalForm() + "/" + target;
		String schema = retval.substring(0, retval.indexOf("://") + 3);
		String url    = retval.substring(schema.length());
		
		url = url.replaceAll("//", "/");
		url = url.replaceAll("/\\./", "/");
		return schema + url;
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
	
	
	private static String createTrackingUrl(URL base, String target) {
		// TODO: To be implemented
		return target;
	}
}
