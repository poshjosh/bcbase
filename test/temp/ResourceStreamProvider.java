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

package temp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 22, 2017 10:46:39 PM
 */
public interface ResourceStreamProvider<R> {
    
    InputStream getInputStream(R resource) throws IOException;
    
    OutputStream getOutputStream(R resource) throws IOException;
}
