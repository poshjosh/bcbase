package com.bc.net;

import com.bc.functions.FindExceptionInHeirarchy;
import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
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

    private static transient final Logger logger = 
            Logger.getLogger(RetryConnectionFilter.class.getName());

    private final boolean retryNoConnectionException;
    private final boolean retryFailedConnectionException;
    private final int maxRetrials;
    private final long retrialInterval;
    
    private final BiFunction<Throwable, Predicate<Throwable>, Optional<Throwable>> findExceptionInChain;
    
    private int retrials;
    
    public RetryConnectionFilter(int maxRetrials, long retrialInterval) {
        this(true, true, maxRetrials, retrialInterval);
    }

    public RetryConnectionFilter(RetryConnectionFilter filter) {
        this(filter.retryNoConnectionException, 
                filter.retryFailedConnectionException,
                filter.maxRetrials, filter.retrialInterval);
    }

    public RetryConnectionFilter(
            boolean retryNoConnectionException, 
            boolean retryFailedConnectionException, 
            int maxRetrials, long retrialInterval) {
        this.retryNoConnectionException = retryNoConnectionException;
        this.retryFailedConnectionException = retryFailedConnectionException;
        this.maxRetrials = maxRetrials;
        this.retrialInterval = retrialInterval;
        this.findExceptionInChain = new FindExceptionInHeirarchy();
    }
    
    public RetryConnectionFilter copy() {
        return new RetryConnectionFilter(this);
    }

    public synchronized boolean accept(Throwable t) {
        return this.test(t);
    }
    
    @Override
    public synchronized boolean test(Throwable t) {
        return this.acceptFailedConnectionException(t) || this.acceptNoConnectionException(t);
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
        
        return this.findExceptionInChain.apply(t, new FailedConnectionTest()).isPresent();
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
        return this.findExceptionInChain.apply(t, (e) -> e instanceof UnknownHostException).isPresent();
    }
    
    protected synchronized boolean handleException(Throwable t) {
        
        ++retrials;

        try{
            this.wait(this.retrialInterval);
        }catch(InterruptedException ie) { 
            logger.fine(() -> ie.toString()); 
        }finally{ 
            this.notifyAll(); 
        }

        return this.hasMoreTrials();
    }
    
    public boolean hasMoreTrials() {
        return this.retrials < maxRetrials;
    }

    public long getSleepTime() {
        return retrialInterval;
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
