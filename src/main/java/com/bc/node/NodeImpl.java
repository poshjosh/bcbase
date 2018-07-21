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

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 13, 2017 3:36:07 PM
 */
public class NodeImpl<V> implements Node<V>, Serializable {

    private final String name;
    
    private final V value;
    
    private final Node<V> parent;
    
    private final List<Node<V>> children;

    public NodeImpl(String name) {
        this(name, null, null);
    }
    
    public NodeImpl(String name, V value, Node<V> parent) {
        this.name = Objects.requireNonNull(name);
        this.value = value;
        this.parent = parent;
        this.children = new LinkedList<>();
        if(parent != null) {
            if(this.equals(parent)) {
                throw new IllegalArgumentException("A node may not be parent to itself"); 
            }
            this.parent.addChild(NodeImpl.this);
        }
    }
    
    @Override
    public boolean addChild(Node<V> child) {
        final Object ref = child.getParentOrDefault(null);
        if(Objects.equals(ref, this)) {
            if(!this.children.contains(child)) {    
                return this.children.add(child);
            }else{
                return false;
            }
        }else{
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Optional<Node<V>> findFirst(Node<V> offset, V... path) {
        for (V valueToFind : path) {
//System.out.println("\t\tTo find: " + valueToFind + ", level: " + offset.getLevel() + ", start node: " + offset);            
            final Predicate<Node<V>> nodeTest = (node) -> Objects.equals(node.getValueOrDefault(null), valueToFind);
            final Optional<Node<V>> foundNode = this.findFirst(offset, nodeTest);
            if(foundNode.isPresent()) {
                offset = foundNode.get();
//System.out.println("\t\tFound: " + valueToFind + ", in node: " + offset);            
            }else{
                offset = null;
//System.out.println("\t\tNOT Found: " + valueToFind);            
                break;
            }
        }
        return Optional.ofNullable(offset);
    }
    
    @Override
    public Optional<Node<V>> findFirst(Node<V> offset, Predicate<Node<V>> nodeTest) {
        
        Node<V> found = null;
        
        if(nodeTest.test(offset)) {
            found = offset;
        }else{

            final List<Node<V>> childNodes = offset.getChildren();

            for(Node child : childNodes) {

                final Optional<Node<V>> foundInChild = this.findFirst(child, nodeTest);

                if(foundInChild.isPresent()) {

                    found = foundInChild.get();

                    break;
                }
            }
        }
        
//        System.out.println("\t\tFound: " + (found != null) + ", in node: " + offset);
        
        return Optional.ofNullable(found);
    }

    @Override
    public boolean isLeaf() {
        return children.isEmpty();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public V getValueOrDefault(V outputIfNone) {
        return value == null ? outputIfNone : value;
    }
    

    @Override
    public Node<V> getParentOrDefault(Node<V> outputIfNone) {
        return parent == null ? outputIfNone : parent;
    }

    /**
     * @return An <b>un-modifiable</b> list view of this node's children
     */
    @Override
    public List<Node<V>> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + Objects.hashCode(this.name);
        hash = 11 * hash + Objects.hashCode(this.value);
        hash = 11 * hash + Objects.hashCode(this.parent);
        hash = 11 * hash + Objects.hashCode(this.children);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NodeImpl<?> other = (NodeImpl<?>) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        if (!Objects.equals(this.parent, other.parent)) {
            return false;
        }
        if (!Objects.equals(this.children, other.children)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "NodeImpl{" + name + '=' + value + ", parent=" + (parent == null ? null : ("Node{"+parent.getName()+'='+parent.getValueOrDefault(null)+"}")) + ", children=" + children.size() + '}';
    }
}
