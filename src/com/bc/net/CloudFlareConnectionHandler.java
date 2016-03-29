package com.bc.net;

import com.bc.io.CharFileIO;
import com.bc.util.XLogger;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.script.ScriptException;

/**
 * @author USER
 * @see http://developers-club.com/posts/258101/
 */
public class CloudFlareConnectionHandler implements ConnectionManager.ConnectionHandler {

    private final CharFileIO io;
    
    private int delay;
    
    public CloudFlareConnectionHandler() {
        io = new CharFileIO();
        delay = 5000;
        String charset = "utf-8"; //"windows-1251";
        io.setInputCharset(charset);
        io.setOutputCharset(charset);
    }
    
    /**
     * getInputStream or getErrorStream has already been called by the input ConnectionManager
     * So this just retrieves the cached input or error stream
     * @param connMgr
     * @return
     * @throws IOException 
     */
    @Override
    public InputStream getInputStream(ConnectionManager connMgr) throws IOException {
XLogger.getInstance().log(Level.FINER, "#getInputStream(com.bc.net.ConnectionManager)", this.getClass());
        if(!connMgr.isAddCookies() || !connMgr.isGetCookies()) {
            throw new UnsupportedOperationException("Call #setAddCookies and #setGetCookies on input "+ConnectionManager.class.getName());
        }
    
        // getInputStream or getErrorStream has already been called by the input ConnectionManager
        // So this just retrieves the cached input or error stream
        //
        InputStream in = connMgr.getInputStream();
        
        if(connMgr.getResponseCode() != 403) {
            
            return in;
        }
        
        String contents = io.readChars(in).toString();
        
        URL url = connMgr.getConnection().getURL();

        CloudFlareResponseParameters outputParameters = new CloudFlareResponseParameters();
        
        try{
            outputParameters.generate(url, contents);
        }catch(ScriptException e) {
            throw new IOException(e);
        }
        
        if(!outputParameters.isEmpty()) {

            if(delay > 0) {
                // CloudFlare ожидает ответа после некоторой задержки // CloudFlare expects a response after a delay
                try{
                    Thread.sleep(delay);
                }catch(InterruptedException e) {
                    XLogger.getInstance().log(Level.WARNING, "Thread.sleep("+delay+") threw Exception", this.getClass(), e);
                }
            }

            // url страницы, с которой бы произведено перенаправление // url of the page, which was redirected
            Map<String, Object> headers = new HashMap<>(3, 1.0f); // user agent will be added
            headers.put("Referer", url); 
            headers.put("Accept-Charset", io.getOutputCharset());
            connMgr.setGenerateRandomUserAgent(true); // Random user agent will be added
            return connMgr.getInputStream(url, headers, outputParameters, true);
            
        }else{
            String msg = "Recieved response 'Forbidden (403)' from remote server. However could not find cloud flare javascript challenge within response content";
            XLogger.getInstance().log(Level.FINE, msg+"\n{0}", this.getClass(), contents);
            throw new IOException(msg);
        }
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public String getCharset() {
        return io.getOutputCharset();
    }

    public void setCharset(String charset) {
        this.io.setOutputCharset(charset);
    }
}
