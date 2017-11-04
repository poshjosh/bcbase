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

import java.util.Objects;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 15, 2017 12:37:05 AM
 */
public class NodeTest extends NodeValueTest {

    private final String name;

    public NodeTest(Node<String> nodeToFind, boolean caseInsensitiveValues) {
        this(
                nodeToFind.getLevel(), nodeToFind.getName(),
                nodeToFind.getValueOrDefault(null), caseInsensitiveValues);
    }
    
    public NodeTest(int level, String name, String value, boolean caseInsensitiveValues) {
        super(level, value, caseInsensitiveValues);
        this.name = Objects.requireNonNull(name);
    }
    
    @Override
    public boolean test(Node<String> candidate) {
        final boolean output;
        if(!name.equalsIgnoreCase(candidate.getName())) {
            output = false;
        }else{
            output = super.test(candidate);
        }
        return output;
    }
}
