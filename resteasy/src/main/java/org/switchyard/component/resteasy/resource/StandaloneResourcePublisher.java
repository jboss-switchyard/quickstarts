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
 
package org.switchyard.component.resteasy.resource;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

import org.apache.log4j.Logger;
import com.sun.net.httpserver.HttpServer;
import org.jboss.resteasy.plugins.server.sun.http.HttpContextBuilder;

/**
 * Publishes standalone RESTEasy resource.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class StandaloneResourcePublisher implements ResourcePublisher {
    private static final Logger LOGGER = Logger.getLogger(StandaloneResourcePublisher.class);

    // The global standalone HttpServer
    private static HttpServer _httpServer;
    private static HttpContextBuilder _contextBuilder;

    static {
        try {
            _contextBuilder = new HttpContextBuilder();
            _httpServer = HttpServer.create(new InetSocketAddress(8080), 10);
            _httpServer.setExecutor(null); // creates a default executor
            _httpServer.start();
        } catch (IOException ioe) {
            LOGGER.error("Unable to launch standalone http server", ioe);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Resource publish(String context, List<Object> instances) throws Exception {
        if (_contextBuilder.getPath().equals(context)) {
            _contextBuilder.cleanup();
            try {
                _httpServer.removeContext(_contextBuilder.getPath());
            } catch (IllegalArgumentException iae) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(iae);
                }
            }
            _contextBuilder.getDeployment().getDefaultContextObjects().clear();
            _contextBuilder.setPath(context);
        }
        // Add as singleton instance
        for (Object instance : instances) {
            _contextBuilder.getDeployment().getResources().add(instance);
        }
        _contextBuilder.bind(_httpServer);
        return new StandaloneResource();
    }
}
