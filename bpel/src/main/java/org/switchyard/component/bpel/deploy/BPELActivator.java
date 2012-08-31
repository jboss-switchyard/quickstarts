/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.bpel.deploy;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.riftsaw.engine.BPELEngine;
import org.switchyard.component.bpel.config.model.BPELComponentImplementationModel;
import org.switchyard.component.bpel.exchange.BPELExchangeHandler;
import org.switchyard.component.bpel.exchange.BPELExchangeHandlerFactory;
import org.switchyard.component.bpel.riftsaw.RiftsawServiceLocator;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.deploy.BaseActivator;
import org.switchyard.deploy.ServiceHandler;
import org.switchyard.exception.SwitchYardException;

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
            throw new SwitchYardException("Interface not defined for component with BPEL implementation");
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
