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
package com.bc.io;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
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
public class FileBasedCollectionTest {
    
    public FileBasedCollectionTest() {
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
     * Test of close method, of class FileBasedCollection.
     */
//    @Test
//    public void testReuse() {
//        System.out.println("test reuse");
//        final String dir = System.getProperty("java.io.tmpdir");
//        final String fname = "abcdef";
//        boolean deleteFileOnClose = false;
//        try(FileBasedCollection instance = new FileBasedCollection(dir, fname, deleteFileOnClose, 5)) {
//            instance.addAll(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11));
//        }
//        deleteFileOnClose = true;
//        try(FileBasedCollection instance = new FileBasedCollection(dir, fname, deleteFileOnClose, 5)) {
//            System.out.println("XXXXXXXXXXXXXXXXXXXXX: " + instance.size());
//            instance.stream().forEach((e) -> {
//                System.out.println("XXXXXXXXXXXXXXXXXXXXX: " + e);
//            });
//        }
//    }
    
    /**
     * Test of close method, of class FileBasedCollection.
     */
    @Test
    public void testClose() {
        System.out.println("close");
        try(FileBasedCollection instance = new FileBasedCollection(5)) {
            instance.addAll(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11));
        }
    }

    /**
     * Test of getChunkSize method, of class FileBasedCollection.
     */
    @Test
    public void testGetChunkSize() {
        System.out.println("getChunkSize");
        int expResult = 5;
        try(FileBasedCollection instance = new FileBasedCollection(expResult)) {
            int result = instance.getChunkSize();
            assertEquals(expResult, result);
        }
    }

    /**
     * Test of sort method, of class FileBasedCollection.
     */
    @Test
    public void testSort_0args() throws Exception {
        System.out.println("sort");
        try(FileBasedCollection instance = new FileBasedCollection(5)) {
            instance.addAll(Arrays.asList(4, 3, 2, 1, 5, 6, 7, 8));
            instance.sort();
            final List list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
            assertArrayEquals(instance.toArray(), list.toArray());
        }
    }

    /**
     * Test of iterator method, of class FileBasedCollection.
     */
    @Test
    public void testIterator() {
        System.out.println("iterator");
        try(FileBasedCollection instance = new FileBasedCollection(5)) {
            instance.addAll(Arrays.asList(0, 1, 2, 3, 4, 5, 6));
            FileBasedCollection.FileBasedIterator iter0 = instance.iterator();
            FileBasedCollection.FileBasedIterator iter1 = instance.iterator();
            while(iter0.hasNext() && iter1.hasNext()) {
                assertEquals(iter0.next(), iter1.next());
            }
        }
    }

    /**
     * Test of size method, of class FileBasedCollection.
     */
    @Test
    public void testSize() {
        System.out.println("size");
        try(FileBasedCollection instance = new FileBasedCollection(5)) {
            final List list = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
            instance.addAll(list);
            int expResult = list.size(); 
            int result = instance.size();
            assertEquals(expResult, result);
        }
    }

    /**
     * Test of removeAll method, of class FileBasedCollection.
     */
    @Test
    public void testRemoveAll() {
        System.out.println("removeAll");
        try(FileBasedCollection instance = new FileBasedCollection(5)) {
            final List list = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
            instance.addAll(list);
            instance.removeAll(list);
            assertTrue(instance.isEmpty());
        }
    }

    /**
     * Test of contains method, of class FileBasedCollection.
     */
    @Test
    public void testContains() {
        System.out.println("contains");
        try(FileBasedCollection instance = new FileBasedCollection(5)) {
            final List list = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
            instance.addAll(list);
            final boolean result = instance.contains(11);
            assertTrue(result);
        }
    }

    /**
     * Test of retainAll method, of class FileBasedCollection.
     */
    @Test
    public void testRetainAll() {
        System.out.println("retainAll");
        try(FileBasedCollection instance = new FileBasedCollection(5)) {
            final List list = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
            instance.addAll(list);
            instance.retainAll(list);
            assertEquals(instance.size(), list.size());
        }
    }

    /**
     * Test of add method, of class FileBasedCollection.
     */
    @Test
    public void testAdd_GenericType() {
        System.out.println("add");
        Serializable e = 5;
        try(FileBasedCollection instance = new FileBasedCollection(5)) {
            instance.addAll(Arrays.asList(0, 1, 2, 3, 4));
            boolean expResult = true;
            boolean result = instance.add(e);
            assertEquals(expResult, result);
        }
    }

    /**
     * Test of toArray method, of class FileBasedCollection.
     */
    @Test
    public void testToArray_0args() {
        System.out.println("toArray");
        try(FileBasedCollection instance = new FileBasedCollection(5)) {
            Object[] expResult = {};
            Object[] result = instance.toArray();
            assertArrayEquals(expResult, result);
            final List list = Arrays.asList(0, 1, 2, 3, 4, 5, 6);
            instance.addAll(list);
            expResult = list.toArray();
            result = instance.toArray();
            assertArrayEquals(expResult, result);
        }
    }

    /**
     * Test of remove method, of class FileBasedCollection.
     */
    @Test
    public void testRemove() {
        System.out.println("remove -> THIS METHOD IS NOT SUPPORTED by " + FileBasedCollection.class.getName());
    }

    /**
     * Test of clear method, of class FileBasedCollection.
     */
    @Test
    public void testClear() {
        System.out.println("clear");
        try(FileBasedCollection instance = new FileBasedCollection(5)) {
            instance.clear();
            instance.addAll(Arrays.asList("John", "Ikwuagwu"));
            instance.clear();
            assertTrue(instance.isEmpty());
        }
    }
}
