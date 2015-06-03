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

import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.riftsaw.engine.BPELEngine;
import org.riftsaw.engine.ServiceLocator;
import org.switchyard.component.bpel.deploy.BPELComponent;

/**
 * BPELOSGiComponent
 * <p/>
 * Replaces BPELEngine creation with OSGi service lookup.
 */
public class BPELOSGiComponent extends BPELComponent {

    /**
     * Create a new BPELOSGiComponent.
     */
    public BPELOSGiComponent() {
        super(new BPELEngineInstanceImpl());
    }

    private static final class BPELEngineInstanceImpl implements BPELEngineInstance {
        private BPELEngine _engine;
        private ServiceReference<BPELEngine> _serviceReference;

        @Override
        public void init(ServiceLocator serviceLocator, Properties config) {
        }

        @Override
        public synchronized BPELEngine getBPELEngine() throws Exception {
            if (_engine == null) {
                Bundle thisBundle = FrameworkUtil.getBundle(getClass());
                BundleContext context = thisBundle.getBundleContext();
                _serviceReference = context.getServiceReference(BPELEngine.class);
                _engine = context.getService(_serviceReference);
            }
            return _engine;
        }

        @Override
        public void dispose() throws Exception {
            if (_engine != null) {
                Bundle thisBundle = FrameworkUtil.getBundle(getClass());
                BundleContext context = thisBundle.getBundleContext();
                context.ungetService(_serviceReference);
                _serviceReference = null;
                _engine = null;
            }
        }
    }
}
