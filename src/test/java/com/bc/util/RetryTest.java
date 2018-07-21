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

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Josh
 */
public class RetryTest {
    
    private final boolean debug = false;
    private final int interval = 2_000;
    private final long delay = interval;
    private final int max = 2;
    
    public RetryTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of apply method, of class Retry.
     */
//    @Test
    public void testApply() {
        System.out.println("apply");
    }

    /**
     * Test of retryOn method, of class Retry.
     */
//    @Test
    public void testRetryOn_Callable_Class() {
        System.out.println("retryOn(Callable, Class)");
    }

    /**
     * Test of retryOn method, of class Retry.
     */
    @Test
    public void testRetryOn_3args() {
        System.out.println("retryOn(Callable, Class, int)");
        final int retryLimit = 3;
        final boolean shouldReturn = true;
        final int failLimit = shouldReturn ? retryLimit - 1 : retryLimit;
        final Retry instance = new Retry(retryLimit, 2_000);
        instance.retryOn(this.getFailingTask("IOEx", IOException.class, failLimit), IOException.class);
        instance.retryOn(this.getFailingTask("SQLEx", SQLException.class, failLimit), IOException.class);
    }

    /**
     * Test of retryAsyncIf method, of class Retry.
     */
//    @Test
    public void testRetryAsyncIf_3args() {
        this.testRetryAsyncIf_3args("A", true);
        this.testRetryAsyncIf_3args("B", false);
    }

    public void testRetryAsyncIf_3args(Object id, boolean failAll) {
        System.out.println("retryAsyncIf(Callable, Predicate, long)");
        final int limit = failAll ? max : max -1 ;
        final Retry instance = new Retry(debug, max, interval);
        final Integer outputIfNone = Integer.MIN_VALUE;
        final Integer expResult = failAll ? outputIfNone : limit;
        final Callable<Integer> callable = this.getFailingTask(id, IOException.class, limit);
        final ScheduledFuture<Integer> future = instance.retryAsyncIf(callable, (e) -> true, delay, outputIfNone);
        try{
            final Integer result = future.get(delay * (max + 1), TimeUnit.MILLISECONDS);
            assertEquals(expResult, result);
        }catch(Exception e) {
            System.out.println(e.toString());
        }
    }

    /**
     * Test of retryIf method, of class Retry.
     */
//    @Test
    public void testRetryIf_3args() {
        this.testRetryIf_3args(true);
        this.testRetryIf_3args(false);
    }

    public void testRetryIf_3args(boolean failAll) {
        System.out.println("retryIf(Callable, Predicate, <T>)");
        final int limit = failAll ? max : max -1 ;
        final Retry instance = new Retry(debug, max, interval);
        final Integer outputIfNone = Integer.MIN_VALUE;
        final Integer expResult = failAll ? outputIfNone : limit;
        final Callable<Integer> callable = this.getFailingTask(limit);
        final Integer result = instance.retryIf(callable, (e) -> true, outputIfNone);
        assertEquals(expResult, result);
    }
    
    public void log(Object msg) {
        System.out.println(LocalDateTime.now() + ". " + msg + "\t@"+this.getClass());
    }

    public Callable<Integer> getFailingTask(int limit) {
        return this.getFailingTask("", IOException.class, limit);
    }
    
    public <E extends Exception> Callable<Integer> getFailingTask(Object id, Class<E> exType, int limit) {
        final AtomicInteger pos = new AtomicInteger(); 
        final Callable<Integer> callable = () -> {
            final int i = pos.get();
            log("(" + id + ")Retry. Offset: " + i);
            pos.incrementAndGet();
            if(i < limit) {
                throw exType.getConstructor().newInstance();
            }else{
                log("(" + id + ")Retry. RETURNING result: " + i);
                return i;
            }
        };
        return callable;
    }
}
