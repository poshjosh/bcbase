package com.bc.util;

import java.io.IOException;
import java.util.Properties;


/**
 * @(#)NewInterface.java   02-Jan-2015 01:50:54
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */
public interface PropertiesService extends PropertiesView {

    boolean isUseCache();

    Properties load(String defaultPath, String path) throws IOException;

    void load(Properties props, String path) throws IOException;

    Properties loadByName(String defaultFilename, String filename) throws IOException;

    String loadPropertyFor(String filename, String key) throws IOException;

    String loadPropertyFor(String filename, String key, String defaultValue) throws IOException;

    String loadPropertyFor(String defaultfilename, String filename, String key, String defaultValue) throws IOException;

    void setUseCache(boolean useCache);

    void store() throws IOException;
    
    void store(String path) throws IOException;

    void store(Properties props, String path) throws IOException;

    void storeByName(String filename) throws IOException;

    void storePropertyFor(String filename, String key, String value) throws IOException;

    void storePropertyFor(String defaultfilename, String filename, String key, String value) throws IOException;

}
