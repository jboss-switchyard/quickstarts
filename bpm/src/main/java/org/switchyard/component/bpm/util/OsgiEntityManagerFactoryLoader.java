/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.component.bpm.util;

import java.util.Collection;

import javax.persistence.EntityManagerFactory;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.switchyard.ServiceDomain;

/**
 * Retrieves the EMF from the bundle context.
 */
public class OsgiEntityManagerFactoryLoader extends EntityManagerFactoryLoader {
    @Override
    public EntityManagerFactory getEntityManagerFactory(final ServiceDomain domain) {
        try {
            final Bundle bundle = getApplicationBundle(domain);
            if (bundle != null) {
                final Collection<ServiceReference<EntityManagerFactory>> refs = bundle.getBundleContext()
                        .getServiceReferences(EntityManagerFactory.class, "(osgi.unit.name=org.jbpm.persistence.jpa)");
                if (refs != null && refs.size() > 0) {
                    final EntityManagerFactory emf = bundle.getBundleContext().getService(refs.iterator().next());
                    if (emf != null) {
                        return emf;
                    }
                }
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        // try the default
        return super.getEntityManagerFactory(domain);
    }

    private Bundle getApplicationBundle(final ServiceDomain domain) {
        return (Bundle) domain.getProperty("switchyard.deployment.bundle");
    }
}
