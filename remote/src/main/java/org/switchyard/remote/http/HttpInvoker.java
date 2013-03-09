/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
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
package org.switchyard.remote.http;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.switchyard.Context;
import org.switchyard.remote.RemoteInvoker;
import org.switchyard.remote.RemoteMessage;
import org.switchyard.serial.FormatType;
import org.switchyard.serial.Serializer;
import org.switchyard.serial.SerializerFactory;

/**
 * Remote service invoker which uses HTTP as a transport.
 */
public class HttpInvoker implements RemoteInvoker {
    
    /**
     * HTTP header used to communicate the domain name for a service invocation.
     */
    public static final String SERVICE_HEADER = "switchyard-service";

    private static Logger _log = Logger.getLogger(HttpInvoker.class);
    private Serializer _serializer = SerializerFactory.create(FormatType.JSON, null, true);
    private URL _endpoint;
    
    /**
     * Create a new HttpInvoker from the specified URL string.
     * @param endpoint url string
     */
    public HttpInvoker(String endpoint) {
        try {
            _endpoint = new URL(endpoint);
        } catch (MalformedURLException badURL) {
            throw new IllegalArgumentException(
                    "Invalid URL for remote endpoint: " + endpoint, badURL);
        }
    }
    
    /**
     * Create a new HttpInvoker with the specified URL.
     * @param endpoint the endpoint URL
     */
    public HttpInvoker(URL endpoint) {
        _endpoint = endpoint;
    }

    @Override
    public RemoteMessage invoke(RemoteMessage request) throws java.io.IOException {
        RemoteMessage reply = null;
        HttpURLConnection conn = null;
        
        if (_log.isDebugEnabled()) {
            _log.debug("Invoking " + request.getService() + " at endpoint " + _endpoint.toString());
        }
        
        // Initialize HTTP connection
        conn = (HttpURLConnection)_endpoint.openConnection();
        conn.setDoOutput(true);
        conn.addRequestProperty(SERVICE_HEADER, request.getService().toString());
        conn.connect();
        OutputStream os = conn.getOutputStream();
        
        // Sanitize context properties
        if (request.getContext() != null) {
            Context ctx = request.getContext().copy();
            request.setContext(ctx);
        }
        
        // Write the request message
        _serializer.serialize(request, RemoteMessage.class, os);
        os.flush();
        os.close();
        
        // Check for response and process accordingly
        if (conn.getResponseCode() == 200) {
            if (_log.isDebugEnabled()) {
                _log.debug("Processing reply for service " + request.getService());
            }
            reply = _serializer.deserialize(conn.getInputStream(), RemoteMessage.class);
        }
        
        return reply;
    }
}
