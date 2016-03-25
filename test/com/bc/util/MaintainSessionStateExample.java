package com.bc.util;

import com.bc.htmlparser.ParseJob;
import com.bc.io.CharFileIO;
import com.bc.net.ConnectionManager;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML;


/**
 * @(#)Temp.java   20-Jan-2015 09:02:18
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
public class MaintainSessionStateExample {

    public static void main(String [] args) {

        try{
            
            ConnectionManager cm = new ConnectionManager();

            cm.setAddCookies(true);
            cm.setGetCookies(true);

            URL url = new URL("http://localhost:8080/gx?rh=login");
// Using this does not require the Map below            
//            URL url = new URL("http://localhost:8080/gx?rh=login&emailAddress=looseboxes@gmail.com&password=1kjvdul-");

            Map<String, String> output = new HashMap<>(2, 1.0f);
            output.put("emailAddress", "looseboxes@gmail.com");
            output.put("password", "1kjvdul-");
            
            InputStream in;
            int code;
            String msg;
            List<String> cookies;
            String contents;
            String [] titleAndMessage;

            in = cm.getInputStreamForUrlEncodedForm(url, output, false);

            code = cm.getResponseCode();
            msg = cm.getResponseMessage();
            cookies = cm.getCookies();
            
System.out.println(code);
System.out.println("Cookies: "+cookies);
 
            CharFileIO io = new CharFileIO();
            
            contents = io.readChars(in).toString();
            
            titleAndMessage = getPageTitleAndMessage(contents);
System.out.println("Title: "+titleAndMessage[0]);
System.out.println("Message: "+titleAndMessage[1]);

            if(code < 400) {
                
                url = new URL("http://localhost:8080/admin?password=x86-1B");

                in = cm.getInputStreamForUrlEncodedForm(url, null, false);

                code = cm.getResponseCode();
                msg = cm.getResponseMessage();
                cookies = cm.getCookies();

System.out.println("Status code: "+code);
System.out.println("Cookies: "+cookies);

                contents = io.readChars(in).toString();

                titleAndMessage = getPageTitleAndMessage(contents);
System.out.println("Title: "+titleAndMessage[0]);
System.out.println("Message: "+titleAndMessage[1]);
                
            }else{
                
                System.err.println(msg);
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    private static String [] getPageTitleAndMessage(String input) {
        
        String title;
        
        ParseJob parseJob = new ParseJob();
        
        try{
            title = parseJob.innerHtml(true).accept(HTML.Tag.TITLE).separator("\n").parse(input).toString();
        }catch(IOException e) {
            title = "";
        }
        

        SimpleAttributeSet as = new SimpleAttributeSet();
        as.addAttribute("id", "myMessage");
        
        String myMessage;
        try{
            myMessage = parseJob.innerHtml(true).accept(as).separator("\n").parse(input).toString();
        }catch(IOException e) {
            myMessage = "";
        }

        return new String[]{title, myMessage};
    }
}
