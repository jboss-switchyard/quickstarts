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
package org.switchyard.deploy.osgi.internal;

import org.apache.camel.TypeConverter;
import org.apache.camel.core.osgi.OsgiCamelContextHelper;
import org.apache.camel.core.osgi.OsgiFactoryFinderResolver;
import org.apache.camel.core.osgi.OsgiTypeConverter;
import org.apache.camel.core.osgi.utils.BundleContextUtils;
import org.apache.camel.impl.CompositeRegistry;
import org.apache.camel.spi.FactoryFinder;
import org.apache.camel.spi.Registry;
import org.osgi.framework.BundleContext;
import org.switchyard.common.camel.SwitchYardCamelContextImpl;

/**
 */
public class OsgiSwitchYardCamelContextImpl extends SwitchYardCamelContextImpl {

    private final BundleContext bundleContext;

    public OsgiSwitchYardCamelContextImpl(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        OsgiCamelContextHelper.osgiUpdate(this, bundleContext);
    }

    @Override
    protected CompositeRegistry createRegistry() {
        return (CompositeRegistry) OsgiCamelContextHelper.wrapRegistry(this, super.createRegistry(), bundleContext);
    }

    @Override
    protected TypeConverter createTypeConverter() {
        // CAMEL-3614: make sure we use a bundle context which imports org.apache.camel.impl.converter package
        BundleContext ctx = BundleContextUtils.getBundleContext(getClass());
        if (ctx == null) {
            ctx = bundleContext;
        }
        FactoryFinder finder = new OsgiFactoryFinderResolver(bundleContext).resolveDefaultFactoryFinder(getClassResolver());
        return new OsgiTypeConverter(ctx, getInjector(), finder);
    }

}
