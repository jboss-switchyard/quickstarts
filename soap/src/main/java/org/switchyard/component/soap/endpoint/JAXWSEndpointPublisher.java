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
 
package org.switchyard.component.soap.endpoint;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.wsdl.WSDLException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Endpoint;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.handler.MessageContext;

import org.switchyard.component.soap.InboundHandler;
import org.switchyard.component.soap.WebServicePublishException;
import org.switchyard.component.soap.config.model.SOAPBindingModel;
import org.switchyard.component.soap.util.WSDLUtil;

/**
 * Handles publishing of Webservice Endpoints on JAX-WS implementations.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class JAXWSEndpointPublisher extends AbstractEndpointPublisher {

    private static final String HTTP_SCHEME = "http";

    /**
     * {@inheritDoc}
     */
    public synchronized WSEndpoint publish(final SOAPBindingModel config, final String bindingId, final InboundHandler handler, WebServiceFeature... features) {
        JAXWSEndpoint wsEndpoint = null;
        try {
            initialize(config);
            List<Source> metadata = new ArrayList<Source>();
            StreamSource source = WSDLUtil.getStream(config.getWsdl());
            metadata.add(source);
            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put(Endpoint.WSDL_SERVICE, config.getPort().getServiceQName());
            properties.put(Endpoint.WSDL_PORT, config.getPort().getPortQName());
            properties.put(MessageContext.WSDL_DESCRIPTION, getWsdlLocation());

            String publishUrl = HTTP_SCHEME + "://" + config.getSocketAddr().getHost() + ":" + config.getSocketAddr().getPort() + "/" + getContextPath();

            wsEndpoint = new JAXWSEndpoint(bindingId, handler, features);
            wsEndpoint.getEndpoint().setMetadata(metadata);
            wsEndpoint.getEndpoint().setProperties(properties);
            wsEndpoint.publish(publishUrl);
        } catch (MalformedURLException e) {
            throw new WebServicePublishException(e);
        } catch (WSDLException e) {
            throw new WebServicePublishException(e);
        }
        return wsEndpoint;
    }
}
