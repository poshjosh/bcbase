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
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 4, 2017 9:50:43 PM
 */
public interface MapBuilder {
    
    @FunctionalInterface
    interface MethodFilter {
        
        MethodFilter ACCEPT_ALL = (Class type, Object instance, Method method, String columnName) -> true;
        
        boolean test(Class type, Object instance, Method method, String columnName);
        
        default MethodFilter and(MethodFilter other) {
            return (type, instance, method, columnName) -> 
                    this.test(type, instance, method, columnName)
                    && other.test(type, instance, method, columnName);
        }
        
        default MethodFilter negate() {
            return (type, instance, method, columnName) -> !this.test(type, instance, method, columnName);
        }

        default MethodFilter or(MethodFilter other) {
            return (type, instance, method, columnName) -> 
                    this.test(type, instance, method, columnName)
                    || other.test(type, instance, method, columnName);
        }
    }
    
    @FunctionalInterface
    interface RecursionFilter extends BiPredicate<Class, Object> {
        
        RecursionFilter DEFAULT = (Class type, Object instance) -> !type.isPrimitive();
    }
    
    interface ContainerFactory{
        ContainerFactory DEFAULT = new ContainerFactory() {
            @Override
            public Map createMapContainer() {
                return new LinkedHashMap();
            }
            @Override
            public Collection createCollectionContainer() {
                return this.createCollectionContainer(10);
            }
            @Override
            public Collection createCollectionContainer(int initialCapacity) {
                return new ArrayList(initialCapacity);
            }
        };
        Map createMapContainer();
        Collection createCollectionContainer();
        Collection createCollectionContainer(int initialCapacity);
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
    
    MapBuilder containerFactory(ContainerFactory containerFactory);

    MapBuilder transformer(Transformer transformer);
}
