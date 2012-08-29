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
 
package org.switchyard.component.http;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.switchyard.component.http.composer.HttpRequestBindingData;
import org.switchyard.component.http.composer.HttpResponseBindingData;

/**
 * Hanldes HTTP requests to invoke a SwitchYard service.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class HttpGatewayServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(HttpGatewayServlet.class);

    private transient InboundHandler _handler;

    /**
     * Set the SwitchYard component handler for this instance.
     * @param handler the SwicthYard component handler
     */
    public void setHandler(InboundHandler handler) {
        _handler = handler;
    }

    /**
     * {@inheritDoc}
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handle(request, response);
    }

    /**
     * {@inheritDoc}
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handle(request, response);
    }

    /**
     * {@inheritDoc}
     */
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handle(request, response);
    }

    /**
     * {@inheritDoc}
     */
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handle(request, response);
    }

    /**
     * {@inheritDoc}
     */
    public void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handle(request, response);
    }

    /**
     * {@inheritDoc}
     */
    public void doTrace(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handle(request, response);
    }

    /**
     * {@inheritDoc}
     */
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handle(request, response);
    }

    /**
     * Handles a HTTP Servlet request and responds.
     * @param request the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if any Servlet exception
     * @throws IOException if the streams could not be written or read
     */
    public void handle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

            HttpRequestBindingData httpRequest = new HttpRequestBindingData();
            byte[] responseBody = null;
            try {
                httpRequest.setContentType(new ContentType(request.getContentType()));
                httpRequest.setBodyFromStream(request.getInputStream());
                for (Enumeration<String> headerNames = request.getHeaderNames(); headerNames.hasMoreElements();) {
                    String name = headerNames.nextElement();
                    for (Enumeration<String> values = request.getHeaders(name); values.hasMoreElements();) {
                        String value = values.nextElement();
                        httpRequest.addHeader(name, value);
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Unexpected Exception while reading request", e);
            }
            HttpResponseBindingData httpResponse = _handler.invoke(httpRequest);
            try {
                if (httpResponse != null) {
                    Iterator<Map.Entry<String, List<String>>> entries = httpResponse.getHeaders().entrySet().iterator();
                    while (entries.hasNext()) {
                        Map.Entry<String, List<String>> entry = entries.next();
                        String name = entry.getKey();
                        List<String> values = entry.getValue();
                        for (String value : values) {
                            response.setHeader(name, value);
                        }
                    }
                    if (httpResponse.getBodyBytes() != null) {
                        response.setStatus(httpResponse.getStatus());
                        httpResponse.writeBodyToStream(response.getOutputStream());
                    } else {
                        response.setStatus(httpResponse.getStatus());
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            } catch (IOException e) {
                LOGGER.error("Unexpected Exception while writing response", e);
            }
    }
}
