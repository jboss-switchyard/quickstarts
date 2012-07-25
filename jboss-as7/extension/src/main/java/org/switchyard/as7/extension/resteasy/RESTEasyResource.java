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
