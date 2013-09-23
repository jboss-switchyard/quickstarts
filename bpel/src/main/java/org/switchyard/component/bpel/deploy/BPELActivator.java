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
package org.switchyard.component.bpel.deploy;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.riftsaw.engine.BPELEngine;
import org.switchyard.config.model.implementation.bpel.BPELComponentImplementationModel;
import org.switchyard.component.bpel.BPELMessages;
import org.switchyard.component.bpel.exchange.BPELExchangeHandler;
import org.switchyard.component.bpel.exchange.BPELExchangeHandlerFactory;
import org.switchyard.component.bpel.riftsaw.RiftsawServiceLocator;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.deploy.BaseActivator;
import org.switchyard.deploy.ServiceHandler;

/**
 * Activator for the BPEL component.
 *
 */
public class BPELActivator extends BaseActivator {
    
    /**
     * BPEL component activator type name.
     */
    public static final String BPEL_TYPE = "bpel";

    private static final Logger LOG = Logger.getLogger(BPELActivator.class);
    
    private static Map<QName, BPELExchangeHandler> _handlers = new HashMap<QName , BPELExchangeHandler>();

    private BPELEngine _engine = null;
    private RiftsawServiceLocator _locator = null;
    private java.util.Properties _config = null;
    
    
    /**
     * Constructs a new Activator of type "bpel".
     * 
     * @param engine The BPEL engine
     * @param locator The service locator
     * @param config The properties
     */
    public BPELActivator(BPELEngine engine, RiftsawServiceLocator locator, java.util.Properties config) {
        super(BPEL_TYPE);
        
        _engine = engine;
        _locator = locator;
        _config = config;
    }
    
    @Override
    public ServiceHandler activateService(QName serviceName, ComponentModel config) {
        
        if (LOG.isDebugEnabled()) {
            LOG.debug("Activate service: "+serviceName+" config="+config);
        }
        
        BPELExchangeHandler handler = BPELExchangeHandlerFactory.instance().newBPELExchangeHandler(getServiceDomain());
        BPELComponentImplementationModel bciModel = (BPELComponentImplementationModel)config.getImplementation();
        ComponentServiceModel service = null;
        for (ComponentServiceModel csm : config.getServices()) {
            if (csm.getQName().equals(serviceName)) {
                service = csm;
                break;
            }
        }
        
        if (service.getInterface() == null) {
            throw BPELMessages.MESSAGES.interfaceNotDefinedForComponentWithBPELImplementation();
        }

        // take care of references
        for (ComponentReferenceModel crm : config.getReferences()) {
            _locator.addServiceDomain(crm.getQName(), getServiceDomain());
            _locator.initialiseReference(crm);
        }
        
        handler.init(serviceName, bciModel,
                service.getInterface().getInterface(), _engine, _config);
        
        _handlers.put(serviceName, handler);
            
        return handler;
    }
    
    @Override
    public void deactivateService(QName name, ServiceHandler handler) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("De-activate service: " + name + " handler=" + handler);
        }
        
        _handlers.remove(name);
    }

}
