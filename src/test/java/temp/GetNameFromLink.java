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

package temp;

import java.util.function.Function;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 19, 2018 5:03:13 PM
 */
public class GetNameFromLink implements Function<String, String> {

    @Override
    public String apply(String link) {

        final String s = link.replace('\\', '/');
        
        final String name = s.substring(s.lastIndexOf('/') + 1);
        
        return name;
    }
}
