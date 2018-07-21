/*
 * Copyright 2016 NUROX Ltd.
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

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * @author Chinomso Bassey Ikwuagwu on Sep 19, 2016 2:06:02 PM
 */
public class Temp {

    public static void main(String [] args) {
        try{
            
           Date date = new Date();
           long time = date.getTime();
System.out.println(time);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

            //Here you say to java the initial timezone. This is the secret
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            
            String dateStr = sdf.format(calendar.getTime());
            
            //Will print in UTC
            System.out.println(dateStr); 
            
            //Here you set to your timezone
            sdf.setTimeZone(TimeZone.getDefault());
            
            //Will print on your default Timezone
            dateStr = sdf.format(calendar.getTime());
            System.out.println(dateStr);
            
//            sdf.setTimeZone(TimeZone.getTimeZone("GMT+07:00"));
            sdf.setTimeZone(TimeZone.getTimeZone("PST"));
            try{
                date = sdf.parse(dateStr); 
System.out.println(date.getTime());
                long diff = time - date.getTime();
System.out.println(TimeUnit.MILLISECONDS.toHours(diff));
            }catch(Exception e) {
                e.printStackTrace();
            }

if(true) {
    return;
}
            
log("LocalHost: "+InetAddress.getLocalHost());

log("LoopbackAddress: "+InetAddress.getLoopbackAddress());

            final String [] urlStrings = {"http://yahoo.com", "http://www.looseboxes.com"};
            
            for(String urlString : urlStrings) {

                final URL url = new URL(urlString);
log("URL: " + url);
                final String host = url.getHost();
log("Host: " + host);            

                final InetAddress address = InetAddress.getByName(host);
log("InetAddress: " + address);

                final String ip = address.getHostAddress();

log("Host address: " + ip);            
            }
        }catch(MalformedURLException | UnknownHostException e) {
            e.printStackTrace();
        }
    }
    
    private static void log(Object msg) {
System.out.println(""+new Date()+" = = = = = = = "+msg);        
    }
}
