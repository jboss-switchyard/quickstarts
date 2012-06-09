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
 
package org.switchyard.component.soap.endpoint;

import java.util.Map;
import java.util.ServiceLoader;

import org.apache.log4j.Logger;
import org.jboss.wsf.spi.classloading.ClassLoaderProvider;
import org.jboss.wsf.spi.deployment.Endpoint;
import org.jboss.wsf.spi.metadata.webservices.WebservicesMetaData;
import org.jboss.wsf.spi.publish.Context;
import org.jboss.wsf.spi.publish.EndpointPublisher;
import org.jboss.wsf.spi.publish.EndpointPublisherFactory;
import org.switchyard.component.soap.InboundHandler;
import org.switchyard.component.soap.WebServicePublishException;

/**
 * Wrapper for JBossWS endpoints.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class JBossWSEndpoint implements WSEndpoint {

    private static final Logger LOG = Logger.getLogger(JBossWSEndpoint.class);
    private static final String HOST = "default-host";
    private static final EndpointPublisherFactory FACTORY;

    private EndpointPublisher _publisher;
    private Context _context;

    /**
     * Construct a JBossWS endpoint on default host.
     * @throws Exception If a publisher could not be created
     */
    public JBossWSEndpoint() throws Exception {
        _publisher = FACTORY.newEndpointPublisher(HOST);
    }

    /**
     * Construct a JBossWS endpoint on specified host.
     * @param host The host on which the pubhlisher should created
     * @throws Exception if a publisher could not be created
     */
    public JBossWSEndpoint(String host) throws Exception {
        _publisher = FACTORY.newEndpointPublisher(host);
    }

    /**
     * {@inheritDoc}
     */
    public void publish(String contextRoot, Map<String, String> urlPatternToClassNameMap, WebservicesMetaData metadata, InboundHandler handler) throws Exception {
        _context = _publisher.publish(contextRoot, Thread.currentThread().getContextClassLoader(), urlPatternToClassNameMap, metadata);
        for (Endpoint ep : _context.getEndpoints()) {
            BaseWebService wsProvider = (BaseWebService) ep.getInstanceProvider().getInstance(BaseWebService.class.getName()).getValue();
            wsProvider.setInvocationClassLoader(Thread.currentThread().getContextClassLoader());
            // Hook the handler
            wsProvider.setConsumer(handler);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {
        if (_context != null && _publisher != null) {
            try {
                //undeploy endpoints
                _publisher.destroy(_context);
            } catch (Exception e) {
                LOG.error(e);
            }
        }
    }

    static {
        try {
            ClassLoader loader = ClassLoaderProvider.getDefaultProvider().getWebServiceSubsystemClassLoader();
            FACTORY = ServiceLoader.load(EndpointPublisherFactory.class, loader).iterator().next();
        } catch (Exception e) {
            throw new WebServicePublishException(e);
        }
    }
}
