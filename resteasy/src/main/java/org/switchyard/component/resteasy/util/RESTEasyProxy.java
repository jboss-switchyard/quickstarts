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

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.switchyard.component.resteasy.InboundHandler;
import org.switchyard.component.resteasy.composer.RESTEasyBindingData;

/**
 * A proxy for RESTEasy instances.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public final class RESTEasyProxy implements InvocationHandler {

    private static final Logger LOGGER = Logger.getLogger(RESTEasyProxy.class);

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
    public Object invoke(Object proxy, Method method, Object[] args) {
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
                for (Map.Entry<String, List<String>> entry : headers.getRequestHeaders().entrySet()) {
                    LOGGER.trace(entry.getKey() + " = " + entry.getValue());
                }
                LOGGER.trace("]");
            }
        }
        requestData.setOperationName(methodName);
        if ((args != null) && (args.length > 0)) {
            requestData.setParameters(args);
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(args);
            }
        }
        RESTEasyBindingData responseData = _serviceConsumer.invoke(requestData, method.getReturnType().equals(Void.TYPE));
        if (method.getReturnType().equals(Response.class)) {
            Response.ResponseBuilder builder = Response.ok();
            if (responseData != null) {
                if (responseData.getStatusCode() != null) {
                    builder.status(responseData.getStatusCode());
                }
                if (responseData.getParameters().length > 0) {
                    builder.entity(responseData.getParameters()[0]);
                }
                for (Map.Entry<String, List<String>> entry : responseData.getHeaders().entrySet()) {
                    builder.header(entry.getKey(), entry.getValue());
                }
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("Outgoing Headers from SwitchYard through InboundHandler [");
                    for (Map.Entry<String, List<String>> entry : responseData.getHeaders().entrySet()) {
                        LOGGER.trace(entry.getKey() + " = " + entry.getValue());
                    }
                    LOGGER.trace("]");
                }
            }
            return builder.build();
        } else if ((responseData != null) && (responseData.getParameters().length > 0)) {
            return responseData.getParameters()[0];
        }
        return null;
    }
}
