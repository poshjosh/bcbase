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

    public Object getValue(Object object, String name) {
        
        return this.getValue(object.getClass(), object, object.getClass().getDeclaredMethods(), name);
    }
    
    public Object getValue(Class aClass, 
            Object object, Method [] methods, String name) {
        
        final Method method = getMethod(false, methods, name);

        if(method == null) {
            throw new IllegalArgumentException("Could not find matching method for: "+name+" in class: "+aClass);
        }
        
        try{
            
            return method.invoke(object);
            
        }catch(Exception e) {
            
            StringBuilder builder = new StringBuilder("Error getting entity value.");
            builder.append(" Entity: ").append(object);
            builder.append(", Method: ").append(method==null?null:method.getName());
            builder.append(", Column: ").append(name);

            throw new UnsupportedOperationException(builder.toString(), e);
        }
    }

    public void setValue(Object object, String name, Object value) {
        
        this.setValue(object.getClass(), object, object.getClass().getDeclaredMethods(), name, value);
    }
    
    public void setValue(Class aClass, 
            Object object, Method [] methods, 
            String name, Object value) {
        
        final Method method = getMethod(true, methods, name);
        if(method == null) {
            throw new IllegalArgumentException("Could not find matching method for: "+name+" in class: "+aClass);
        }
        
        try{
            
            method.invoke(object, value);
            
        }catch(Exception e) {
            
            StringBuilder builder = new StringBuilder("Error setting entity value.");
            builder.append(" Object: ").append(object);
            builder.append(", Method: ").append(method==null?null:method.getName());
            builder.append(", Name: ").append(name);
            builder.append(", Value: ").append(value);
            builder.append(", Value type: ").append(value==null?null:value.getClass());
            builder.append(", Expected type: ").append(method==null?null:method.getParameterTypes()[0]);

            throw new UnsupportedOperationException(builder.toString(), e);
        }
    }
    
    /**
     * Methods of the {@link java.lang.Object} class are not considered
     * @param setter boolean, if true only setter methods are considered
     * @param methods The array of methods within which to search for a 
     * method matching the specified column name.
     * @param columnName The column name for which a method with a matching
     * name is to be returned.
     * @return A method whose name matches the input columnName or null if none was found
     */
    public Method getMethod(boolean setter, Method [] methods, String columnName) {
        Method method = null;
        final String prefix = setter ? "set" : "get";
        // remove all _
        //
        String normalizedColName = removeAll(columnName, '_').toString();
        for(Method m:methods) {
            if(m.getDeclaringClass() == Object.class) {
                continue;
            }
            String methodName = m.getName();
            if(!methodName.startsWith(prefix)) {
                continue;
            }
            // remove get or set
            //
            String normalizedMethodName = methodName.substring(prefix.length());
            if(normalizedColName.equalsIgnoreCase(normalizedMethodName)) {
                method = m;
                break;
            }
        }
        return method;
    }
    
    private StringBuilder removeAll(String input, char toRemove) {
        StringBuilder builder = new StringBuilder(input.length());
        for(int i=0; i<input.length(); i++) {
            char ch = input.charAt(i);
            if(ch == toRemove) {
                continue;
            }
            builder.append(ch);
        }
        return builder;
    }
}
