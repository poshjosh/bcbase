package com.bc.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @(#)ObjectIO.java   10-Apr-2015 17:16:44
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
public class ObjectIO<NAME_TYPE> implements Serializable {

    private static final Logger LOG = Logger.getLogger(ObjectIO.class.getName());

    private final Function<NAME_TYPE, String> getPathForName;
    
    public ObjectIO(Function<NAME_TYPE, String> getPathForName) {
        this.getPathForName = Objects.requireNonNull(getPathForName);
    }
    
    public boolean deleteNamedObject(NAME_TYPE name, boolean deleteOnExit) {
        return this.deleteObject(this.getPathForName.apply(name), deleteOnExit);
    }
    
    public boolean deleteObject(String path, boolean deleteOnExit) {
        File file = new File(path);
        if(!file.delete()) {
            file.deleteOnExit();
            return false;
        }else{
            return true;
        }
    }
    
    /**
     * Uses the default object folder as parent to folder to input fileName
     * @param name The object will be saved as this file name
     * @param object The object to save
     * @return true if operation successful, false otherwise
     */
    public boolean saveNamedObject(NAME_TYPE name, Object object) {
        final String path = this.getPathForName.apply(name);
        return saveObject(path, object);
    }
    
    public boolean saveObject(String path, Object object) {

        boolean result = false;

        try {
//LOG.info("Saving object "+object.getClass().getName()+" to: "+path);
            writeObject(path, object);

            result = true;
            
        }catch(IOException e) {
            LOG.log(Level.WARNING, "", e);
        }

        return result;
    }

    /**
     * loads object saved as fileName from default folder
     * @param name
     * @return
     */
    public Object loadNamedObject(NAME_TYPE name) {
        final String path = this.getPathForName.apply(name);
        return this.loadObject(path);
    }

    public Object loadObject(String path) {

        Object result = null;

        try {
//LOG.info("Loading object from: " + path);
            result = readObject(path);

        }catch(FileNotFoundException e) {
            // Lighter logging, without stack trace
            LOG.log(Level.WARNING, e.toString());
        }catch(IOException | ClassNotFoundException e) {
            LOG.log(Level.WARNING, "", e);
        }

        return result;
    }
    
    public Object readObject(String source) throws ClassNotFoundException, IOException {
        
        Object result = null;
        
        FileInputStream     fis = null;
        BufferedInputStream bis = null;
        ObjectInputStream   ois = null;
        
        try {

            fis = new FileInputStream(source);
            bis = new BufferedInputStream(fis);
            ois = new ObjectInputStream(bis);

            result = ois.readObject();
        
        }catch(IOException e) {
            
            throw e;
        
        }finally {
        
            if (ois != null) try { ois.close(); }catch(IOException e) {}
            if (bis != null) try { bis.close(); }catch(IOException e) {}
            if (fis != null) try { fis.close(); }catch(IOException e) {}
        }
        
        return result;
    }

    public void writeObject(String destination, Object obj) throws FileNotFoundException, IOException {
        
        FileOutputStream     fos = null;
        BufferedOutputStream bos = null;
        ObjectOutputStream oos = null;
        
        try{
            
            fos = new FileOutputStream(destination);
            bos = new BufferedOutputStream(fos);
            oos = new ObjectOutputStream(bos);

            oos.writeObject(obj);
        
        }catch(IOException e) {
            
            throw e;
        
        }finally {
        
            if (oos != null) try { oos.close(); }catch(IOException e) { LOG.log(Level.WARNING, "", e); }
            if (bos != null) try { bos.close(); }catch(IOException e) { LOG.log(Level.WARNING, "", e); }
            if (fos != null) try { fos.close(); }catch(IOException e) { LOG.log(Level.WARNING, "", e); }
        }
    }
}//END
