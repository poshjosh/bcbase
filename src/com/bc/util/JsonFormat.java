package com.bc.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
//import org.json.simple.JSONAware;

/**
 * @(#)JsonFormat.java   17-Oct-2014 19:12:03
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */
/**
 * Create a json string from various input types. E.g Map, List, Array or
 * plain key value pairs.
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public class JsonFormat {
    
    private boolean tidyOutput;
    
    private boolean escapeOutput;
    
    private int depth;
    
    private String indent;
    
    public JsonFormat() {
        escapeOutput = true;
        indent = "    ";
    }
    
    public void appendJSONString(Map m, StringBuilder sb) {
        
        boolean first = true;
	Iterator iter = m.entrySet().iterator();
        
        sb.append('{');
        
        ++depth;
        
        while(iter.hasNext()){
            
            if(first) {
                first = false;
            }else{
                sb.append(',');
            }
            
            this.appendNewline(depth, sb);
            
            Map.Entry entry=(Map.Entry)iter.next();
            appendJSONPair(String.valueOf(entry.getKey()),entry.getValue(), sb);
	}
        
        --depth;
        this.appendNewline(depth, sb);
        
        sb.append('}');
    }

    /**
     * Given key: <tt>name</tt> and value: <tt>Ada Rekiya Titi</tt>
     * output is <tt>{"name":"Ada Rekiya Titi"}</tt>
     * @param key
     * @param value
     * @param sb 
     */
    public void appendJSONString(String key, Object value, StringBuilder sb){
        Map<String, Object> map = Collections.singletonMap(key, value);
        this.appendJSONString(map, sb);
    }
    
    /**
     * Given key: <tt>name</tt> and value: <tt>Ada Rekiya Titi</tt>
     * output is <tt>"name":"Ada Rekiya Titi"</tt>
     * @param key
     * @param value
     * @param sb 
     */
    public void appendJSONPair(String key, Object value, StringBuilder sb){
        
        sb.append('\"');
        if(key == null) {
            sb.append("null");
        }else{
            if(this.isEscapeOutput()) {
                this.escape(key, sb);
            }else{
                sb.append(key);
            }
        }    
        sb.append('\"').append(':');

        this.appendJSONString(value, sb);
    }
    
    /**
     * Convert a list to JSON text. The result is a JSON array. 
     * If this list is also a JSONAware, JSONAware specific behaviours will be omitted at this top level.
     * 
     * @see org.json.simple.JSONValue#appendJSONString(Object)
     * 
     * @param list
     * @return JSON text, or "null" if list is null.
     */
    public String appendJSONString(Collection list, StringBuilder sb){
        
        if(list == null) {
            return "null";
        }    

        boolean first = true;

        Iterator iter = list.iterator();

        sb.append('[');
        
        while(iter.hasNext()){

            if(first) {
                first = false;
            }else{
                sb.append(',');
            }    
        
            Object value = iter.next();
            
            if(value == null){
                sb.append("null");
                continue;
            }
            
            this.appendJSONString(value, sb);
        }
            
        sb.append(']');

        return sb.toString();
    }
    
    /**
     * Convert an object to JSON text.
     * <p>
     * If this object is a Map or a List, and it's also a JSONAware, JSONAware will be considered firstly.
     * <p>
     * DO NOT call this method from appendJSONPair() of a class that implements both JSONAware and Map or List with 
     * "this" as the parameter, use JSONObject.appendJSONPair(Map) or JSONArray.appendJSONPair(List) instead. 
     * 
     * @see org.json.simple.JSONObject#appendJSONString(Map)
     * @see org.json.simple.JSONArray#appendJSONString(List)
     * @see org.json.simple.JSONValue#appendJSONString(java.lang.Object) 
     * @see com.scrapper.config.TidyJsonMap#appendJSONString(java.lang.Object, java.lang.StringBuilder); 
     * 
     * @param value
     * @return JSON text, or "null" if value is null or it's an NaN or an INF number.
     */
    public String toJSONString(Object value){
        StringBuilder builder = new StringBuilder();
        this.appendJSONString(value, builder);
        return builder.toString();
    }
    
    /**
     * Convert an object to JSON text and appends it to the Buffer
     * <p>
     * If this object is a Map or a List, and it's also a JSONAware, JSONAware will be considered firstly.
     * <p>
 DO NOT call this method from appendJSONPair() of a class that implements both JSONAware and Map or List with 
 "this" as the parameter, use JSONObject.appendJSONPair(Map) or JSONArray.appendJSONPair(List) instead. 
     * 
     * @see org.json.simple.JSONObject#appendJSONString(Map)
     * @see org.json.simple.JSONArray#appendJSONString(List)
     * @see org.json.simple.JSONValue#appendJSONString(java.lang.Object) 
     */
    public void appendJSONString(Object value, StringBuilder appendTo){
        
        if(value == null) {
            appendTo.append("null");
            return;
        }        

        if(value instanceof String) {
            appendTo.append('\"');
            if(this.isEscapeOutput()) {
                escape((String)value, appendTo);
            }else{
                appendTo.append(value);
            }    
            appendTo.append('\"');
            return;
        }        

        if(value instanceof Double){
            if(((Double)value).isInfinite() || ((Double)value).isNaN()) {
                appendTo.append("null");
            }else{
                appendTo.append(value);
            }        
            return;
        }

        if(value instanceof Float){
            if(((Float)value).isInfinite() || ((Float)value).isNaN()) {
                appendTo.append("null");
            }else{
                appendTo.append(value);
            }        
            return;
        }		

        if(value instanceof Number) {
            appendTo.append(value);
            return;
        }        

        if(value instanceof Boolean) {
            appendTo.append(value);
            return;
        }        

//        if((value instanceof JSONAware)) {
//            appendTo.append(((JSONAware)value).toJSONString());
//            return;
//        }        

        if(value instanceof Map) {
            appendJSONString((Map)value, appendTo);
            return;
        }        

        if(value instanceof Collection) {
            appendJSONString((Collection)value, appendTo);
            return;
        }  
        
        if(value instanceof Object) {
            appendTo.append('\"');
            appendTo.append(value);
            appendTo.append('\"');
            return;
        }
    }
    
    /**
     * Escape quotes, \, /, \r, \n, \b, \f, \t and other control characters (U+0000 through U+001F).
     */
    public String escape(String s){
        if(s==null) {
            return null;
        }    
        StringBuilder sb = new StringBuilder(s.length() + (s.length()/10));
        escape(s, sb);
        return sb.toString();
    }
        
    /**
     * Escape quotes, \, /, \r, \n, \b, \f, \t and other control characters (U+0000 through U+001F).
     */
    public void escape(String s, StringBuilder sb) {
        for(int i=0;i<s.length();i++){
            char ch=s.charAt(i);
            switch(ch){
            case '"':
                    sb.append("\\\"");
                    break;
            case '\\':
                    sb.append("\\\\");
                    break;
            case '\b':
                    sb.append("\\b");
                    break;
            case '\f':
                    sb.append("\\f");
                    break;
            case '\n':
                    sb.append("\\n");
                    break;
            case '\r':
                    sb.append("\\r");
                    break;
            case '\t':
                    sb.append("\\t");
                    break;
            case '/':
                    sb.append("\\/");
                    break;
            default:
    //Reference: http://www.unicode.org/versions/Unicode5.1.0/
                    if((ch>='\u0000' && ch<='\u001F') || (ch>='\u007F' && ch<='\u009F') || (ch>='\u2000' && ch<='\u20FF')){
                            String ss=Integer.toHexString(ch);
                            sb.append("\\u");
                            for(int k=0;k<4-ss.length();k++){
                                    sb.append('0');
                            }
                            sb.append(ss.toUpperCase());
                    }
                    else{
                            sb.append(ch);
                    }
            }
        }//for
    }
    
    private void appendNewline(int depth, StringBuilder appendTo) {
        if(!this.isTidyOutput()) {
            return;
        }
        appendTo.append('\n');
        for(int i=0; i<depth; i++) {
            appendTo.append(indent);
        }
    }

    public boolean isEscapeOutput() {
        return escapeOutput;
    }

    public void setEscapeOutput(boolean escape) {
        this.escapeOutput = escape;
    }

    public boolean isTidyOutput() {
        return tidyOutput;
    }

    public void setTidyOutput(boolean tidyOutput) {
        this.tidyOutput = tidyOutput;
    }

    public String getIndent() {
        return indent;
    }

    public void setIndent(String indent) {
        this.indent = indent;
    }
}
