package com.bc.util;

import java.util.Properties;
import java.util.TreeMap;
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
public class PropertiesServiceTest {
    
    public PropertiesServiceTest() { }
    
    @BeforeClass
    public static void setUpClass() { }
    
    @AfterClass
    public static void tearDownClass() { }
    
    @Before
    public void setUp() { }
    
    @After
    public void tearDown() { }

    @Test
    public void testAll() {
System.out.println("Testing "+this.getClass().getName());

        DefaultPropertiesService instance = new DefaultPropertiesService();
        String separator;
        separator = ".."; // didn't work 
        separator = "$$"; // didn't work
        separator = "."; 
        separator = "a..b"; 
        separator = "x"; 
        separator = "$"; 
        separator = "#"; 
        separator = "##"; 
        separator = "xx"; 
        Properties names = new Properties();
        names.setProperty("name"+separator+"first", "John");
        names.setProperty("name"+separator+"last", "Doe");
        Properties favs = new Properties();
        favs.setProperty("favorite"+separator+"color", "blue");
        favs.setProperty("favorite"+separator+"animal", "sheep,horse");
        instance.put("names.properties", names);
        instance.put("favorites.properties", favs);
        
        Properties props = instance.subset("name", separator);
System.out.println(props);        
    }
}
