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
import org.riftsaw.engine.ServiceLocator;
import org.riftsaw.engine.internal.BPELEngineImpl;
import org.switchyard.ExchangeHandler;
import org.switchyard.ServiceReference;
import org.switchyard.component.bpel.config.model.BPELComponentImplementationModel;
import org.switchyard.component.bpel.exchange.BPELExchangeHandler;
import org.switchyard.component.bpel.exchange.BPELExchangeHandlerFactory;
import org.switchyard.component.bpel.riftsaw.RiftsawServiceLocator;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.deploy.BaseActivator;
import org.switchyard.exception.SwitchYardException;

/**
 * Activator for the BPEL component.
 *
 */
public class BPELActivator extends BaseActivator {

    private static final Logger LOG = Logger.getLogger(BPELActivator.class);
    
    private Map<QName, BPELExchangeHandler> _handlers = new HashMap<QName , BPELExchangeHandler>();

    private static BPELEngine _engine=null;
    
    /**
     * Constructs a new Activator of type "bpel".
     */
    public BPELActivator() {
        super("bpel");
    }

    protected void init() {
        synchronized (this) {
            if (_engine == null) {
                _engine = BPELEngineFactory.getEngine();
                
                try {
                    ServiceLocator locator=new RiftsawServiceLocator(getServiceDomain());
                    
                    java.util.Properties props=new java.util.Properties();
        
                    // Temporary approach until can get properties from environment
                    // https://issues.jboss.org/browse/RIFTSAW-432
                    try {
                        java.io.InputStream is=BPELEngineImpl.class.getClassLoader().getResourceAsStream("bpel.properties");
                
                        props.load(is);
                    } catch (Exception e) {
                        throw new SwitchYardException("Failed to load properties: "+ e, e);
                    }
        
                    _engine.init(locator, props);
                } catch (Exception e) {
                    throw new SwitchYardException("Failed to initialize the engine: "+ e, e);
                }
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public ExchangeHandler init(QName qname, Model model) {
        
        init();
        
        if (model instanceof ComponentServiceModel) {
            BPELExchangeHandler handler = BPELExchangeHandlerFactory.instance().newBPELExchangeHandler(getServiceDomain());
            
            BPELComponentImplementationModel bciModel=null;
            
               if (((ComponentServiceModel)model).getComponent().getImplementation() instanceof BPELComponentImplementationModel) {
                bciModel = (BPELComponentImplementationModel)((ComponentServiceModel)model).getComponent().getImplementation();
            } else {
                throw new SwitchYardException("Component is not BPEL");
            }
            
            if (((ComponentServiceModel) model).getInterface() == null) {
                throw new SwitchYardException("Interface not defined for component with BPEL implementation");
            }
            
            handler.init(qname, bciModel,
                    ((ComponentServiceModel) model).getInterface().getInterface(),
                    _engine);
            
            _handlers.put(qname, handler);
            return handler;
        } else if (model instanceof ComponentReferenceModel) {
            ComponentReferenceModel crm=(ComponentReferenceModel)model;
            
            // Initialize the reference, by setting up a mapping from the referenced
            // wsdl to the referenced service
            ((RiftsawServiceLocator)_engine.getServiceLocator()).initialiseReference(crm);
            
            return null;
        }
        
        throw new SwitchYardException("No BPEL component implementations found for service " + qname);
    }
    
    /**
     * {@inheritDoc}
     */
    public void start(ServiceReference serviceRef) {
        BPELExchangeHandler handler = _handlers.get(serviceRef.getName());
        if (handler != null) {
            handler.start(serviceRef);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop(ServiceReference serviceRef) {
        BPELExchangeHandler handler = _handlers.get(serviceRef.getName());
        if (handler != null) {
            handler.stop(serviceRef);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void destroy(ServiceReference serviceRef) {
        BPELExchangeHandler handler = _handlers.get(serviceRef.getName());
        if (handler != null) {
            try {
                handler.destroy(serviceRef);
            } finally {
                _handlers.remove(serviceRef.getName());
            }
        }
        
        // Check if engine should be removed
        synchronized (this) {
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
}
