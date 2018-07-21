package com.bc.util;

import java.util.Arrays;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Josh
 */
public class IntegerArrayTest {
    
    public IntegerArrayTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of clone method, of class IntegerArray.
     */
    @Test
    public void testClone() {
System.out.println("clone");
        IntegerArray instance = new IntegerArray();
        instance.add(2); instance.add(300); instance.add(-4001);
        IntegerArray expResult = instance;
        IntegerArray result = new IntegerArray(instance);
if(!Arrays.equals(expResult.toArray(), result.toArray())) {
    fail("IntegerArray.clone Failed");
}
    }

    /**
     * Test of clear method, of class IntegerArray.
     */
    @Test
    public void testClear() {
System.out.println("clear");
        IntegerArray instance = new IntegerArray();
        instance.add(2); instance.add(300); instance.add(-4001);
        instance.clear();
if(instance.size() > 0) {       
        fail("Cleared object contains "+instance.size()+" elements.");
}        
    }

    /**
     * Test of addAll method, of class IntegerArray.
     */
    @Test
    public void testAddAll_IntegerArray() {
System.out.println("addAll");
        IntegerArray arr = new IntegerArray();
        arr.add(2); arr.add(300); arr.add(-4001);
        IntegerArray instance = new IntegerArray();
        instance.addAll(arr);
        assertEquals(arr, instance);
    }

    /**
     * Test of addAll method, of class IntegerArray.
     */
    @Test
    public void testAddAll_intArr() {
System.out.println("addAll");
        int[] arr = {2, 300, -4001};
        IntegerArray instance = new IntegerArray();
        instance.addAll(arr);
if(instance.size() != 3) {
    fail("After adding 3 elements, size: "+instance.size());
}   
if(!instance.contains(300)) {
    fail("IntegerArray.contains failed");
}
    }

    /**
     * Test of add method, of class IntegerArray.
     */
    @Test
    public void testAdd() {
System.out.println("add");
        int i = 0;
        IntegerArray instance = new IntegerArray();
        instance.add(i);
    }

    /**
     * Test of get method, of class IntegerArray.
     */
    @Test
    public void testGet() {
System.out.println("get");
        IntegerArray instance = new IntegerArray();
        int expResult = -1213;
        instance.add(expResult);
        int result = instance.get(0);
        assertEquals(expResult, result);
    }

    /**
     * Test of contains method, of class IntegerArray.
     */
    @Test
    public void testContains() {
System.out.println("contains");
        int element = 0;
        IntegerArray instance = new IntegerArray();
        instance.add(21234); instance.add(-21234);
if(!instance.contains(21234)) {
    fail("Element added not found");
}        
    }

    /**
     * Test of isEmpty method, of class IntegerArray.
     */
    @Test
    public void testIsEmpty() {
System.out.println("isEmpty");
        IntegerArray instance = new IntegerArray();
        boolean expResult = true;
        boolean result = instance.isEmpty();
        assertEquals(expResult, result);
    }

    /**
     * Test of indexOf method, of class IntegerArray.
     */
    @Test
    public void testIndexOf() {
System.out.println("indexOf");
        IntegerArray instance = new IntegerArray();
        instance.add(212); instance.add(-212);
        int expResult = 1;
        int result = instance.indexOf(-212);
        assertEquals(expResult, result);
    }

    /**
     * Test of size method, of class IntegerArray.
     */
    @Test
    public void testSize() {
System.out.println("size");
        IntegerArray instance = new IntegerArray();
        int expResult = 0;
        int result = instance.size();
        assertEquals(expResult, result);
        instance.add(2);
        expResult = 1;
        result = instance.size();
        assertEquals(expResult, result);
    }

    /**
     * Test of toArray method, of class IntegerArray.
     */
    @Test
    public void testToArray() {
System.out.println("toArray");
        int [] arr = {2, 300, -4001};
        IntegerArray instance = new IntegerArray();
        instance.addAll(arr);
        int [] a = instance.toArray();
if(!Arrays.equals(a, arr)) {
    fail("IntegerArray.addAll Failed");
}
    }

    /**
     * Test of toString method, of class IntegerArray.
     */
    @Test
    public void testToString() {
//System.out.println("toString");
    }

    /**
     * Test of equals method, of class IntegerArray.
     */
    @Test
    public void testEquals() {
System.out.println("equals");
        IntegerArray arr = new IntegerArray();
        arr.add(2); arr.add(300); arr.add(-4001);
        IntegerArray instance = new IntegerArray();
        instance.addAll(arr);
if(!instance.equals(arr)) {
    fail("IntegerArray.equals Failed");
}        
    }

    /**
     * Test of hashCode method, of class IntegerArray.
     */
    @Test
    public void testHashCode() {
//System.out.println("hashCode");
    }
}
