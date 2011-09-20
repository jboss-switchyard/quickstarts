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
import org.switchyard.ServiceReference;
import org.switchyard.component.soap.InboundHandler;
import org.switchyard.component.soap.OutboundHandler;
import org.switchyard.component.soap.WebServiceConsumeException;
import org.switchyard.component.soap.WebServicePublishException;
import org.switchyard.component.soap.config.model.SOAPBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.deploy.BaseActivator;
import org.switchyard.exception.SwitchYardException;


/**
 * SOAP Activator.
 */
public class SOAPActivator extends BaseActivator {

    private static final String SOAP_TYPE = "soap";
    
    private Map<QName, InboundHandler> _inboundGateways = 
        new HashMap<QName, InboundHandler>();
    private Map<QName, OutboundHandler> _outboundGateways = 
        new HashMap<QName, OutboundHandler>();
    private Configuration _environment;
    
    /**
     * Creates a new activator for SOAP endpoints.
     */
    public SOAPActivator() {
        super(SOAP_TYPE);
    }


    @Override
    public ExchangeHandler init(QName name, Model config) {
        if (config instanceof CompositeServiceModel) {
            for (BindingModel binding : ((CompositeServiceModel)config).getBindings()) {
                if (binding instanceof SOAPBindingModel) {
                    SOAPBindingModel soapModel = (SOAPBindingModel)binding;
                    soapModel.setEnvironment(_environment);
                    InboundHandler handler = new InboundHandler(soapModel);
                    _inboundGateways.put(name, handler);
                    return handler;
                }
            }
        }

        if (config instanceof CompositeReferenceModel) {
            for (BindingModel binding : ((CompositeReferenceModel)config).getBindings()) {
                if (binding instanceof SOAPBindingModel) {
                    SOAPBindingModel soapModel = (SOAPBindingModel)binding;
                    soapModel.setEnvironment(_environment);
                    OutboundHandler handler = new OutboundHandler(soapModel);
                    _outboundGateways.put(name, handler);
                    return handler;
                }
            }
        }

        // no bindings were found, raise a deployment error
        throw new SwitchYardException("No SOAP bindings found for service " + name);
    }

    @Override
    public void start(ServiceReference service) {
        if (_inboundGateways.containsKey(service.getName())) {
            try {
                _inboundGateways.get(service.getName()).start(service);
            } catch (WebServicePublishException ex) {
                throw new SwitchYardException(
                        "Failed to start inbound gateway for service " + service.getName(), ex);
            }
        }
        if (_outboundGateways.containsKey(service.getName())) {
            try {
                _outboundGateways.get(service.getName()).start();
            } catch (WebServiceConsumeException ex) {
                throw new SwitchYardException(
                        "Failed to start outbound gateway for service " + service.getName(), ex);
            }
        }
    }

    @Override
    public void stop(ServiceReference service) {
        if (_inboundGateways.containsKey(service.getName())) {
                _inboundGateways.get(service.getName()).stop();
        }
        if (_outboundGateways.containsKey(service.getName())) {
                _outboundGateways.get(service.getName()).stop();
        }
    }

    @Override
    public void destroy(ServiceReference service) {
        _inboundGateways.remove(service.getName());
        _outboundGateways.remove(service.getName());
    }

    /**
     * Set the Environment configuration for the activator.
     * @param config The global environment configuration.
     */
    public void setEnvironment(Configuration config) {
        _environment = config;
    }
}
