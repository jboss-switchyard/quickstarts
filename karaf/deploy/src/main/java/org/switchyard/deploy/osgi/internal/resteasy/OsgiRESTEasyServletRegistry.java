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

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.resteasy.spi.Registry;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;

/**
 * A registry for RESTEasy servlet.
 */
public class OsgiRESTEasyServletRegistry {

    private final ConcurrentHashMap<String, OsgiRESTEasyServletWrapper> _servlets = new ConcurrentHashMap<String, OsgiRESTEasyServletWrapper>();
    private HttpService _httpService;

    /**
     * Sets OSGi HttpService.
     * @param service OSGi HttpService
     * @return this OsgiRESTEasyServletRegistry (useful for chaining)
     */
    public OsgiRESTEasyServletRegistry setOsgiHttpService(HttpService service) {
        _httpService = service;
        return this;
    }

    /**
     * Registers a RESTEasy servlet.
     * @param alias alias
     * @param servlet RESTEasy servlet
     * @param initparams servlet init params
     * @param context HttpContext
     * @throws Exception if it fails to register a servlet
     */
    public void registerRESTEasyServlet(String alias, OsgiRESTEasyServletWrapper servlet, Dictionary<?,?> initparams, HttpContext context) throws Exception {
        _httpService.registerServlet(alias, servlet, initparams, context);
        _servlets.put(alias, servlet);
    }

    /**
     * Gets registered RESTEasy servlet.
     * @param alias alias
     * @return servlet
     */
    public OsgiRESTEasyServletWrapper getRegisteredRESTEasyServlet(String alias) {
        return _servlets.get(alias);
    }

    /**
     * Unregisters a RESTEasy servlet.
     * @param alias alias
     */
    public void unregisterRESTEasyServlet(String alias) {
        OsgiRESTEasyServletWrapper servlet = getRegisteredRESTEasyServlet(alias);
        if (servlet == null) {
            throw new IllegalArgumentException("No RESTEasy servlet is registered for the alias '" + alias + "'");
        }

        if (servlet.getDispatcher().getRegistry().getSize() != 0) {
            throw new IllegalStateException("Cannot remove RESTEasy Servlet '" + alias + "' - registry is not empty");
        }

        _httpService.unregister(alias);
        _servlets.remove(alias);
    }

    /**
     * Registers RESTEasy resources to the registered RESTEasy servlet.
     * @param alias alias
     * @param resources resource instances
     * @return classes of the registered resources
     */
    public List<Class<?>> registerRESTEasyResources(String alias, List<Object> resources) {
        OsgiRESTEasyServletWrapper servlet = getRegisteredRESTEasyServlet(alias);
        if (servlet == null) {
            throw new IllegalArgumentException("No servlet is registered for the alias '" + alias + "'");
        }

        Registry registry = servlet.getDispatcher().getRegistry();
        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (Object instance : resources) {
            registry.addSingletonResource(instance);
            classes.add(instance.getClass());
        }
        return classes;
    }

    /**
     * Unregisters RESTEasy resources from the servlet.
     * @param alias alias
     * @param resourceClasses resource classes
     */
    public void unregisterRESTEasyResources(String alias, List<Class<?>> resourceClasses) {
        OsgiRESTEasyServletWrapper servlet = getRegisteredRESTEasyServlet(alias);
        if (servlet == null) {
            throw new IllegalArgumentException("No servlet is registered for the alias '" + alias + "'");
        }

        Registry registry = servlet.getDispatcher().getRegistry();
        for (Class<?> clazz : resourceClasses) {
            registry.removeRegistrations(clazz);
        }
    }
}
