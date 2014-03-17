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
 * ConfigurationExtension.
 */
public class ConfigurationExtension extends SimpleExtension {

    private final Logger _logger = LoggerFactory.getLogger(SwitchYardExtender.class);

    private final SwitchYardExtender _extender;
    private SimpleNamespaceHandler _handler;
    private ServiceRegistration<NamespaceHandler> _registration;

    /**
     * Create a new instance of ConfigurationExtension.
     * @param extender SY extender
     * @param bundle configuration bundle
     */
    public ConfigurationExtension(SwitchYardExtender extender, Bundle bundle) {
        super(bundle);
        _extender = extender;
    }

    @Override
    protected void doStart() throws Exception {
        URL configUrl = getBundle().getEntry(Descriptor.DEFAULT_PROPERTIES);
        Properties properties = new PropertiesPuller().pull(configUrl);
        _handler = new SimpleNamespaceHandler(getBundle(), properties);
        _logger.info("Registering namespace handler for " + _handler.getNamespaces());
        if (_registration == null) {
            Dictionary<String, Object> props = new Hashtable<String, Object>();
            props.put(NamespaceHandler.NAMESPACES, _handler.getNamespaces());
            _registration = getBundleContext().registerService(NamespaceHandler.class, _handler, props);
        }
    }

    @Override
    protected void doDestroy() throws Exception {
        if (_handler != null) {
            _logger.info("Unregistering namespace handler for " + _handler.getNamespaces());
            if (_registration != null) {
                _registration.unregister();
                _registration = null;
            }
        }
    }

}
