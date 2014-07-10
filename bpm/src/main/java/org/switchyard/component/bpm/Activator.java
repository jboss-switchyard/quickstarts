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
package org.switchyard.component.bpm;

import java.util.Hashtable;
import java.util.concurrent.Callable;

import org.jbpm.persistence.correlation.JPACorrelationKeyFactory;
import org.kie.internal.process.CorrelationKeyFactory;
import org.kie.internal.utils.ServiceRegistryImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * Bundle activator for BPM component. We need to register some KIE services in
 * OSGi environments.
 */
public class Activator implements BundleActivator {

    private ServiceRegistration<CorrelationKeyFactory> _correlationKeyFactoryService;

    @Override
    public void start(final BundleContext context) throws Exception {
        _correlationKeyFactoryService = context.registerService(CorrelationKeyFactory.class,
                new JPACorrelationKeyFactory(), new Hashtable<String, Object>());
        ServiceRegistryImpl.getInstance().registerLocator(CorrelationKeyFactory.class, new Callable<CorrelationKeyFactory>() {
            @Override
            public CorrelationKeyFactory call() throws Exception {
                return context.getService(_correlationKeyFactoryService.getReference());
            }
        });
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        ServiceRegistryImpl.getInstance().unregisterLocator(CorrelationKeyFactory.class);
        _correlationKeyFactoryService.unregister();
    }

}
