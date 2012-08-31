/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2010-2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more 
 * details. You should have received a copy of the GNU Lesser General Public 
 * License, v.2.1 along with this distribution; if not, write to the Free 
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  
 * 02110-1301, USA.
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
