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
 
package org.switchyard.as7.extension.ws;

import java.net.URL;
import java.util.Map;
import java.util.ServiceLoader;

import org.apache.log4j.Logger;
import org.jboss.wsf.spi.classloading.ClassLoaderProvider;
import org.jboss.wsf.spi.deployment.Endpoint;
import org.jboss.wsf.spi.metadata.webservices.JBossWebservicesMetaData;
import org.jboss.wsf.spi.metadata.webservices.WebservicesMetaData;
import org.jboss.wsf.spi.publish.Context;
import org.jboss.wsf.spi.publish.EndpointPublisher;
import org.jboss.wsf.spi.publish.EndpointPublisherFactory;
import org.switchyard.common.type.Classes;
import org.switchyard.component.soap.InboundHandler;
import org.switchyard.component.soap.WebServicePublishException;
import org.switchyard.component.soap.config.model.EndpointConfigModel;
import org.switchyard.component.soap.config.model.SOAPBindingModel;
import org.switchyard.component.soap.endpoint.BaseWebService;
import org.switchyard.component.soap.endpoint.WSEndpoint;

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
    public void publish(String contextRoot, Map<String, String> urlPatternToClassNameMap, WebservicesMetaData wsMetadata, SOAPBindingModel bindingModel, InboundHandler handler) throws Exception {
        EndpointConfigModel epcModel = bindingModel.getEndpointConfig();
        JBossWebservicesMetaData jbwsMetadata = null;
        if (epcModel != null) {
            String configFile = epcModel.getConfigFile();
            if (configFile != null) {
                URL jbwsURL = Classes.getResource(configFile, getClass());
                jbwsMetadata = new JBossWebservicesMetaData(jbwsURL);
                jbwsMetadata.setConfigFile(configFile);
            }
            String configName = epcModel.getConfigName();
            if (configName != null) {
                if (jbwsMetadata == null) {
                    jbwsMetadata = new JBossWebservicesMetaData(null);
                }
                jbwsMetadata.setConfigName(configName);
            }
        }
        ClassLoader tccl = Classes.getTCCL();
        _context = _publisher.publish(contextRoot, tccl, urlPatternToClassNameMap, wsMetadata, jbwsMetadata);
        for (Endpoint ep : _context.getEndpoints()) {
            BaseWebService wsProvider = (BaseWebService)ep.getInstanceProvider().getInstance(BaseWebService.class.getName()).getValue();
            wsProvider.setInvocationClassLoader(tccl);
            // Hook the handler
            wsProvider.setConsumer(handler);
            // Hook the interceptors
            Interceptors.addInterceptors(ep, bindingModel, tccl);
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
