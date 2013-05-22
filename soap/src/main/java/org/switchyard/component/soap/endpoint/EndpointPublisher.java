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
import javax.xml.ws.WebServiceFeature;

import org.switchyard.component.soap.InboundHandler;
import org.switchyard.component.soap.config.model.SOAPBindingModel;

/**
 * Interface for allowing SwitchYard to publish and stop Webservice endpoints.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public interface EndpointPublisher {

    /**
     * Publish a Webservice Endpoint.
     * @param config The SOAP binding config
     * @param bindingId The SOAP binding version
     * @param handler The handler instance that contains the actual invoke method implementation
     * @return The published endpoint
     */
    WSEndpoint publish(SOAPBindingModel config, String bindingId, InboundHandler handler);

    /**
     * Publish a Webservice Endpoint.
     * @param config The SOAP binding config
     * @param bindingId The SOAP binding version
     * @param handler The handler instance that contains the actual invoke method implementation
     * @param features A list of WebServiceFeature to configure on the endpoint.
     * @return The published endpoint
     */
    WSEndpoint publish(SOAPBindingModel config, String bindingId, InboundHandler handler, WebServiceFeature... features);
}
