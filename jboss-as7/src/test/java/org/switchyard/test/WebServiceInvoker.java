/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.switchyard.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Invokes a WebService over HTTP.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class WebServiceInvoker {
    private URL _endpointURL;
    private int _timeout = 10000;

    /**
     * Create a Synchronous invoker.
     * @param endpointURL The target endpoint (URL).
     * @throws MalformedURLException if the URL is malformed.
     */
    public WebServiceInvoker(String endpointURL) throws MalformedURLException {
        this._endpointURL = new URL(endpointURL);
    }

    /**
     * Create a WebService endpoint invoker instance to the specified URL.
     * @param endpointURL The endpoint URL.
     * @return This object.
     * @throws MalformedURLException if the URL is malformed.
     */
    public static WebServiceInvoker target(String endpointURL) throws MalformedURLException {
        return new WebServiceInvoker(endpointURL);
    }

    /**
     * Set the response wait timeout.
     * @param timeout Timeout in milliseconds.
     */
    public void setTimeout(int timeout) {
        this._timeout = timeout;
    }

    /**
     * Send a payload to the target endpoint and wait for a response.
     *
     * @param payload The payload to send to the target endpoint.
     * @return The return value.
     */
    public String send(final String payload) {
        String output = null;
        try {
            HttpURLConnection con = (HttpURLConnection) _endpointURL.openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestProperty("Content-type", "text/xml; charset=utf-8");
            con.setReadTimeout(_timeout);
            OutputStream outStream = con.getOutputStream();
            outStream.write(payload.getBytes());
            InputStream inStream = con.getInputStream();
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            byte[] byteBuf = new byte[256];
            int len = inStream.read(byteBuf);
            while (len > -1) {
                byteStream.write(byteBuf, 0, len);
                len = inStream.read(byteBuf);
            }
            outStream.close();
            inStream.close();
            byteStream.close();
            output =  byteStream.toString();

        } catch (IOException ioe) {
            output = "<error>" + ioe + "</error>";
        }
        return output;
    }
}
