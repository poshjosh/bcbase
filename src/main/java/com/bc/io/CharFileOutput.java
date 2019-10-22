package com.bc.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @(#)CharFileOutput.java   20-Jan-2012 22:32:40
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/licenses/software.html
 */
/**
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public class CharFileOutput extends FileOutput{

    private static final Logger LOG = Logger.getLogger(CharFileOutput.class.getName());
    
    private String charset;

    public CharFileOutput() { }

    public CharFileOutput(String charset) { 
        this.charset = charset;
    }
    
    @Override
    public void write(boolean append, String src, File dest) throws IOException{

        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try{
            fos = new FileOutputStream(dest, append);
            if(charset != null) {
                osw = new OutputStreamWriter(fos, charset);
            }else{
                osw = new OutputStreamWriter(fos);
            }
            bw = new BufferedWriter(osw);
            bw.write(src);
            bw.flush();
        }finally{
            if(bw != null) try{ bw.close(); }catch(IOException e){
                LOG.log(Level.WARNING, "", e);
            }
            if(osw != null) try{ osw.close(); }catch(IOException e){
                LOG.log(Level.WARNING, "", e);
            }
            if(fos != null) try{ fos.close(); }catch(IOException e){
                LOG.log(Level.WARNING, "", e);
            }
        }
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
}
