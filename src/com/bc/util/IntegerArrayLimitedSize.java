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

/**
 * @author Chinomso Bassey Ikwuagwu on Nov 20, 2016 1:42:52 AM
 */
public class IntegerArrayLimitedSize extends IntegerArray {

    private static final long serialVersionUID = 1409026770989149539L;

    private final int limit;

    private final float bucket;

    public IntegerArrayLimitedSize(int initialCapacity, int limit) {
        this(initialCapacity, limit, 0.2f);
    }

    public IntegerArrayLimitedSize(int initialCapacity, int limit, float bucket) {
        super(initialCapacity);
        this.validate(initialCapacity, limit, bucket);
        this.limit = limit;
        this.bucket = bucket;
    }

    public IntegerArrayLimitedSize(IntegerArray ia) {
        this(ia, ia.size(), 0.2f);
    }

    public IntegerArrayLimitedSize(IntegerArray ia, int limit, float bucket) {
        super(ia);
        this.validate(ia.size(), limit, bucket);
        this.limit = limit;
        this.bucket = bucket;
    }

    @Override
    public void add(int toAdd) {
        super.add(toAdd);
        this.truncate();
    }

    @Override
    public void addAll(int[] arr) {
        super.addAll(arr);
        this.truncate();
    }

    @Override
    public void addAll(IntegerArray arr) {
        super.addAll(arr);
        this.truncate();
    }

    private void truncate() {

        final int size = size();

        if (size > limit) {

            int toRemove = (int)(limit * bucket);

            if (toRemove < 1) {
                toRemove = 1;
            }

            final int toRetain = size - toRemove;

            this.truncate(this, toRetain);
        }
    }

    public void truncate(IntegerArrayLimitedSize intArr, int newSize) {

        if(newSize < 0) {
            throw new IllegalArgumentException();
        }

        final int size = intArr.size();

        if(size > newSize) {

            final int [] array = new int[newSize];

            for(int i=0; i<newSize; i++) {

                array[i] = this.get(i);
            }

            this.clear();
            this.addAll(array);
        }
    }

    private void validate(int initialCapacity, int limit, float bucket) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("initialCapacity <= 0. initialCapacity: "+limit);
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("limit <= 0. limit: "+limit);
        }
        if(limit < initialCapacity) {
            throw new IllegalArgumentException("limit < initialCapacity. "+limit+" < " + initialCapacity);
        }
        if(bucket <= 0) {
            throw new IllegalArgumentException("bucket <= 0. bucket: "+limit);
        }
        if(bucket > 1) {
            throw new IllegalArgumentException("bucket > 1. bucket: "+bucket);
        }
    }
}
