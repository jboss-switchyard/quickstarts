/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.sca.deploy;

import java.util.ServiceLoader;

import org.apache.log4j.Logger;
import org.switchyard.ServiceDomain;
import org.switchyard.component.sca.RemoteEndpointPublisher;
import org.switchyard.deploy.Activator;
import org.switchyard.deploy.BaseComponent;

/**
 * The "remote" component used in SwitchYard for providing and consuming clustered endpoints.
 */
public class SCAComponent extends BaseComponent {
    
    private static final String CONTEXT_PATH = "switchyard-remote";
    private static Logger _log = Logger.getLogger(SCAComponent.class);
    private RemoteEndpointPublisher _endpointPublisher;

    /**
     * Default constructor.
     */
    public SCAComponent() {
        super(SCAActivator.TYPES);
        setName("RemoteComponent");
        try {
            _endpointPublisher = ServiceLoader.load(RemoteEndpointPublisher.class).iterator().next();
            _endpointPublisher.init(CONTEXT_PATH);
        } catch (Exception ex) {
            _log.warn("Failed to initialize remote endpoint publisher", ex);
        }
    }
    
    @Override
    public Activator createActivator(ServiceDomain domain) {
        SCAActivator activator = new SCAActivator(getConfig());
        activator.setServiceDomain(domain);
        activator.setEndpointPublisher(_endpointPublisher);
        return activator;
    }

    @Override
    public synchronized void destroy() {
        try {
            _endpointPublisher.stop();
        } catch (Exception ex) {
            _log.warn("Failed to destroy remote endpoint publisher", ex);
        }
    }
}
