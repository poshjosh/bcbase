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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 4, 2017 9:42:32 PM
 * @edited shows changes waiting to be tested
 */
public class MapBuilderImpl implements MapBuilder {
    
    private static final XLogger logger = XLogger.getInstance();

    private final Map<Class, Method []> entityToMethodsMappings;
    
    private final Set<Class> builtTypes;
    
    private final Set<Class> builtTypesWithinCollection;
    
    private Class currentCollectionGenericType;
    
    private final ReflectionUtil reflection;
    
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
    
    private MapBuilder.ContainerFactory containerFactory;
    
    private MapBuilder.Transformer transformer;
    
    public MapBuilderImpl() {
        this.reflection = new ReflectionUtil();
        this.builtTypes = new HashSet();
        this.builtTypesWithinCollection = new HashSet();
        this.entityToMethodsMappings = new HashMap<>();
    }

    @Override
    public Map build() {
        
        this.entityToMethodsMappings.clear();
        this.builtTypes.clear();
        this.builtTypesWithinCollection.clear();
        
        if(this.methodFilter == null) {
            this.methodFilter = MethodFilter.ACCEPT_ALL;
        }
        
        if(this.recursionFilter == null) {
            this.recursionFilter = RecursionFilter.DEFAULT;
        }
        
        if(this.containerFactory == null) {
            this.containerFactory = ContainerFactory.DEFAULT;
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
            this.target = this.containerFactory.createMapContainer();
        }
        
        return this.build(sourceType, source, this.transformer, 0, this.target, this.builtTypes, false);
    }

