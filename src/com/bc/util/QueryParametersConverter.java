package com.bc.util;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * @(#)QueryParametersConverter.java   25-Dec-2013 02:35:40
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */
/**
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public class QueryParametersConverter implements Serializable {
    
    private final boolean emptyStringsAllowed;
    
    private final boolean nullsAllowed;
    
    /**
     * When there are multiple separators, the separator at this index
     * will be used to divide a pair.<br/>
     * Given the input: <tt>abc=1=d</tt><br/>
     * Considering the first '=' as separator we have. <tt>abc : 1=d</tt><br/>
     * Considering the second '=' as separator we have. <tt>abc=1 : d</tt><br/>
     * If this value is less than 0, then first separator will be used. If it is 
     * greater then the index of the last separator then the last separator will 
     * be used.
     */
    private final int separatorIndex;
    
    private final String separator;
    
    public QueryParametersConverter() { 
        this("&");
    }
    
    public QueryParametersConverter(String separator) { 
        this(false, separator);
    }
    
    public QueryParametersConverter(boolean emptyStringsAllowed, String separator) { 
        this(emptyStringsAllowed, false, 0, separator);
    }

    public QueryParametersConverter(
            boolean emptyStringsAllowed, boolean nullsAllowed, String separator) { 
        
        this(emptyStringsAllowed, nullsAllowed, 0, separator);
    }
    
    public QueryParametersConverter(
            boolean emptyStringsAllowed, boolean nullsAllowed, 
            int separatorIndex, String separator) { 
        this.emptyStringsAllowed = emptyStringsAllowed;
        this.nullsAllowed = nullsAllowed;
        this.separatorIndex = separatorIndex;
        this.separator = separator;
    }

    /**
     * @param queryString String of format 
     * <tt>key_0=val_0[SEPARATOR]key_1=val_1[SEPARATOR]... key_n=val_n</tt>
     * to convert into a map with corresponding key-value pairs. The default
     * value of <tt>[SEPARATOR]</tt> is '&'
     * @return The Map representation of the key-value pairs in the input query string.
     * @see #toMap(java.lang.String) 
     */
    public Map<String, String> reverse(String queryString) {
        return this.toMap(queryString);
    }
    
    /**
     * 
     * @param queryString String of format 
     * <tt>key_0=val_0[SEPARATOR]key_1=val_1[SEPARATOR]... key_n=val_n</tt>
     * to convert into a map with corresponding key-value pairs. The default
     * value of <tt>[SEPARATOR]</tt> is '&'
     * @return The Map representation of the key-value pairs in the input query string.
     */
    public Map<String, String> toMap(String queryString) {
        return this.toMap(queryString, separator);
    }
    
    /**
     * @param queryString String of format 
     * <tt>key_0=val_0[SEPARATOR]key_1=val_1[SEPARATOR]... key_n=val_n</tt>
     * to convert into a map with corresponding key-value pairs.
     * @param separator The separator between query pairs. The default is <tt>'&'</tt>
     * @return The Map representation of the key-value pairs in the input query string.
     */
    public Map<String, String> toMap(String queryString, String separator) {
        
        if(queryString == null) {
            throw new NullPointerException();
        }
        
        if(queryString.startsWith("?")) {
            queryString = queryString.substring(1);
        }
        
XLogger.getInstance().log(Level.FINER, "Separator {0}. nulls allowed: {1}, empty strings allowed: {2}, query: {3}", 
        this.getClass(), separator, nullsAllowed, emptyStringsAllowed, queryString); 

        LinkedHashMap<String, String> result = new LinkedHashMap<>();

        String [] queryPairs = queryString.split(separator);

        for(int i=0; i<queryPairs.length; i++) {
            
XLogger.getInstance().log(Level.FINEST, "Pair[{0}]: {1}", this.getClass(), i, queryPairs[i]);

            String [] parts = queryPairs[i].split("=");
            
//XLogger.getInstance().log(Level.FINEST, "Pair[{0}]: {1}", this.getClass(), i, parts==null?null:Arrays.toString(parts));

            String key;
            String val;
            
            if(parts == null || parts.length == 0) {
                continue;
            }else if (parts.length == 1) {
                if(this.emptyStringsAllowed) {
                    key = parts[0];
                    val = ""; //We prefer an empty String to null -> Query standards
                }else{
                    continue;
                }
            }else if(parts.length == 2) {
                key = parts[0];
                val = parts[1];
            }else{
                
                final int index = separatorIndex < 0 ? 0 : separatorIndex >= parts.length-1 ? parts.length - 2 : separatorIndex;

                StringBuilder builder = new StringBuilder();
                for(int partIndex=0; partIndex<index+1; partIndex++) {
                    builder.append(parts[partIndex]);
                    if(partIndex < index) {
                        builder.append('=');
                    }
                }
                key = builder.toString();
                
                builder.setLength(0);
                for(int partIndex=index+1; partIndex<parts.length; partIndex++) {
                    builder.append(parts[partIndex]);
                    if(partIndex < parts.length-1) {
                        builder.append('=');
                    }
                }
                val = builder.toString();
            }
            
            result.put(
                    this.reverseKey(key.trim()), 
                    this.reverseValue(val==null?null:val.trim()));
        }
        
XLogger.getInstance().log(Level.FINER, "{0}. Output: {1}", this.getClass(), result);        

        return result;
    }
    
    public String reverseKey(String key) {
        return key;
    }
    
    public String reverseValue(String val) {
        return val;
    }
    
    public String convert(Map params) {
        return this.toQueryString(params);
    }
    
    /**
     * Converts the key/value pairs in the input map to a query string
     * for the format <tt>key_0=val_0&key_1=val_1...</tt>
     * @param params The Map whose key/value pairs will be converted to a
     * query String.
     * @return The query string representation of the input Map. 
     */
    public String toQueryString(Map params) {
        
        StringBuilder builder = new StringBuilder();
        
        this.appendQueryString(params, builder);
        
        return builder.toString();
    }

    public void appendQueryString(Map params, StringBuilder appendTo) {

        Iterator iter = params.entrySet().iterator();
        
        int pos = 0;
        
        while(iter.hasNext()) {
            
            java.util.Map.Entry entry = (java.util.Map.Entry)iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            
            if(!this.emptyStringsAllowed && val instanceof String && ((String)val).isEmpty()) {
                continue;
            }
            
            if(!this.nullsAllowed && val == null) {
                continue;
            }
            
            if(pos > 0) {
                appendTo.append(separator);
            }
            
            appendTo.append(this.convertKey(key));
            appendTo.append('=');
            appendTo.append(this.convertValue(val));
            
            ++pos;
        }
    }
    
    public Object convertKey(Object key) {
        return key;
    }
    
    public Object convertValue(Object val) {
        
        return val;
    }

    /**
     * @param val The value to URL encode
     * @param charset The name of the charset to use in URL encoding the input value
     * @return The URL encoded value
     * @throws java.lang.RuntimeException if {@link java.io.UnsupportedEncodingException} is thrown
    */
    public Object urlEncode(Object val, String charset) {
        try {
            val = val == null ? null : URLEncoder.encode(val.toString(), charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return val;
    }

    public final int getSeparatorIndex() {
        return separatorIndex;
    }

    public final boolean isEmptyStringsAllowed() {
        return emptyStringsAllowed;
    }

    public final boolean isNullsAllowed() {
        return nullsAllowed;
    }

    public final String getSeparator() {
        return separator;
    }
}
