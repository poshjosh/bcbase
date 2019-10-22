/**
 * @(#)StringComparator.java   17-Jun-2010 14:09:14
 *
 * Copyright 2009 BC Enterprise, Inc. All rights reserved.
 * BCE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.bc.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * @author  chinomso bassey ikwuagwu
 * @version 1.1
 * @since   1.0
 */
public class StringArrayComparatorImpl extends StringComparatorImpl 
        implements java.io.Serializable, StringArrayComparator {

  public StringArrayComparatorImpl() { }

  public StringArrayComparatorImpl(boolean ignoreCase, boolean removeWhiteSpace) {
    super(ignoreCase, removeWhiteSpace);
  }

  @Override
  public boolean compare(final Object lhs, final Object rhs, float tolerance) {
    return super.compare(lhs==null?null:lhs.toString(), rhs==null?null:rhs.toString(), tolerance);
  }

/**
 * Compares the string values of adjacent objects in two arrays of Objects
 * It compares each arrayOne[i] to its corresponding arrayTwo[i]. All the similar
 * objects and their corresponding indexes are returned. To compare every object
 * in the first array to every other object in the second use {@link #compareAll}
 * @returns an array of 4 Maps designated as follows:
 * array[0] represents the similar indices in table one.
 * array[1] represents the similar values in table one.
 * array[2] represents the similar indices in table two.
 * array[3] represents the similar values in table two.
 */
    @Override
  public IndexedMap[] compare (Object [] one, Object [] two) {
    return compare(one, two, 0);
  }
    @Override
  public IndexedMap[] compare (Object [] one, Object [] two, float tolerance) {
    return  compare(Arrays.asList(one), Arrays.asList(two), tolerance);
  }

  /**
   * Compare() performs only adjacent comparisons as against compareAll() which
   * compares each element against every other element.
   * @param v1
   * @param v2
   * @return
   */
    @Override
  public IndexedMap[] compare(Collection c1, Collection c2) {
    return compare(c1, c2, 0);
  }

    @Override
  public IndexedMap[] compare(Collection c1, Collection c2, float tolerance) {

    IndexedMap agreedOne         = new IndexedMap();
    IndexedMap agreedTwo         = new IndexedMap();

    Iterator iter1 = c1.iterator();
    Iterator iter2 = c2.iterator();

    for(int i=0; iter1.hasNext() && iter2.hasNext(); i++) {
      Object o1 = iter1.next();
      Object o2 = iter2.next();
      if (compare(o1, o2, tolerance)) {
        agreedOne.put(i, o1);
        agreedTwo.put(i, o2);
      }
    }

    IndexedMap disagreedOne = doDisagreed(agreedOne, c1);
    IndexedMap disagreedTwo = doDisagreed(agreedTwo, c2);

    return new IndexedMap[]{agreedOne, agreedTwo, disagreedOne, disagreedTwo};
  }

/**
 * Takes two arrays and compares the string value each object in one array to the
 * the string value of every object in the second array, as against compare() which
 * which compares adjacent objects in both arrays.
 * @see compare()
 */
    @Override
  public IndexedMap[] compareAll (Object [] one, Object [] two) {
    return compareAll(one, two, 0);
  }
    @Override
  public IndexedMap[] compareAll (Object [] one, Object [] two, float tolerance) {
    return  compareAll(Arrays.asList(one), Arrays.asList(two), tolerance);
  }
  // compareAll() compares each element against every other element as against
  // compare() which performs only adjacent comparisons.
    @Override
  public IndexedMap[] compareAll(Collection c1, Collection c2) {
    return compareAll(c1, c2, 0);
  }

    @Override
  public IndexedMap[] compareAll(Collection c1, Collection c2, float tolerance) {
//com.bravocharlie.Debugger.println(StringMgr.class.getName()+"#compareAll(Vector,Vector,double)");
    IndexedMap agreedOne         = new IndexedMap();
    IndexedMap agreedTwo         = new IndexedMap();

    int i = 0;
    for(Object o1:c1) {

        int j = 0;
        for(Object o2:c2) {

            if(compare(o1, o2, tolerance)) {

                agreedOne.put(i, o1);
                agreedTwo.put(j, o2);
            }
            ++j;
        }
        ++i;
    }

    IndexedMap disagreedOne = doDisagreed(agreedOne, c1);
    IndexedMap disagreedTwo = doDisagreed(agreedTwo, c2);
//com.bravocharlie.Debugger.println(StringMgr.class.getName()+"#compareAll, agreedOne.size:" + agreedOne.size() + "   agreedTwo.size:"+agreedTwo.size());
//com.bravocharlie.Debugger.println(StringMgr.class.getName()+"#compareAll, disagreedOne.size:" + disagreedOne.size() + "   disagreedTwo.size:"+disagreedTwo.size());
    return new IndexedMap[]{agreedOne, agreedTwo, disagreedOne, disagreedTwo};
  }

  private IndexedMap doDisagreed(Map agreed, Collection c) {
    return doDisagreed(agreed,  c.toArray());
  }

  private IndexedMap doDisagreed(Map agreed, Object [] arr) {
      
//com.bravocharlie.Debugger.println(StringMgr.class.getName()+"#doDisagreed");
    IndexedMap disagreed = new IndexedMap();
    for (int i=0; i<arr.length; i++) {
      Object obj = arr[i];
      // Always use contains value as two keys may have the same value
      if(!agreed.containsValue(obj)) {
        disagreed.put(i,obj);
      }
    }
    return disagreed;
  }
}//~END
