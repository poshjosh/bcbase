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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 19, 2018 12:47:34 PM
 */
public class JarIO implements Serializable {

    private transient static final Logger LOG = Logger.getLogger(JarIO.class.getName());
    
    public void extractJarToDir(URL url, String destDir) 
            throws MalformedURLException, IOException {
        
        final JarFile jarFile = this.downloadJar(url.toExternalForm());

        this.extractJarToDir(jarFile, destDir);
    }

    public JarFile downloadJar(String urlStr) throws MalformedURLException, IOException {
        
        final URL jarUrl = this.toJarUrl(urlStr);
        
        return this.downloadJar(jarUrl);
    }

    public JarFile downloadJar(URL jarUrl) throws IOException {
        
        final JarURLConnection conn = (JarURLConnection)jarUrl.openConnection();

        final JarFile jarFile = conn.getJarFile();

        return jarFile;
    }

    public void extractJarToDir(String jarFile, String destDir) throws IOException {
        
        try (java.util.jar.JarFile jar = new java.util.jar.JarFile(jarFile)) {
            
            this.extractJarToDir(jar, destDir);
        }
    }
    
    public void extractJarToDir(JarFile jarFile, String destDir) throws IOException {
        
        final Enumeration enumEntries = jarFile.entries();
        
        while (enumEntries.hasMoreElements()) {

            final JarEntry jarEntry = (JarEntry) enumEntries.nextElement();

            final File file = new File(destDir + java.io.File.separator + jarEntry.getName());

            if (jarEntry.isDirectory()) { 
                file.mkdir();
            }else{
                try (InputStream is = jarFile.getInputStream(jarEntry); 
                        FileOutputStream fos = new FileOutputStream(file)) {
                    while (is.available() > 0) { 
                        fos.write(is.read());
                    }
                }
            }
        }
    }

    public URL toJarUrl(String url) throws MalformedURLException {
        Objects.requireNonNull(url);
        final StringBuilder builder = new StringBuilder();
        if(!url.startsWith("jar:")) {
            builder.append("jar:");
        }
        builder.append(url);
        if(!url.endsWith("!/")) {
            builder.append("!/");
        }
        return new URL(builder.toString());
    }
    
    public JarEntry toJarEntry(File file) {
        
        final JarEntry entry;
        
        if (file.isDirectory()) {
            
            String name = file.getPath().replace("\\", "/");
          
            if (!name.isEmpty()) {
                if (!name.endsWith("/")) {
                    name += "/";
                }    
                entry = new JarEntry(name);
                entry.setTime(file.lastModified());
            }else{
                throw new IllegalArgumentException(name);
            }    
        }else{
            entry = new JarEntry(file.getPath().replace("\\", "/"));
            entry.setTime(file.lastModified());
        }
        return entry;
    }

    public void copy(JarFile jarFile, String dest) throws IOException {
        
        LOG.info(() -> "Copying: " + jarFile.getName() + " to " + dest);
        
        final Manifest manifest = jarFile.getManifest();
      
        try (JarOutputStream target = new JarOutputStream(new FileOutputStream(dest), manifest)) {
            
            final Enumeration enumEntries = jarFile.entries();

            while (enumEntries.hasMoreElements()) {

                final JarEntry jarEntry = (JarEntry) enumEntries.nextElement();
                
                this.add(jarFile, jarEntry, target);
            }
        }
    }

    public void add(JarFile jarFile, JarEntry entry, JarOutputStream target) throws IOException {

        try (BufferedInputStream in = new BufferedInputStream(jarFile.getInputStream(entry))){
            
            target.putNextEntry(entry);

            final byte[] buffer = new byte[1024];
            
            while (true) {
                
                final int count = in.read(buffer);
                
                if (count == -1) {
                    break;
                }
                
                target.write(buffer, 0, count);
            }
            
            target.closeEntry();
        }
    }

    public void add(File file, String dest, Manifest manifest) throws IOException {
        
        try (JarOutputStream target = new JarOutputStream(new FileOutputStream(dest), manifest)) {
            
            this.add(file, target);
        }
    }
    
    public void add(File file, JarOutputStream target) throws IOException {

        BufferedInputStream in = null;

        try {
            
            final JarEntry entry = toJarEntry(file);
            
            if (file.isDirectory()) {
                target.putNextEntry(entry);
                target.closeEntry();
                for (File nestedFile: file.listFiles()) {
                    add(nestedFile, target);
                }  
            }else{
            
                target.putNextEntry(entry);
                in = new BufferedInputStream(new FileInputStream(file));

                final byte[] buffer = new byte[1024];
                while (true) {
                    final int count = in.read(buffer);
                    if (count == -1) {
                        break;
                    }  
                    target.write(buffer, 0, count);
                }
                target.closeEntry();
            }
        }finally {
            if (in != null){
                in.close();
            }  
        }
    }

    public void copyTempJar(JarFile jarFile, String dest) throws IOException {
        
        LOG.info(() -> "Copying: " + jarFile.getName() + " to " + dest);
        
        final Path source = Paths.get(jarFile.getName().replace(".tmp", ".jar"));
        
        final Path target = Paths.get(dest);
        
        Files.copy(source, target, 
//                StandardCopyOption.ATOMIC_MOVE,
                StandardCopyOption.COPY_ATTRIBUTES,
                StandardCopyOption.REPLACE_EXISTING);
    }
}
