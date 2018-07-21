/**
 * @(#)StringComparator.java   17-Jun-2010 14:09:14
 *
 * Copyright 2009 BC Enterprise, Inc. All rights reserved.
 * BCE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.bc.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author  chinomso bassey ikwuagwu
 * @version 1.1
 * @since   1.0
 */
public class StringComparatorImpl implements java.io.Serializable, StringComparator {

  private boolean ignoreCase;
  private boolean removeWhiteSpace;

  public StringComparatorImpl() { 
    this(true, true);  
  }

  public StringComparatorImpl(boolean ignoreCase, boolean removeWhiteSpace) { 
    this.ignoreCase = ignoreCase;
    this.removeWhiteSpace = removeWhiteSpace;
  }
  
  private String format(String s) {
    return s == null ? null : (!removeWhiteSpace) ? s :
        s.replaceAll("\\s", "");
  }

  /**
   * Compares the obj1.toString to obj2.toString. A tolerance of 0.1 means only 10
   * percent of characters are allowed to be wrong (ie an allowance of 10%)
   * A tolerance of 0 means obj1.equals(obj2) is used as comparison.
   */
    @Override
  public boolean compare(final Object obj1, final Object obj2, float tolerance) {
//com.bravocharlie.Debugger.println(StringMgr.class.getName()+"#compare(Object, Object, float)");
    if (tolerance >= 1) {
      return true;
    }

    String s1 = format(obj1.toString());
    String s2 = format(obj2.toString());

    if(s1 == null || s2 == null) return false;

    // Get the greater length
    //
    final int greaterLength = s1.length() > s2.length() ? s1.length() : s2.length();

//com.bravocharlie.Debugger.println("..............greaterLength:" + greaterLength);
    final int MIN = 1;
//com.bravocharlie.Debugger.println(StringMgr.class.getName()+"#compare, s1:"+s1+"  s2:"+s2);
    if (greaterLength > MIN && tolerance != 0.0f) {

      double similar = 0.0;

      if (isNumeric(s1) && isNumeric(s2)) {

        if (obj1 instanceof Number && obj2 instanceof Number) {

          // This is done because for SQL type currency (which is of class Number)
          // Comparison is more accurate with lesser decimal places so we give
          // our data 2 decimal places.
//          s1 = toDecimalPlaces(2, s1);
//          s2 = toDecimalPlaces(2, s2);
//com.bravocharlie.Debugger.println(StringMgr.class.getName()+"compare, INSTANCE OF NUMBER, s1: "+s1+"   s2: "+s2);
        }
        similar = getSimilarAdjacentIndices(s1, s2).size();
      }else {
        similar = getSimilarIndices(s1, s2).size();
      }

      double minSimilarCols = greaterLength * (1-tolerance);
//com.bravocharlie.Debugger.println(" similar:" + similar + "   minSimilarCols:" + minSimialrCols);

      if (similar >= minSimilarCols) {
// com.bravocharlie.Debugger.println(StringMgr.class.getName()+"#compare, similar >= minSimilarCols: MATCH");
        return true;
      }else {
 //com.bravocharlie.Debugger.println(StringMgr.class.getName()+"#compare, similar < minSimilarCols: NO MATCH");
        return false;
      }
    }else {
      // if length <= MIN OR tolerance == 0.0f
      if (ignoreCase) {
        return (s1.equalsIgnoreCase(s2));
      }else{
        return s1.equals(s2);
      }
    }
  }

  private boolean isNumeric(String str) {
    try {
      double d = Double.parseDouble(str);
    }catch(Exception e) {
      return false;
    }
    return true;
  }

// @todo implement this method
//
//  public boolean compareNumbers(final Object obj1, final Object obj2, float tolerance) {
//com.bravocharlie.Debugger.println(StringMgr.class.getName()+"#compareNumbers(Object, Object, float)");
//    if (true) {
//      throw new UnsupportedOperationException("Method not yet supported");
//    }
//    return false;
//  }

  public Set getSimilarIndices (String s1, String s2) {

    HashSet s = new HashSet();

    for (int i = 0; i < s1.length(); i++) {

      for (int j = 0; j < s2.length(); j++) {

        if (compare(s1.charAt(i), s2.charAt(j)) == 0) {
          if(s.add(j)) { // s.add returns true if the set did not already contain the specified element
            break;
          }
        }
      }
    }
//com.bravocharlie.Debugger.println("s.size():"+s.size());
    return s;
  }

  public Set getSimilarAdjacentIndices(String s1, String s2){

    HashSet s = new HashSet();

    // Get the lesser length
    //
    final int lesserLength = s1.length() < s2.length() ? s1.length() : s2.length();

    for (int i = 0; i < lesserLength; i++) {
      if(compare(s1.charAt(i), s2.charAt(i)) == 0) {
          s.add(i);
      }
    }
//com.bravocharlie.Debugger.println("s.size():"+s.size());
    return s;
  }

    @Override
  public int compare(char c1, char c2) {
    if(ignoreCase) {
        c1 = Character.toUpperCase(c1);
        c2 = Character.toUpperCase(c2);
    }
    if(c1 > c2) {
        return -1;
    }else if(c1 == c2) {
        return 0;
    }else {
        return 1;
    }
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

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

    public boolean isRemoveWhiteSpace() {
        return removeWhiteSpace;
    }

    public void setRemoveWhiteSpace(boolean removeWhiteSpace) {
        this.removeWhiteSpace = removeWhiteSpace;
    }
}//~END
