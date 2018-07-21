/**
 * @(#)IndexedMap.java   17-Jun-2010 15:11:41
 *
 * Copyright 2009 BC Enterprise, Inc. All rights reserved.
 * BCE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.bc.util;

import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * @author  chinomso bassey ikwuagwu
 * @version 1.0
 * @since   1.0
 */
public class IndexedMap<K, V> extends LinkedHashMap<K , V> implements java.io.Serializable {

    private String name;

    public IndexedMap() {}

    public IndexedMap(String name) {
        setName(name);
    }

    public void reset() { 
        name  = null;
        this.clear();
    }
    
    public int indexOf(K key) {
        Iterator<K> iter = this.keySet().iterator();
        for(int i=0; iter.hasNext(); i++) {
            K myKey = iter.next();
            if((myKey == null && key == null) || (myKey.equals(key))) return i;
        }
        return -1;
    }

    public K getKeyAt(int index) {
        checkIndex(index);
        Iterator<K> iter = this.keySet().iterator();
        for(int i=0; iter.hasNext(); i++) {
            K key = iter.next();
            if(i == index) return key;
        }
        return null;
    }
    
    public V getValueAt(int index) { 
        checkIndex(index);
        Iterator<V> iter = this.values().iterator();
        for(int i=0; iter.hasNext(); i++) {
            V value = iter.next();
            if(i == index) return value;
        }
        return null;
    }

    private void checkIndex(int index) {
        if(index < 0) {
            throw new IndexOutOfBoundsException(""+index+" < 0");
        }else if(index >= this.size()) {
            throw new IndexOutOfBoundsException(""+index+" >= size:"+this.size());
        }
    }

    public final void setName(String name) {this.name = name;}
    public final String getName() {return name;}
  
    @Override
    public String toString() { return name + ":" + super.toString(); }
}