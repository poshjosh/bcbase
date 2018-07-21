package com.bc.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @(#)CharFileIO.java   21-Jan-2012 08:21:46
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
public class CharFileIO extends FileIO {
    
    public CharFileIO() {
        setFileInput(new CharFileInput());
        setFileOutput(new CharFileOutput());
    }
    
    public CharFileIO(String charset) {
        setFileInput(new CharFileInput(charset));
        setFileOutput(new CharFileOutput(charset));
    }
    
    public CharSequence readChars(String path) throws IOException {
        return ((CharFileInput)getFileInput()).readChars(path);
    }

    public CharSequence readChars(File source) throws IOException {
        return ((CharFileInput)getFileInput()).readChars(source);
    }

    public CharSequence readChars(InputStream in) throws IOException {
        return readChars(in, -1);
    }
    
    public CharSequence readChars(InputStream in, int sourceLength) throws IOException {
        return ((CharFileInput)getFileInput()).readChars(in, sourceLength);
    }
    
    public CharSequence readChars(Reader reader) throws IOException {
        return ((CharFileInput)getFileInput()).readChars(reader);
    }
    
    public CharSequence readChars(Reader reader, int sourceLength) throws IOException {
        return ((CharFileInput)getFileInput()).readChars(reader, sourceLength);
    }

    /**
     * Does not close the inputstream or outputstream
     * @param src The source stream
     * @param dest The destination stream
     * @throws IOException If an IO error occurs
     * @return long the number of chars copied
     */
    public long copyChars(InputStream is, OutputStream os) throws IOException {

        InputStreamReader isr = null;
        OutputStreamWriter osw = null;
        try{
            
            String inputCharset = this.getInputCharset();
            
            if(inputCharset == null) {
                isr = new InputStreamReader(is);
            }else{
                isr = new InputStreamReader(is, inputCharset);
            }
          
            String outputCharset = this.getOutputCharset();
            if(outputCharset == null) {
                osw = new OutputStreamWriter(os);
            }else{
                osw = new OutputStreamWriter(os, outputCharset);
            }
            
            long copied = this.copyChars(isr, osw);
            
            osw.flush();
            
            return copied;
            
        }finally{
            if(isr != null) try{ isr.close(); }catch(IOException e){
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "", e);
            }
            if(osw != null) try{ osw.close(); }catch(IOException e){
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "", e);
            }
        }
    }
    
    public long copyChars(Readable from, Appendable to) throws IOException {
        
        CharBuffer buf = CharBuffer.allocate(this.getBufferSize());
        
        long total = 0;
        
        while (from.read(buf) != -1) {
            buf.flip();
            to.append(buf);
            total += buf.remaining();
            buf.clear();
        }
        return total;
    }
    
    public String getOutputCharset() {
        return ((CharFileOutput)this.getFileOutput()).getCharset();
    }
    
    public void setOutputCharset(String charset) {
        ((CharFileOutput)this.getFileOutput()).setCharset(charset);
    }

    public String getInputCharset() {
        return ((CharFileInput)this.getFileInput()).getCharset();
    }
    
    public void setInputCharset(String charset) {
        ((CharFileInput)this.getFileInput()).setCharset(charset);
    }
}
