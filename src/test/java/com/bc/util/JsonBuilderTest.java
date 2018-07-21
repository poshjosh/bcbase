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
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Josh
 */
public class JsonBuilderTest {
    
    public JsonBuilderTest() {
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
     * Test of appendJSONString method, of class JsonBuilder.
     */
    @Test
    public void testAppendJSONString_Map_GenericType() throws Exception {
        System.out.println("appendJSONString");
        Map<String, Object> m = new HashMap<>();
        m.put("&bull;", "&bull;");
        m.put("&#8211;", "&#8211;");
        m.put("&#039;s", "&#039;s");
        for(String key : m.keySet()) {
            final Object val = m.get(key);
            final Object esc = Entities.HTML40.unescape(val.toString());
            final Object esc2 = StringEscapeUtils.unescapeHtml(val.toString());
            System.out.println(key + " = " + val + ". Escaped, HTML 4.0: " + esc + ", also: " + esc2);
        }
        Appendable appendTo = System.out;
        JsonBuilder instance = new JsonBuilder(true, true, "  "){
            @Override
            public void escape(CharSequence s, Appendable appendTo) throws IOException {
                final String input = s.toString();
                final String output = Entities.HTML40.unescape(input);
                if(input.equals(output)) {
                    super.escape(output, appendTo); 
                }else{
                    appendTo.append(output);
                }
            }
        };
        try{
            instance.appendJSONString(m, appendTo);
        }finally{
            System.out.flush();
        }
    }
}
