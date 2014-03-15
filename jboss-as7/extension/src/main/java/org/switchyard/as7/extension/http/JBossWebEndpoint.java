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
 
package org.switchyard.as7.extension.http;

import org.apache.catalina.Container;
import org.apache.catalina.core.StandardContext;
import org.jboss.logging.Logger;
import org.switchyard.component.common.Endpoint;

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
    public void start() {
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
