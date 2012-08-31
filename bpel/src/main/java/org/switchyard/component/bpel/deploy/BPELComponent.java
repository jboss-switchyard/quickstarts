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

import org.apache.log4j.Logger;
import org.riftsaw.engine.BPELEngine;
import org.riftsaw.engine.BPELEngineFactory;
import org.riftsaw.engine.internal.BPELEngineImpl;
import org.switchyard.ServiceDomain;
import org.switchyard.component.bpel.riftsaw.RiftsawServiceLocator;
import org.switchyard.config.Configuration;
import org.switchyard.deploy.Activator;
import org.switchyard.deploy.BaseComponent;
import org.switchyard.exception.SwitchYardException;

/**
 * An implementation of BPEL component.
 */
public class BPELComponent extends BaseComponent {

    private static final Logger LOG = Logger.getLogger(BPELComponent.class);
    
    private static BPELEngine _engine=null;
    private static java.util.Properties _config=null;
    private static RiftsawServiceLocator _locator=new RiftsawServiceLocator();
    
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
    }
    
    protected BPELEngine getEngine() {
        
        synchronized (this) {
            if (_engine == null) {
                LOG.info("Init BPEL component");
                
                ClassLoader current=Thread.currentThread().getContextClassLoader();
                
                Thread.currentThread().setContextClassLoader(BPELComponent.class.getClassLoader());

                try {
                    _engine = BPELEngineFactory.getEngine();
                    
                    _config = new java.util.Properties();
        
                    // Load default properties
                    try {
                        java.io.InputStream is=BPELEngineImpl.class.getClassLoader().getResourceAsStream("bpel.properties");
                
                        _config.load(is);
                    } catch (Exception e) {
                        throw new SwitchYardException("Failed to load default properties: "+ e, e);
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
                    
                    _engine.init(_locator, _config);
                } catch (Exception e) {
                    throw new SwitchYardException("Failed to initialize the engine: "+ e, e);
                } finally {
                    Thread.currentThread().setContextClassLoader(current);
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

        LOG.info("Destroy BPEL component");
        
        if (_engine != null) {
            try {
                _engine.close();
                _engine = null;
            } catch (Exception e) {
                LOG.error("Failed to close BPEL engine", e);
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
            engine = getEngine();
        }
        
        BPELActivator activator=new BPELActivator(engine, _locator,
                                    _config);
        
        activator.setServiceDomain(domain);
        
        return activator;
    }
    
}
