package com.bc.io;


/**
 * @(#)IOWrapper.java   10-Apr-2015 17:19:03
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
public abstract class IOWrapper<K> extends ObjectIO {

    private K target;
    
    private String filename;
    
    public IOWrapper() { }
    
    public IOWrapper(K target, String filename) {
        this.target = target;
        this.filename = filename;
    }

    public K getTarget() {
        if(this.getFilename() == null) {
            throw new NullPointerException();
        }
        if(target == null) {
            target = (K)this.loadMyObject(this.getFilename());
        }
//XLogger.getInstance().log(Level.FINER, "getTarget: {0}", this.getClass(), target);
        return target;
    }

    public void setTarget(K target) {
        if(this.getFilename() == null) {
            throw new NullPointerException();
        }
        this.target = target;
//XLogger.getInstance().log(Level.FINER, "setTarget: {0}", this.getClass(), target);
        if(target == null) {
            boolean deleteOnExit = false;
            // We don't delete on exit because another instance
            // may be written to the same location, which will
            // subsequently be deleted on exit
            this.deleteMyObject(this.getFilename(), deleteOnExit);
        }else{
            this.saveMyObject(this.getFilename(), target);
        }
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
