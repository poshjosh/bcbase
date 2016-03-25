package com.bc.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

/**
 * @(#)IntegerArray.java   18-Jan-2012 15:01:52
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/licenses/software.html
 */
/**
 * <b>Note:</b><br/>
 * <b>
 * 1. An IntegerArray cannot be added to itself.<br/>
 * 2. All Integers with the exception of {@link java.lang.Integer#MIN_VALUE} 
 * may be added as an element to this Object.
 * </b>
 * @author   chinomso bassey ikwuagwu
 * @version  2.1.0 
 * 1. Added method toArray()<br/>
 * 2. Changed space holder element from -1 to Integer.MIN_VALUE<br/>
 * 3. Changed the access modifiers of {@linkplain #writeObject(java.io.ObjectOutputStream)} 
 * and {@linkplain #readObject(java.io.ObjectInputStream)} from public to private
 * @version 3.0
 * Added method set(int pos, int toAdd)
 * @since    2.0
 */
public class IntegerArray implements Serializable, Cloneable {

    private static final long serialVersionUID = 0x137d3cf27d2a9c90L;
    
    private final int SPACE_HOLDER = Integer.MIN_VALUE;
    private int cursor;
    private int batchSize;
    private int elementData[];

    public IntegerArray() {
        this(10);
    }

    public IntegerArray(int size) {
        reset(size);
    }

    public IntegerArray(IntegerArray ia) {
        this(ia.size());
        IntegerArray.this.addAll(ia);
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

    public void addAll(IntegerArray arr) {
        if(this == arr) throw new IllegalArgumentException("Cannot add an IntegerArray to itself");
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
        
        int [] arr = new int[size()];
        
        if(arr.length == 0) return arr;

        System.arraycopy(elementData, 0, arr, 0, arr.length);
        
        return arr;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
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
        final IntegerArray other = (IntegerArray) obj;
        if (!Arrays.equals(this.elementData, other.elementData)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Arrays.hashCode(this.elementData);
        return hash;
    }
}
