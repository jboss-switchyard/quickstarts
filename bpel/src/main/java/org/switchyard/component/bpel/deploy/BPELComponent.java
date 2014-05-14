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

import java.util.Properties;

import org.jboss.logging.Logger;
import org.apache.ode.bpel.evt.BpelEvent;
import org.riftsaw.engine.BPELEngine;
import org.riftsaw.engine.BPELEngineListener;
import org.riftsaw.engine.ServiceLocator;
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
    
    private final BPELEngineInstance _bpelEngineInstance;
    private static java.util.Properties _config;
    private static BPELEngine _engine;
    private static boolean _initialized;
    
    /**
     * Default constructor.
     */
    public BPELComponent() {
        this(new BPELEngineInstanceImpl());
    }

    protected BPELComponent(BPELEngineInstance bpelEngineInstance) {
        super(BPELActivator.BPEL_TYPE);
        setName("BPELComponent");
        _bpelEngineInstance = bpelEngineInstance;
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
    protected void initConfig() {
        synchronized (BPELComponent.class) {
            if (_initialized) {
                return;
            }
            
            BPELLogger.ROOT_LOGGER.initBPELComponent();
            
            ClassLoader current=Thread.currentThread().getContextClassLoader();
            
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
    
            try {
                // Initialize the BPEL engine configuration
                _config = new java.util.Properties();
    
                // Load default properties
                try {
                    java.io.InputStream is = getClass().getClassLoader().getResourceAsStream("bpel.properties");
            
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
                _bpelEngineInstance.init(new RiftsawServiceLocator(), _config);
            } finally {
                Thread.currentThread().setContextClassLoader(current);
            }
            
            _initialized = true;
        }
    }
    
    protected BPELEngine getEngine(final ServiceDomain domain) {
        synchronized (BPELComponent.class) {
            if (_engine == null) {
                initConfig();
                try {
                    _engine = _bpelEngineInstance.getBPELEngine();
                } catch (Exception e) {
                    throw BPELMessages.MESSAGES.failedToInitializeTheEngine(e);
                }
                _engine.register(new BPELEngineListener() {
                    public void onEvent(BpelEvent bpelEvent) {
                        domain.getEventPublisher().publish(bpelEvent);
                    }
                });
            }
        }
        return _engine;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        super.destroy();

        BPELLogger.ROOT_LOGGER.destroyBPELComponent();
        synchronized (BPELComponent.class) {
            if (_engine != null) {
                try {
                    synchronized (_engine) {
                        _bpelEngineInstance.dispose();
                        _engine = null;
                    }
                } catch (Exception e) {
                    BPELLogger.ROOT_LOGGER.failedToCloseBPELEngine(e);
                }
            }
        }
    }
    
    /* (non-Javadoc)
     * @see org.switchyard.deploy.Component#createActivator(org.switchyard.ServiceDomain)
     */
    @Override
    public Activator createActivator(ServiceDomain domain) {
        if (domain == null) {
            throw new NullPointerException("domain cannot be null");
        }

        BPELEngine engine = getEngine(domain);
        BPELActivator activator = new BPELActivator(engine, (RiftsawServiceLocator) engine.getServiceLocator(), _config);
        activator.setServiceDomain(domain);

        return activator;
    }

    /**
     * Simple interface for managing the BPELEngine.
     */
    protected static interface BPELEngineInstance {
        
        /**
         * Initialize the factory.
         * 
         * @param serviceLocator the service locator.
         * @param config the configuration.
         */
        public void init(ServiceLocator serviceLocator, java.util.Properties config);

        /**
         * Returns the BPELEngine instance.
         * 
         * @return the BPELEngine.
         * @throws Exception if something goes awry.
         */
        public BPELEngine getBPELEngine() throws Exception;
        
        /**
         * Dispose any resources allocated by the factory.
         * 
         * @throws Exception if something goes awry.
         */
        public void dispose() throws Exception;
    }
    
    private static final class BPELEngineInstanceImpl implements BPELEngineInstance {
        
        private BPELEngine _engine;
        
        @Override
        public synchronized void init(ServiceLocator serviceLocator, Properties config) {
            org.riftsaw.engine.BPELEngineFactory.setConfig(config);
            org.riftsaw.engine.BPELEngineFactory.setServiceLocator(serviceLocator);
        }

        @Override
        public synchronized BPELEngine getBPELEngine() throws Exception {
            if (_engine == null) {
                final ClassLoader origTCCL = Thread.currentThread().getContextClassLoader();
                try {
                    Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                    _engine = org.riftsaw.engine.BPELEngineFactory.getEngine();
                } finally {
                    Thread.currentThread().setContextClassLoader(origTCCL);
                }
            }
            return _engine;
        }
        
        @Override
        public synchronized void dispose() throws Exception {
            if (_engine != null) {
                _engine.close();
                _engine = null;
            }
        }
    }
}
