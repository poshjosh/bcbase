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

import com.bc.util.UrlUtil;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Josh
 */
public class UrlUtilTest {
    
    public UrlUtilTest() { }
    
    /**
     * Test of removeHashPart method, of class UrlUtil.
     */
    @Test
    public void removeHashPart() {
        System.out.println("removeHashPart");
        
        final Map<String, String> map = new HashMap<>();
        String str = "http://www.abc.com/page0.html";
        map.put(str + "#intro", str);
        map.put(str, str);
        
        str = "https://abc.com/custom#align/page0.jsp/def";
        map.put(str + "#intro", str);
        map.put(str, str);
        
        str = "ftp://.../page0#align?abort=false";
        map.put(str + "#keys#intro", str);
        map.put(str, str);
        
        for(String key : map.keySet()) {
            final String expRes = map.get(key);    
            System.out.println();
            System.out.println("Expected: " + expRes);
            final String result = UrlUtil.removeHashPart(key);
            System.out.println("   Found: " + result);
            assertEquals(expRes, result);
        }
    }

    /**
     * Test of toWWWFormat method, of class UrlUtil.
     */
    @Test
    public void testToWWWFormat() throws Exception {
        System.out.println("toWWWFormat");
    }

    /**
     * Test of getBaseURLs method, of class UrlUtil.
     */
    @Test
    public void testGetBaseURLs() {
        System.out.println("getBaseURLs");
    }

    /**
     * Test of getBaseURL method, of class UrlUtil.
     */
    @Test
    public void testGetBaseURL_String() {
        System.out.println("getBaseURL");
    }

    /**
     * Test of getBaseURL method, of class UrlUtil.
     */
    @Test
    public void testGetBaseURL_URL() {
        System.out.println("getBaseURL");
    }

    /**
     * Test of getImageUrlRegex method, of class UrlUtil.
     */
    @Test
    public void testGetImageUrlRegex_0args() {
        System.out.println("getImageUrlRegex");
    }

    /**
     * Test of getImageUrlRegex method, of class UrlUtil.
     */
    @Test
    public void testGetImageUrlRegex_StringArr() {
        System.out.println("getImageUrlRegex");
    }
    
}
