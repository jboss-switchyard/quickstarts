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

import java.net.MalformedURLException;

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
    public synchronized WSEndpoint publish(final SOAPBindingModel config, final String bindingId, final InboundHandler handler) {
        return publish(config, bindingId, handler, null);
    }
}
