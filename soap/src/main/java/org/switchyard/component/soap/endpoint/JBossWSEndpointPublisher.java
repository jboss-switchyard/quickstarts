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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jboss.wsf.spi.metadata.webservices.PortComponentMetaData;
import org.jboss.wsf.spi.metadata.webservices.WebserviceDescriptionMetaData;
import org.jboss.wsf.spi.metadata.webservices.WebservicesMetaData;
import org.switchyard.component.soap.InboundHandler;
import org.switchyard.component.soap.WebServicePublishException;
import org.switchyard.component.soap.config.model.SOAPBindingModel;

/**
 * Handles publishing of Webservice Endpoints on JBossWS stack.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class JBossWSEndpointPublisher extends AbstractEndpointPublisher {

    private static final Logger LOGGER = Logger.getLogger(JBossWSEndpointPublisher.class);

    private static final String SEI = "org.switchyard.component.soap.endpoint.BaseWebService";

    /**
     * {@inheritDoc}
     */
    public synchronized WSEndpoint publish(final SOAPBindingModel config, final String bindingId, final InboundHandler handler) {
        JBossWSEndpoint wsEndpoint = null;
        try {
            initialize(config);

            WebservicesMetaData metadata = new WebservicesMetaData();
            WebserviceDescriptionMetaData webserviceDescription = new WebserviceDescriptionMetaData(metadata);
            metadata.addWebserviceDescription(webserviceDescription);
            webserviceDescription.setWsdlFile(getWsdlLocation());
            PortComponentMetaData portComponent = new PortComponentMetaData(webserviceDescription);
            portComponent.setPortComponentName(config.getServiceName() 
                                                + ":" + config.getPort().getServiceQName().getLocalPart() 
                                                + ":" + config.getPort().getPortQName().getLocalPart()); //unique ID
            portComponent.setServiceEndpointInterface(SEI);
            portComponent.setWsdlPort(config.getPort().getPortQName());
            portComponent.setWsdlService(config.getPort().getServiceQName());
            webserviceDescription.addPortComponent(portComponent);
            Map<String,String> map = new HashMap<String, String>();
            map.put("/" + config.getPort().getServiceName(), SEI);

            wsEndpoint = new JBossWSEndpoint();
            if (config.getContextPath() != null) {
                wsEndpoint.publish(getContextRoot(), map, metadata, handler);
            } else {
                wsEndpoint.publish(getContextPath(), map, metadata, handler);
            }
        } catch (Exception e) {
            throw new WebServicePublishException(e);
        }
        return wsEndpoint;
    }
}
