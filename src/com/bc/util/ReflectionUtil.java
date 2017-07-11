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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 25, 2017 8:51:02 PM
 */
public class ReflectionUtil {
    
    public ReflectionUtil() { }
    
    public Object newInstanceForCollectionType(Class type) {
        try{
            return this.newInstance(type);
        }catch(RuntimeException ignored) {
            return this.newInstance(this.getClassForCollectionType(type));
        }
    }
    
    public Class getClassForCollectionType(Class type) {
        if(Set.class.isAssignableFrom(type)) {
            return LinkedHashSet.class;
        }else if(Collection.class.isAssignableFrom(type)) {
            return ArrayList.class;
        }else if(Map.class.isAssignableFrom(type)) {
            return LinkedHashMap.class;
        }else{
            throw new UnsupportedOperationException();
        }
    }
    
    public <T> T newInstance(Class<T> entityType) {
        try{
            return entityType.getConstructor().newInstance();
        }catch(NoSuchMethodException | SecurityException | InstantiationException | 
                IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    
    public Type [] getGenericReturnTypeArguments(Method method) {
        final Type genericReturnType = method.getGenericReturnType();
        return ((ParameterizedType)genericReturnType).getActualTypeArguments();
    }

    public List<Type []> getGenericParameterTypeArguments(Method method) {
        final Type [] genericParameterTypes = method.getGenericParameterTypes();
        final List<Type[]> output = new ArrayList(genericParameterTypes.length);
        for(Type t : genericParameterTypes) {
            if(t instanceof ParameterizedType) {
                final Type [] actual = ((ParameterizedType)t).getActualTypeArguments();
                output.add(actual);
            }
        }
        return output;
    }
    
    public Type [] getGenericTypeArguments(Field method) {
        final Type genericReturnType = method.getGenericType();
        return ((ParameterizedType)genericReturnType).getActualTypeArguments();
    }
}
