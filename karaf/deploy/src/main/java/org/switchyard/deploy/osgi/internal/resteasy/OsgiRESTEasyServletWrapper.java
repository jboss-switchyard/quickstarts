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
package org.switchyard.deploy.osgi.internal.resteasy;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.switchyard.common.type.Classes;

/**
 * A wrapper servlet to plug it into RESTEasy.
 */
public class OsgiRESTEasyServletWrapper extends HttpServletDispatcher {

    private static final long serialVersionUID = 8811690971687086278L;
    private static final Logger _logger = Logger.getLogger(OsgiRESTEasyServletWrapper.class);

    private ClassLoader _classLoader;

    /**
     * Sets a class loader to be used for TCCL on execution.
     * @param loader class loader
     * @return this OsgiRESTEasyServletWrapper (useful for chaining)
     */
    public OsgiRESTEasyServletWrapper setClassLoader(ClassLoader loader) {
        _classLoader = loader;
        return this;
    }

    @Override
    public void service(String httpMethod, HttpServletRequest request, HttpServletResponse response) throws IOException {
        ClassLoader origCL = null;
        try {
            origCL = Classes.setTCCL(_classLoader);
            if (_logger.isDebugEnabled()) {
                String br = System.getProperty("line.separator");
                StringBuilder buf = new StringBuilder()
                    .append("Executing RESTEasy servlet:").append(br)
                    .append("\tClassLoader:\t").append(_classLoader).append(br)
                    .append("\tHTTP Headers:\t");
                Enumeration<?> headerenum = request.getHeaderNames();
                while(headerenum.hasMoreElements()) {
                    String key = headerenum.nextElement().toString();
                    buf.append(key).append("={");
                    Enumeration<?> e = request.getHeaders(key);
                    while(e.hasMoreElements()) {
                        buf.append(e.nextElement().toString()).append(";");
                    }
                    buf.append("} ");
                }
                buf.append(br)
                   .append("\tHTTP Request:\t").append(request.toString()).append(br)
                   .append("\tContent Type:\t").append(request.getContentType()).append(br)
                   .append("\tContent Length:\t").append(request.getContentLength()).append(br)
                   .append("\tHTTP Parameters:\t").append(request.getParameterMap());
                _logger.debug(buf.toString());
            }
            super.service(httpMethod, request, response);
        } finally {
            Classes.setTCCL(origCL);
        }
    }
}
