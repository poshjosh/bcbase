package com.bc.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @(#)PropertiesService.java   17-Aug-2014 23:27:18
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
public class DefaultPropertiesService 
        extends DefaultPropertiesView implements PropertiesService {

    private boolean useCache;
    
    private boolean merged;
    
    public DefaultPropertiesService() { }
    
    public void unmerge() {
        this.clear();
        this.merged = false;
    }
    
    public Properties getMerged() {
        return this.get(this.getDefaultFilename());
    }

    public Properties getProperties() {
        return this.getMerged();
    }
    
    public void merge() throws IOException {
        this.merge(this.getDefaultPropertiesDir(), this.getPropertiesDir());
    }
    
    public void merge(String defaultPropertiesDir, String propertiesDir) 
            throws IOException {
        
        if(propertiesDir == null) {
            throw new NullPointerException();
        }
        
        if(this.isUseCache()) {
            throw new UnsupportedOperationException();
        }
        
        String tgt = defaultPropertiesDir == null ? propertiesDir: defaultPropertiesDir;
        
XLogger.getInstance().log(Level.INFO, "Merging all properties file in: {0}", this.getClass(), tgt);
        
        File file = this.getFile(tgt);
        
        String [] names = file.list(this.getFilter());

        Properties all = new Properties();
        
        for(String name:names) {
            
XLogger.getInstance().log(Level.INFO, "Merging: {0}", this.getClass(), this.getPath(name)); 

            // If use cache is true add the loaded properties to the cache
            //
            Properties props = this.loadByName(name, name);
            
            Set<String> keys = props.stringPropertyNames();
            
            for(String key:keys) {
                
                if(all.getProperty(key) != null) {
                
                    throw new UnsupportedOperationException("Property '"+key+"' is duplicated in file: "+name);
                }
                
                all.setProperty(key, props.getProperty(key));
            }
        }
        
        this.put(this.getDefaultFilename(), all);
        
        this.merged = true;
    }
    
    public String getPropertiesDir() {
        throw new UnsupportedOperationException();
    }

    public String getDefaultPropertiesDir() {
        return null;
    }
    
    @Override
    public String loadPropertyFor(String filename, String key) throws IOException{
        Properties properties = this.load((String)null, this.getPath(filename));
        return properties.getProperty(key);
    }
    
    @Override
    public String loadPropertyFor(String filename, String key, String defaultValue) throws IOException{
        Properties properties = this.load((String)null, this.getPath(filename));
        return properties.getProperty(key, defaultValue);
    }
    
    @Override
    public String loadPropertyFor(String defaultfilename, String filename, String key, String defaultValue) throws IOException{
        Properties properties = this.load(this.getDefaultPath(defaultfilename), this.getPath(filename));
        return properties.getProperty(key, defaultValue);
    }
    
    @Override
    public void storePropertyFor(String filename, String key, String value) throws IOException{
        Properties properties = this.load((String)null, this.getPath(filename));
        properties.setProperty(key, value);
        this.storeByName(filename);
    }
    
    @Override
    public void storePropertyFor(String defaultfilename, String filename, String key, String value) throws IOException{
        Properties properties = this.load(this.getDefaultPath(defaultfilename), this.getPath(filename));
        properties.setProperty(key, value);
        this.storeByName(filename);
    }

    @Override
    public Properties loadByName(String defaultFilename, String filename) throws IOException{
        return load(this.getDefaultPath(defaultFilename), this.getPath(filename));
    }
    
    @Override
    public Properties load(String defaultPath, String path) throws IOException {

        String name = getFile(path).getName();
        
        Properties output = null;
        
        if(!this.isMerged() && this.isUseCache()) {
            output = this.get(name);
        }
        
        if(output == null) {
            
            Properties defaults = null;
            if(defaultPath != null) {
                defaults = new Properties();
                load(defaults, defaultPath);
            }

            output = new Properties(defaults);
            load(output, path);
            
            if(!this.isMerged() && this.isUseCache()) {
                this.put(name, output);
            }
        }
        
        return output;
    }

    @Override
    public void store() throws IOException {
        this.storeByName(this.getDefaultFilename());
    }
    
    @Override
    public void storeByName(String filename) throws IOException {
        this.store(this.getPath(filename));
    }
    
    @Override
    public void store(String path) throws IOException {

        if(this.isMerged()) {
            this.storeMerged(path);
            return;
        }
        
        if(!this.isUseCache()) {
            return;
        }

        String name = getFile(path).getName();
        
        Properties props = this.get(name);
        
        if(props != null) {
            this.store(props, path);
        }
    }
    
    private void storeMerged(String path) throws IOException {
        Properties props = new Properties();
        this.load(props, path);
        Set<String> names = props.stringPropertyNames();
        for(String name:names) {
            String prop = this.getProperty(name);
            props.setProperty(name, prop);
        }
        this.store(props, path);
    }
    
    @Override
    public void load(Properties props, String path) throws IOException {

        InputStream in = null;
        // create and load default properties
        try{

Logger.getLogger(this.getClass().getName()).log(Level.FINER, 
"Loading properties from: {0}", path);           
            
            in = getInputStream(path);
            
            if(in == null) {
                throw new NullPointerException();
            }
            
            props.load(in);
            
Logger.getLogger(this.getClass().getName()).log(Level.FINER, 
"Loaded from {0}, properties: {1}", new Object[]{path, props});           
        
        }finally{
            if(in != null) {
                try{
                    in.close();
                }catch(IOException ignored) {}
            }
        }
    }

    @Override
    public void store(Properties props, String path) throws IOException {
  
        FileOutputStream out = null;
        
        // create and load default properties
        try{
            
            // now load properties 
            // from last invocation
            out = new FileOutputStream(getFile(path), false);
            
Logger.getLogger(this.getClass().getName()).log(Level.FINER, 
"Saving to {0}, properties: {1}", new Object[]{path, props});           
            
            props.store(out, "Saved on: "+new Date());
            
        }finally{
            if(out != null) {
                try{
                    out.close();
                }catch(IOException ignored) {}
            }
        }
    }
    
    public InputStream getInputStream(String path) throws FileNotFoundException {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        InputStream in;
        
        URL url = classLoader.getResource(path);
        
	try {
	    in = url != null ? url.openStream() : null;
	} catch (IOException e) {
	    in = null;
	}

XLogger.getInstance().log(Level.FINER, "Filename: {0}, Resource: {1}", this.getClass(), path, url);            

        return in;
    }
    
    public File getFile(String path) {
        URL url = Thread.currentThread().getContextClassLoader().getResource(path);
XLogger.getInstance().log(Level.FINER, "Name: {0}, url: {1}", this.getClass(), path, url);
        try{
            File file = Paths.get(url.toURI()).toFile();
XLogger.getInstance().log(Level.FINE, "Name: {0}, url: {1} file: {2}", this.getClass(), path, url, file);
            return file;
        }catch(URISyntaxException notexpected) { 
            throw new RuntimeException(notexpected);
        }
    }
    
    private String getDefaultPath(String filename) {
        if(filename == null) {
            return null;
        }
        if(this.getDefaultPropertiesDir() == null) {
            return null;
        }
        return this.getDefaultPropertiesDir() + File.separatorChar + filename;
    }

    private String getPath(String filename) {
        if(filename == null) {
            throw new NullPointerException();
        }
        return this.getPropertiesDir() + File.separatorChar + filename;
    }

    public boolean isMerged() {
        return merged;
    }

    @Override
    public boolean isUseCache() {
        return useCache;
    }

    @Override
    public void setUseCache(boolean useCache) {
        if(!useCache) {
            this.clear();
        }
        this.useCache = useCache;
    }
    
    @Override
    public String toString() {
        return this == null ? this.getClass().getName() : 
                this.getClass().getName() + ": " + this.keySet();
    }
}
