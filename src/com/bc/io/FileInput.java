/**
 * @(#)FileInput.java   Aug 28, 2011 3:39:04 PM
 *
 * Copyright 2009 BC Enterprise, Inc. All rights reserved.
 * BCE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.bc.io;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author  Chinomso Bassey Ikwuagwu
 * @version 1.0
 * @since   1.0
 */
public class FileInput implements Serializable {
    
    private int bufferSize = 0x800; // 2K chars (4K bytes) 

    public byte [] read(String path) throws IOException {

        return read(new File(path));
    }

    public byte [] read(File source) throws IOException {

        if(!source.exists()) throw new FileNotFoundException(source.getPath());

        FileInputStream fis = new FileInputStream(source);

        int sourceLength = (int)source.length();
Logger.getLogger(this.getClass().getName()).log(Level.FINE, 
        "{0}. Source: {1}, source length: {2}", 
        new Object[]{this.getClass().getName(), source, sourceLength});

        return read(fis, sourceLength);
    }
    
    public byte [] read(InputStream in, int sourceLength) throws IOException {

        if(sourceLength < 1) {
            return null;
        }

        if(in == null) throw new NullPointerException();
        
        byte [] arr = new byte[sourceLength];

        try {

            int read = in.read(arr);

Logger.getLogger(this.getClass().getName()).log(Level.FINE, "{0}. Read: {1}", 
        new Object[]{this.getClass().getName(), read});
        }finally{
            if(in != null) try{ in.close(); }catch(IOException e){
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "", e);
            }
        }

        return arr;
    }

  /**
   * Attempts to read enough bytes from the stream to fill the given byte array,
   * with the same behavior as {@link DataInput#readFully(byte[])}.
   * Does not close the stream.
   *
   * @param in the input stream to read from.
   * @param b the buffer into which the data is read.
   * @throws EOFException if this stream reaches the end before reading all
   *     the bytes.
   * @throws IOException if an I/O error occurs.
   */
    public void readFully(InputStream in, byte[] b) throws IOException {
        readFully(in, b, 0, b.length);
    }
    
  /**
   * Attempts to read {@code len} bytes from the stream into the given array
   * starting at {@code off}, with the same behavior as
   * {@link DataInput#readFully(byte[], int, int)}. Does not close the
   * stream.
   *
   * @param in the input stream to read from.
   * @param b the buffer into which the data is read.
   * @param off an int specifying the offset into the data.
   * @param len an int specifying the number of bytes to read.
   * @throws EOFException if this stream reaches the end before reading all
   *     the bytes.
   * @throws IOException if an I/O error occurs.
   */
    public void readFully(
        InputStream in, byte[] b, int off, int len) throws IOException {
        int read = read(in, b, off, len);
        if (read != len) {
            throw new EOFException("reached end of stream after reading "
                + read + " bytes; " + len + " bytes expected");
        }
    }
    
  /**
   * Reads some bytes from an input stream and stores them into the buffer array
   * {@code b}. This method blocks until {@code len} bytes of input data have
   * been read into the array, or end of file is detected. The number of bytes
   * read is returned, possibly zero. Does not close the stream.
   *
   * <p>A caller can detect EOF if the number of bytes read is less than
   * {@code len}. All subsequent calls on the same stream will return zero.
   *
   * <p>If {@code b} is null, a {@code NullPointerException} is thrown. If
   * {@code off} is negative, or {@code len} is negative, or {@code off+len} is
   * greater than the length of the array {@code b}, then an
   * {@code IndexOutOfBoundsException} is thrown. If {@code len} is zero, then
   * no bytes are read. Otherwise, the first byte read is stored into element
   * {@code b[off]}, the next one into {@code b[off+1]}, and so on. The number
   * of bytes read is, at most, equal to {@code len}.
   *
   * @param in the input stream to read from
   * @param b the buffer into which the data is read
   * @param off an int specifying the offset into the data
   * @param len an int specifying the number of bytes to read
   * @return the number of bytes read
   * @throws IOException if an I/O error occurs
   */
    public int read(InputStream in, byte[] b, int off, int len)
        throws IOException {
        
        if(in == null || b == null) {
            throw new NullPointerException();
        }
        
        if (len < 0) {
            throw new IndexOutOfBoundsException("len is negative");
        }
        
        int total = 0;
        while (total < len) {
            int result = in.read(b, off + total, len - total);
            if (result == -1) {
                break;
            }
            total += result;
        }
        return total;
    }
    
    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }
}//END
