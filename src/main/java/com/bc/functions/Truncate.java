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

package com.bc.functions;

import java.util.function.BiFunction;

/**
 * @author Chinomso Bassey Ikwuagwu on Dec 2, 2017 11:33:08 PM
 */
public class Truncate implements BiFunction<String, Integer, String> {

    private final boolean ellipsize;

    public Truncate() {
        this(false);
    }
    
    public Truncate(boolean ellipsize) {
        this.ellipsize = ellipsize;
    }
    
    @Override
    public String apply(String str, Integer maxLen) {
        String output;
        if(str == null || str.isEmpty() || str.length() <= maxLen) {
            output = str;
        }else {
            final String prefix = ellipsize ? "..." : "";
            output = str.substring(0, maxLen-prefix.length()) + prefix;
        }
        return output;
    }
}
