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
 
package org.switchyard.component.resteasy.osgi;

import java.util.List;

import org.jboss.logging.Logger;
import org.switchyard.component.common.Endpoint;

/**
 * An OSGi RESTEasy resource.
 */
public class OsgiRESTEasyResource implements Endpoint {

    private static final Logger LOGGER = Logger.getLogger(OsgiRESTEasyResource.class);

    private OsgiRESTEasyServletRegistry _registry;
    private String _alias;
    private List<Class<?>> _classes;

    /**
     * Sets OsgiRESTEasyServletRegistry.
     * @param registry OsgiRESTEasyServletRegistry
     * @return this OsgiRESTEasyResource (useful for chaining)
     */
    public OsgiRESTEasyResource setServletRegistry(OsgiRESTEasyServletRegistry registry) {
        _registry = registry;
        return this;
    }

    /**
     * Sets alias.
     * @param alias alias
     * @return this OsgiRESTEasyResource (useful for chaining)
     */
    public OsgiRESTEasyResource setAlias(String alias) {
        _alias = alias;
        return this;
    }

    /**
     * Sets resource class.
     * @param classes resource class
     * @return this OsgiRESTEasyResource (useful for chaining)
     */
    public OsgiRESTEasyResource setResourceClasses(List<Class<?>> classes) {
        _classes = classes;
        return this;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        _registry.unregisterRESTEasyResources(_alias, _classes);
        OsgiRESTEasyServletWrapper servlet = _registry.getRegisteredRESTEasyServlet(_alias);
        if (servlet != null && servlet.getDispatcher().getRegistry().getSize() == 0) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Unregistering RESTEasy servlet with an alias '" + _alias + "'");
            }
            _registry.unregisterRESTEasyServlet(_alias);
        }
    }

}