    protected Map build(Class srcType, Object src, Transformer transformer, 
            int depth, Map tgt, Set<Class> alreadyBuilt, boolean addToAlreadyBuilt) {
        
        if(srcType == null && src != null) {
            srcType = src.getClass();
        }
        if(srcType != null && src == null) {
            src = this.newInstance(srcType);
        }
        
        final Level level = Level.FINER;
        final Class cls = this.getClass();
        
        logger.log(level, "= = = = = Building Map for: {0}", cls, src);

        Objects.requireNonNull(srcType);
        Objects.requireNonNull(src);

        if(addToAlreadyBuilt && alreadyBuilt != null) {
            alreadyBuilt.add(srcType);
        }
        
        final Method [] methods = this.getMethods(srcType);
        
        final StringBuilder buff = new StringBuilder();
        
        for(Method method : methods) {
            
            String key = this.appendColumnName(false, method, buff, null);
            
            if(key == null) {
                logger.log(Level.FINEST, "Not a getter method: {0}", cls, method.getName());                
                continue;
            }
            
            logger.log(level, "- - - - - Processing {0}#{1}", cls, srcType.getName(), key); 

            if(!this.filterTypesToAcceptOrIgnore(level, method, depth)) {
                continue;
            }
            
            if(!this.methodFilter.test(srcType, src, method, key)) {
                logger.log(level, "Rejected: {0}#{1}", cls, srcType.getName(), key);     
                continue;
            }
            
            Class valueType = method.getReturnType();
            
            final boolean collectionType = Collection.class.isAssignableFrom(valueType);
            final Class collectionValueType = !collectionType ? null : (Class)this.reflection.getGenericReturnTypeArguments(method)[0];
            
            if(this.isAlreadyBuilt(level, collectionType ? collectionValueType : valueType, key)) {
                continue;
            }
            
            if(this.maxCollectionSize < 1 && collectionType) {
                continue;
            }
            
            Object value;
            try{
                logger.log(level, "BEFORE {0}#{1}", cls, src.getClass().getName(), method.getName());

                value = method.invoke(src);

                logger.log(level, " AFTER {0}#{1} = {2}", cls, src.getClass().getName(), method.getName(), value);

            }catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {

                this.logMethodError(e, srcType, src, method, key);

                continue;
            }

            if(transformer != null) {
                
                final String oldKey = key;
                
                key = transformer.transformKey(src, key);
                
                value = transformer.transformValue(src, oldKey, key, value);
                
                if(value != null && !valueType.isAssignableFrom(value.getClass())) {
                    logger.log(level, "Changing value type from {0} to {1}",
                            cls, valueType, value.getClass());
                    valueType = value.getClass();
                }
            }

            if(!nullsAllowed && value == null) {
                continue;
            }
            
            if(maxDepth > 0) {

                if(value instanceof Collection) {

                    Collection collection = (Collection)value;

                    logger.log(level, "Collection: {0} of generic type: {1}, and {2} values", 
                            cls, key, collectionValueType.getName(), collection.size());

                    if(collection.size() > maxCollectionSize) {

                        collection = this.truncate(collection, maxCollectionSize);

                        value = collection;
                    }

                    if(!collection.isEmpty()) {
                        
                        final boolean shouldRecurse = this.shouldRecurse(level, collectionValueType, key, null);

                        if(!shouldRecurse) {
                            continue;
                        }
                        
                        logger.log(level, "Recursing collection: {0} of generic type: {1}, and {2} values", 
                                cls, key, collectionValueType.getName(), collection.size());

                        final Collection update = this.containerFactory.createCollectionContainer();

                        final Iterator iter = collection.iterator();

                        ++depth;
                        
                        this.currentCollectionGenericType = collectionValueType;
                        
                        while(iter.hasNext()) {

                            final Object subValue = iter.next();
                            
                            if(!collectionValueType.isAssignableFrom(subValue.getClass())) {
                            
                                logger.log(Level.WARNING, "collection generic type: {0} is not assignable from: {1}",
                                        cls, collectionValueType, subValue.getClass());
                                continue;       
                            }
                            
                            this.builtTypesWithinCollection.clear();
                            
                            final Map subMap = build(collectionValueType, subValue, 
                                    Transformer.NO_OPERATION, depth, this.containerFactory.createMapContainer(), 
                                    this.builtTypesWithinCollection, !iter.hasNext());

                            update.add(subMap);
                        }
                        
                        this.currentCollectionGenericType = null;

                        --depth;

                        value = update;
                    }
                }else if(value != null && valueType != null){

                    final boolean shouldRecurse = this.shouldRecurse(level, valueType, key, value);
                    
                    if(shouldRecurse) {
                        
                        ++depth;
                        
                        final Map valueMap = build(valueType, value, 
                                MapBuilder.Transformer.NO_OPERATION, depth, this.containerFactory.createMapContainer(), 
                                this.currentCollectionGenericType == null ? this.builtTypes : this.builtTypesWithinCollection, 
                                true);

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
        if(logger.isLoggable(level, cls)) {        
            logger.log(level, "Extracted: {0}", cls, tgt.keySet());
        }

        return tgt;
    }
    
    public boolean shouldRecurse(Level level, Class valueType, String key, Object value) {
        
        final boolean shouldRecurse = 
                !this.isAlreadyBuilt(level, valueType, key) && this.recursionFilter.test(valueType, value);

        logger.log(level, "Should recurse: {0}, {1} {2}", 
            this.getClass(), shouldRecurse, valueType.getName(), key); 

        return shouldRecurse;
    }
    
    public boolean isAlreadyBuilt(Level level, Class type, String key) {
        final boolean output;
        if(this.currentCollectionGenericType == null) {
            output = this.builtTypes.contains(type);
        }else{
            if(this.currentCollectionGenericType.equals(type)) {
                output = true;
            }else{
                output = this.builtTypes.contains(type) || this.builtTypesWithinCollection.contains(type);
            }
        }
        logger.log(level, "Already built: {0}, {1} {2}", 
            this.getClass(), output, type.getName(), key); 
        return output;
    }
    
    protected <E> void append(Class<E> entityType, E entity, String key, Object value, Map appendTo) {
        
        appendTo.put(key, value);
    }
    
    public Collection truncate(Collection collection, int maxSize) {
        
        Collection output;
        
        if(maxSize < 1) {
            
            output = Collections.EMPTY_SET;
            
        }else{
            
            
            output = this.containerFactory.createCollectionContainer(maxSize);
            
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
    
    private boolean filterTypesToAcceptOrIgnore(Level level, Method method, int depth) {
        
        final Class returnType = method.getReturnType();
        
        final boolean collectionReturnType = Collection.class.isAssignableFrom(returnType);
        
        final Class type;
        if(!collectionReturnType) {
            type = returnType;
        }else{
            type = (Class)this.reflection.getGenericReturnTypeArguments(method)[0];
        }
        
        return this.filterTypesToAcceptOrIgnore(level, type, depth);
    }
    
    private boolean filterTypesToAcceptOrIgnore(Level level, Class type, int depth) {
        
if(logger.isLoggable(level, this.getClass()))
logger.log(level, "Type: {0}, depth < maxDepth: {1}, !typesToIgnore.contains(type): {2}, !builtTypes.contains(type): {3}", 
this.getClass(), type.getName(), (depth<maxDepth), !typesToIgnore.contains(type), !builtTypes.contains(type));

        return depth < maxDepth  && 
//                !builtTypes.contains(type) &&  @edited removed
                (typesToAccept.contains(type) || (typesToAccept.isEmpty() && !typesToIgnore.contains(type)));
    }
    
    Method [] getMethods(Class entityType) {
        Method [] output = entityToMethodsMappings.get(entityType);
        if(output == null) {
            output = entityType.getMethods(); 
//            XLogger.getInstance().log(Level.FINER, "Entity type: {0}, methods: {1}", 
//                    this.getClass(), entityType, output == null ? null : Arrays.toString(output));
            entityToMethodsMappings.put(entityType, output);
        }
        return output;
    }
    
    void logMethodError(Exception e, Class type, Object value, Method method, String key) {
        StringBuilder msg = new StringBuilder();
        msg.append("Object type: ").append(type.getName());
        msg.append(", Object: ").append(value);
        msg.append(", Method: ").append(method.getName());
        msg.append(", key: ").append(key);
        logger.log(Level.WARNING, msg.toString(), this.getClass(), e);
    }

    /**
     * Methods of the {@link java.lang.Object} class are not considered
     * @param setter boolean, if true only setter methods are considered
     * @param method Method. The method for which a column with a name 
     * matching the method name will be returned/
     * @return A column whose name matches the input Method name, or null if
     * no such column could be inferred.
     */
    private String appendColumnName(boolean setter, Method method, StringBuilder buff, String outputIfNone) {

        final String output;
        
        buff.setLength(0);

        final String prefix = setter ? "set" : "get";
        
        String methodName = method.getName();
        
        if(method.getDeclaringClass() == Object.class || 
                !methodName.startsWith(prefix)) {
        
            output = null;
            
        }else{
        
            final int prefixLen = prefix.length();
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
            
            output = buff.length() == 0 ? null : buff.toString();
        }
        
        return output == null ? outputIfNone : output;
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
    public MapBuilder containerFactory(ContainerFactory containerFactory) {
        this.containerFactory = containerFactory;
        return this;
    }

    @Override
    public MapBuilder transformer(Transformer transformer) {
        this.transformer = transformer;
        return this;
    }
    
    public <T> T newInstance(Class<T> type) {
        try{
            return type.getConstructor().newInstance();
        }catch(NoSuchMethodException | SecurityException | InstantiationException | 
                IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
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
