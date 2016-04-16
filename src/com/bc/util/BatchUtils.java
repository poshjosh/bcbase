package com.bc.util;

import java.util.logging.Level;

/**
 * @(#)BatchUtils.java   26-Jul-2014 20:19:00
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
public class BatchUtils {
    
    public static int getBatch(int index, int batchSize) {
        if(index < 0) {
            throw new IllegalArgumentException("index < 0");
        }
        if(batchSize <= 0) {
            throw new IllegalArgumentException("batchSize <= 0");
        }
        int output = index / batchSize;
        return output;
    }
    
    public static int getIndexInBatch(int index, int batchSize) {
        if(index < 0) {
            throw new IllegalArgumentException("index < 0");
        }
        if(batchSize <= 0) {
            throw new IllegalArgumentException("batchSize <= 0");
        }
        int output = index % batchSize;
        return output;
    }

    public static int getBatchCount(int batchSize, int size) {
        if(batchSize <= 0) {
            throw new IllegalArgumentException("batchSize <= 0");
        }
        if (size <= 0) {
            return 0;
        }        
        int batchCount = size / batchSize;
        if (size % batchSize > 0) {
            ++batchCount;
        }        
        return batchCount;
    }
    
    public static int getStart(int batch, int batchSize, int size) {
        return getStart(batch, batchSize, size, true, false);
    }
    
    public static int getStart(int batch, int batchSize, int size, 
            boolean forward, boolean firstElementZero) {
        if(forward) {
            return getForwardStart(batch, batchSize, size, firstElementZero);
        }else{
            return getReverseStart(batch, batchSize, size, firstElementZero);
        }
    }

    public static int getEnd(int batch, int batchSize, int size) {
        return getEnd(batch, batchSize, size, true, false);
    }
    
    public static int getEnd(int batch, int batchSize, int size, 
            boolean forward, boolean firstElementZero) {
        if (forward) {
            return getForwardEnd(batch, batchSize, size, firstElementZero);
        }else{
            return getReverseEnd(batch, batchSize, size, firstElementZero);
        }    
    }

    private static int getForwardStart(int batch, int batchSize, 
            int size, boolean firstElementZero) {
        final int batchCount = getBatchCount(batchSize, size);
XLogger.getInstance().log(Level.FINER, "Batch: {0}, batchSize: {1}, size: {2}. batchCount: {3}", 
    BatchUtils.class, batch, batchSize, size, batchCount);
        if (size <= 0) {
            return 0;
        }    
        if (batch < 0) {
            throw new IndexOutOfBoundsException("Batch: "+batch+" is less than 0");
        }    
        if (batch >= batchCount) {
            throw new IndexOutOfBoundsException("Batch: "+batch+" is greater than batchCount: "+batchCount);
        }        
        int batchStart = (batch * batchSize) + (firstElementZero?0:1);
XLogger.getInstance().log(Level.FINER, "Forward. batch start: {0}.", 
        BatchUtils.class, batchStart);
        return batchStart;
    }

    private static int getReverseStart(int batch, int batchSize, 
            int size, boolean firstElementZero) {
XLogger.getInstance().log(Level.FINER, "Batch: {0}, batchSize: {1}, size: {2}.", 
    BatchUtils.class, batch, batchSize, size);
        int batchStart = getForwardStart(batch, batchSize, size, firstElementZero);
        int reverseStart = (size - batchStart) + (firstElementZero?0:1);
XLogger.getInstance().log(Level.FINER, "Reverse. batch start: {0}.", 
        BatchUtils.class, reverseStart);
        return reverseStart;
    }

    private static int getForwardEnd(int batch, int batchSize, 
            int size, boolean firstElementZero) {
        
        int start = getStart(batch, batchSize, size, true, firstElementZero) - (firstElementZero?0:1);
        
XLogger.getInstance().log(Level.FINER, "Start: {0}, batchSize: {1}, size: {2}.", 
    BatchUtils.class, start, batchSize, size);
        if (size <= 0) {
            return 0;
        } else {
            int forwardEnd = start + batchSize;
            forwardEnd = forwardEnd <= size ? forwardEnd : size;
XLogger.getInstance().log(Level.FINER, 
"Forward. batch end: {0}.", BatchUtils.class, forwardEnd);
            return forwardEnd;
        }
    }

    private static int getReverseEnd(int batch, int batchSize, 
            int size, boolean firstElementZero) {
        
        int start = getStart(batch, batchSize, size, false, firstElementZero) + (firstElementZero?0:1);
        
XLogger.getInstance().log(Level.FINER, 
"Forward: {0}. Start: {1}, batchSize: {2}, size: {3}.", 
BatchUtils.class, start, batchSize, size);

        if (size <= 0) {
            return 0;
        } else {
            int reverseEnd = start - batchSize;
            reverseEnd = reverseEnd >= 0 ? reverseEnd : 0;
XLogger.getInstance().log(Level.FINER, 
"Reverse. batch end: {0}.", BatchUtils.class, reverseEnd);
            return reverseEnd;
        }
    }
}
