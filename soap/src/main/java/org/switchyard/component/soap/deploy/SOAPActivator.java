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

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.switchyard.ExchangeHandler;
import org.switchyard.Service;
import org.switchyard.component.soap.InboundHandler;
import org.switchyard.component.soap.config.model.SOAPBindingModel;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.deploy.Activator;

/**
 * SOAP Activator.
 */
public class SOAPActivator implements Activator {
    
    private Map<QName, InboundHandler> _inboundGateways = 
        new HashMap<QName, InboundHandler>();


    @Override
    public ExchangeHandler init(QName name, Model config) {
        if (config instanceof CompositeServiceModel) {
            for (BindingModel binding : ((CompositeServiceModel)config).getBindings()) {
                if (binding instanceof SOAPBindingModel) {
                    InboundHandler handler = new InboundHandler((SOAPBindingModel)binding);
                    _inboundGateways.put(name, handler);
                    return handler;
                }
            }
        }

        // no bindings were found, raise a deployment error
        throw new RuntimeException("No SOAP bindings found for service " + name);
    }

    @Override
    public void start(Service service) {
        if (_inboundGateways.containsKey(service.getName())) {
            try {
                _inboundGateways.get(service.getName()).start(service);
            } catch (org.switchyard.component.soap.WebServicePublishException ex) {
                throw new RuntimeException(
                        "Failed to start inbound gateway for service " + service.getName(), ex);
            }
        }
    }

    @Override
    public void stop(Service service) {
        if (_inboundGateways.containsKey(service.getName())) {
            try {
                _inboundGateways.get(service.getName()).start(service);
            } catch (org.switchyard.component.soap.WebServicePublishException ex) {
                throw new RuntimeException(
                        "Failed to stop inbound gateway for service " + service.getName(), ex);
            }
        }
        
    }

    @Override
    public void destroy(Service service) {
        _inboundGateways.remove(service.getName());
    }
}
