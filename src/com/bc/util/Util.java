package com.bc.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;


/**
 * @(#)Util.java   11-Apr-2015 07:02:12
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
public class Util {
    
  
  public final static String removeNonBasicMultilingualPlaneChars(String test) {
      
    StringBuilder sb = new StringBuilder(test.length());
    
    for (int ii = 0; ii < test.length(); ) {
          
       int codePoint = test.codePointAt(ii);
       
       if (codePoint > 0xFFFF) {
         ii += Character.charCount(codePoint);
       }else {
         sb.appendCodePoint(codePoint);
         ii++;
       }
    }
    
    return sb.toString();
  }

  /**
     * @param path The path for which a relative path will be returned
     * @param basePath The base path of the path for which a relative path will be returned
     * @return A relative path 
     * @throws IllegalArgumentException If the second method argument 
     * (<code>path</code>) does not start with the first (<code>basePath</code>).
     */
    public static String getRelativePath(String basePath, String path) throws IllegalArgumentException {

        path = normalize(path);
        basePath = normalize(basePath);
        
// When deployed real path starts with '/'
//
//        if(absolutePath.charAt(0) == '/') {
//            throw new IllegalArgumentException();
//        }

        int index = path.indexOf(basePath);
        
        if(index < 0) throw new IllegalArgumentException("Input must start with: "+basePath+", Found: "+path);

        String output = path.substring(index + basePath.length());
//Logger.getLogger(this.getClass().getName()).info("Input: "+absolutePath+
//        "\nOutput: "+output);
        return output;
    }
    
    private static String normalize(String path) {
        return path.replace('\\', '/').trim();
    }

    /**
     * @param url 
     * @return The base URL of the method argument or <code>null</code> if the 
     * method argument is a malformed URL
     */
    public static String getBaseURL(String url) {
        try{
            return getBaseURL(new URL(url));
        }catch(MalformedURLException e) {
            return null;
        }
    }
    
    public static String getBaseURL(URL url) {
        return url.getProtocol() + "://" + url.getHost();
    }

    /**
     * @param path The path whose file name extension will be returned
     * @return The file name extension of the input path. <br/><br/>
     * For a path of <tt>/folder/folder/file.xyz</tt> returns <tt>xyz</tt>
     */
    public static String getExtension(String path) {
//        This doesn't work for filenames with ':'. e.g http://
//        String ext = Paths.get(path).getFileName().toString();
        int n = path.lastIndexOf('.', path.length()-1); 
        
        String ext = n == -1 ? null : path.substring(n + 1);
        
        return ext;
    }
    
    /**
     * Returns an <code>int</code> value with a positive sign, greater 
     * than or equal to <code>0</code> and less than input <code>size</code>. 
     * Returned values are chosen pseudorandomly with (approximately) 
     * uniform distribution from that range. 
     * 
     * <p>When this method is first called, it creates a single new
     * pseudorandom-number generator, exactly as if by the expression
     * <blockquote><pre>new java.util.Random</pre></blockquote> This
     * new pseudorandom-number generator is used thereafter for all
     * calls to this method and is used nowhere else.
     * 
     * <p>This method is properly synchronized to allow correct use by
     * more than one thread. However, if many threads need to generate
     * pseudorandom numbers at a great rate, it may reduce contention
     * for each thread to have its own pseudorandom-number generator.
     *  
     * @param size The returned int will be of range: 0 - <tt>size</tt>
     * @return  a pseudorandom <code>int</code> greater than or equal 
     * to <code>0</code> and less than the input<code>size</code>.
     * @see     java.lang.Math#random()
     */
    public static int randomInt(int size) {
        
        double random = Math.random();

        double numbr = (random * size);

        return (int)Math.floor(numbr);
    }
    
    public static boolean removeNulls(Collection c) {
    
        if(c == null || c.isEmpty()) {
            return false;
        }
        
        // Collection.remove(null) will only remove the first null value
        // So we use Collection.removeAll(Collection c);
        //
        ArrayList nullList = new ArrayList(1);
        nullList.add(null);

        boolean success = c.removeAll(nullList); 
        
XLogger.getInstance().log(Level.FINER, "AFTER REMOVING null values: {0}", Util.class, c);

        return success;
    }
    
    /**
     * @return true if the ExecutorService terminated correctly, false otherwise
     * @see com.bc.process.ProcessManager#shutdownAndAwaitTermination(java.util.concurrent.ExecutorService, long, java.util.concurrent.TimeUnit, java.util.List) 
     * @see java.util.concurrent.ExecutorService
     */
    public static boolean shutdownAndAwaitTermination(
            ExecutorService pool, long timeout, TimeUnit unit) {
        
        return shutdownAndAwaitTermination(pool, timeout, unit, null);
    }
    
    /**
     * Shuts down an ExecutorService in two phases, first by calling 
     * shutdown to reject incoming tasks, and then calling shutdownNow,
     * if necessary, to cancel any lingering tasks.
     * <br/><br/>
     * <b>Note:</b> This method was culled from the ExecutorService documentation
     * @return true if the ExecutorService terminated correctly, false otherwise
     * @see java.util.concurrent.ExecutorService
     */
    public static boolean shutdownAndAwaitTermination(
            ExecutorService pool, long timeout, 
            TimeUnit unit, List<Runnable> addInterruptedHere)  {

        if(!pool.isShutdown()) {
            pool.shutdown(); // Disable new tasks from being submitted
        }

        return awaitTermination(pool, timeout, unit, addInterruptedHere);
    }

    /**
     * <b>Note:</b> This method was culled from the ExecutorService documentation
     * @return true if the ExecutorService terminated correctly, false otherwise
     * @see java.util.concurrent.ExecutorService
     */
    public static boolean awaitTermination(
            ExecutorService pool, long timeout, TimeUnit unit)  {
        
        return awaitTermination(pool, timeout, unit, null);
    }
    /**
     * <b>Note:</b> This method was culled from the ExecutorService documentation
     * @return true if the ExecutorService terminated correctly, false otherwise
     * @see java.util.concurrent.ExecutorService
     */
    public static boolean awaitTermination(
            ExecutorService pool, long timeout, 
            TimeUnit unit, List<Runnable> addInterruptedHere)  {
        
        if(timeout <= 0) {
            timeout = 1;
        }

        try {
             
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(timeout, unit)) {
                 
                List interrupted = pool.shutdownNow(); // Cancel currently executing tasks
                
                if(addInterruptedHere != null && 
                        (interrupted != null && !interrupted.isEmpty())) {
                    addInterruptedHere.addAll(interrupted);
                }
               
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(timeout, unit)) { 
                    return false;
                }    
             }
        } catch (InterruptedException ie) {
            
            // (Re-)Cancel if current thread also interrupted
            List interrupted = pool.shutdownNow();
            
            if(addInterruptedHere != null && 
                    (interrupted != null && !interrupted.isEmpty())) {
                addInterruptedHere.addAll(interrupted);
            }
            
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
         
        return true;
    }
}
