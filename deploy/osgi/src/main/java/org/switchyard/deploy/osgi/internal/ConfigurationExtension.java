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

package org.switchyard.deploy.osgi.internal;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.switchyard.common.io.pull.PropertiesPuller;
import org.switchyard.config.model.Descriptor;
import org.switchyard.deploy.osgi.NamespaceHandler;
import org.switchyard.deploy.osgi.base.SimpleExtension;

import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

/**
 */
public class ConfigurationExtension extends SimpleExtension {

    private final Logger logger = LoggerFactory.getLogger(SwitchyardExtender.class);

    private final SwitchyardExtender extender;
    private SimpleNamespaceHandler handler;
    private ServiceRegistration<NamespaceHandler> registration;

    public ConfigurationExtension(SwitchyardExtender extender, Bundle bundle) {
        super(bundle);
        this.extender = extender;
    }

    @Override
    protected void doStart() throws Exception {
        URL configUrl = getBundle().getEntry(Descriptor.DEFAULT_PROPERTIES);
        Properties properties = new PropertiesPuller().pull(configUrl);
        handler = new SimpleNamespaceHandler(getBundle(), properties);
        logger.info("Registering namespace handler for " + handler.getNamespaces());
        if (registration == null) {
            Dictionary<String, Object> props = new Hashtable<String, Object>();
            props.put(NamespaceHandler.NAMESPACES, handler.getNamespaces());
            registration = getBundleContext().registerService(NamespaceHandler.class, handler, props);
        }
    }

    @Override
    protected void doDestroy() throws Exception {
        if (handler != null) {
            logger.info("Unregistering namespace handler for " + handler.getNamespaces());
            if (registration != null) {
                registration.unregister();
                registration = null;
            }
        }
    }

}
