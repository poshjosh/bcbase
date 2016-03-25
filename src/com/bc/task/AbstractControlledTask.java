package com.bc.task;

import com.bc.util.XLogger;
import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;


/**
 * @(#)AbstractControlledTask.java   30-May-2015 04:08:04
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
public abstract class AbstractControlledTask implements Serializable, ControlledTask {

    private int offset = 0;
    
    private int lastoffset;
    
    private int batchSize = 100;
    
    private long batchInterval = 60000;
    
    private int max = -1; // -1 implies no limit
    
    private Level logLevel;
    
    private final Serializable lock = new Serializable() {};
    
    private volatile boolean stopRequested;
    
    private volatile boolean running;
    
    private int total;
    
    public AbstractControlledTask() {
        logLevel = Level.FINE;
    }
    
    protected abstract int execute(int offset) throws Exception;
    
    @Override
    public int getTotal() {
        return total;
    }
    
    @Override
    public void start() {
this.log(". ... ... ...Starting");
        this.startAt(offset);
    }
    
    @Override
    public void resume() {
this.log(". ... ... ...Resuming");
        this.startAt(lastoffset);
    }

    @Override
    public void startAt(int off) {
        if(this.isRunning()) {
            throw new UnsupportedOperationException();
        }
        stopRequested = false;
        int transfered = 0;
        try{
            transfered = this.exec0();
        }catch(Exception e) {
this.logError("Error executing", e);
        }finally{
            lastoffset += transfered;
        }
    }

    @Override
    public boolean stop() {
this.log(". ... ... ...Stopping");
        if(!this.isRunning()) {
            return false;
        }else{
            synchronized(lock) {
                stopRequested = true;
                lock.notifyAll();
            }
            return true;
        }
    }
    
    private int exec0() throws Exception {
        stopRequested = false;
        running = true;
        int executed = 0;
        try{
            executed = this.exec1();
this.log(". ... ... ...Completed execution: "+executed);
        }catch(Exception e) {
this.logError("Error executing", e);
        }finally{
            running = false;
            lastoffset += executed;
this.log(this.getClass().getName()+". ... ... ...Exiting");
        }
        return executed;
    }
    
    private int exec1() throws Exception {
        
        this.total = 0;
        
        int batch;
        
        do{
            
            if(stopRequested) {
                break;
            }
            
            batch = this.execute(offset + total);
            
            total += batch;
log("Executed so far: "+total);                    

            if(batch < batchSize || (max != -1 && total >= max)) {
                break;
            }

            synchronized(lock) {
                try{
log("Waiting: "+new Date());                    
                    lock.wait(batchInterval);
log("Done waiting: "+new Date());                    
                }finally{
                    lock.notifyAll();
                }
            }
            
        }while(true);
        
        return total;
    }
    
    public void log(String msg) {
XLogger.getInstance().log(logLevel, "{0}", this.getClass(), msg);
    }
    
    public void logError(String msg, Exception e) {
XLogger.getInstance().log(Level.WARNING, msg, this.getClass(), e);
    }

    public Level getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(Level logLevel) {
        this.logLevel = logLevel;
    }
    
    @Override
    public boolean isRunning() {
        return running;
    }
    
    @Override
    public int getLastoffset() {
        return lastoffset;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public int getBatchSize() {
        return batchSize;
    }

    @Override
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    @Override
    public long getBatchInterval() {
        return batchInterval;
    }

    @Override
    public void setBatchInterval(long batchInterval) {
        this.batchInterval = batchInterval;
    }

    @Override
    public int getMax() {
        return max;
    }

    @Override
    public void setMax(int max) {
        this.max = max;
    }

    @Override
    public boolean isStopRequested() {
        return stopRequested;
    }
}
