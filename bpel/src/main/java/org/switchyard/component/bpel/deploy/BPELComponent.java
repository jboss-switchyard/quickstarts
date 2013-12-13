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

import org.jboss.logging.Logger;
import org.apache.ode.bpel.evt.BpelEvent;
import org.riftsaw.engine.BPELEngine;
import org.riftsaw.engine.BPELEngineFactory;
import org.riftsaw.engine.BPELEngineListener;
import org.riftsaw.engine.internal.BPELEngineImpl;
import org.switchyard.ServiceDomain;
import org.switchyard.component.bpel.riftsaw.RiftsawServiceLocator;
import org.switchyard.config.Configuration;
import org.switchyard.component.bpel.BPELLogger;
import org.switchyard.component.bpel.BPELMessages;
import org.switchyard.deploy.Activator;
import org.switchyard.deploy.BaseComponent;

/**
 * An implementation of BPEL component.
 */
public class BPELComponent extends BaseComponent {

    private static final Logger LOG = Logger.getLogger(BPELComponent.class);
    
    private static BPELEngine _engine = null;
    private static java.util.Properties _config=null;
    private static RiftsawServiceLocator _locator=new RiftsawServiceLocator();
    private static boolean _initialized=false;
    
    /**
     * Default constructor.
     */
    public BPELComponent() {
        super(BPELActivator.BPEL_TYPE);
        setName("BPELComponent");
    }

    /* (non-Javadoc)
     * @see org.switchyard.deploy.Component#init(org.switchyard.config.Configuration)
     */
    @Override
    public void init(Configuration config) {
        super.init(config);
        
        initConfig();
    }
    
    /**
     * This method initializes the configuration information for use
     * by the BPEL engine.
     */
    protected synchronized void initConfig() {
        if (_initialized) {
            return;
        }
        
        BPELLogger.ROOT_LOGGER.initBPELComponent();
        
        ClassLoader current=Thread.currentThread().getContextClassLoader();
        
        Thread.currentThread().setContextClassLoader(BPELComponent.class.getClassLoader());

        try {
            // Initialize the BPEL engine service locator
            BPELEngineFactory.setServiceLocator(_locator);
            
            // Initialize the BPEL engine configuration
            _config = new java.util.Properties();

            // Load default properties
            try {
                java.io.InputStream is=BPELEngineImpl.class.getClassLoader().getResourceAsStream("bpel.properties");
        
                _config.load(is);
            } catch (Exception e) {
                throw BPELMessages.MESSAGES.failedToLoadDefaultProperties(e);
            }

            if (getConfig() != null) {
                // Overwrite default properties with values from configuration
                for (Configuration child : getConfig().getChildren()) {
                    if (LOG.isDebugEnabled()) {
                        if (_config.containsKey(child.getName())) {
                            LOG.debug("Overriding BPEL property: "+child.getName()
                                    +" = "+child.getValue());
                        } else {
                            LOG.debug("Setting BPEL property: "+child.getName()
                                    +" = "+child.getValue());
                        }
                    }
                    _config.put(child.getName(), child.getValue());
                }
            }
            
            BPELEngineFactory.setConfig(_config);
            
        } finally {
            Thread.currentThread().setContextClassLoader(current);
        }
        
        _initialized = true;
    }
    
    protected BPELEngine getEngine(final ServiceDomain domain) {
        
        synchronized (this) {
            if (_engine == null) {
                initConfig();
                
                try {
                    _engine = BPELEngineFactory.getEngine();
                    
                    _engine.register(new BPELEngineListener() {
                        public void onEvent(BpelEvent bpelEvent) {
                            domain.getEventPublisher().publish(bpelEvent);
                        }
                    });
                    
                } catch (Exception e) {
                    throw BPELMessages.MESSAGES.failedToInitializeTheEngine(e);
                }
            }
        }
        
        return (_engine);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        super.destroy();

        BPELLogger.ROOT_LOGGER.destroyBPELComponent();
        if (_engine != null) {
            try {
                synchronized (_engine) {
                   _engine.close();
                    _engine = null;
                }
            } catch (Exception e) {
                BPELLogger.ROOT_LOGGER.failedToCloseBPELEngine(e);
            }
        }
    }
    
    /* (non-Javadoc)
     * @see org.switchyard.deploy.Component#createActivator(org.switchyard.ServiceDomain)
     */
    @Override
    public Activator createActivator(ServiceDomain domain) {
        BPELEngine engine=null;
        
        if (domain != null) {
            engine = getEngine(domain);
        }
        
        BPELActivator activator=new BPELActivator(engine, _locator,
                                    _config);
        
        activator.setServiceDomain(domain);
        
        return activator;
    }
    
}
