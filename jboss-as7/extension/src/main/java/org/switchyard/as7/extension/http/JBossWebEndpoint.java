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
 
package org.switchyard.as7.extension.http;

import org.apache.catalina.Container;
import org.apache.catalina.core.StandardContext;
import org.jboss.logging.Logger;
import org.switchyard.component.http.endpoint.Endpoint;

/**
 * A JBossWeb HTTP endpoint.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class JBossWebEndpoint implements Endpoint {

    private static final Logger LOG = Logger.getLogger("org.switchyard");

    private StandardContext _context;

    /**
     * Construct a JBossWebEndpoint with the given context.
     * @param context The StandardContext
     */
    public JBossWebEndpoint(final StandardContext context) {
        _context = context;
    }

    /**
     * Sets the context associated with this HTTP endpoint.
     * @return The StandardContext
     */
    public StandardContext getContext() {
        return _context;
    }

    /**
     * Sets the context associated with this HTTP endpoint.
     * @param context The StandardContext
     */
    public void setContext(StandardContext context) {
        _context = context;
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {
        if (_context != null) {
            // Destroy the web context unless if it is default
            if (!_context.getPath().equals("/")) {
                try {
                    Container container = _context.getParent();
                    container.removeChild(_context);
                    _context.stop();
                    _context.destroy();
                    LOG.info("Destroyed HTTP context " + _context.getPath());
                } catch (Exception e) {
                    LOG.error("Unable to destroy web context", e);
                }
            }
        }
    }
}
