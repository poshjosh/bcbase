package com.bc.net;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;


/**
 * @(#)MyURLStreamHandler.java   02-Oct-2015 01:49:46
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */

/**
 * The first line of the response was found to contain invalid content in some
 * cases. This leads to a responseCode of <tt>-1</tt>. Notwithstanding, 
 * {@link java.net.HttpURLConnection#getInputStream()} returns the input stream
 * in Java 6 but throws an {@link java.io.IOException} in Java 7 or later.
 * This class mimics the Java 6 behaviour.
 * @see http://www.oracle.com/technetwork/java/javase/compatibility-417013.html
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public class HttpStreamHandlerForBadStatusLine extends sun.net.www.protocol.http.Handler {

    @Override
    protected URLConnection openConnection(URL url) throws IOException {

        return new HttpURLConnectionForBadStatusLine(url, HttpStreamHandlerForBadStatusLine.this);
    }
}
