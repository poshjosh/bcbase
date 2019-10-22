package com.bc.net;

import com.bc.functions.FindExceptionInHeirarchy;
import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class RetryConnectionFilter implements Predicate<Throwable>, Serializable {

    private static transient final Logger LOG = Logger.getLogger(RetryConnectionFilter.class.getName());

    private final boolean retryNoConnectionException;
    private final boolean retryFailedConnectionException;
    private final int maxRetrials;
    private final long intervalBetweenTrials;
    
    private final FindExceptionInHeirarchy findExceptionInChain;
    private final Predicate<Throwable> noConnectionTest;
    private final Predicate<Throwable> failedConnectionTest;
    
    private int retrials;
    
    public RetryConnectionFilter(int maxRetrials, long retrialInterval) {
        this(true, true, maxRetrials, retrialInterval);
    }

    public RetryConnectionFilter(RetryConnectionFilter filter) {
        this(filter.retryNoConnectionException, 
                filter.retryFailedConnectionException,
                filter.maxRetrials, filter.intervalBetweenTrials);
    }

    public RetryConnectionFilter(
            boolean retryNoConnectionException, 
            boolean retryFailedConnectionException, 
            int maxRetrials, long retrialInterval) {
        this.retryNoConnectionException = retryNoConnectionException;
        this.retryFailedConnectionException = retryFailedConnectionException;
        this.maxRetrials = maxRetrials;
        this.intervalBetweenTrials = retrialInterval;
        this.findExceptionInChain = new FindExceptionInHeirarchy();
        this.noConnectionTest = (t) -> t instanceof UnknownHostException; 
        this.failedConnectionTest = new FailedConnectionTest();
    }
    
    public RetryConnectionFilter reset() {
        retrials = 0;
        return this;
    }
    
    public RetryConnectionFilter copy() {
        return new RetryConnectionFilter(this);
    }

    public synchronized boolean accept(Throwable t) {
        return this.test(t);
    }
    
    @Override
    public synchronized boolean test(Throwable t) {
        
        if(retrials > 0) {
            try{
                this.wait(this.intervalBetweenTrials);
            }catch(InterruptedException ie) { 
                LOG.warning(ie.toString()); 
                LOG.log(Level.FINE, null, ie);
            }finally{ 
                this.notifyAll(); 
            }
        }

        try{
            final boolean result = this.hasMoreTrials() && 
                    (this.acceptFailedConnectionException(t) || this.acceptNoConnectionException(t));

            return result;
        }finally{
            ++retrials;
        }
    }

    /**
     * If {@link java.net.SocketTimeoutException} or
     * {@link java.net.ConnectException} is thrown, or the stream is closed for
     * any reason, while content is being read, then the 
     * {@link IOException IOException} which is thrown is caught and the process
     * repeated till all the data is read or any other
     * {@link IOException IOException} is thrown.
     * @param t The Exception to Filter (i.e accept or decline)
     * @return true if the Exception was accepted, false otherwise
     */
    protected synchronized boolean acceptFailedConnectionException(Throwable t) {

        if(!this.retryFailedConnectionException) return false;
        
        return this.findExceptionInChain.apply(t, this.failedConnectionTest, null) != null;
    }

    /**
     * Accepts {@link java.net.UnknownHostException} if 
     * {@linkplain #retryNoConnectionException} is true.
     * @param t The Exception to Filter (i.e accept or decline)
     * @return true if the Exception was accepted, false otherwise
     */
    protected synchronized boolean acceptNoConnectionException(Throwable t) {

        if(!this.retryNoConnectionException) return false;
        
        // UnknownHostException usually signals that the internet connection
        // has been lost
        return this.findExceptionInChain.apply(t, this.noConnectionTest, null) != null;
    }
    
    public boolean hasMoreTrials() {
        return this.retrials < maxRetrials;
    }

    public long getIntervalBetweenTrials() {
        return intervalBetweenTrials;
    }

    public boolean isRetryFailedConnectionException() {
        return retryFailedConnectionException;
    }

    public boolean isRetryNoConnectionException() {
        return retryNoConnectionException;
    }

    public int getMaxTrials() {
        return maxRetrials;
    }

    public int getTrials() {
        return retrials;
    }
}//END
