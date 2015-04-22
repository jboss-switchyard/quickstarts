/* 
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
 
package org.switchyard.deploy.osgi.internal.soap;

import java.net.MalformedURLException;

import javax.xml.ws.WebServiceFeature;

import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;
import org.switchyard.ServiceDomain;
import org.switchyard.component.common.Endpoint;
import org.switchyard.component.soap.InboundHandler;
import org.switchyard.component.soap.WebServicePublishException;
import org.switchyard.component.soap.config.model.SOAPBindingModel;
import org.switchyard.component.soap.endpoint.AbstractEndpointPublisher;
import org.switchyard.component.soap.endpoint.CXFJettyEndpoint;

/**
 * Handles publishing of Webservice Endpoints on CXF implementations.
 *
 */
public class OsgiEndpointPublisher extends AbstractEndpointPublisher {
    /**
     * {@inheritDoc}
     */
    public synchronized Endpoint publish(ServiceDomain domain, final SOAPBindingModel config, final String bindingId, final InboundHandler handler, WebServiceFeature... features) {
        CXFJettyEndpoint wsEndpoint = null;
        try {
            initialize(config);

            String publishUrl = "/" + getContextPath();

            wsEndpoint = new CXFJettyEndpoint(bindingId, handler, new AddressingInterceptor(), features);
            wsEndpoint.getEndpoint().setWsdlURL(getWsdlLocation());
            wsEndpoint.getEndpoint().setServiceName(config.getPort().getServiceQName());
            wsEndpoint.publish(publishUrl);
        } catch (MalformedURLException e) {
            throw new WebServicePublishException(e);
        }
        return wsEndpoint;
    }

    @Override
    public Interceptor<? extends Message> createAddressingInterceptor() {
        return new AddressingInterceptor();
    }

}
