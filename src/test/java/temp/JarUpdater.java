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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 19, 2018 12:45:37 PM
 */
public class JarUpdater {

    private transient static final Logger LOG = 
            Logger.getLogger(JarUpdater.class.getName());
    
    private final UIContext uiCtx;

    private final Function<String, String> getNameFromLink;

    public JarUpdater() {
        this(new UIContext(), new GetNameFromLink());
    }

    public JarUpdater(UIContext uiCtx, Function<String, String> getNameFromLink) {
        this.uiCtx = Objects.requireNonNull(uiCtx);
        this.getNameFromLink = Objects.requireNonNull(getNameFromLink);
    }

    public void update(String[] appLibJarUrls, String appJarUrl) 
            throws MalformedURLException, IOException {
        
        uiCtx.showProgressBarPercent("Starting", 2);

        final JarIO jarIO = new JarIO();

        final Map<String, JarFile> jarFiles = new LinkedHashMap(appLibJarUrls.length, 1.0f);

        final int downloadPerFilePercent = 70 / appLibJarUrls.length; 

        uiCtx.showProgressBarPercent("Downloading updates", 5);

        for(String urlStr : appLibJarUrls) {

            final JarFile jarFile = jarIO.downloadJar(urlStr);

            jarFiles.put(urlStr, jarFile);

            this.log("Downloaded", urlStr, jarFile, downloadPerFilePercent);
        }

        final JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select location to save download");
        chooser.setFileHidingEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        uiCtx.positionCenterScreen(chooser);
        chooser.showDialog(null, "Select Folder");

        final File destDir = chooser.getSelectedFile();

        final int copyPerFilePercent = 20 / appLibJarUrls.length; 

        final File libDir = new File(destDir.toString() + File.separatorChar + "lib");

        libDir.mkdirs();

        final Set<String> urlStrSet = jarFiles.keySet();

        for(String urlStr : urlStrSet) {

            final JarFile jarFile = jarFiles.get(urlStr);
            
            jarIO.copyTempJar(jarFile, this.createFilename(libDir, urlStr));

            this.log("Copied", urlStr, jarFile, copyPerFilePercent);
        }

        final JarFile appJar = jarIO.downloadJar(appJarUrl);

        this.log("Downloaded", appJarUrl, appJar, 5);

        jarIO.copyTempJar(appJar, this.createFilename(destDir, appJarUrl));

        LOG.info("Done");

        uiCtx.showProgressBarPercent("Done", 100);
    }
    
    private void log(String action, String urlStr, JarFile jarFile, int addToProgressPercent) {
        
        LOG.log(Level.INFO, () -> action + ": " + urlStr + " to " + jarFile.getName());

        uiCtx.addProgressBarPercent(addToProgressPercent);
    }
    
    private String createFilename(File dir, String jarUrl) {
        
        return dir.toString() + File.separatorChar + this.getNameFromLink.apply(jarUrl);
    }
}
