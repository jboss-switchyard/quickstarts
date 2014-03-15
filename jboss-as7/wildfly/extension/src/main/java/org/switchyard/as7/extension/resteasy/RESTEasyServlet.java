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
 
package org.switchyard.as7.extension.resteasy;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

/**
 * A RESTEasy servlet dispatcher.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2014 Red Hat Inc.
 */
public class RESTEasyServlet extends HttpServletDispatcher {

    private static final Logger LOG = Logger.getLogger("org.switchyard");
    private Registry _registry;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        ServletContext servletContext = servletConfig.getServletContext();
        _registry = (Registry)servletContext.getAttribute(Registry.class.getName());
    }

    @Override
    public void destroy() {
        //_deployment.stop();
    }

    @Override
    protected void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        super.service(new RESTEasyServletRequest(httpServletRequest), httpServletResponse);
    }

    /**
     * Register instances associated with this resource deployment.
     * @param instances The List of instances
     */
    public void addInstances(List<Object> instances) {
        // Add as singleton instance
        for (Object instance : instances) {
            _registry.addSingletonResource(instance);
            if (LOG.isTraceEnabled()) {
                LOG.trace("Registering instance of " + instance.getClass());
            }
        }
    }

    /**
     * Unregister instances associated with this resource deployment.
     * @param instances The List of instances
     */
    public void removeInstances(List<Object> instances) {
        // Add as singleton instance
        for (Object instance : instances) {
            _registry.removeRegistrations(instance.getClass());
            if (LOG.isTraceEnabled()) {
                LOG.trace("Unregistering instance of " + instance.getClass());
            }
        }
    }
}
