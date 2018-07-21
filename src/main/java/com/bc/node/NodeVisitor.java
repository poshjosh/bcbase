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
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 16, 2017 9:21:53 PM
 */
public class NodeVisitor<T, R> implements Consumer<Node<T>>{

    private static final Logger logger = Logger.getLogger(NodeVisitor.class.getName());

    private final Predicate<Node<T>> filter;
    
    private final Consumer<Node<T>> consumer;
    
    private final int depth;

    public NodeVisitor(
            Predicate<Node<T>> filter,
            Consumer<Node<T>> consumer) {
        
        this(filter, consumer, Integer.MAX_VALUE);
    }
    
    public NodeVisitor(
            Predicate<Node<T>> filter,
            Consumer<Node<T>> consumer,
            int depth) {
        this.filter = Objects.requireNonNull(filter);
        this.consumer = Objects.requireNonNull(consumer);
        this.depth = depth;
    }
    
    @Override
    public void accept(Node<T> node) {
        
        this.visit(node, this.depth);
    }
    
    public void visit(Node<T> node, int depth) {
        
        logger.finer(() -> this.toString(node));
        
        this.visit(filter, consumer, node);
        
        if(depth > 0) {
        
            final List<Node<T>> childNodeSet = node.getChildren();

            for(Node<T> childNode : childNodeSet) {

                this.visit(childNode, depth-1);
            }
        }
    }
    
    private void visit(Predicate<Node<T>> test, Consumer<Node<T>> action, Node<T> node) {
        
        final boolean testPassed = test.test(node);

        final Level level = testPassed ? Level.FINE : Level.FINER;
        
        logger.log(level, () -> "Test Passed: " + testPassed + ", node: " + node);

        if(testPassed) {
            
            action.accept(node);

            logger.fine(() -> "Processed node: " + node);
        }
    }
    
    private String toString(Node node) {
        return this.appendIndents(node, new StringBuilder()).append(node).toString();
    }
    
    private StringBuilder appendIndents(Node node, StringBuilder appendTo) {
        appendTo.append('\n');
        for(int pos = node.getLevel(); pos > 0; --pos) {
            appendTo.append('\t');
        }
        return appendTo;
    }
}
