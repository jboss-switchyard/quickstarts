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

import javax.xml.ws.WebServiceFeature;

import org.switchyard.ServiceDomain;
import org.switchyard.component.common.Endpoint;
import org.switchyard.component.soap.InboundHandler;
import org.switchyard.component.soap.config.model.SOAPBindingModel;
import org.switchyard.component.soap.util.WSDLUtil;

/**
 * Hanldes configuration of Webservice Endpoints.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public abstract class AbstractEndpointPublisher implements EndpointPublisher {

    private String _ctxRoot;
    private String _ctxPath;
    private String _wsdlLocation;

    /**
     * Returns the context root for this publisher.
     * @return The context root
     */
    public String getContextRoot() {
        return _ctxRoot;
    }

    /**
     * Returns the context path for this publisher.
     * @return The context path
     */
    public String getContextPath() {
        return _ctxPath;
    }

    /**
     * Returns the location of the WSDL for this publisher.
     * @return The wsdl location
     */
    public String getWsdlLocation() {
        return _wsdlLocation;
    }

    /**
     * Initialize paths and location for this publisher.
     * @param The SOAP binding configuration
     */
    protected void initialize(final SOAPBindingModel config) throws MalformedURLException {
        if (config.getContextPath() != null) {
            _ctxRoot = config.getContextPath();
            _ctxPath = _ctxRoot + "/" + config.getPort().getServiceName();
        } else {
            _ctxRoot = "";
            _ctxPath = config.getPort().getServiceName();
        }
        _wsdlLocation = WSDLUtil.getURL(config.getWsdl()).toExternalForm();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized Endpoint publish(ServiceDomain domain, final SOAPBindingModel config, final String bindingId, final InboundHandler handler) {
        return publish(domain, config, bindingId, handler, (WebServiceFeature)null);
    }
}
