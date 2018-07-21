/*
 * Copyright 2017 NUROX Ltd.
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

package com.bc.functions;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Nov 4, 2017 9:45:04 PM
 */
public class GetSingle<T> implements Function<List<T>, T>, Serializable {

    private transient static final Logger LOG = Logger.getLogger(GetSingle.class.getName());

    @Override
    public T apply(List<T> list) {
        return this.getOrException(list);
    }
    
    public T getOrException(List<T> list) {
        final T output = this.getOrDefault(list, null);
        if(output == null) {
            throw new IllegalArgumentException("List item not found");
        }
        return output;
    }

    public T getOrDefault(List<T> list, T outputIfNone) {
        Objects.requireNonNull(list, () -> "Expected list of size: 1, found: null");
        if(list.isEmpty()) {
            return outputIfNone;
        }else if(list.size() > 1) {
            LOG.finest(() -> "Results: " + (list.size() > 100 ? list.subList(0, 100) : list));
            throw new IllegalArgumentException("Expected 1 item, found: " + list.size());
        }else{
            return list.get(0);
        }
    }
}
