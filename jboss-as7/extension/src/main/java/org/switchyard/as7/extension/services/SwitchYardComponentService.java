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
package org.switchyard.as7.extension.services;

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
import org.switchyard.as7.extension.CommonAttributes;
import org.switchyard.config.Configuration;
import org.switchyard.config.ConfigurationPuller;
import org.switchyard.config.Configurations;
import org.switchyard.deploy.Component;

/**
 * The SwitchYard Component service.
 * 
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class SwitchYardComponentService implements Service<Component> {

    private static final Logger LOG = Logger.getLogger("org.switchyard");

    /**
     * Represents a SwitchYard Component initializer service name.
     */
    public static final ServiceName SERVICE_NAME = ServiceName.of("SwitchYardComponentService");

    private final InjectedValue<Map> _injectedValues = new InjectedValue<Map>();
    private final InjectedValue<ResourceAdapterRepository> _resourceAdapterRepository = new InjectedValue<ResourceAdapterRepository>();

    private String _moduleId;
    private ModelNode _model;
    private Component _component;

    /**
     * Constructs a SwitchYard Component service.
     * 
     * @param moduleId the module identifier
     * @param model the Module's model operation
     */
    public SwitchYardComponentService(String moduleId, ModelNode model) {
        _moduleId = moduleId;
        _model = model;
    }

    @Override
    public Component getValue() throws IllegalStateException,
            IllegalArgumentException {
        return _component;
    }

    @Override
    public void start(StartContext context) throws StartException {
        Class<?> componentClass;
        String className = _model.get(CommonAttributes.IMPLCLASS).asString();
        try {
            componentClass = Module.loadClassFromCallerModuleLoader(ModuleIdentifier.fromString(_moduleId), className);
            try {
                _component = (Component) componentClass.newInstance();
                ModelNode properties = _model.hasDefined(CommonAttributes.PROPERTIES) ? _model.get(CommonAttributes.PROPERTIES) : null;
                _component.init(createEnvironmentConfig(properties));
                LOG.debug("Initialized component " + _component);
                _component.addResourceDependency(_resourceAdapterRepository.getValue());
            } catch (InstantiationException ie) {
                LOG.error("Unable to instantiate class " + className, ie);
            } catch (IllegalAccessException iae) {
                LOG.error("Unable to access constructor for " + className, iae);
            }
        } catch (ClassNotFoundException cnfe) {
            LOG.error("Unable to load class " + className, cnfe);
        } catch (ModuleLoadException mle) {
            LOG.error("Unable to load module " + _moduleId, mle);
        }
    }

    private Configuration createEnvironmentConfig(ModelNode properties) {
        Configuration envConfig = Configurations.newConfiguration();
        if (properties != null) {
            Set<String> propertyNames = properties.keys();
            if (propertyNames != null) {
                for (String propertyName : propertyNames) {
                    Configuration propConfig = new ConfigurationPuller().pull(new QName(propertyName));
                    String value = properties.get(propertyName).asString();
                    if (value.startsWith(CommonAttributes.DOLLAR)) {
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
        LOG.info("Stopping SwitchYard component " + _component.getName());
        try {
            _component.destroy();
        }  catch (Exception e) {
            LOG.error("Unable to stop " + _component.getName(), e);
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
