package com.bc.io;

import java.nio.file.Paths;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;

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
public class IOWrapper<K> extends ObjectIO<String> {

    private transient static final Logger LOG = Logger.getLogger(IOWrapper.class.getName());

    private K target;
    
    private String filename;

    public IOWrapper() { 
        this((name) -> Paths.get(System.getProperty("java.io.tmpdir"), name).toString());
    }
    
    public IOWrapper(UnaryOperator<String> getPathForName) { 
        this(null, null, getPathForName);
    }
    
    public IOWrapper(K target, String filename, UnaryOperator<String> getPathForName) {
        super(getPathForName);
        this.target = target;
        this.filename = filename;
    }

    public K getTarget() {
        if(this.getFilename() == null) {
            throw new NullPointerException();
        }
        if(target == null) {
            target = (K)this.loadNamedObject(this.getFilename());
        }
        LOG.log(Level.FINEST, "Target:\n{0}", target);
        return target;
    }

    public void setTarget(K target) {
        if(this.getFilename() == null) {
            throw new NullPointerException();
        }
        this.target = target;
        LOG.log(Level.FINEST, "Target:\n{0}", target);
        if(target == null) {
            boolean deleteOnExit = false;
            // We don't delete on exit because another instance
            // may be written to the same location, which will
            // subsequently be deleted on exit
            this.deleteNamedObject(this.getFilename(), deleteOnExit);
        }else{
            this.saveNamedObject(this.getFilename(), target);
        }
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
