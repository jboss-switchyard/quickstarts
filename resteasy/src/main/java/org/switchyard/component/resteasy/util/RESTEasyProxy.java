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
 
package org.switchyard.component.resteasy.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.server.servlet.ServletSecurityContext;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.switchyard.common.type.reflect.Access;
import org.switchyard.common.type.reflect.FieldAccess;
import org.switchyard.component.resteasy.InboundHandler;
import org.switchyard.component.resteasy.composer.RESTEasyBindingData;

/**
 * A proxy for RESTEasy instances.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public final class RESTEasyProxy implements InvocationHandler {

    private static final Logger LOGGER = Logger.getLogger(RESTEasyProxy.class);

    private static final Access<HttpServletRequest> SERVLET_REQUEST_ACCESS;
    static {
        final Access<HttpServletRequest> servletRequestAccess = new FieldAccess<HttpServletRequest>(ServletSecurityContext.class, "request");
        SERVLET_REQUEST_ACCESS = servletRequestAccess.isReadable() ? servletRequestAccess : null;
    }

    private InboundHandler _serviceConsumer;

    /**
     * Create a RESTEasy proxy instance.
     *
     * @param serviceConsumer the SwitchYard handler
     * @param interfaze the RESTEasy resource class
     * @return the proxy instance
     */
    public static Object newInstance(final InboundHandler serviceConsumer, Class<?> interfaze) {
        return Proxy.newProxyInstance(interfaze.getClassLoader(),
                                    new Class<?>[] {interfaze},
                                    new RESTEasyProxy(serviceConsumer));
    }

    private RESTEasyProxy(final InboundHandler serviceConsumer) {
        _serviceConsumer = serviceConsumer;
    }

    /**
     * Sets the service handler.
     * @param serviceConsumer the service handler.
     */
    public void setConsumer(final InboundHandler serviceConsumer) {
        _serviceConsumer = serviceConsumer;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if (methodName.equals("toString")) {
            return this.toString();
        } else if (methodName.equals("equals")) {
            return this.equals(proxy);
        } else if (methodName.equals("hashCode")) {
            return this.hashCode();
        }
        RESTEasyBindingData requestData = new RESTEasyBindingData();
        HttpHeaders headers = ResteasyProviderFactory.getContextData(HttpHeaders.class);
        if (headers != null) {
            requestData.setHeaders(headers.getRequestHeaders());
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Incoming Headers to SwitchYard through InboundHandler [");
                traceLog(LOGGER, headers.getRequestHeaders());
                LOGGER.trace("]");
            }
        }
        SecurityContext securityContext = ResteasyProviderFactory.getContextData(SecurityContext.class);
        if (securityContext != null) {
            if (securityContext instanceof ServletSecurityContext && SERVLET_REQUEST_ACCESS != null) {
                HttpServletRequest servletRequest = SERVLET_REQUEST_ACCESS.read((ServletSecurityContext)securityContext);
                requestData.setServletRequest(servletRequest);
            }
            requestData.setSecured(securityContext.isSecure());
            requestData.setPrincipal(securityContext.getUserPrincipal());
        }
        requestData.setOperationName(methodName);
        if ((args != null) && (args.length > 0)) {
            requestData.setParameters(args);
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(args);
            }
        }
        RESTEasyBindingData responseData = _serviceConsumer.invoke(requestData, method.getReturnType().equals(Void.TYPE));
        Response.ResponseBuilder builder = Response.ok();
        if (responseData != null) {
            if (method.getReturnType().equals(Response.class)) {
                if (responseData.getParameters().length > 0) {
                    Object param = responseData.getParameters()[0];
                    if (param instanceof Response) {
                        // In future use builder = Response.ResponseBuilder.fromResponse((Response)param);
                        Response response = (Response)param;
                        builder.entity(response.getEntity());
                        builder.status(response.getStatus());
                    } else {
                        builder.entity(param);
                    }
                }
                // Data overrides status
                if (responseData.getStatusCode() != null) {
                    builder.status(responseData.getStatusCode());
                }
                for (Map.Entry<String, List<String>> entry : responseData.getHeaders().entrySet()) {
                    String name = entry.getKey();
                    List<String> values = entry.getValue();
                    for (String value : values) {
                        builder.header(name, value);
                    }
                }
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("Outgoing Headers from SwitchYard through InboundHandler [");
                    traceLog(LOGGER, responseData.getHeaders());
                    LOGGER.trace("]");
                }
            } else if (responseData.getParameters().length > 0) {
                return responseData.getParameters()[0];
            }
        }
        return builder.build();
    }

    /**
     * Trace log header keys and values.
     * @param logger The Logger
     * @param headers The header map
     */
    public static void traceLog(final Logger logger, final Map<String, List<String>> headers) {
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String name = entry.getKey();
            List<String> values = entry.getValue();
            for (String value : values) {
                logger.trace(name + " = " + value);
            }
        }
    }
}
