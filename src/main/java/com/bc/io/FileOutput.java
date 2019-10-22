/**
 * @(#)FileOutput.java   Aug 28, 2011 3:39:04 PM
 *
 * Copyright 2009 BC Enterprise, Inc. All rights reserved.
 * BCE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.bc.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author  Chinomso Bassey Ikwuagwu
 * @version 1.0
 * @since   1.0
 */
public class FileOutput implements Serializable {

    private static final Logger LOG = Logger.getLogger(FileOutput.class.getName());

    public File write(String src, String dest, boolean rotate) throws IOException{
        return write(src.getBytes(), new File(dest), rotate);
    }
    public File write(byte [] src, String dest, boolean rotate) throws IOException{
        return write(src, new File(dest), rotate);
    }

    /**
     * Use {@linkplain #write(java.lang.String, java.lang.String, boolean)}
     * @deprecated
     */
    public File write(boolean append, String src, String dest, boolean rotate) throws IOException{

        return write(append, src, new File(dest), rotate);
    }

    public File write(String src, File dest, boolean rotate) throws IOException{
        return this.write(src.getBytes(), dest, rotate);
    }
    public File write(byte [] src, File dest, boolean rotate) throws IOException{
        if(rotate && dest.exists()) {
            dest = this.rotate(dest);
        }
        this.write(false, src, dest);
        return dest;
    }

    /**
     * Use {@linkplain #write(java.lang.String, java.io.File, boolean)}
     * @deprecated
     */
    public File write(boolean append, String src, File dest, boolean rotate) throws IOException{

        if(!append) {
            if(rotate && dest.exists()) {
                dest = this.rotate(dest);
            }
        }

        this.write(append, src, dest);

        return dest;
    }

    public void write(boolean append, String src, String dest) throws IOException{
        write(append, src.getBytes(), new File(dest));
    }
    public void write(boolean append, byte [] src, String dest) throws IOException{
        write(append, src, new File(dest));
    }

    public void write(boolean append, String src, File dest) throws IOException{
        this.write(append, src.getBytes(), dest);
    }
    public void write(boolean append, byte [] src, File dest) throws IOException{

        FileOutputStream out = null;
        try{
            out = new FileOutputStream(dest, append);
            out.write(src);
        }finally{
            if(out != null) try{ out.close(); }catch(IOException e){
                LOG.log(Level.WARNING, "", e);
            }
        }
    }

    public File rotate(String path) throws IOException {
        return rotate(new File(path));
    }

    public File rotate(File file) throws IOException {

        if(!file.exists()) {
            if(!file.createNewFile()) {
                throw new IOException("Failed to create file: "+file);
            }
        }

        String dest = file.getAbsolutePath();

        int n = dest.lastIndexOf('.');

        boolean hasExtension = n > 0;

        String main = null;
        String ext  = null;

        if(hasExtension) {
            main = dest.substring(0, n);
            ext = dest.substring(n+1);
        } else{
            main = dest;
            ext = "";
        }

        int i = 1;
        do{
            file = new File(main + i + "." + ext);
            ++i;
        }while(file.exists());

        return file;
    }

    public static boolean DEBUG = false;
}//END
