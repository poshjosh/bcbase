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

package com.bc.net;

import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.util.function.Predicate;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 4, 2017 8:55:30 PM
 */
public class FailedConnectionTest implements Predicate<Throwable> {

    @Override
    public boolean test(Throwable t) {
        
        if(t instanceof SocketTimeoutException || t instanceof ConnectException
                || t instanceof NoRouteToHostException) {

            return true;

        }else{

            String exceptionMsg = t.getMessage();

            if (exceptionMsg != null){
                
                if(exceptionMsg.contains("is closed") | exceptionMsg.contains("IS CLOSED") |
                        exceptionMsg.contains("Connection reset")) { ///// Added recently /////

                    return true;
                }
            }
        }
        
        return false;
    }

}
