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
public abstract class ObjectIO implements Serializable {

    public ObjectIO() { }

    public abstract String getPath(String fileName);
    
    public boolean deleteMyObject(String fname, boolean deleteOnExit) {
        return this.deleteObject(this.getPath(fname), deleteOnExit);
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
     * @param fileName The object will be saved as this file name
     * @param object The object to save
     * @return true if operation successful, false otherwise
     */
    public boolean saveMyObject(String fileName, Object object) {
        
        final String path = this.getPath(fileName);

        return saveObject(path, object);
    }
    
    public boolean saveObject(String path, Object object) {

        boolean result = false;

        try {
//Logger.getLogger(this.getClass().getName()).info("Saving object "+object.getClass().getName()+" to: "+path);
            writeObject(path, object);

            result = true;
            
        }catch(IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "", e);
        }

        return result;
    }

    /**
     * loads object saved as fileName from default folder
     * @param fileName
     * @return
     */
    public Object loadMyObject(String fileName) {
        
        final String path = this.getPath(fileName);
     
        return this.loadObject(path);
    }

    public Object loadObject(String path) {

        Object result = null;

        try {
//Logger.getLogger(this.getClass().getName()).info("Loading object from: " + path);
            result = readObject(path);

        }catch(FileNotFoundException e) {
            // Lighter logging, without stack trace
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, 
            "{0}. {1}", new Object[]{this.getClass().getName(), e});
        }catch(IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "", e);
        }catch(ClassNotFoundException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "", e);
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
        
            if (oos != null) try { oos.close(); }catch(IOException e) { Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "", e); }
            if (bos != null) try { bos.close(); }catch(IOException e) { Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "", e); }
            if (fos != null) try { fos.close(); }catch(IOException e) { Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "", e); }
        }
    }
}//END
