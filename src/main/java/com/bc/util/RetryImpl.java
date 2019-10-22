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
import java.util.Objects;
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
public class RetryImpl implements Serializable, Retry {

    private transient static final Logger LOG = Logger.getLogger(RetryImpl.class.getName());

    private final int trials; 
    
    private final int interval; 
    
    private final TimeUnit timeUnit;

    public RetryImpl(int trials, int interval, TimeUnit timeUnit) {
        this.trials = trials;
        this.interval = interval;
        this.timeUnit = Objects.requireNonNull(timeUnit);
    }
        
    @Override
    public <E extends Throwable, T> Optional<T> retryOn(Callable<T> callable, Class<E> type) {
        
        final T result = this.retryOn(callable, type, null);
        
        return Optional.ofNullable(result);
    }

    @Override
    public <E extends Throwable, T> T retryOn(Callable<T> callable, Class<E> type, T outputIfNone) {
    
        final BiFunction<Throwable, Predicate<Throwable>, Optional<Throwable>> find =
                new FindExceptionInHeirarchy();

        final Predicate<Throwable> test = (e) -> find.apply(e, (t) -> type.isInstance(t)).isPresent();
        
        return this.retryIf(callable, test, trials, outputIfNone);
    }

    @Override
    public <T> Optional<ScheduledFuture<T>> retryAsyncIf(Callable<T> callable, Predicate<Throwable> test, long delay) {
        
        final ScheduledFuture<T> result = this.retryAsyncIf(callable, test, delay, null);
        
        return Optional.ofNullable(result);
    }
    
    @Override
    public <T> ScheduledFuture<T> retryAsyncIf(Callable<T> callable, Predicate<Throwable> test, long delay, T outputIfNone) {
        
        final String poolName = this.getClass().getName() + '_' + callable.getClass().getName();
        
        final ScheduledExecutorService svc = Executors
                .newSingleThreadScheduledExecutor(new NamedThreadFactory(poolName));
        
        final Callable<T> asyncTask = () -> {
            try{
                return this.retryIf(callable, test, trials, outputIfNone);
            }finally{
                try{
                    Util.shutdownAndAwaitTermination(svc, 100, TimeUnit.MILLISECONDS);
                }catch(RuntimeException e) {
                    LOG.warning(() -> "Exception ecountered shutting down: ScheduledExecutorService named: " + 
                            poolName + '\n' + e.toString());
                }
            }
        };
        
        final ScheduledFuture<T> result = svc.schedule(asyncTask, delay, TimeUnit.MILLISECONDS);

        if( ! svc.isShutdown()) {
            svc.shutdown();
        }
        
        return result;
    }

    @Override
    public <T> Optional<T> retryIf(Callable<T> callable, Predicate<Throwable> test) {

        final T result = this.retryIf(callable, test, null);
        
        return Optional.ofNullable(result);
    }
    
    @Override
    public <T> T retryIf(Callable<T> callable, Predicate<Throwable> test, T outputIfNone) {
        
        return this.retryIf(callable, test, trials, outputIfNone);
    }
    
    protected <T> T retryIf(Callable<T> callable, Predicate<Throwable> test, int trialsLeft, T resultIfNone) {

        T result;
        
        if(trialsLeft < 1) {

            result  = null;
            
        }else{
            
            try{
            
                result = callable.call();
            
            }catch(Exception e) {
                
                final boolean passed = test.test(e);
                
                LOG.fine(() -> "Retry Test passed: " + passed + " by: " + e);
                
                if( ! passed) {
                    
                    result = null;
                    
                }else{
                    
                    final String msg = "Retrys left: " + (trialsLeft - 1);
                    
                    if(LOG.isLoggable(Level.FINE)) {
                        LOG.log(Level.WARNING, msg, e);
                    }else{
                        LOG.warning(() -> msg + ", after exception: " + e.toString()); 
                    }
                    
                    this.wait(callable);
                    
                    result = retryIf(callable, test, trialsLeft - 1, resultIfNone);
                }
            }
        }
        
        LOG.log(Level.FINER, "Result: {0}", result);
        
        return result == null ? resultIfNone : result;
    }
    
    private void wait(Callable callable) {
        
        if(interval > 0) {

            synchronized(callable) {

                try{

                    callable.wait(timeUnit.toMillis(interval));

                }catch(InterruptedException ie) { 

                    final String msg = "Interrupted while waiting between retrys.";
                    
                    if(LOG.isLoggable(Level.FINE)) {
                        LOG.log(Level.WARNING, msg, ie);
                    }else{
                        LOG.fine(() -> msg + " " + ie); 
                    }
                }finally{ 

                    callable.notifyAll(); 
                }
            }
        }
    }
}
