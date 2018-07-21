/*
 * Copyright 2018 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bc.util;

import com.bc.functions.FindExceptionInHeirarchy;
import com.bc.util.concurrent.NamedThreadFactory;
import java.io.Serializable;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 6, 2018 11:25:14 PM
 */
public class Retry implements Serializable,
        BiFunction<Callable, Predicate<Throwable>, Optional> {

    private transient static final Logger LOG = Logger.getLogger(Retry.class.getName());

    private final boolean debug;
    
    private final int trials; 
    
    private final int interval; 

    public Retry(int trials, int interval) {
        
        this(false, trials, interval);
    }
    
    public Retry(boolean debug, int trials, int interval) {
        this.debug = debug;
        this.trials = trials;
        this.interval = interval;
    }

    @Override
    public Optional apply(Callable callable, Predicate<Throwable> test) {

        return this.retryIf(callable, test);
    }
        
    public <E extends Throwable > Optional retryOn(Callable callable, Class<E> type) {
        
        final Object result = this.retryOn(callable, type, null);
        
        return Optional.ofNullable(result);
    }

    public <E extends Throwable, T> T retryOn(Callable<T> callable, Class<E> type, T outputIfNone) {
    
        final BiFunction<Throwable, Predicate<Throwable>, Optional<Throwable>> find =
                new FindExceptionInHeirarchy();

        final Predicate<Throwable> test = (e) -> find.apply(e, (t) -> type.isInstance(t)).isPresent();
        
        return this.retryIf(callable, test, trials, outputIfNone);
    }

    public <T> ScheduledFuture<T> retryAsyncIf(Callable<T> callable, Predicate<Throwable> test, long delay) {
        
        return this.retryAsyncIf(callable, test, delay, null);
    }
    
    public <T> Optional<T> retryIf(Callable<T> callable, Predicate<Throwable> test) {

        final T result = this.retryIf(callable, test, null);
        
        return Optional.ofNullable(result);
    }
    
    public <T> ScheduledFuture<T> retryAsyncIf(Callable<T> callable, Predicate<Throwable> test, long delay, T outputIfNone) {
        
        final String poolName = this.getClass().getName() + '_' + callable;
        
        final ScheduledExecutorService svc = Executors
                .newSingleThreadScheduledExecutor(new NamedThreadFactory(poolName));
        
        final Callable<T> asyncTask = () -> {
            try{
                return this.retryIf(callable, test, trials, outputIfNone);
            }finally{
                Util.shutdownAndAwaitTermination(svc, 1, TimeUnit.MILLISECONDS);
            }
        };
        
        return svc.schedule(asyncTask, delay, TimeUnit.MILLISECONDS);
    }

    public <T> T retryIf(Callable<T> callable, Predicate<Throwable> test, T outputIfNone) {
        
        return this.retryIf(callable, test, trials, outputIfNone);
    }
    
    protected <T> T retryIf(Callable<T> callable, Predicate<Throwable> test, int trialsLeft, T outputIfNone) {

        if(trialsLeft < 1) {

            return outputIfNone;
            
        }else{
            
            try{
            
                return callable.call();
            
            }catch(Exception e) {
                
                if(!test.test(e)) {
                    
                    return outputIfNone;
                    
                }else{
                    
                    final String msg = "Retrys left: " + (trialsLeft - 1);
                    
                    if(debug) {
                        LOG.log(Level.WARNING, msg, e);
                    }else{
                        LOG.warning(() -> msg + ", after exception: " + e.toString()); 
                    }
                    
                    synchronized(callable) {
            
                        try{
                        
                            callable.wait(interval);
                        
                        }catch(InterruptedException ie) { 
                        
                            if(debug) {
                                LOG.log(Level.WARNING, "", ie);
                            }else{
                                LOG.fine(() -> ie.toString()); 
                            }
                        }finally{ 
                            
                            callable.notifyAll(); 
                        }
                    }

                    return retryIf(callable, test, trialsLeft - 1, outputIfNone);
                }
            }
        }
    }
}
