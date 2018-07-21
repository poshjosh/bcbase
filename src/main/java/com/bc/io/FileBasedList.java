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

import com.bc.io.FileBasedCollection.FileBasedIterator;
import java.io.Closeable;
import java.io.IOException;
import java.util.AbstractList;
import java.util.Collection;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 10, 2018 3:23:30 PM
 */
public class FileBasedList<E> extends AbstractList<E> implements Closeable {

    private final FileBasedCollection delegate;

    /**
     * Default constructor
     */
    public FileBasedList() {
        this(100);
    }

    /**
     * @param chunkSize The size of each chunk.
     */
    public FileBasedList(int chunkSize) {
        this(System.getProperty("java.io.tmpdir"), chunkSize);
    }
    
    /**
     * @param saveToDir The directory/folder to save to
     */
    public FileBasedList(String saveToDir) {
        this(saveToDir, 100);
    }
    
    /**
     * @param saveToDir The directory/folder to save to
     * @param chunkSize The size of each chunk.
     */
    public FileBasedList(String saveToDir, int chunkSize) {
        this.delegate = new FileBasedCollection(saveToDir, chunkSize);
    }

    public FileBasedList(String saveToDir, String fileId, boolean deleteFileOnClose, int chunkSize) {
        this.delegate = new FileBasedCollection(saveToDir, fileId, deleteFileOnClose, chunkSize);
    }

    @Override
    public E get(int index) {
        Object output = null;
        try(final FileBasedIterator iter = delegate.iterator()) {
            for(int i=0; iter.hasNext(); i++) {
                final Object next = iter.next();
                if(i == index) {
                    output = next; 
                    break;
                }
            }
        }catch(IOException ignored) { }
        if(output == null) {
            throw new IndexOutOfBoundsException("Index: " + index + ", size: " + delegate.getRealSize());
        }
        return (E)output;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        if(index == size()) {
            return delegate.addAll(c);
        }else{
            throw new UnsupportedOperationException("Not supported");
        }
    }

    @Override
    public void add(int index, E element) {
        if(index == size()) {
            delegate.add(element);
        }else{
            throw new UnsupportedOperationException("Not supported");
        }
    }

    @Override
    public E set(int index, E element) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public boolean add(E e) {
        return delegate.add(e);
    }
    
    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public void close() {
        this.delegate.close();
    }

    @Override
    public int size() {
        return delegate.size();
    }

    public int getChunkSize() {
        return delegate.getChunkSize();
    }

    public void sort(int bucketSize) throws IOException {
        delegate.sort(bucketSize);
    }

    public void sort() throws IOException {
        delegate.sort();
    }

    public long getRealSize() {
        return delegate.getRealSize();
    }

    public void flush() {
        delegate.flush();
    }

    @Override
    public boolean removeAll(Collection c) {
        return delegate.removeAll(c);
    }

    @Override
    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    @Override
    public boolean retainAll(Collection c) {
        return delegate.retainAll(c);
    }
}
