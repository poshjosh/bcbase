/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package temp;

import com.bc.io.CharFileIO;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 20, 2018 8:23:29 PM
 */
public class Main {

    public static void main(String [] arg) {
        
        final Main main = new Main();
//        final String urlStr = "http://www.google.com/ig/calculator?hl=en&q=1GBP=?USD";
        final String urlStr = main.getY();
        System.out.println("URL: " + urlStr);
        try{
            final String contents = main.getUrlContents(urlStr, null);
            System.out.println(contents);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public String getUrlContents(String urlStr, String outputIfNone) throws IOException {
        
        final URL url = new URL(urlStr);
        
        final CharFileIO fileIO = new CharFileIO();
        
        final CharSequence cs;
        
        try(final InputStream in = url.openStream()) {
        
            cs = fileIO.readChars(in);
        }
        
        return cs == null ? outputIfNone : cs.toString();
    }
    
    public String getYall() {
        return "http://query.yahooapis.com/v1/public/yql?q=select * from yahoo.finance.xchange where pair in (\"USDEUR\", \"USDJPY\", \"USDBGN\", \"USDCZK\", \"USDDKK\", \"USDGBP\", \"USDHUF\", \"USDLTL\", \"USDLVL\", \"USDPLN\", \"USDRON\", \"USDSEK\", \"USDCHF\", \"USDNOK\", \"USDHRK\", \"USDRUB\", \"USDTRY\", \"USDAUD\", \"USDBRL\", \"USDCAD\", \"USDCNY\", \"USDHKD\", \"USDIDR\", \"USDILS\", \"USDINR\", \"USDKRW\", \"USDMXN\", \"USDMYR\", \"USDNZD\", \"USDPHP\", \"USDSGD\", \"USDTHB\", \"USDZAR\", \"USDISK\")&env=store://datatables.org/alltableswithkeys";
    }
    public String getY() {
        return "http://query.yahooapis.com/v1/public/yql?q=select * from yahoo.finance.xchange where pair in (\"USDEUR\")&env=store://datatables.org/alltableswithkeys";
    }
}
