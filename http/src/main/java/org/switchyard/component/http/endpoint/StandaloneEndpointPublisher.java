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
 
package org.switchyard.component.http.endpoint;

import java.io.IOException;
import java.net.InetSocketAddress;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jboss.com.sun.net.httpserver.HttpContext;
import org.jboss.com.sun.net.httpserver.HttpExchange;
import org.jboss.com.sun.net.httpserver.HttpHandler;
import org.jboss.com.sun.net.httpserver.HttpServer;
import org.switchyard.component.http.ContentType;
import org.switchyard.component.http.InboundHandler;
import org.switchyard.component.http.composer.HttpRequestBindingData;
import org.switchyard.component.http.composer.HttpResponseBindingData;

/**
 * Publishes standalone HTTP endpoint.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class StandaloneEndpointPublisher implements EndpointPublisher {

    private static final Logger LOGGER = Logger.getLogger(StandaloneEndpointPublisher.class);

    private static final String CONTENT_TYPE = "Content-Type";

    // The global standalone HttpServer
    private static HttpServer _httpServer;

    static {
        try {
            _httpServer = HttpServer.create(new InetSocketAddress(8080), 10);
            _httpServer.setExecutor(null); // creates a default executor
            _httpServer.start();
        } catch (IOException ioe) {
            LOGGER.error("Unable to launch standalone http server", ioe);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Endpoint publish(String context, InboundHandler handler) throws Exception {
        HttpContext httpContext = null;
        if (!context.startsWith("/")) {
            context = "/" + context;
        }
        if (_httpServer != null) {
            httpContext = _httpServer.createContext(context, new StandaloneHandler(handler));
        }
        return new StandaloneEndpoint(httpContext);
    }

    private static class StandaloneHandler implements HttpHandler {

        private InboundHandler _handler;

        public StandaloneHandler(InboundHandler handler) {
            _handler = handler;
        }

        public void handle(HttpExchange exchange) {
            HttpRequestBindingData httpRequest = new HttpRequestBindingData();
            byte[] responseBody = null;
            try {
                String contentType = exchange.getRequestHeaders().getFirst(CONTENT_TYPE);
                httpRequest.setContentType(new ContentType(contentType));
                httpRequest.setBodyFromStream(exchange.getRequestBody());
                httpRequest.setHeaders(exchange.getRequestHeaders());
            } catch (IOException e) {
                LOGGER.error("Unexpected Exception while reading request", e);
            }
            HttpResponseBindingData httpResponse = _handler.invoke(httpRequest);
            try {
                if (httpResponse != null) {
                    exchange.getResponseHeaders().putAll(httpResponse.getHeaders());
                    if (httpResponse.getBodyBytes() != null) {
                        exchange.sendResponseHeaders(httpResponse.getStatus(), httpResponse.getBodyBytes().length);
                        httpResponse.writeBodyToStream(exchange.getResponseBody());
                    } else {
                        exchange.sendResponseHeaders(httpResponse.getStatus(), 0);
                    }
                } else {
                    exchange.sendResponseHeaders(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 0);
                }
            } catch (IOException e) {
                LOGGER.error("Unexpected Exception while writing response", e);
            }
        }
    }
}
