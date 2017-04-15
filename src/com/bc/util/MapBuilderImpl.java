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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 4, 2017 9:42:32 PM
 */
public class MapBuilderImpl implements MapBuilder {
    
    private final Map<Class, Method []> entityToMethodsMappings;
    
    private final Set<Class> builtTypes;
    
    private Map target;
    
    private Class sourceType;
    
    private Object source;

    private boolean nullsAllowed;
    
    private int maxDepth = Integer.MAX_VALUE;
    
    private int maxCollectionSize = Integer.MAX_VALUE;
    
    private Set<Class> typesToAccept;
    
    private Set<Class> typesToIgnore;
    
    private MapBuilder.MethodFilter methodFilter;
    
    private MapBuilder.RecursionFilter recursionFilter;
    
    private MapBuilder.Transformer transformer;

    public MapBuilderImpl() {
        this.builtTypes = new HashSet();
        this.entityToMethodsMappings = new HashMap<>();
    }

    @Override
    public Map build() {
        
        Objects.requireNonNull(source);
        
        this.entityToMethodsMappings.clear();
        this.builtTypes.clear();
        
        if(this.methodFilter == null) {
            this.methodFilter = MethodFilter.ACCEPT_ALL;
        }
        
        if(this.recursionFilter == null) {
            this.recursionFilter = RecursionFilter.DEFAULT;
        }
        
        if(this.sourceType == null) {
            this.sourceType = this.source.getClass();
        }
        
        if(this.transformer == null) {
            this.transformer = Transformer.NO_OPERATION;
        }
        
        if(this.typesToAccept == null) {
            this.typesToAccept = Collections.EMPTY_SET;
        }
        
        if(this.typesToIgnore == null) {
            this.typesToIgnore = Collections.EMPTY_SET;
        }
        
        if(this.target == null) {
            this.target = new LinkedHashMap();
        }
        
        return this.build(sourceType, source, this.transformer, 0, this.target);
    }

    protected Map build(Class srcType, Object src, Transformer tx, int depth, Map tgt) {
        
XLogger logger = XLogger.getInstance();
Level level = Level.FINER;
Class cls = this.getClass();

logger.log(level, "Building Map for entity: {0}", cls, src);

        Objects.requireNonNull(src);

        builtTypes.add(srcType);
        
        Method [] methods = this.getMethods(srcType);
        
        StringBuilder buff = new StringBuilder();
        
        for(Method method:methods) {
            
            buff.setLength(0);
            this.appendColumnName(false, method, buff);
            String key = buff.length() == 0 ? null : buff.toString();
            
            boolean foundGetterMethod = key != null;
            
            if(!foundGetterMethod) {
logger.log(level, "Not a getter method: {0}", cls, method.getName());                
                continue;
            }
            
            if(!this.mayRecurse(logger, level, method, depth)) {
                continue;
            }
            
            if(!this.methodFilter.accept(method, key)) {
                continue;
            }
            
            Object value;
            if(this.maxCollectionSize < 1 && 
                    Util.isSubclassOf(method.getReturnType(), Collection.class)) {
//System.out.println("-------------------------------------------- key: "+key+", return type: "+method.getReturnType());                
                value = null;
                
            }else{
                
                try{

                    value = method.invoke(src);

                }catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {

                    this.logMethodError(logger, cls, e, src, method, key);

                    continue;
                }
            }

            if(tx != null) {
                final String oldKey = key;
                key = tx.transformKey(src, key);
                value = tx.transformValue(src, oldKey, key, value);
            }

            if(!nullsAllowed && value == null) {
                continue;
            }
            
            if(maxDepth > 0) {

                if(value instanceof Collection) {

                    Collection collection = (Collection)value;

                    if(collection.size() > maxCollectionSize) {

                        collection = this.truncate(collection, maxCollectionSize);

                        value = collection;
                    }

                    if(collection.isEmpty()) {
                        
logger.log(level, "{0} is an empty collection", cls, key);

                        value = null;
                        
                    }else{
                    
                        final Class collectionValueType = this.getTypeOfGenericReturnTypesArgument(method);

logger.log(level, "{0} has generic type {1}", cls, key, collectionValueType);                      
                        
                        final List list = new ArrayList();

logger.log(level, "Recursing collection: {0} and {1} values", cls, key, collection.size());

                        final Iterator iter = collection.iterator();

                        ++depth;

                        while(iter.hasNext()) {

                            Object subValue = iter.next();

                            final Map subMap = build(collectionValueType, subValue, 
                                    Transformer.NO_OPERATION, depth, new LinkedHashMap());

                            list.add(subMap);
                        }

                        --depth;

                        value = list;
                    }
                }else if(value != null){

                    final Class valueType = value.getClass();
                    
                    final boolean shouldRecurse = this.recursionFilter.shouldRecurse(valueType, value);
logger.log(level, "Key: {0}, value type: {1}, should recurse: {2}", 
    cls, key, valueType, shouldRecurse); 
                    
                    if(shouldRecurse) {
logger.log(level, "Recursing value with key: {0}", cls, key);  

                        ++depth;

                        final Map valueMap = build(valueType, value, 
                                MapBuilder.Transformer.NO_OPERATION, depth, new LinkedHashMap());

                        --depth;

                        value = valueMap.isEmpty() ? null : valueMap;
//                        value = valueMap.isEmpty() ? value : valueMap;
                    }
                }
            }

//logger.log(Level.INFO, "Nulls: {0}, append: {1}, {2} = {3}", 
//        cls, nullsAllowed, (nullsAllowed || value != null), key, value);

            if(nullsAllowed || value != null) {

                this.append(srcType, src, key, value, tgt);
            }
        }
if(logger.isLoggable(level, cls))        
logger.log(level, "Extracted: {0}", cls, tgt.keySet()); 

        return tgt;
    }
    
