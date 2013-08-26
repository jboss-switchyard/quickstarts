/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.remote.http;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.jboss.logging.Logger;
import org.switchyard.remote.RemoteInvoker;
import org.switchyard.remote.RemoteMessage;
import org.switchyard.remote.RemoteMessages;
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
            throw RemoteMessages.MESSAGES.invalidURLForEndpoint(endpoint, badURL);
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
