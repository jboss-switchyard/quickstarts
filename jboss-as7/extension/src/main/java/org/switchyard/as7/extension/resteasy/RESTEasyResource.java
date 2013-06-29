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
 
package org.switchyard.as7.extension.resteasy;

import java.util.List;

import org.apache.catalina.Container;
import org.apache.catalina.core.StandardContext;
import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.Registry;
import org.switchyard.component.resteasy.resource.Resource;

/**
 * A standalone RESTEasy resource.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class RESTEasyResource implements Resource {

    private static final Logger LOG = Logger.getLogger("org.switchyard");

    private StandardContext _context;
    private List<Class<?>> _classes;

    /**
     * Sets the context associated with this resource deployment.
     * @return The StandardContext
     */
    public StandardContext getContext() {
        return _context;
    }

    /**
     * Sets the context associated with this resource deployment.
     * @param context The StandardContext
     */
    public void setContext(StandardContext context) {
        _context = context;
    }

    /**
     * Gets the list of classes associated with this resource deployment.
     * @return The List of classes
     */
    public List<Class<?>> getClasses() {
        return _classes;
    }

    /**
     * Sets the list of classes associated with this resource deployment.
     * @param classes The List of classes
     */
    public void setClasses(List<Class<?>> classes) {
        _classes = classes;
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {
        if (_context != null) {
            Registry registry = (Registry)_context.getServletContext().getAttribute(Registry.class.getName());
            if (registry != null) {
                // Remove registrations
                for (Class<?> clazz : _classes) {
                    LOG.debug("Stopping ... " + clazz);
                    registry.removeRegistrations(clazz);
                }
                // Destroy the web context unless if it is default
                if ((registry.getSize() == 0) && (!_context.getPath().equals("/"))) {
                    try {
                        Container container = _context.getParent();
                        container.removeChild(_context);
                        _context.stop();
                        _context.destroy();
                        LOG.info("Destroyed RESTEasy context " + _context.getPath());
                    } catch (Exception e) {
                        LOG.error("Unable to destroy web context", e);
                    }
                }
            }
        }
    }
}
