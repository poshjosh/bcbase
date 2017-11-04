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

import java.util.function.Predicate;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 15, 2017 12:37:05 AM
 */
public class NodeValueTest implements Predicate<Node<String>> {

    private final boolean caseInsensitive;
    
    private final int level;
    
    private final String value;

    public NodeValueTest(Node<String> nodeToFind, boolean caseInsensitive) {
        this(nodeToFind.getLevel(), nodeToFind.getValueOrDefault(null), caseInsensitive);
    }
    
    public NodeValueTest(int level, String value, boolean caseInsensitiveValues) {
        this.level = level;
        this.value = value;
        this.caseInsensitive = caseInsensitiveValues;
    }
    
    @Override
    public boolean test(Node<String> candidate) {
        final boolean output;
        if(level > -1 && level != candidate.getLevel()) {
            output = false;
        }else{
            final String val = candidate.getValueOrDefault(null);
            output = (val == null && value == null) || 
                    (val != null && eq(val, value));
        }
        return output;
    }

    private boolean eq(String a, String b) {
        return !this.caseInsensitive ? a.equals(b) : a.equalsIgnoreCase(b);
    }
}
