/**
 * @(#)FileIO.java   Jun 11, 2009 4:48:04 PM   
 *
 * Copyright 2009 BC Enterprise, Inc. All rights reserved.
 * BCE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.bc.io;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author  Chinomso Bassey Ikwuagwu
 * @version 1.0
 * @since   1.0
 */
public class FileIO implements Serializable {

    private transient static final Logger LOG = Logger.getLogger(FileIO.class.getName());
    
    private FileInput fileInput;
    private FileOutput fileOutput;

    public FileIO() {
        fileInput = new FileInput();
        fileOutput = new FileOutput();
    }

    public byte [] read(String path) throws IOException {
        return fileInput.read(path);
    }

    public byte [] read(File source) throws IOException {
        return fileInput.read(source);
    }

    public byte [] read(InputStream in, int sourceLength) throws IOException {
        return fileInput.read(in, sourceLength);
    }
    
    public void readFully(InputStream in, byte [] bytes) throws IOException {
        fileInput.readFully(in, bytes);
    }
    
    public void readFully(InputStream in, byte [] bytes, int off, int len) throws IOException {
        fileInput.readFully(in, bytes, off, len);
    }
    
    public int read(InputStream in, byte [] bytes, int off, int len) throws IOException {
        return fileInput.read(in, bytes, off, len);
    }
    
    public File write(String src, String dest, boolean rotate) throws IOException{
        return fileOutput.write(src, dest, rotate);
    }
    public File write(byte[] src, String dest, boolean rotate) throws IOException{
        return fileOutput.write(src, dest, rotate);
    }
    
    /**
     * Use {@linkplain #write(java.lang.String, java.lang.String, boolean)}
     * @deprecated 
     */
    public File write(boolean append, String src, String dest, boolean rotate) throws IOException{
        return fileOutput.write(append, src, dest, rotate);
    }

    public File write(String src, File dest, boolean rotate) throws IOException{
        return fileOutput.write(src, dest, rotate);
    }
    public File write(byte[] src, File dest, boolean rotate) throws IOException{
        return fileOutput.write(src, dest, rotate);
    }

    /**
     * Use {@linkplain #write(java.lang.String, java.io.File, boolean)}
     * @deprecated
     */
    public File write(boolean append, String src, File dest, boolean rotate) throws IOException{
        return fileOutput.write(append, src, dest, rotate);
    }

    public void write(boolean append, String src, String dest) throws IOException{
        fileOutput.write(append, src, dest);
    }
    public void write(boolean append, byte[] src, String dest) throws IOException{
        fileOutput.write(append, src, dest);
    }

    public void write(boolean append, String src, File dest) throws IOException{
        fileOutput.write(append, src, dest);
    }
    public void write(boolean append, byte[] src, File dest) throws IOException{
        fileOutput.write(append, src, dest);
    }

    public void cut(boolean append, String src, String dest) throws IOException {

        cut(append, new File(src), new File(dest), false);
    }
    public void cut(boolean append, File src, File dest) throws IOException {

        cut(append, src, dest, false);
    }

    public void cut(boolean append, String src, String dest, boolean rotate) throws IOException {

        cut(append, new File(src), new File(dest), rotate);
    }
    public void cut(boolean append, File src, File dest, boolean rotate) throws IOException {

        this.transfer(true, append, src, dest, null, rotate);
    }

    public void copy(boolean append, String src, String dest) throws IOException {

        copy(append, src, dest, false);
    }
    public void copy(boolean append, File src, File dest) throws IOException {

        copy(append, src, dest, false);
    }

    public void copy(boolean append, String src, String dest, boolean rotate) throws IOException {

        copy(append, new File(src), new File(dest), rotate);
    }
    public void copy(boolean append, File src, File dest, boolean rotate) throws IOException {

        this.transfer(false, append, src, dest, null, rotate);
    }

