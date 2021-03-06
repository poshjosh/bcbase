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

import java.util.Collection;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 19, 2016 9:10:42 PM
 */
public interface StringArrayComparator extends StringComparator {

    /**
     * @see #compare(java.lang.String, java.lang.String, float) 
     */
    boolean compare(final Object obj1, final Object obj2, float tolerance);

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
     * @deprecated 
     */
    @Deprecated
    IndexedMap[] compare(Object[] one, Object[] two);

    @Deprecated
    IndexedMap[] compare(Object[] one, Object[] two, float tolerance);

    /**
     * Compare() performs only adjacent comparisons as against compareAll() which
     * compares each element against every other element.
     * @param v1
     * @param v2
     * @return
     * @deprecated 
     */
    @Deprecated
    IndexedMap[] compare(Collection c1, Collection c2);

    @Deprecated
    IndexedMap[] compare(Collection c1, Collection c2, float tolerance);

    /**
     * Takes two arrays and compares the string value each object in one array to the
     * the string value of every object in the second array, as against compare() which
     * which compares adjacent objects in both arrays.
     * @see compare()
     * @deprecated 
     */
    @Deprecated
    IndexedMap[] compareAll(Object[] one, Object[] two);

    @Deprecated
    IndexedMap[] compareAll(Object[] one, Object[] two, float tolerance);

    // compareAll() compares each element against every other element as against
    // compare() which performs only adjacent comparisons.
    @Deprecated
    IndexedMap[] compareAll(Collection c1, Collection c2);

    @Deprecated
    IndexedMap[] compareAll(Collection c1, Collection c2, float tolerance);
}