    protected <E> void append(Class<E> entityType, E entity, String key, Object value, Map appendTo) {
        
        appendTo.put(key, value);
    }
    
    Collection truncate(Collection collection, int maxSize) {
        
        Collection output;
        
        if(maxSize < 1) {
            
            output = Collections.EMPTY_SET;
            
        }else{
            
            output = new ArrayList(maxSize);
            
            int i = 0;

            for(Object object:collection) {

                output.add(object);

                if(++i >= maxSize) {
                    break;
                }
            }
        }    
        
        return output;
    }
    
    boolean mayRecurse(XLogger log, Level level, Method method, int depth) {
        
        final Class returnType = method.getReturnType();
        
        final boolean collectionReturnType = Util.isSubclassOf(returnType, Collection.class);
        
        final Class type;
        if(!collectionReturnType) {
            type = returnType;
        }else{
            type = this.getTypeOfGenericReturnTypesArgument(method);
        }
        
        return this.mayRecurse(log, level, type, depth);
    }
    
    Class getTypeOfGenericReturnTypesArgument(Method method) {
        final Type genericReturnType = method.getGenericReturnType();
        ParameterizedType parameterizedType = (ParameterizedType)genericReturnType;
        Type [] typeArg = parameterizedType.getActualTypeArguments();
        return (Class)typeArg[0];
    }
    
    boolean mayRecurse(XLogger log, Level level, Class type, int depth) {
        
if(log.isLoggable(level, this.getClass()))
log.log(level, "Type: {0}, depth < maxDepth: {1}, !typesToIgnore.contains(type): {2}, !builtTypes.contains(type): {3}", 
this.getClass(), type.getName(), (depth<maxDepth), !typesToIgnore.contains(type), !builtTypes.contains(type));

        return depth < maxDepth  && !builtTypes.contains(type) 
                && (typesToAccept.contains(type) || (typesToAccept.isEmpty() && !typesToIgnore.contains(type)));
    }
    
    Method [] getMethods(Class entityType) {
        Method [] output = entityToMethodsMappings.get(entityType);
        if(output == null) {
            output = entityType.getMethods();
            entityToMethodsMappings.put(entityType, output);
        }
        return output;
    }
    
    void logMethodError(XLogger logger, Class cls, Exception e, Object entity, Method method, String key) {
        StringBuilder msg = new StringBuilder();
        msg.append("Object: ").append(entity);
        msg.append(", Method: ").append(method.getName());
        msg.append(", key: ").append(key);
        logger.log(Level.WARNING, msg.toString(), cls, e);
    }

    /**
     * Methods of the {@link java.lang.Object} class are not considered
     * @param setter boolean, if true only setter methods are considered
     * @param method Method. The method for which a column with a name 
     * matching the method name will be returned/
     * @return A column whose name matches the input Method name, or null if
     * no such column could be inferred.
     */
    private void appendColumnName(boolean setter, Method method, StringBuilder buff) {

        final String prefix = setter ? "set" : "get";
        
        String methodName = method.getName();
        
        if(method.getDeclaringClass() == Object.class || prefix != null && 
                !methodName.startsWith(prefix)) {
        
            return;
            
        }else{
        
            final int prefixLen = prefix == null ? 0 : prefix.length();
            final int len = methodName.length();

            boolean doneFirst = false;
            for(int i=0; i<len; i++) {

                if(i < prefixLen) {
                    continue;
                }

                char ch = methodName.charAt(i);

                if(!doneFirst) {
                    doneFirst = true;
                    ch = Character.toLowerCase(ch);
                }

                buff.append(ch);
            }
        }
    }

    @Override
    public MapBuilder target(Map target) {
        this.target = target;
        return this;
    }
    
    @Override
    public MapBuilder sourceType(Class sourceType) {
        this.sourceType = sourceType;
        return this;
    }

    @Override
    public MapBuilder source(Object source) {
        this.source = source;
        return this;
    }

    @Override
    public MapBuilder nullsAllowed(boolean nullsAllowed) {
        this.nullsAllowed = nullsAllowed;
        return this;
    }

    @Override
    public MapBuilder maxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }

    @Override
    public MapBuilder maxCollectionSize(int maxCollectionSize) {
        this.maxCollectionSize = maxCollectionSize;
        return this;
    }

    @Override
    public MapBuilder typesToAccept(Set<Class> typesToAccept) {
        this.typesToAccept = typesToAccept;
        return this;
    }

    @Override
    public MapBuilder typesToIgnore(Set<Class> typesToIgnore) {
        this.typesToIgnore = typesToIgnore;
        return this;
    }

    @Override
    public MapBuilder methodFilter(MethodFilter methodFilter) {
        this.methodFilter = methodFilter;
        return this;
    }

    @Override
    public MapBuilder recursionFilter(RecursionFilter recursionFilter) {
        this.recursionFilter = recursionFilter;
        return this;
    }

    @Override
    public MapBuilder transformer(Transformer transformer) {
        this.transformer = transformer;
        return this;
    }

    public Class getSourceType() {
        return sourceType;
    }

    public Object getSource() {
        return source;
    }

    public boolean isNullsAllowed() {
        return nullsAllowed;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public int getMaxCollectionSize() {
        return maxCollectionSize;
    }

    public Set<Class> getTypesToAccept() {
        return typesToAccept;
    }

    public Set<Class> getTypesToIgnore() {
        return typesToIgnore;
    }

    public MethodFilter getMethodFilter() {
        return methodFilter;
    }

    public RecursionFilter getRecursionFilter() {
        return recursionFilter;
    }

    public Transformer getTransformer() {
        return transformer;
    }
}
