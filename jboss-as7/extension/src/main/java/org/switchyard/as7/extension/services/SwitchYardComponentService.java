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
package org.switchyard.as7.extension.services;

import static org.switchyard.as7.extension.CommonAttributes.DOLLAR;
import static org.switchyard.as7.extension.CommonAttributes.IMPLCLASS;
import static org.switchyard.as7.extension.CommonAttributes.MODULES;
import static org.switchyard.as7.extension.CommonAttributes.PROPERTIES;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.jboss.dmr.ModelNode;
import org.jboss.jca.core.spi.rar.ResourceAdapterRepository;
import org.jboss.logging.Logger;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoadException;
import org.jboss.msc.inject.Injector;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;
import org.switchyard.config.Configuration;
import org.switchyard.config.ConfigurationPuller;
import org.switchyard.config.Configurations;
import org.switchyard.deploy.Component;

/**
 * The SwitchYard Component initializer service.
 * 
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class SwitchYardComponentService implements Service<List<Component>> {

    private static final Logger LOG = Logger.getLogger("org.switchyard");

    /**
     * Represents a SwitchYard Component initializer service name.
     */
    public static final ServiceName SERVICE_NAME = ServiceName.of("SwitchYardComponentService");

    private final InjectedValue<Map> _injectedValues = new InjectedValue<Map>();
    private final InjectedValue<ResourceAdapterRepository> _resourceAdapterRepository = new InjectedValue<ResourceAdapterRepository>();
    
    private ModelNode _operation;
    private List<Component> _components = new ArrayList<Component>();

    /**
     * Constructs a SwitchYard Component initializer service.
     * 
     * @param operation the Subsystem Add operation
     */
    public SwitchYardComponentService(ModelNode operation) {
        _operation = operation;
    }

    @Override
    public List<Component> getValue() throws IllegalStateException,
            IllegalArgumentException {
        return _components;
    }

    @Override
    public void start(StartContext context) throws StartException {
        if (_operation.has(MODULES)) {
            ModelNode opmodules = _operation.get(MODULES);
            Set<String> keys = opmodules.keys();
            if (keys != null) {
                for (String current : keys) {
                    ModuleIdentifier moduleIdentifier = ModuleIdentifier.fromString(current);
                    Class<?> componentClass;
                    String className = opmodules.get(current).get(IMPLCLASS).asString();
                    try {
                        componentClass = Module.loadClassFromCallerModuleLoader(moduleIdentifier, className);
                        Component component;
                        try {
                            component = (Component) componentClass.newInstance();
                            ModelNode opmodule = opmodules.get(current);
                            ModelNode properties = opmodule.has(PROPERTIES) ? opmodule.get(PROPERTIES) : null;
                            component.init(createEnvironmentConfig(properties));
                            LOG.debug("Initialized component " + component);
                            component.addResourceDependency(_resourceAdapterRepository.getValue());
                            _components.add(component);
                        } catch (InstantiationException ie) {
                            LOG.error("Unable to instantiate class " + className);
                        } catch (IllegalAccessException iae) {
                            LOG.error("Unable to access constructor for " + className);
                        }
                    } catch (ClassNotFoundException cnfe) {
                        LOG.error("Unable to load class " + className);
                    } catch (ModuleLoadException mle) {
                        LOG.error("Unable to load module " + moduleIdentifier);
                    }
                }
            }
        }
    }

    private Configuration createEnvironmentConfig(ModelNode properties) {
        Configuration envConfig = Configurations.emptyConfig();
        if (properties != null) {
            Set<String> propertyNames = properties.keys();
            if (propertyNames != null) {
                for (String propertyName : propertyNames) {
                    Configuration propConfig = new ConfigurationPuller().pull(new QName(propertyName));
                    String value = properties.get(propertyName).asString();
                    if (value.startsWith(DOLLAR)) {
                        String key = value.substring(1);
                        String injectedValue = (String) _injectedValues.getValue().get(key);
                        if (injectedValue != null) {
                            propConfig.setValue(injectedValue);
                            envConfig.addChild(propConfig);
                        }
                    } else {
                        propConfig.setValue(value);
                        envConfig.addChild(propConfig);
                    }
                }
            }
        }
        return envConfig;
    }

    @Override
    public void stop(StopContext context) {
        for (Component component : _components) {
            LOG.info("Stopping SwitchYard component " + component.getName());
            try {
                component.destroy();
            }  catch (Exception e) {
                LOG.error("Unable to stop " + component.getName(), e);
            }
        }
    }

    /**
     * Injection point for injectValues.
     * 
     * @return a map of injected values
     */
    public InjectedValue<Map> getInjectedValues() {
        return _injectedValues;
    }

    /**
     * Injection point for ResourceAdapterRepository.
     * @return ResourceAdapterRepository
     */
    public Injector<ResourceAdapterRepository> getResourceAdapterRepository() {
        return _resourceAdapterRepository;
    }

}
