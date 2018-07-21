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

package com.bc.selection;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 29, 2017 8:22:09 PM
 */
public interface SelectionContext extends SelectionValues<Class> {
    
    SelectionContext NO_OP = new SelectionContext() {
        @Override
        public boolean isSelectionType(Class entityType) { return false; }
        @Override
        public String getSelectionColumn(Class entityType, String outputIfNone) { return outputIfNone; }
        @Override
        public List<Selection> getSelectionValues(Class entityType) { return Collections.EMPTY_LIST; }
        @Override
        public <T> Selection<T> getDefaultSelection(Class<T> entityType, String columnName) {
            throw new UnsupportedOperationException("Not supported."); 
        }
        @Override
        public <T> Selection<T> getSelection(T entity) {
            Objects.requireNonNull(entity);
            final Class entityType = entity.getClass();
            return this.getSelection(entityType, entity, this.getSelectionColumn(entityType, null));
        }
        @Override
        public <T> Selection<T> getSelection(Class<T> entityType, T entity, String columnName) {
            throw new UnsupportedOperationException("Not supported."); 
        }
    };
    
    boolean isSelectionType(Class entityType);
    
    String getSelectionColumn(Class entityType, String outputIfNone);
    
    <T> Selection<T> getDefaultSelection(Class<T> entityType, String columnName);
    
    <T> Selection<T> getSelection(T entity);
    
    <T> Selection<T> getSelection(Class<T> entityType, T entity, String columnName);
}
