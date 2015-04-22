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
package org.switchyard.deploy.osgi.internal;

import org.apache.camel.TypeConverter;
import org.apache.camel.core.osgi.OsgiCamelContextHelper;
import org.apache.camel.core.osgi.OsgiFactoryFinderResolver;
import org.apache.camel.core.osgi.OsgiTypeConverter;
import org.apache.camel.core.osgi.utils.BundleContextUtils;
import org.apache.camel.impl.CompositeRegistry;
import org.apache.camel.spi.FactoryFinder;
import org.osgi.framework.BundleContext;
import org.switchyard.common.camel.SwitchYardCamelContextImpl;

/**
 * OsgiSwitchYardCamelContextImpl.
 */
public class OsgiSwitchYardCamelContextImpl extends SwitchYardCamelContextImpl {

    private final BundleContext _bundleContext;

    /**
     * Create a new instance of OsgiSwitchYardCamelContextImpl.
     * @param bundleContext bundleContext
     */
    public OsgiSwitchYardCamelContextImpl(BundleContext bundleContext) {
        _bundleContext = bundleContext;
        OsgiCamelContextHelper.osgiUpdate(this, bundleContext);
    }

    @Override
    protected CompositeRegistry createRegistry() {
        return (CompositeRegistry) OsgiCamelContextHelper.wrapRegistry(this, super.createRegistry(), _bundleContext);
    }

    @Override
    protected TypeConverter createTypeConverter() {
        // CAMEL-3614: make sure we use a bundle context which imports org.apache.camel.impl.converter package
        BundleContext ctx = BundleContextUtils.getBundleContext(getClass());
        if (ctx == null) {
            ctx = _bundleContext;
        }
        FactoryFinder finder = new OsgiFactoryFinderResolver(_bundleContext).resolveDefaultFactoryFinder(getClassResolver());
        return new OsgiTypeConverter(ctx, getInjector(), finder);
    }

}
