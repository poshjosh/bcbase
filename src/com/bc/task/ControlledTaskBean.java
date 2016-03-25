package com.bc.task;

import java.io.Serializable;


/**
 * @(#)ControlledTaskBean.java   30-May-2015 04:22:26
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
public abstract class ControlledTaskBean extends AbstractControlledTask implements Serializable {

    private final int maxMessageBufferLen;
    private final StringBuilder messages;
  
    public ControlledTaskBean()
    {
        this.maxMessageBufferLen = 10000;
        this.messages = new StringBuilder(this.maxMessageBufferLen + this.maxMessageBufferLen / 10);
    }
    
    public void setStart(boolean start) {
        if(start) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    ControlledTaskBean.this.start();
                }
            };
            t.start();
        }
    }
    
    public void setResume(boolean resume) {
        if(resume) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    ControlledTaskBean.this.resume();
                }
            };
            t.start();
        }
    }
    
    public void setStop(boolean stop) {
        if(stop) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    ControlledTaskBean.this.stop();
                }
            };
            t.start();
        }
    }
    
    public String getMessages() {
        String output;
        synchronized(messages) {
            output = messages.toString();
            messages.setLength(0);
        }
        return output;
    }
    
    @Override
    public void log(String msg) {
        super.log(msg);
        this.doLog(msg);
    }
    @Override
    public void logError(String msg, Exception e) {
        super.logError(msg, e);
        this.doLog(msg.replace("\n", "<br/>\n"));
        this.doLog(e.toString());
    }
    
    public void doLog(String msg) {
        synchronized(messages) {
            if(messages.length() >= maxMessageBufferLen) {
                messages.setLength(maxMessageBufferLen/2);
            }
            messages.append(msg).append("<br/>\n");
        }
    }
}
