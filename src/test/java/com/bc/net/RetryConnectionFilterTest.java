/*
 * Copyright 2019 NUROX Ltd.
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
package com.bc.net;

import java.io.FileNotFoundException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ConcurrentModificationException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Josh
 */
public class RetryConnectionFilterTest {
    
    private final int maxRetrials = 2;
    private final int intervalBetweenTrials = 100;
    
    private final Throwable [] acceptable = {
        new UnknownHostException(), new SocketTimeoutException(), 
        new NoRouteToHostException(), new ConnectException()};
    
    private final Throwable [] unacceptable = {
        new FileNotFoundException(), new MalformedURLException(), 
        new SQLException(), new ConcurrentModificationException()};

    public RetryConnectionFilterTest() { }

    /**
     * Test of copy method, of class RetryConnectionFilter.
     */
    @Test
    public void testCopy() {
        System.out.println("copy");
        this.testCopy(acceptable);
        this.testCopy(unacceptable);
    }

    public void testCopy(Throwable [] arr) {
        final RetryConnectionFilter instance = this.newInstance();
        final RetryConnectionFilter copy = instance.copy();
        instance.reset(); copy.reset();
        for(int i=0; i<arr.length; i++) {
            final Throwable t = arr[i];
            System.out.println("["+i+"] = " + t);
            final boolean a = instance.test(t);
            final boolean b = copy.test(t);
            assertEquals("Failed on: " + t.getClass().getName(), a, b);
        }
    }

    /**
     * Test of test method, of class RetryConnectionFilter.
     */
    @Test
    public void testTest() {
        System.out.println("test");
        this.testTest(acceptable, true);
        this.testTest(unacceptable, false);
    }
    
    public void testTest(Throwable [] arr, boolean defaultResult) {
        final RetryConnectionFilter instance = this.newInstance();
        for(int i=0; i<arr.length; i++) {
            final boolean a = defaultResult && i < maxRetrials;
            final Throwable t = arr[i];
            System.out.println("["+i+"] = " + t);
            final boolean b = instance.test(t);
            assertEquals("Failed on: " + t.getClass().getName(), a, b);
        }
    }

    private RetryConnectionFilter newInstance() {
        return new RetryConnectionFilter(maxRetrials, intervalBetweenTrials);
    }
}
