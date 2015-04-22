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
import javax.xml.ws.WebServiceFeature;

import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;
import org.switchyard.ServiceDomain;
import org.switchyard.component.common.Endpoint;
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
     * @param domain The ServiceDomain for the application
     * @param config The SOAP binding config
     * @param bindingId The SOAP binding version
     * @param handler The handler instance that contains the actual invoke method implementation
     * @return The published endpoint
     */
    Endpoint publish(ServiceDomain domain, SOAPBindingModel config, String bindingId, InboundHandler handler);

    /**
     * Publish a Webservice Endpoint.
     * @param domain The ServiceDomain for the application
     * @param config The SOAP binding config
     * @param bindingId The SOAP binding version
     * @param handler The handler instance that contains the actual invoke method implementation
     * @param features A list of WebServiceFeature to configure on the endpoint.
     * @return The published endpoint
     */
    Endpoint publish(ServiceDomain domain, SOAPBindingModel config, String bindingId, InboundHandler handler, WebServiceFeature... features);
    
    /**
     * @return a new interceptor used to hook addressing
     */
    Interceptor<? extends Message> createAddressingInterceptor();
}
