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

package com.bc.util;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 4, 2017 9:50:43 PM
 */
public interface MapBuilder {
    
    interface MethodFilter {
        MethodFilter ACCEPT_ALL = new MethodFilter() {
            @Override
            public boolean accept(Method method, String columnName) {
                return true;
            }
        };
        boolean accept(Method method, String columnName);
    }
    
    interface RecursionFilter {
        RecursionFilter DEFAULT = new RecursionFilter() {
            @Override
            public boolean shouldRecurse(Class type, Object value) {
                return !type.isPrimitive();
            }
        };
        boolean shouldRecurse(Class type, Object value);
    }

    interface Transformer {
        
        Transformer NO_OPERATION = new Transformer() {
            @Override
            public String transformKey(Object entity, String key) {
                return key;
            }
            @Override
            public Object transformValue(Object entity, String oldKey, String newKey, Object value) {
                return value;
            }
        };
        
        String transformKey(Object entity, String key);
        
        Object transformValue(Object entity, String oldKey, String newKey, Object value);
    }
    
    Map build();
    
    MapBuilder target(Map target);
    
    MapBuilder maxCollectionSize(int maxCollectionSize);

    MapBuilder maxDepth(int maxDepth);

    MapBuilder nullsAllowed(boolean nullsAllowed);
    
    MapBuilder source(Object source);

    MapBuilder sourceType(Class sourceType);

    MapBuilder typesToAccept(Set<Class> typesToAccept);

    MapBuilder typesToIgnore(Set<Class> typesToIgnore);

    MapBuilder methodFilter(MethodFilter methodFilter);

    MapBuilder recursionFilter(RecursionFilter recursionFilter);

    MapBuilder transformer(Transformer transformer);
}
