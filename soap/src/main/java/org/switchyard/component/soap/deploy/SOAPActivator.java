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

package org.switchyard.component.soap.deploy;

import javax.xml.namespace.QName;

import org.switchyard.component.soap.InboundHandler;
import org.switchyard.component.soap.OutboundHandler;
import org.switchyard.component.soap.config.model.SOAPBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.deploy.BaseActivator;
import org.switchyard.deploy.ServiceHandler;


/**
 * SOAP Activator.
 */
public class SOAPActivator extends BaseActivator {

    static final String SOAP_TYPE = "soap";
    private Configuration _environment;
    
    /**
     * Creates a new activator for SOAP endpoints.
     */
    public SOAPActivator() {
        super(SOAP_TYPE);
    }

    @Override
    public ServiceHandler activateBinding(QName name, BindingModel config) {
        SOAPBindingModel binding = (SOAPBindingModel)config;
        binding.setEnvironment(_environment);
        
        if (binding.isServiceBinding()) {
            return new InboundHandler(binding, getServiceDomain());
        } else {
            return new OutboundHandler(binding);
        }
    }
    
    @Override
    public void deactivateBinding(QName name, ServiceHandler handler) {
        // Nothing to do here
    }

    /**
     * Set the Environment configuration for the activator.
     * @param config The global environment configuration.
     */
    public void setEnvironment(Configuration config) {
        _environment = config;
    }
}
