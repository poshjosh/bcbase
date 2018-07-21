package com.bc.html;

import java.io.Serializable;
import java.util.*;

public class HtmlGen implements Serializable {

	private boolean useNewLine;

	public HtmlGen() {
		useNewLine = true;
	}

	public static StringBuilder AHREF(CharSequence href, Object value, StringBuilder appendTo) {
		if (appendTo == null)
			appendTo = new StringBuilder();
		return appendTo.append("<a href=\"").append(href).append("\">").append(value).append("</a>");
	}

	public final String getAHREF(CharSequence href, Object value, Map linkAttributes) {
		if (linkAttributes == null) {
			return enclosingTag("a", "href", href, value, new StringBuilder()).toString();
		} else {
			linkAttributes.put("href", href);
			return enclosingTag("a", linkAttributes, value, new StringBuilder()).toString();
		}
	}

	public final String getMailto(CharSequence emailAddress, Object displayValue, Map params) {
		return getAHREF((new StringBuilder()).append("mailto:").append(emailAddress).toString(), displayValue, params);
	}

	public StringBuilder img(Map params, StringBuilder builder) {
		return straightTag("img", params, builder);
	}

	public StringBuilder input(Map params, StringBuilder builder) {
		return straightTag("input", params, builder);
	}
        
        public String formatNameToLabel(String name) {
// @todo implement this method            
            return name;
        }

	public StringBuilder selectOption(boolean useDummyStart, Object selectedKey, Map listParams, Map optionPairs, StringBuilder builder) {
		builder = tagStart("select", listParams, builder);
		if (useDummyStart) {
			String LABEL = formatNameToLabel((String)listParams.get("name"));
			String tagBody = (new StringBuilder()).append("Select a ").append(LABEL).toString();
			builder = enclosingTag("option", "value", "", tagBody, builder);
		}
		Map optionParams = new LinkedHashMap();
		for (Iterator iter = optionPairs.keySet().iterator(); iter.hasNext();) {
			String key = (String)iter.next();
			Object val = optionPairs.get(key);
			optionParams.clear();
			optionParams.put("value", key);
			if (selectedKey != null && key.equals(selectedKey))
				optionParams.put("selected", "true");
			builder = enclosingTag("option", optionParams, val.toString(), builder);
		}

		builder.append("</select>");
		if (useNewLine)
			builder.append('\n');
		return builder;
	}

	public StringBuilder straightTag(String tag, Map params, StringBuilder builder) {
		builder.append('<');
		builder.append(tag);
		builder.append(' ');
		builder = pairs(params, builder);
		builder.append('/').append('>');
		if (useNewLine)
			builder.append('\n');
		return builder;
	}

	public StringBuilder straightTag(String tag, String key, Object val, StringBuilder builder) {
		builder.append('<');
		builder.append(tag);
		builder.append(' ');
		builder = pair(key, val, builder);
		builder.append('/').append('>');
		if (useNewLine)
			builder.append('\n');
		return builder;
	}

	public StringBuilder enclosingTag(String tag, Map params, Object tagBody, StringBuilder appendTo) {
		appendTo = tagStart(tag, params, appendTo);
		if (tagBody != null)
			appendTo.append(tagBody);
		if (useNewLine)
			appendTo.append('\n');
                tagEnd(tag, appendTo);
		return appendTo;
	}
        
	public StringBuilder enclosingTag(String tag, String key, Object val, Object tagBody, StringBuilder appendTo) {
		appendTo = tagStart(tag, key, val, appendTo);
		if (tagBody != null)
			appendTo.append(tagBody);
		if (useNewLine)
			appendTo.append('\n');
                tagEnd(tag, appendTo);
		return appendTo;
	}

	public StringBuilder enclosingTag(String tag, Object tagBody, StringBuilder appendTo) {
		appendTo.append('<');
		appendTo.append(tag);
		appendTo.append('>');
		if (tagBody != null)
			appendTo.append(tagBody);
		if (useNewLine)
			appendTo.append('\n');
                tagEnd(tag, appendTo);
		return appendTo;
	}

        public StringBuilder tagEnd(String tag, StringBuilder appendTo) {
		appendTo.append('<').append('/');
		appendTo.append(tag);
		appendTo.append('>');
		if (useNewLine)
			appendTo.append('\n');
                return appendTo;
        }

	public StringBuilder tagStart(String tag, Map map, StringBuilder builder) {
		builder.append('<');
		builder.append(tag);
		builder.append(' ');
		builder = pairs(map, builder);
		builder.append('>');
		if (useNewLine)
			builder.append('\n');
		return builder;
	}

	public StringBuilder tagStart(String tag, String key, Object val, StringBuilder builder) {
		builder.append('<');
		builder.append(tag).append(' ');
		builder = pair(key, val, builder);
		builder.append('>');
		if (useNewLine)
			builder.append('\n');
		return builder;
	}

	public StringBuilder pairs(Map params, StringBuilder builder) {
		if (params == null)
			return builder;
		for (Object key:params.keySet()) {
                        this.pair(key, params.get(key), builder);
		}

		return builder;
	}

	public StringBuilder pair(Object key, Object val, StringBuilder builder) {
		builder.append(key);
		builder.append('=').append('"');
		builder.append(val);
		return builder.append('"').append(' ');
	}

	public boolean isUseNewLine() {
		return useNewLine;
	}

	public void setUseNewLine(boolean useNewLine) {
		this.useNewLine = useNewLine;
	}
}
