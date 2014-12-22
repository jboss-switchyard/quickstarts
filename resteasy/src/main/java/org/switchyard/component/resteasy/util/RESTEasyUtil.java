/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.ext.ExceptionMapper;

import org.jboss.logging.Logger;
import org.jboss.resteasy.client.core.ClientErrorInterceptor;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.util.Types;
import org.switchyard.common.type.Classes;

/**
 * Utility class that recognizes RESTEasy providers and client interceptors from context params.
 */
public final class RESTEasyUtil {

    private static final Logger LOGGER = Logger.getLogger(RESTEasyUtil.class);

    private static final String CLIENT_ERROR_INTERCEPTORS = "resteasy.client.error.interceptors";

    private RESTEasyUtil() {
    }

    /**
     * Finds the RESTEasy parameter and return its value as list of class names.
     *
     * @param contextParams the map of all context parameters
     * @param paramName the name of the context parameter
     * @return a List of RESTEasy parmaters or null if not present
     */
    public static List<String> getParamValues(Map<String, String> contextParams, String paramName) {
        if (contextParams != null) {
            String providers = contextParams.get(paramName);
            if (providers != null) {
                return Arrays.asList(providers.split(","));
            }
        }
        return null;
    }

    /**
     * Finds the RESTEasy providers parameter and return its value as list of classes.
     *
     * @param contextParams the map of all context parameters
     * @return a List of RESTEasy provider classes or null if not present
     */
    public static List<Class<?>> getProviderClasses(Map<String, String> contextParams) {
        List<String> providers = getParamValues(contextParams, ResteasyContextParameters.RESTEASY_PROVIDERS);
        if (providers != null) {
            List<Class<?>> providerClasses = new ArrayList<Class<?>>(providers.size());
            for (String provider : providers) {
                Class<?> pc = Classes.forName(provider.trim());
                if (pc != null) {
                    providerClasses.add(pc);
                }
            }
            return providerClasses.isEmpty() ? null : providerClasses;
        }
        return null;
    }

    /**
     * Finds the RESTEasy providers parameter and return its value as map of classes with its generic types.
     *
     * @param contextParams the map of all context parameters
     * @return a Map of RESTEasy exception-provider classes
     */
    public static Map<Class<?>, Class<?>> getExceptionProviderMap(Map<String, String> contextParams) {
        Map<Class<?>, Class<?>> providerMap = new HashMap<Class<?>, Class<?>>();
        List<String> providers = getParamValues(contextParams, ResteasyContextParameters.RESTEASY_PROVIDERS);
        if (providers != null) {
            for (String provider : providers) {
                Class<?> providerClass = Classes.forName(provider.trim());
                if (providerClass != null) {
                    Type exceptionType = Types.getActualTypeArgumentsOfAnInterface(providerClass, ExceptionMapper.class)[0];
                    Class<?> exceptionClass = Types.getRawType(exceptionType);
                    providerMap.put(exceptionClass, providerClass);
                }
            }
        }
        return providerMap;
    }

    /**
     * Finds the RESTEasy client error interceptor parameter and return its value as list of instances.
     *
     * @param contextParams the map of all context parameters
     * @return a List of RESTEasy client interceptor instances or null if not present
     */
    public static List<ClientErrorInterceptor> getClientErrorInterceptors(Map<String, String> contextParams) {
        List<ClientErrorInterceptor> interceptorInstances = null;
        List<String> interceptors = getParamValues(contextParams, CLIENT_ERROR_INTERCEPTORS);
        if (interceptors != null) {
            interceptorInstances = new ArrayList<ClientErrorInterceptor>(interceptors.size());
            for (String interceptor : interceptors) {
                Class<?> ic = Classes.forName(interceptor.trim());
                if (ic != null) {
                    try {
                        ClientErrorInterceptor instance = (ClientErrorInterceptor) ic.newInstance();
                        interceptorInstances.add(instance);
                    } catch (InstantiationException ie) {
                        LOGGER.warn(ie);
                    } catch (IllegalAccessException iae) {
                        LOGGER.warn(iae);
                    }
                }
            }
        }
        return interceptorInstances;
    }

}
