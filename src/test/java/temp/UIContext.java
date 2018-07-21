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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 19, 2018 1:36:33 PM
 */
public class UIContext {
    
    private transient final Logger logger = Logger.getLogger(UIContext.class.getName());

    private final JFrame pbarFrame;
    private final ProgressbarPanel pbarPanel;
    
    public UIContext() {

        this.pbarPanel = new ProgressbarPanel(); 
        
        this.pbarFrame = new JFrame();
        this.pbarFrame.setSize(new Dimension(512, 32));
        this.pbarFrame.setPreferredSize(this.pbarPanel.getPreferredSize());
        final boolean setAlwaysOnTopIsOk = false;
        this.pbarFrame.setAlwaysOnTop(setAlwaysOnTopIsOk);
        this.pbarFrame.setUndecorated(true);
        this.pbarFrame.setType(Window.Type.UTILITY);
        this.pbarFrame.getContentPane().add(pbarPanel);
    }
    
    public void dispose() {
        if(this.pbarFrame.isVisible()) {
            this.pbarFrame.setVisible(false);
        }
        this.pbarFrame.dispose();
    }

    public void addProgressBarPercent(int val) {
        val = this.pbarPanel.getProgressBar().getValue() + val;
        this.showProgressBar(""+val+'%', 0, val, 100);
    }
    
    public void addProgressBarPercent(String msg, int val) {
        val = this.pbarPanel.getProgressBar().getValue() + val;
        this.showProgressBar(msg, 0, val, 100);
    }
    
    public void showProgressBarPercent(int val) {
        this.showProgressBar(""+val+'%', 0, val, 100);
    }

    public void showProgressBarPercent(String msg, int val) {
        this.showProgressBar(msg, 0, val, 100);
    }
    
    public void showProgressBar(String msg, int min, int val, int max) {
        
        if(SwingUtilities.isEventDispatchThread()) {
            
            showProgress(msg, min, val, max);
            
        }else{
            
            java.awt.EventQueue.invokeLater(() -> {
                showProgress(msg, min, val, max);
            });
        }
    }

    public void showProgress(String msg, int min, int val, int max) {    
        
        final JProgressBar pbar = pbarPanel.getProgressBar();
        
        if(val >= max) {
            if(pbarFrame.isVisible()) {
                pbarFrame.setVisible(false);
            }
        }else{
            
            final boolean indeterminate =  val < min;
            if(pbar.isIndeterminate() != indeterminate) {
                pbar.setIndeterminate(indeterminate);
            }
            
            if(!pbarFrame.isVisible()) {
                
                pbar.setStringPainted(msg != null);
                
                this.positionCenterScreen(pbarFrame);

                pbarFrame.setVisible(true);
            }
        }
        if(msg != null) {
            pbar.setString(msg);
        }
        if(min != pbar.getMinimum()) {
            pbar.setMinimum(min);
        }
        if(val != pbar.getValue()) {
            pbar.setValue(val);
        }
        if(max != pbar.getMaximum()) {
            pbar.setMaximum(max);
        }
    }

    public JProgressBar getProgressBar() {
        return pbarPanel.getProgressBar();
    }
    
    public boolean positionFullScreen(Component c) {
        try{
            final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            final Dimension custom = new Dimension(screenSize.width, screenSize.height - 50);
            c.setLocation(0, 0);
            c.setSize(custom);
            c.setPreferredSize(custom);
            return true;
        }catch(Exception ignored) { 
            return false;
        }
    }
    
    public boolean positionHalfScreenLeft(Component c) {
        try{
            final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            final Dimension custom = new Dimension(screenSize.width/2, screenSize.height - 50);
            c.setLocation(0, 0);
            c.setSize(custom); 
            c.setPreferredSize(custom);
            return true;
        }catch(Exception ignored) { 
            return false;
        }
    }

    public boolean positionHalfScreenRight(Component c) {
        try{
            final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            final Dimension custom = new Dimension(screenSize.width/2, screenSize.height - 50);
            c.setLocation(custom.width, 0);
            c.setSize(custom); 
            c.setPreferredSize(custom);
            return true;
        }catch(Exception ignored) { 
            return false;
        }
    }

    public boolean positionCenterScreen(Component c) {
        try{
            final Dimension dim = c.getSize(); // c.getPreferredSize();
            final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            final int left = screenSize.width/2 - dim.width/2;
            final int top = screenSize.height/2 - dim.height/2;
            c.setLocation(left, top);
            return true;
        }catch(Exception ignored) {
            return false;
        }
    }
}
