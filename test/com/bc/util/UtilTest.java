/*
 * Copyright 2016 NUROX Ltd.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Josh
 */
public class UtilTest {
    
    public UtilTest() { }
    
    @BeforeClass
    public static void setUpClass() { }
    
    @AfterClass
    public static void tearDownClass() { }
    
    @Before
    public void setUp() { }
    
    @After
    public void tearDown() { }

    @Test
    public void testGetImageUrlRegex() {
        System.out.println("testGetImageUrlRegex");
        
        final String regex = Util.getImageUrlRegex();
System.out.println("Regex: "+regex);        
        String input = "http://wwww.looseboxes.com/images/appicon.jpeg";
        boolean matches = Pattern.matches(regex, input);
System.out.println("Input: "+input+", matches: "+matches);
        assertTrue(matches);
        
        input = "http://wwww.looseboxes.com/_/images/12011_cmu-abc.png";
        matches = Pattern.matches(regex, input);
System.out.println("Input: "+input+", matches: "+matches);
        assertTrue(matches);
        
        input = ".../images/appicon.jpg";
        matches = Pattern.matches(regex, input);
System.out.println("Input: "+input+", matches: "+matches);
        assertTrue(matches);
        
        input = "http://wwww.looseboxes.com/images/appicon.jpg?key_0=val_1&key_2=val_2";
        matches = Pattern.matches(regex, input);
System.out.println("Input: "+input+", matches: "+matches);
        assertTrue(matches);
        
        input = "/images/appicon.jpg#part_0";
        matches = Pattern.matches(regex, input);
System.out.println("Input: "+input+", matches: "+matches);
        assertTrue(matches);
        
        input = "/images/appicon.jpgx";
        matches = Pattern.matches(regex, input);
System.out.println("Input: "+input+", matches: "+matches);
        assertFalse(matches);
    }

    /**
     * Test of removeNonBasicMultilingualPlaneChars method, of class Util.
     */
    @Test
    public void testRemoveNonBasicMultilingualPlaneChars() {
        System.out.println("removeNonBasicMultilingualPlaneChars");
        
        String test = "0~1¬2`3^4£";
//        String expResult = "";
        String result = Util.removeNonBasicMultilingualPlaneChars(test);
System.out.println(result);        
//        assertEquals(expResult, result);
    }

    /**
     * Test of getRelativePath method, of class Util.
     */
    @Test
    public void testGetRelativePath() {
        System.out.println("getRelativePath");
        
        String basePath = "http://www.domainname.com/context";
        String path = "http://www.domainname.com/context/dir/year/month/file.jsp?name_0=value_0&amp;name_1=value_1";
        String expResult = "/dir/year/month/file.jsp?name_0=value_0&amp;name_1=value_1";
        String result = Util.getRelativePath(basePath, path);
System.out.println("Base path: "+basePath+"\n Input: "+path+"\nResult: "+result);        
        assertEquals(expResult, result);
        
        basePath = "context\\dir\\";
        path = "context\\dir\\year\\month\\file.jsp?name_0=value_0&name_1=value_1";
        expResult = "year/month/file.jsp?name_0=value_0&name_1=value_1";
        result = Util.getRelativePath(basePath, path);
System.out.println("Base path: "+basePath+"\n Input: "+path+"\nResult: "+result);        
        assertEquals(expResult, result);
    }

    /**
     * Test of getBaseURL method, of class Util.
     */
    @Test
    public void testGetBaseURL_String() {
        System.out.println("getBaseURL(String)");
        
        String url = "http://subdomain.domainname.com.ng/dir/pages/index.html";
        String expResult = "http://subdomain.domainname.com.ng";
        String result = Util.getBaseURL(url);
System.out.println("URL: "+url+"\nResult: "+result);        
        assertEquals(expResult, result);
        
        url = "http://domainname.com/dir/pages/index.html";
        expResult = "http://domainname.com";
        result = Util.getBaseURL(url);
System.out.println("URL: "+url+"\nResult: "+result);        
        assertEquals(expResult, result);
    }

    /**
     * Test of getExtension method, of class Util.
     */
    @Test
    public void testGetExtension() {
        System.out.println("getExtension");
        
        String path = "..folder/folder/file.xyz";
        String expResult = "xyz";
        String result = Util.getExtension(path);
System.out.println("Path: "+path+"\nResult: "+result);        
        assertEquals(expResult, result);
        
        path = "file:\\\\C:\\folder\\folder\\file.xyz?key_0=value_0";
        expResult = "xyz";
        result = Util.getExtension(path);
System.out.println("Path: "+path+"\nResult: "+result);        
        assertEquals(expResult, result);
    }

    /**
     * Test of randomInt method, of class Util.
     */
    @Test
    public void testRandomInt() {
        System.out.println("randomInt");
        
        final int start = 1;
        final int end = 1000;
        
        double total = 0.0;
        
        for(int i=start; i<end; i++) {
            final double random = Util.random(i);
            assertTrue(random >= 0.0);
            assertTrue(random < i);
            total += random;
        }
        
        final int count = end - start;
System.out.println("Average random: "+(total/count)+", after generating "+count+" randoms");        
    }

    /**
     * Test of removeNulls method, of class Util.
     */
    @Test
    public void testRemoveNulls() {
        
        System.out.println("removeNulls");
        
        Collection<String> c = new ArrayList(Arrays.asList("A", null, "B", "C", null));
System.out.println("Input: " + c);        
        Collection<String> cExpected = Arrays.asList("A", "B", "C");
        boolean expResult = true;
        boolean result = Util.removeNulls(c);
System.out.println("Output: "+c);        
        assertEquals(expResult, result);
        assertEquals(c, cExpected);
        
        c = new ArrayList();
        cExpected = new ArrayList();
System.out.println("Input: " + c);        
        expResult = false;
        result = Util.removeNulls(c);
System.out.println("Output: " + c);        
        assertEquals(expResult, result);
        assertEquals(c, cExpected);
    }

    /**
     * Test of shutdownAndAwaitTermination method, of class Util.
     */
    @Test
    public void testShutdownAndAwaitTermination_3args() {
        System.out.println("shutdownAndAwaitTermination");
        System.out.println("- - - - - - - @todo - - - - - - -");
    }

    /**
     * Test of shutdownAndAwaitTermination method, of class Util.
     */
    @Test
    public void testShutdownAndAwaitTermination_4args() {
        System.out.println("shutdownAndAwaitTermination");
        System.out.println("- - - - - - - @todo - - - - - - -");
    }

    /**
     * Test of awaitTermination method, of class Util.
     */
    @Test
    public void testAwaitTermination_3args() {
        System.out.println("awaitTermination");
        System.out.println("- - - - - - - @todo - - - - - - -");
    }

    /**
     * Test of awaitTermination method, of class Util.
     */
    @Test
    public void testAwaitTermination_4args() {
        System.out.println("awaitTermination");
        System.out.println("- - - - - - - @todo - - - - - - -");
    }
}
