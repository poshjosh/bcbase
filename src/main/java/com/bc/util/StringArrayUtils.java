package com.bc.util;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @(#)StringArrayUtils.java   06-Oct-2015 19:16:50
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
public class StringArrayUtils {

    private transient static final Logger LOG = Logger.getLogger(StringArrayUtils.class.getName());

    public static boolean contains(String [] arr, String elem) {
        return matches(arr, elem, MatchType.EQUALS);
    }
    
    public static boolean matches(String [] arr, String elem, MatchType matchType) {
        return indexOf(arr, elem, matchType) != -1;
    }
    
    public static enum MatchType {EQUALS, EQUALS_IGNORE_CASE, CONTAINS, STARTSWITH, 
    ENDSWITH, MATCHES, REGION_MATCHES, REGION_MATCHES_IGNORE_CASE}
    
    public static int indexOf(String [] arr, String elem, MatchType matchType) {
        
        if(arr == null || elem == null) {
            throw new NullPointerException();
        }
        
        synchronized(arr) {
            for(int i=0; i<arr.length; i++) {
                boolean found;
                switch(matchType) {
                    case EQUALS:
                        found = arr[i].equals(elem); break;
                    case EQUALS_IGNORE_CASE:
                        found = arr[i].equalsIgnoreCase(elem); break;
                    case CONTAINS:
                        found = arr[i].contains(elem) || elem.contains(arr[i]); break;
                    case STARTSWITH:
                        found = arr[i].startsWith(elem) || elem.startsWith(arr[i]); break;
                    case ENDSWITH:
                        found = arr[i].endsWith(elem) || elem.endsWith(arr[i]); break;
                    case MATCHES:
                        found = arr[i].matches(elem) || elem.matches(arr[i]); break;
                    case REGION_MATCHES:
                        found = arr[i].regionMatches(0, elem, 0, elem.length()) || 
                                elem.regionMatches(0, arr[i], 0, arr[i].length()); break;
                    case REGION_MATCHES_IGNORE_CASE:    
                        found = arr[i].regionMatches(true, 0, elem, 0, elem.length()) || 
                                elem.regionMatches(true, 0, arr[i], 0, arr[i].length()); break;
                    default:    
                        throw new IllegalArgumentException("Unexpected "+MatchType.class.getName()+": "+matchType);
                }
                
                if(LOG.isLoggable(Level.FINEST)) {                
                    LOG.log(Level.FINEST, 
                            "Found {0}, value 1: {1}, value 2: {2}, match type: {3}", 
                            new Object[]{found, elem, arr[i], matchType});
                }
                
                if(found) {
                    return i;
                }
            }
        }
        return -1;
    }
}
