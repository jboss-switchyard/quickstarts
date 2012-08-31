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
package org.switchyard.component.jca.deploy;

import org.switchyard.ServiceDomain;
import org.switchyard.component.jca.JCAConstants;
import org.switchyard.deploy.Activator;
import org.switchyard.deploy.BaseComponent;
import org.jboss.jca.core.spi.rar.ResourceAdapterRepository;

/**
 * An implementation of JCA component.
 *
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public class JCAComponent extends BaseComponent {

    private ResourceAdapterRepository _raRepository = null;
    
    /**
     * Default constructor.
     */
    public JCAComponent() {
        super(JCAActivator.TYPES);
        setName(JCAConstants.COMPONENT_NAME);
    }

    /* (non-Javadoc)
     * @see org.switchyard.deploy.Component#createActivator(org.switchyard.ServiceDomain)
     */
    @Override
    public Activator createActivator(ServiceDomain domain) {
        JCAActivator activator = new JCAActivator();
        activator.setServiceDomain(domain);
        activator.setResourceAdapterRepository(_raRepository);
        return activator;
    }
    
    @Override
    public void addResourceDependency(Object repository) {
        if (repository instanceof ResourceAdapterRepository) {
            _raRepository = (ResourceAdapterRepository)repository;
        }
    }
}
