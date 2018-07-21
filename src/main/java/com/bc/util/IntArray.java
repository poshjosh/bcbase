package com.bc.util;

import java.io.Serializable;
import java.util.Arrays;


/**
 * @(#)IntArray.java   21-Jun-2015 21:07:17
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */

/**
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public class IntArray implements Serializable, Cloneable {

    private static final long serialVersionUID = 0x137d3ef2bd2a7c90L;
    
    private final int SPACE_HOLDER = Integer.MIN_VALUE;
    private int cursor;
    private int batchSize;
    private int elementData[];

    public IntArray() {
        this(10);
    }

    public IntArray(int size) {
        reset(size);
    }

    public IntArray(IntArray ia) {
        this(ia.size());
        IntArray.this.addAll(ia);
    }

//@todo newly added logic ... needs testing    
    public IntArray(int [] arr) {
        this(arr.length);
        IntArray.this.addAll(arr);
    }

    public void clear() {
        reset(batchSize);
    }

    private void reset(int batchSize) {
        cursor = -1;
        elementData = new int[batchSize];
        Arrays.fill(elementData, SPACE_HOLDER);
        this.batchSize = batchSize;
    }

    public void addAll(IntArray arr) {
        if(this == arr) throw new IllegalArgumentException("Cannot add an IntArray to itself");
        for(int i=0; i<arr.elementData.length; i++) {
            int e = arr.elementData[i];
            if(e == SPACE_HOLDER) break;
            this.add(e);
        }
    }

    public void addAll(int [] arr) {
        for (int i = 0; i < arr.length; i++) {
            add(arr[i]);
        }        
    }
    
    public void add(int toAdd) {
        if (toAdd == SPACE_HOLDER) {
            throw new IllegalArgumentException("All integers with the exception of "+toAdd+" may be added as elements.");
        }    
        if (size() >= elementData.length) {
            int dest[] = new int[elementData.length + batchSize];
            System.arraycopy(elementData, 0, dest, 0, elementData.length);
            Arrays.fill(dest, elementData.length, dest.length, SPACE_HOLDER);
            elementData = dest;
        }
        elementData[++cursor] = toAdd;
    }
    
    public void set(int pos, int toAdd) {
        if (toAdd == SPACE_HOLDER) {
            throw new IllegalArgumentException("All integers with the exception of "+toAdd+" may be added as elements.");
        }    
        elementData[pos] = toAdd;
    }

    public int get(int index) {
        return elementData[index];
    }

    public boolean contains(int element) {
        return indexOf(element) != -1;
    }

    public boolean isEmpty() {
        return this.size() <= 0;
    }

    public int indexOf(int element) {
        int arr$[] = elementData;
        int len$ = arr$.length;
        int i$ = 0;
        do {
            if (i$ >= len$)
                break;
            int e = arr$[i$];
            if (e == SPACE_HOLDER)
                break;
            if (e == element)
                return i$;
            i$++;
        } while (true);
        return -1;
    }

    public int size() {
        return cursor + 1;
    }
    
    public int [] toArray() {

        return this.toArray(0, this.size());
    }

//@todo newly added logic ... needs testing
    public int [] toArray(int offset, int length) {
        
        int [] arr = new int[length];
        
        if(length > 0) {

            System.arraycopy(elementData, offset, arr, 0, length);
        }
        
        return arr;
    }
    
    @Override
    public String toString() {
        return Arrays.toString(this.toArray());
    }

    @Override
    public boolean equals(Object obj) {
        
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final IntArray other = (IntArray) obj;
        
        return Arrays.equals(this.elementData, other.elementData);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Arrays.hashCode(this.elementData);
        return hash;
    }
}

