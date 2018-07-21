package com.bc.html;

import com.bc.io.CharFileInput;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @(#)CSSParser.java   03-May-2013 16:54:23
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
public class CSSParser {

    public CSSParser() { } 
    
    public Map<String, String> parse(File file) throws IOException {
        
        CharFileInput input = new CharFileInput();
        
        CharSequence textCSS = input.readChars(file);
        
        if(textCSS == null) throw new NullPointerException();
        
        return parse(textCSS);
    }
    
    public Map<String, String> parse(InputStream in) throws IOException {
        
        CharFileInput input = new CharFileInput();
        
        CharSequence textCSS = input.readChars(in);
        
        if(textCSS == null) throw new NullPointerException();
        
        return parse(textCSS);
    }
    
    public Map<String, String> parse(CharSequence textCss) {
        
        StringBuilder builder = new StringBuilder();
        builder.append(".+").append(Pattern.quote("{"));
        builder.append(".+").append(Pattern.quote("}"));
        
        Matcher matcher = Pattern.compile(builder.toString()).matcher(textCss);
        
        final LinkedHashMap<String, String> nodes = new LinkedHashMap<>();
        
        while(matcher.find()) {
            String group = matcher.group().trim();
            int a = group.indexOf('{');
            String key = group.substring(0, a);
            String value = group.substring(a+1, group.length()-1);
            nodes.put(key, value);
        }
        return nodes;
    }
    
    public String parse(String name, String source) {
        return parse(name, source, 0, source.length());
    }

    public String parse(String name, String input, int offset, int end) {

        input = input.substring(offset, end);
        
        //.searchResultsRow{ width:100% }    
        // Here the output will be width:100%
        //
        name = name.trim();
        
        StringBuilder regex = new StringBuilder(name);
        
        regex.append(Pattern.quote("{")).append(".+").append(Pattern.quote("}"));
        
        Matcher matcher = Pattern.compile(regex.toString()).matcher(input);
        
        if(matcher.find()) {
            String group = matcher.group().trim();
            return group.substring(name.length(), group.length()-1);
        }else{
            return null;
        }
    }
}
