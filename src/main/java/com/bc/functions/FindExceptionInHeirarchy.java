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

import java.io.Serializable;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 4, 2017 8:31:52 PM
 */
public class FindExceptionInHeirarchy implements Serializable, 
        BiFunction<Throwable, Predicate<Throwable>, Optional<Throwable>> {

    public FindExceptionInHeirarchy() { }

    public <T> Optional<T> apply(Throwable t, Class<T> type) {
        
        final Predicate<Throwable> predicate = (e) -> type.isAssignableFrom(e.getClass());
                
        return (Optional<T>)this.apply(t, predicate);
    }

    @Override
    public Optional<Throwable> apply(Throwable t, Predicate<Throwable> test) {
        
        final Throwable found = this.apply(t, test, null);
        
        return Optional.ofNullable(found);
    }

    public Throwable apply(Throwable t, Predicate<Throwable> test, Throwable outputIfNone) {
        
        Throwable output = null;

        while(true) {

            if(test.test(t)) {

                output = t;

                break;
            }

            t = t.getCause();

            if(t == null) {

                break;
            }
        }
        
        return output == null ? outputIfNone : output;
    }
}
