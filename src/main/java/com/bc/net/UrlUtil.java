/*
 * Copyright 2017 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bc.net;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 5, 2017 2:22:10 PM
 */
public class UrlUtil {
    
    private static final Logger logger = Logger.getLogger(UrlUtil.class.getName());

  public static String toWWWFormat(String url) throws MalformedURLException {
    String x = "//";
    int n = url.indexOf(x);
    String b;
    String a;
    if (n == -1) {
      a = "";
      b = url;
    } else {
      n += x.length();
      a = url.substring(0, n);
      b = url.substring(n);
    }
    
    String[] parts = b.split("\\.");
    
    if (parts.length == 1) {
      throw new MalformedURLException("Not a URL: " + url);
    }
    
    StringBuilder builder = new StringBuilder(a);
    
    if (parts.length == 2) {
      builder.append("www.").append(parts[0]).append('.').append(parts[1]);
    } else {
      for (int i = 0; i < parts.length; i++) {
        String part = i == 0 ? "www" : parts[i];
        builder.append(part);
        if (i < parts.length - 1) {
          builder.append('.');
        }
      }
    }
    
//    System.out.println(" Input: " + url + "\nOutput: " + builder);
    
    return builder.toString();
  }
  
  public static List<String> getBaseURLs(String urlString)
  {
    String baseURL = getBaseURL(urlString);
    logger.log(Level.FINER, "Base url: {0}", baseURL);
    LinkedList<String> urls = new LinkedList();
    urls.add(baseURL);
    if (baseURL.equals(urlString)) return urls;
    String s = urlString.substring(baseURL.length());
    logger.log(Level.FINER, "URL file: {0}", s);
    
    if (s.startsWith("/")) { s = s.substring(1);
    }
    String[] parts = s.split("/");
    logger.log(Level.FINER, "URL file parts: {0}", (parts==null?null:Arrays.toString(parts)));
    StringBuilder builder = new StringBuilder();
    for (String part : parts) {
      builder.setLength(0);
      baseURL = baseURL + '/' + part;
      urls.add(baseURL);
    }
    return urls;
  }
    
    /**
     * @param url 
     * @return The base URL of the method argument or <code>null</code> if the 
     * method argument is a malformed URL
     */
    public static String getBaseURL(String url) {
        try{
            return getBaseURL(new URL(url));
        }catch(MalformedURLException e) {
            return null;
        }
    }
    
    public static String getBaseURL(URL url) {
        return url.getProtocol() + "://" + url.getHost();
    }

    public final static String getImageUrlRegex() {
        return getImageUrlRegex("jpg", "gif", "png", "jpeg");
    }
    
    public final static String getImageUrlRegex(String... imageExtensions) {
// Example:   (?:([^:/?#]+):)?(?://([^/?#]*))?([^?#]*\\.(?:jpe?g|gif|png))(?:\\?([^#]*))?(?:#(.*))?      
        StringBuilder builder = new StringBuilder("(?:([^:/?#]+):)?(?://([^/?#]*))?([^?#]*\\.(?:");
        for(int i=0; i<imageExtensions.length; i++) {
            builder.append(imageExtensions[i]);
            if(i < imageExtensions.length - 1) {
                builder.append('|');
            }
        }
        builder.append("))(?:\\?([^#]*))?(?:#(.*))?");
        return builder.toString();
    }
}
