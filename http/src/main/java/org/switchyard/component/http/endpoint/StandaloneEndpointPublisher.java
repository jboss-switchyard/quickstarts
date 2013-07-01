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
 
package org.switchyard.component.http.endpoint;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jboss.com.sun.net.httpserver.BasicAuthenticator;
import org.jboss.com.sun.net.httpserver.HttpContext;
import org.jboss.com.sun.net.httpserver.HttpExchange;
import org.jboss.com.sun.net.httpserver.HttpHandler;
import org.jboss.com.sun.net.httpserver.HttpServer;
import org.switchyard.component.http.ContentType;
import org.switchyard.component.http.InboundHandler;
import org.switchyard.component.http.composer.HttpRequestBindingData;
import org.switchyard.component.http.composer.HttpRequestInfo;
import org.switchyard.component.http.composer.HttpResponseBindingData;
import org.switchyard.security.jboss.credential.extractor.HttpExchangeCredentialExtractor;

/**
 * Publishes standalone HTTP endpoint.
 * <p>
 *     By default it will be published in port {@value #DEFAULT_PORT}. This can be configured making use of
 *     <i>{@value #DEFAULT_PORT_PROPERTY}</i> system property.
 * </p>
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class StandaloneEndpointPublisher implements EndpointPublisher {

    /**
     * Default port in which the standalone publisher is started.
     */
    public static final int DEFAULT_PORT = 8080;

    /**
     * System property to adjust the port in which the standalone publisher is started.
     */
    public static final String DEFAULT_PORT_PROPERTY = "org.switchyard.component.http.standalone.port";

    private static final Logger LOGGER = Logger.getLogger(StandaloneEndpointPublisher.class);

    private static final String CONTENT_TYPE = "Content-Type";

    // The global standalone HttpServer
    private static HttpServer _httpServer;

    static {
        try {
            _httpServer = HttpServer.create(new InetSocketAddress(getPort()), 10);
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
            try {
                HttpRequestBindingData httpRequest = new HttpRequestBindingData();
                byte[] responseBody = null;
                try {
                    String contentTypeStr = exchange.getRequestHeaders().getFirst(CONTENT_TYPE);
                    ContentType contentType = new ContentType(contentTypeStr);
                    httpRequest.setContentType(contentType);
                    httpRequest.setBodyFromStream(exchange.getRequestBody());
                    httpRequest.setHeaders(exchange.getRequestHeaders());
                    httpRequest.setRequestInfo(getRequestInfo(exchange, contentType));
                } catch (IOException e) {
                    LOGGER.error("Unexpected Exception while reading request", e);
                }
                HttpResponseBindingData httpResponse = _handler.invoke(httpRequest);
                try {
                    if (httpResponse != null) {
                        exchange.getResponseHeaders().putAll(httpResponse.getHeaders());
                        if (httpResponse.getBodyBytes() != null) {
                            exchange.sendResponseHeaders(httpResponse.getStatus(), httpResponse.getBodyBytes().available());
                            httpResponse.writeBodyToStream(exchange.getResponseBody());
                        } else {
                            if ((httpResponse != null) && (httpResponse.getStatus() != null)) {
                                exchange.sendResponseHeaders(httpResponse.getStatus(), 0);
                            } else {
                                exchange.sendResponseHeaders(HttpServletResponse.SC_ACCEPTED, 0);
                            }
                        }
                    } else {
                        exchange.sendResponseHeaders(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 0);
                    }
                } catch (IOException e) {
                    LOGGER.error("Unexpected Exception while writing response", e);
                }
            } catch (Exception e) {
                LOGGER.error("Unexpected Exception while handling http request", e);
            }
        }
    }

    /**
     * Method for get request information from a http exchange.
     *
     * @param request HttpExchange
     * @param type ContentType
     * @return Request information from a http exchange
     * @throws IOException when the request information could not be read
     */
    public static HttpRequestInfo getRequestInfo(HttpExchange request, ContentType type) throws IOException {
        HttpRequestInfo requestInfo = new HttpRequestInfo();

        if (request.getHttpContext().getAuthenticator() instanceof BasicAuthenticator) {
            requestInfo.setAuthType(HttpServletRequest.BASIC_AUTH);
        }
        URI u = request.getRequestURI();
        URI requestURI = null;
        try {
            requestURI = new URI(u.getScheme(), u.getUserInfo(), u.getHost(), u.getPort(), u.getPath(), null, null);
        } catch (URISyntaxException e) {
            // Strange that this could happen when copying from another URI.
            LOGGER.debug(e);
        }
        requestInfo.setCharacterEncoding(type.getCharset());
        requestInfo.setContentType(type.toString());
        requestInfo.setContextPath(request.getHttpContext().getPath());
        requestInfo.setLocalAddr(request.getLocalAddress().getAddress().getHostAddress());
        requestInfo.setLocalName(request.getLocalAddress().getAddress().getHostName());
        requestInfo.setMethod(request.getRequestMethod());
        requestInfo.setProtocol(request.getProtocol());
        requestInfo.setQueryString(u.getQuery());
        requestInfo.setRemoteAddr(request.getRemoteAddress().getAddress().getHostAddress());
        requestInfo.setRemoteHost(request.getRemoteAddress().getAddress().getHostName());
        if (request.getHttpContext().getAuthenticator() instanceof BasicAuthenticator) {
            requestInfo.setRemoteUser(request.getPrincipal().getUsername());
        }
        requestInfo.setContentLength(request.getRequestBody().available());
        // requestInfo.setRequestSessionId(request.getRequestedSessionId());
        if (requestURI != null) {
            requestInfo.setRequestURI(requestURI.toString());
        }
        requestInfo.setScheme(u.getScheme());
        requestInfo.setServerName(u.getHost());
        requestInfo.setRequestPath(u.getPath());

        // Http Query params...
        if (requestInfo.getQueryString() != null) {
            StringTokenizer params = new StringTokenizer(requestInfo.getQueryString(), "&");
            while (params.hasMoreTokens()) {
                String param = params.nextToken();
                String[] nameValue = param.split("=");
                requestInfo.addQueryParam(nameValue[0], nameValue[1]);
            }
        }

        // Credentials...
        requestInfo.getCredentials().addAll(new HttpExchangeCredentialExtractor().extract(request));

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(requestInfo);
        }

        return requestInfo;
    }

    /**
     * Returns the port where the standalone publisher will be started
     * @return the port
     */
    static int getPort() {
        int port = DEFAULT_PORT;
        final String portAsStr = System.getProperty(DEFAULT_PORT_PROPERTY);
        if (portAsStr != null) {
            port = Integer.parseInt(portAsStr);
        }
        return port;
    }
}
