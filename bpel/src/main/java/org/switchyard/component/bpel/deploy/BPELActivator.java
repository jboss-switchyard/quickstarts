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
import org.riftsaw.engine.BPELEngineFactory;
import org.riftsaw.engine.internal.BPELEngineImpl;
import org.switchyard.component.bpel.config.model.BPELComponentImplementationModel;
import org.switchyard.component.bpel.exchange.BPELExchangeHandler;
import org.switchyard.component.bpel.exchange.BPELExchangeHandlerFactory;
import org.switchyard.component.bpel.riftsaw.RiftsawServiceLocator;
import org.switchyard.config.Configuration;
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

    private static final Logger LOG = Logger.getLogger(BPELActivator.class);
    
    private static Map<QName, BPELExchangeHandler> _handlers = new HashMap<QName , BPELExchangeHandler>();

    private static BPELEngine _engine=null;
    private static Configuration _configuration=null;
    private static RiftsawServiceLocator locator = new RiftsawServiceLocator();
    
    
    /**
     * Constructs a new Activator of type "bpel".
     */
    public BPELActivator() {
        super("bpel");
    }
    
    
    @Override
    public ServiceHandler activateService(QName serviceName, ComponentModel config) {
        init();
        
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
            locator.addServiceDomain(crm.getQName(), getServiceDomain());
            ((RiftsawServiceLocator)_engine.getServiceLocator()).initialiseReference(crm);
        }
        
        handler.init(serviceName, bciModel, service.getInterface().getInterface(), _engine);
        _handlers.put(serviceName, handler);
            
        return handler;
    }
    
    @Override
    public void deactivateService(QName name, ServiceHandler handler) {
        _handlers.remove(name);
        // Check if engine should be removed
        synchronized (BPELActivator.class) {
            if (_handlers.size() == 0 && _engine != null) {
                try {
                    _engine.close();
                    _engine = null;
                } catch (Exception e) {
                    LOG.error("Failed to close BPEL engine", e);
                }
            }
        }
    }


    /**
     * Associate the configuration with the activator.
     * 
     * @param config The configuration
     */
    protected void setConfiguration(Configuration config) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Setting configuration to: "+config);
        }
        _configuration = config;
    }

    protected void init() {
        // _engine is a static member, so this synchronization needs to be on the class
        synchronized (BPELActivator.class) {
            if (_engine == null) {
                _engine = BPELEngineFactory.getEngine();
                
                try {
                    java.util.Properties props=new java.util.Properties();

                    // Load default properties
                    try {
                        java.io.InputStream is=BPELEngineImpl.class.getClassLoader().getResourceAsStream("bpel.properties");
                
                        props.load(is);
                    } catch (Exception e) {
                        throw new SwitchYardException("Failed to load default properties: "+ e, e);
                    }

                    if (_configuration != null) {
                        // Overwrite default properties with values from configuration
                        for (Configuration child : _configuration.getChildren()) {
                            if (LOG.isDebugEnabled()) {
                                if (props.containsKey(child.getName())) {
                                    LOG.debug("Overriding BPEL property: "+child.getName()
                                            +" = "+child.getValue());
                                } else {
                                    LOG.debug("Setting BPEL property: "+child.getName()
                                            +" = "+child.getValue());
                                }
                            }
                            props.put(child.getName(), child.getValue());
                        }
                    }
                    
                    _engine.init(locator, props);
                } catch (Exception e) {
                    throw new SwitchYardException("Failed to initialize the engine: "+ e, e);
                }
            }
        }
    }
}
