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

package com.bc.node;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 13, 2017 2:58:54 PM
 * @param <V> The type of the value returned by this node
 */
public interface Node<V> {
    
    public static Node of(String name) {
        return new NodeImpl(name);
    }
    
    public static <V> Node<V> of(String name, V value, Node<V> parent) {
        return new NodeImpl(name, value, parent);
    }
    
    default boolean isRoot() {
        return getParentOrDefault(null) == null;
    }

    default boolean isLeaf() {
        return getChildren().isEmpty();
    }
        
    /**
     * The root node is the only node at level <code>Zero (0)</code>
     * The direct children of the root node are at level <code>One (0)</code>
     * and so on and forth
     * @return The level of this node
     * @see #getRoot() 
     */
    default int getLevel() {
        final Node<V> parent = this.getParentOrDefault(null);
        if (parent != null) {
            return parent.getLevel() + 1;
        }else{
            return 0;
        }        
    }

    /**
     * @return The topmost <tt>parent node</tt> in this node's heirarchy.
     */
    default Node<V> getRoot() {
        Node<V> target = this;
        while(target.getParentOrDefault(null) != null) {
            target = (Node)target.getParentOrDefault(null);
        }
        return target;
    }
    
    default Optional<Node<V>> findFirstChild(V... path) {
        
        return this.findFirst(this, path);
    }
    
    Optional<Node<V>> findFirst(Node<V> offset, V... path);
        
    default Optional<Node<V>> findFirstChild(Predicate<Node<V>> nodeTest) {
        
        return this.findFirst(this, nodeTest);
    }

    Optional<Node<V>> findFirst(Node<V> offset, Predicate<Node<V>> nodeTest);
            
    boolean addChild(Node<V> child);
    
    /**
     * @return An <b>un-modifiable</b> list view of this node's children
     */
    List<Node<V>> getChildren();

    String getName();
    
    default Optional<V> getValue() {
        return Optional.ofNullable(this.getValueOrDefault(null));
    }
    
    V getValueOrDefault(V outpufIfNone);

    default Optional<Node<V>> getParent() {
        return Optional.ofNullable(this.getParentOrDefault(null));
    }
    
    Node<V> getParentOrDefault(Node<V> outputIfNone);
}
/**
 * 
    default int getLevel_old() {
        int level = 0;
        Node target = this;
        while(target.getParentOrDefault(null) != null) {
            target = (Node)target.getParentOrDefault(null);
            ++level;
        }
        return level;
    }
 * 
 */