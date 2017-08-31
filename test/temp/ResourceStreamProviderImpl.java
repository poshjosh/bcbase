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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 22, 2017 10:48:37 PM
 */
public class ResourceStreamProviderImpl implements ResourceStreamProvider {

    private static final Logger logger = Logger.getLogger(ResourceStreamProviderImpl.class.getName());

    private final ClassLoader classLoader;

    public ResourceStreamProviderImpl(ClassLoader classLoader) {
        this.classLoader = Objects.requireNonNull(classLoader);
    }
    
    @Override
    public InputStream getInputStream(Object source) throws IOException {
        if(source.getClass().equals(String.class)) {
            final String sval = source.toString();
            InputStream in = this.classLoader.getResourceAsStream(sval);
            if(in == null){
                try{
                    return getInputStream(Paths.get((String)source));
                }catch(IOException e) {
                    return getInputStream(URI.create(sval));
                }
            }else{
                return in;
            }
        }else if(source instanceof Path) {
            return getInputStream(((Path)source).toFile());
        }else if(source instanceof File) {
            return new FileInputStream((File)source);
        }else if(source instanceof URI) {
            return getInputStream(((URI)source).toURL());
        }else if(source instanceof URL) {
            return ((URL)source).openStream();
        }else{
            return this.getInputStream(source.toString());
        }
    }

    @Override
    public OutputStream getOutputStream(Object resource) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public Path getPath(URI uri, Path outputIfNone) {
        Path output;
        try{
            output = Paths.get(uri);
        }catch(java.nio.file.FileSystemNotFoundException fsnfe) {
            
            logger.log(Level.WARNING, "For URI: "+uri, fsnfe);
            
            try{
                
                output = this.getPathCreateFileSystemIfNeed(uri);
                    
            }catch(IOException ioe) {
                
                logger.log(Level.WARNING, "Exception creating FileSystem for: "+uri, ioe);
                
                output = outputIfNone;
            }
        }
        
        logger.log(Level.FINE, "Resolved URI: {0} to Path: {1}", new Object[]{uri, output});
        
        return output;
    }
    
    private Path getPathCreateFileSystemIfNeed(URI uri) throws IOException {
        
         final Map<String, String> env = Collections.singletonMap("create", "true");
            
        try(FileSystem fileSystem = FileSystems.newFileSystem(uri, env)) {

            return Paths.get(uri);
        }
    }

    private boolean createFileSystem(Path path) {
        
        try(FileSystem fileSystem = FileSystems.newFileSystem(path, this.classLoader)) {

            return true;

        }catch(IOException ioe) {

            logger.log(Level.WARNING, "Exception creating FileSystem for: "+path, ioe);

            return false;
        }
    }
}
