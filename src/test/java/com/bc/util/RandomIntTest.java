/*
 * Copyright 2018 NUROX Ltd.
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

import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 20, 2018 9:13:57 PM
 */
public class RandomIntTest {

    @Test
    public void testRandomInt() {
        System.out.println("randomInt");
        
        final int start = 1;
        final int end = 1_000_000;
        
        double total = 0.0;
        
        for(int i=start; i<end; i++) {
            final double random = Util.random(i);
            assertTrue(random >= 0.0);
            assertTrue(random < i);
            total += random;
        }
        
        final int count = end - start;
System.out.println("Average random: "+(total/count)+", after generating "+count+" randoms");        
    }
}
