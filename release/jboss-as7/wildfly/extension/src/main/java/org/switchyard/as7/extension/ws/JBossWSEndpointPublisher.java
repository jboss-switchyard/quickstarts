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
 
package org.switchyard.as7.extension.ws;

import java.util.HashMap;
import java.util.Map;

import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.AddressingFeature;
import javax.xml.ws.soap.MTOMFeature;

import org.jboss.logging.Logger;
import org.jboss.wsf.spi.metadata.webservices.PortComponentMetaData;
import org.jboss.wsf.spi.metadata.webservices.WebserviceDescriptionMetaData;
import org.jboss.wsf.spi.metadata.webservices.WebservicesMetaData;
import org.switchyard.ServiceDomain;
import org.switchyard.component.common.Endpoint;
import org.switchyard.component.soap.InboundHandler;
import org.switchyard.component.soap.WebServicePublishException;
import org.switchyard.component.soap.config.model.SOAPBindingModel;
import org.switchyard.component.soap.endpoint.AbstractEndpointPublisher;

/**
 * Handles publishing of Webservice Endpoints on JBossWS stack.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class JBossWSEndpointPublisher extends AbstractEndpointPublisher {

    private static final Logger LOGGER = Logger.getLogger("org.switchyard");

    private static final String SEI = "org.switchyard.component.soap.endpoint.BaseWebService";
    private static final String RESPONSE_STATUS_HANDLER = "ResponseStatusHandler";

    /**
     * {@inheritDoc}
     */
    public synchronized Endpoint publish(ServiceDomain domain, final SOAPBindingModel config, final String bindingId, final InboundHandler handler, WebServiceFeature... features) {
        JBossWSEndpoint wsEndpoint = null;
        try {
            initialize(config);
            Map<String,String> map = new HashMap<String, String>();
            map.put("/" + config.getPort().getServiceName(), SEI);

            WebservicesMetaData wsMetadata = new WebservicesMetaData();
            WebserviceDescriptionMetaData wsDescMetaData = new WebserviceDescriptionMetaData(wsMetadata);
            wsDescMetaData.setWsdlFile(getWsdlLocation());
            PortComponentMetaData portComponent = new PortComponentMetaData(wsDescMetaData);
            portComponent.setPortComponentName(config.getServiceName() 
                                                + ":" + config.getPort().getServiceQName().getLocalPart() 
                                                + ":" + config.getPort().getPortQName().getLocalPart()); //unique ID
            portComponent.setServiceEndpointInterface(SEI);
            portComponent.setWsdlPort(config.getPort().getPortQName());
            portComponent.setWsdlService(config.getPort().getServiceQName());
             // Should be the WSDL's service name and not the SwitchYard config's service name
            portComponent.setServletLink(config.getPort().getServiceQName().getLocalPart());

            for (WebServiceFeature feature : features) {
                if (feature instanceof AddressingFeature) {
                    AddressingFeature addrFeature = (AddressingFeature)feature;
                    portComponent.setAddressingEnabled(addrFeature.isEnabled());
                    portComponent.setAddressingRequired(addrFeature.isRequired());
                    LOGGER.info("Addressing [enabled = " + addrFeature.isEnabled() + ", required = " + addrFeature.isRequired() + "]");
                } else if (feature instanceof MTOMFeature) {
                    MTOMFeature mtom = (MTOMFeature)feature;
                    portComponent.setMtomEnabled(mtom.isEnabled());
                    portComponent.setMtomThreshold(mtom.getThreshold());
                    LOGGER.info("MTOM [enabled = " + mtom.isEnabled() + ", threshold = " + mtom.getThreshold() + "]");
                }
            }
            wsDescMetaData.addPortComponent(portComponent);
            wsMetadata.addWebserviceDescription(wsDescMetaData);

            wsEndpoint = new JBossWSEndpoint();
            if (config.getContextPath() != null) {
                wsEndpoint.publish(domain, getContextRoot(), map, wsMetadata, config, handler);
            } else {
                wsEndpoint.publish(domain, getContextPath(), map, wsMetadata, config, handler);
            }
        } catch (Exception e) {
            throw new WebServicePublishException(e);
        }
        return wsEndpoint;
    }
}
