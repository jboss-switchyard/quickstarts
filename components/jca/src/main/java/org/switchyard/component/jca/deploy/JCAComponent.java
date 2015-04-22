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
