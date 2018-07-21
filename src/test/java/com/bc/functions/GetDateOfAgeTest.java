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
package com.bc.functions;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
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
public class GetDateOfAgeTest {
    
    public GetDateOfAgeTest() {
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
     * Test of apply method, of class GetDateOfAge.
     */
    @Test
    public void testApply() {
        System.out.println("apply");
        final Integer age = 365;
        final TimeUnit timeUnit = TimeUnit.DAYS;
        final GetDateOfAge instance = new GetDateOfAge();
        final Calendar now = Calendar.getInstance();
        now.add(instance.getCalendarField(timeUnit), -age); 
        final Date expResult = now.getTime();
        final Date result = instance.apply(age, timeUnit);
        System.out.println("Expected: " + expResult + "\n   Found: " + result);
        assertEquals(TimeUnit.MILLISECONDS.toDays(expResult.getTime()), TimeUnit.MILLISECONDS.toDays(result.getTime()));
        
        final Date date0 = instance.apply(90, TimeUnit.DAYS);
        final Date date1 = instance.apply((int)TimeUnit.DAYS.toHours(90), TimeUnit.HOURS);
        System.out.println("Expected: " + date0 + "\n   Found: " + date1);
        assertEquals(TimeUnit.MILLISECONDS.toDays(date0.getTime()), TimeUnit.MILLISECONDS.toDays(date1.getTime()));
    }
}