    public void transfer(
            boolean cut, boolean append, 
            File src, File dest, 
            FileFilter filter, boolean rotate) throws IOException {

        if(src.isFile()) {

            if(filter == null || filter.accept(src)) {
                
                this.copyFile(append, src, dest, rotate);

                if(cut) {
                    this.delete(src, true);
                }
            }

        }else if(src.isDirectory()){

            File [] srcChildren = src.listFiles();

            for(File srcChild:srcChildren) {

                String destChild = new StringBuilder(dest.getPath()).append('/').append(srcChild.getName()).toString();

                this.transfer(cut, append, srcChild, new File(destChild), filter, rotate);
            }
        }
    }
    
    /**
     * @param append If true the contents to be written will be appended to any 
     * contents found in the destination file if it exists.
     * @param src The source file
     * @param dest The destination file
     * @param rotate If true the destination file will be renamed if a file with
     * the same name already exists
     * @throws FileNotFoundException If either the source file does not exist or 
     * the source file is a directory or the destination file does not exist.
     * @throws IOException If an IO error occurs
     */
    public void copyFile(boolean append, File src, File dest, boolean rotate) 
            throws FileNotFoundException, IOException {

        if(!src.exists() || !src.isFile()) {
            throw new FileNotFoundException(src.getPath());
        }

// On a server environment where absolute paths often begins with '/' 
// File.isFile will return false for files.        
//        if(!dest.isFile()) {
//            throw new FileNotFoundException(dest.getPath());
//        }

        if(!dest.exists()) {

            if(!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            dest.createNewFile();
        }

        if(!append) {
            if(rotate && dest.exists()) {
                dest = this.rotate(dest);
            }
        }

        FileInputStream fis = null;
        FileOutputStream fos = null;
        try{
            
            fis = new FileInputStream(src);
            fos = new FileOutputStream(dest, append);

            this.copyStream(fis, fos);
            
            fos.flush();
            
        }finally{
            if(fis != null) try{ fis.close(); }catch(IOException e){
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "", e);
            }
            if(fos != null) try{ fos.close(); }catch(IOException e){
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "", e);
            }
        }
    }

    /**
     * Copies all bytes from the input stream to the output stream.
     * Does not close or flush either stream.
     *
     * @param from the input stream to read from
     * @param to the output stream to write to
     * @return the number of bytes copied
     * @throws IOException if an I/O error occurs
     */
    public long copyStream(InputStream from, OutputStream to) throws IOException {
        
        if(from == null || to == null) throw new NullPointerException();
        
        byte[] buf = new byte[this.getBufferSize()];
        
        long total = 0;
        
        while (true) {
            int r = from.read(buf);
            if (r == -1) {
                break;
            }
            to.write(buf, 0, r);
            total += r;
        }
        return total;
    }

    public boolean delete(String path, boolean deleteOnExit) {
        return delete(new File(path), deleteOnExit);
    }

    public boolean delete(File file, boolean deleteOnExit) {

        Set<String> deleted = null;

        try{
            deleted = this.delete(file, null, deleteOnExit);
        }catch(IOException e) {
            // Do nothing
        }

        return deleted != null && !deleted.isEmpty();
    }
    
    public FileFilter getFilterForFilesModifiedAfter(final long time) {
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.lastModified() > time;
            }
        };
        return filter;
    }
    
    public FileFilter getFilterForFilesModifiedBefore(final long time) {
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.lastModified() < time;
            }
        };
        return filter;
    }
    
    public Set<String> deleteFilesModifiedAfter(File file, final Date date)
        throws FileNotFoundException {
        FileFilter filter = this.getFilterForFilesModifiedAfter(date.getTime());
        return this.delete(file, filter, false);
    }

    public Set<String> deleteFilesModifiedBefore(File file, final Date date)
        throws FileNotFoundException {
        FileFilter filter = this.getFilterForFilesModifiedBefore(date.getTime());
        return this.delete(file, filter, false);
    }

    public Set<String> delete(String file, FileFilter filter, boolean deleteOnExit)
            throws FileNotFoundException {

        return delete(new File(file), filter, deleteOnExit);
    }

    public Set<String> delete(File file, FileFilter filter, boolean deleteOnExit)
            throws FileNotFoundException {

        if(!file.exists()) {
            throw new FileNotFoundException(file.getPath());
        }

        Set<String> deleted = new HashSet<String>();

        if(file.isFile()) {

            boolean doDelete = (filter == null) ? true : filter.accept(file);

            if(doDelete) {

                if(file.delete()) {

                    deleted.add(file.getPath());

                }else if(deleteOnExit){

                    file.deleteOnExit();
                }
            }
        }else{

            File [] children = file.listFiles();

            if(children != null && children.length > 0) {
                for(File child:children) {
                    deleted.addAll(this.delete(child, filter, deleteOnExit));
                }
            }else{

                if(file.delete()) {

                    deleted.add(file.getPath());

                }else if(deleteOnExit){

                    file.deleteOnExit();
                }
            }
        }

        return deleted;
    }

    /**
     * @see MyFileIO#renameFile(java.io.File, java.lang.String)
     */
    public boolean rename(String oldPath, String newName) throws IOException {
        return this.rename(new File(oldPath), newName);
    }

    /**
     * <b>Example</b><br/>
     * <code>
     * String oldFile = new File("C:/Users/user/oldName.xml");<br/>
     * String newName = "newName.xml";
     * new FileIO().renameFile(oldFile, newName) returns:<br/>
     * </code>
     * <br/>
     * For the above code, the new path will be: <tt>C:/Users/user/newName.xml</tt>
     * @param oldFile The file to be renamed
     * @param newName The new name to be given the file
     * @return  <code>true</code> if and only if the renaming succeeded;
     *          <code>false</code> otherwise
     * @throws IOException
     *         If the file referenced by the path to be renamed does not exist OR
     *         if the file referenced by the parth to be renamed to does not exist.
     * @throws  SecurityException
     *          If a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
     *          method denies write access to either the old or new pathnames
     * @throws  NullPointerException
     *          If parameter <code>oldFile</code> or <code>newName</code> is <code>null</code>
     */
    public boolean rename(File oldFile, String newName) throws IOException {

        if(oldFile == null || newName == null) {
            throw new NullPointerException();
        }

        if(!oldFile.exists()) {
            throw new FileNotFoundException(oldFile.getPath());
        }

        final String newPath = oldFile.getParent() + "/" + newName;

        LOG.fine(() -> "Old path: "+oldFile+", new path: "+newPath);
        
        File newFile = new File(newPath);
        if(newFile.exists()) {
            String msg = "Cannot rename file:"+oldFile+" to "+newPath+"\n"+
                    "The selected path already exists!";
            throw new IOException(msg);
        }
        return oldFile.renameTo(newFile);
    }

    /**
     * @deprecated
     * @see MyFileIO#renameFile(java.io.File, java.lang.String)
     */
    public boolean renameFile(String oldPath, String newName) throws IOException {
        return this.renameFile(new File(oldPath), newName);
    }

    /**
     * @deprecated
     * <b>Example</b><br/>
     * <code>
     * String oldFile = new File("C/Users/user/oldName.xml");<br/>
     * String newName = "newName";  // don't include the extension.<br/>
     * new FileIO().renameFile(oldFile, newName) returns:<br/>
     * </code>
     * <br/>
     * For the above code, the new path will be: <tt>C/Users/user/newName.xml</tt>
     * @param oldFile The file to be renamed
     * @param newName The new name to be given the file
     * @return  <code>true</code> if and only if the renaming succeeded;
     *          <code>false</code> otherwise
     * @throws IOException
     *         If the file referenced by the path to be renamed does not exist OR
     *         if the file referenced by the parth to be renamed to does not exist.
     * @throws  SecurityException
     *          If a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
     *          method denies write access to either the old or new pathnames
     * @throws  NullPointerException
     *          If parameter <code>oldFile</code> or <code>newName</code> is <code>null</code>
     */
    public boolean renameFile(File oldFile, String newName) throws IOException {

        if(oldFile == null || newName == null) {
            throw new NullPointerException();
        }

        if(!oldFile.exists()) {
            throw new FileNotFoundException(oldFile.getPath());
        }
        String newPath = oldFile.getParent() + "/" + newName;
        if(oldFile.isFile()) {
            String oldfileName = oldFile.getName();
            String extension = oldfileName.substring(oldfileName.lastIndexOf('.')+1);
            newPath += "." + extension;
        }
        
        if(LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Old path: {0}, new path: {1}", new Object[]{oldFile, newPath});
        }
        
        File newFile = new File(newPath);
        if(newFile.exists()) {
            String msg = "Cannot rename file:"+oldFile+" to "+newPath+"\n"+
                    "File already exists!";
            throw new IOException(msg);
        }
        return oldFile.renameTo(newFile);
    }

    public File oldest(File parent) {
        return this.find(parent, null, true);
    }
    public File oldest(File parent, FileFilter filter) {
        return this.find(parent, filter, true);
    }
    public File latest(File parent) {
        return this.find(parent, null, true);
    }
    public File latest(File parent, FileFilter filter) {
        return this.find(parent, filter, true);
    }
    public File earliest(File parent) {
        return this.find(parent, null, false);
    }
    public File earliest(File parent, FileFilter filter) {
        return this.find(parent, filter, false);
    }
    private File find(File parent, FileFilter filter, boolean latest) {

        File file = null;

        File [] children = parent.listFiles();

        if(children == null || children.length == 0) return null;

        for(File child:children) {

            if(filter != null && !filter.accept(child)) continue;

            if(file == null) {
                file = child;
            }else{
                if(latest) {
                    if (child.lastModified() > file.lastModified()) {
                        file = child;
                    }
                }else{
                    if (child.lastModified() < file.lastModified()) {
                        file = child;
                    }
                }
            }
        }
        return file;
    }

    public File rotate(String path) throws IOException {
        return fileOutput.rotate(path);
    }

    public File rotate(File file) throws IOException {
        return fileOutput.rotate(file);
    }

    public FileInput getFileInput() {
        return fileInput;
    }

    public void setFileInput(FileInput fileInput) {
        this.fileInput = fileInput;
    }

    public FileOutput getFileOutput() {
        return fileOutput;
    }

    public void setFileOutput(FileOutput fileOutput) {
        this.fileOutput = fileOutput;
    }

    public int getBufferSize() {
        return this.fileInput.getBufferSize();
    }

    public void setBufferSize(int bufferSize) {
        this.fileInput.setBufferSize(bufferSize);
    }
}//END
/**
 * 
    public void copyFile(boolean append, File src, File dest, boolean rotate) 
            throws FileNotFoundException, IOException {

        if(!src.exists() || !src.isFile()) {
            throw new FileNotFoundException(src.getPath());
        }

// On a server environment where absolute paths often begins with '/' 
// File.isFile will return false for files.        
//        if(!dest.isFile()) {
//            throw new FileNotFoundException(dest.getPath());
//        }

        if(!dest.exists()) {

            if(!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            dest.createNewFile();
        }

        if(!append) {
            if(rotate && dest.exists()) {
                dest = this.rotate(dest);
            }
        }

        FileInputStream fis = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try{
            
            fis = new FileInputStream(src);
            bis = new BufferedInputStream(fis);
            fos = new FileOutputStream(dest, append);
            bos = new BufferedOutputStream(fos);
            
            byte [] arr = new byte[(int)src.length()];
            
            bis.read(arr);
            bos.write(arr);
            
            fos.flush();
            bos.flush();
            
        }finally{
            if(bis != null) try{ bis.close(); }catch(IOException e){
                logger.getLogger(this.getClass().getName()).log(Level.WARNING, "", e);
            }
            if(fis != null) try{ fis.close(); }catch(IOException e){
                logger.getLogger(this.getClass().getName()).log(Level.WARNING, "", e);
            }
            if(bos != null) try{ bos.close(); }catch(IOException e){
                logger.getLogger(this.getClass().getName()).log(Level.WARNING, "", e);
            }
            if(fos != null) try{ fos.close(); }catch(IOException e){
                logger.getLogger(this.getClass().getName()).log(Level.WARNING, "", e);
            }
        }
    }
*/
