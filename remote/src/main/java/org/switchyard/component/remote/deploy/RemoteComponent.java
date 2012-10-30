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
package org.switchyard.component.remote.deploy;

import java.util.ServiceLoader;

import org.switchyard.ServiceDomain;
import org.switchyard.component.remote.RemoteEndpointPublisher;
import org.switchyard.deploy.Activator;
import org.switchyard.deploy.BaseComponent;

/**
 * The "remote" component used in SwitchYard for providing and consuming clustered endpoints.
 */
public class RemoteComponent extends BaseComponent {
    
    private static final String CONTEXT_PATH = "switchyard-remote";
    private RemoteEndpointPublisher _endpointPublisher;

    /**
     * Default constructor.
     */
    public RemoteComponent() {
        super(RemoteActivator.TYPES);
        setName("RemoteComponent");
        try {
            _endpointPublisher = ServiceLoader.load(RemoteEndpointPublisher.class).iterator().next();
            _endpointPublisher.init(CONTEXT_PATH);
            _endpointPublisher.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public Activator createActivator(ServiceDomain domain) {
        RemoteActivator activator = new RemoteActivator(getConfig());
        activator.setServiceDomain(domain);
        activator.setEndpointPublisher(_endpointPublisher);
        return activator;
    }

}
