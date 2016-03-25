package com.bc.net;

import com.bc.util.XLogger;
import java.io.IOException;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.logging.Level;
/**
 * @(#)RetryConnectionFilter.java   04-Apr-2013 20:05:18
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
public class RetryConnectionFilter 
        implements Serializable {

    private boolean retryNoConnectionException;
    private boolean retryFailedConnectionException;
    private int retrials;
    private int maxRetrials;
    private long retrialInterval;
    
    public RetryConnectionFilter() {
        this.retryNoConnectionException = true;
        this.retryFailedConnectionException = true;
        this.maxRetrials = Integer.MAX_VALUE;
        this.retrialInterval = 5000;
    }

    public RetryConnectionFilter(int maxRetrials, long retrialInterval) {
        this.retryNoConnectionException = true;
        this.retryFailedConnectionException = true;
        this.maxRetrials = maxRetrials;
        this.retrialInterval = retrialInterval;
    }
    
    public void reset() {
        retrials = 0;
    }
    
    public synchronized boolean accept(Throwable e) {
        return this.acceptFailedConnectionException(e) || this.acceptNoConnectionException(e);
    }

    /**
     * If {@link java.net.SocketTimeoutException} or
     * {@link java.net.ConnectException} is thrown, or the stream is closed for
     * any reason, while content is being read, then the 
     * {@link IOException IOException} which is thrown is caught and the process
     * repeated till all the data is read or any other
     * {@link IOException IOException} is thrown.
     * @param e The Exception to Filter (i.e accept or decline)
     * @return true if the Exception was accepted, false otherwise
     */
    protected synchronized boolean acceptFailedConnectionException(Throwable e) {

        if(!this.retryFailedConnectionException) return false;
        
        if(e instanceof SocketTimeoutException || e instanceof ConnectException
                || e instanceof NoRouteToHostException) {

            ++retrials;
            
            try{this.wait(this.retrialInterval);}catch(InterruptedException ie)
            { XLogger.getInstance().log(Level.FINE, "{0}", this.getClass(), ie); }
            finally{ this.notifyAll(); }

            return this.hasMoreTrials();

        }else{

            String exceptionMsg = e.getMessage();

            if (exceptionMsg != null){
                
                if(exceptionMsg.contains("is closed") | exceptionMsg.contains("IS CLOSED") |
                        exceptionMsg.contains("Connection reset")) { ///// Added recently /////

                    ++retrials;
                    
                    try{this.wait(this.retrialInterval);}catch(InterruptedException ie)
                    { XLogger.getInstance().log(Level.FINE, "{0}", this.getClass(), ie); }
                    finally{ this.notifyAll(); }

                    return this.hasMoreTrials();
                }
            }
        }

        return false;
    }

    /**
     * Accepts {@link java.net.UnknownHostException} if 
     * {@linkplain #retryNoConnectionException} is true.
     * @param e The Exception to Filter (i.e accept or decline)
     * @return true if the Exception was accepted, false otherwise
     */
    protected synchronized boolean acceptNoConnectionException(Throwable e) {

        if(!this.retryNoConnectionException) return false;
        
        // UnknownHostException usually signals that the internet connection
        // has been lost
        if (e instanceof UnknownHostException) {

            ++retrials;
            
            try{this.wait(this.retrialInterval);}catch(InterruptedException ie)
            { XLogger.getInstance().log(Level.FINE, "{0}", this.getClass(), ie); }
            finally{ this.notifyAll(); }

            return this.hasMoreTrials();

        }else{

            return false;
        }
    }
    
    public boolean hasMoreTrials() {
        return this.retrials < maxRetrials;
    }

    public long getSleepTime() {
        return retrialInterval;
    }

    public void setSleepTime(long sleepTime) {
        this.retrialInterval = sleepTime;
    }

    public boolean isRetryFailedConnectionException() {
        return retryFailedConnectionException;
    }

    public void setRetryFailedConnectionException(boolean retryFailedConnectionException) {
        this.retryFailedConnectionException = retryFailedConnectionException;
    }

    public boolean isRetryNoConnectionException() {
        return retryNoConnectionException;
    }

    public void setRetryNoConnectionException(boolean retryNoConnectionException) {
        this.retryNoConnectionException = retryNoConnectionException;
    }

    public int getMaxTrials() {
        return maxRetrials;
    }

    public void setMaxTrials(int maxTrials) {
        this.maxRetrials = maxTrials;
    }

    public int getTrials() {
        return retrials;
    }

    public void setTrials(int trials) {
        this.retrials = trials;
    }
}//END
