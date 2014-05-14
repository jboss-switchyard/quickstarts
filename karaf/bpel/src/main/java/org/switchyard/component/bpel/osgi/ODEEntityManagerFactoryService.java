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
package org.switchyard.component.bpel.osgi;

import java.util.Collection;

import javax.persistence.EntityManagerFactory;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * Provides access to the ode.emf registered by the BPEL engine.
 */
public class ODEEntityManagerFactoryService {

    private ServiceReference<EntityManagerFactory> _reference;
    private EntityManagerFactory _service;

    /**
     * Create a new ODEEntityManagerFactoryService.
     */
    public ODEEntityManagerFactoryService() {
    }

    /**
     * @return the ODE EMF
     * @throws Exception if something goes awry
     */
    public synchronized EntityManagerFactory getOdeEmf() throws Exception {
        if (_service == null) {
            final Bundle bundle = FrameworkUtil.getBundle(getClass());
            final BundleContext context = bundle.getBundleContext();
            final Collection<ServiceReference<EntityManagerFactory>> references = context.getServiceReferences(
                    EntityManagerFactory.class, "(ode.emf=*)");
            if (references.size() == 0) {
                throw new RuntimeException("Could not locate ode.emf.");
            }
            // if (references.size() > 1) {
            // TODO: warn???
            // }
            _reference = references.iterator().next();
            _service = context.getService(_reference);
        }
        return _service;
    }

    /**
     * Cleaup after ourselves.
     */
    public synchronized void dispose() {
        if (_reference != null) {
            final Bundle bundle = FrameworkUtil.getBundle(getClass());
            final BundleContext context = bundle.getBundleContext();
            context.ungetService(_reference);
            _reference = null;
            _service = null;
        }
    }
}
