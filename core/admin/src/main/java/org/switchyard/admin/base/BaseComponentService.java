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
package org.switchyard.admin.base;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.admin.Application;
import org.switchyard.admin.ComponentReference;
import org.switchyard.admin.ComponentService;
import org.switchyard.config.model.composite.ComponentImplementationModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.config.model.composite.InterfaceModel;

/**
 * BaseComponentService
 * 
 * Base implementation for {@link ComponentService}.
 * 
 * @author Rob Cernich
 */
public abstract class BaseComponentService extends BaseMessageMetricsAware implements ComponentService {

    private final QName _name;
    private final String _implementation;
    private final String _interface;
    private final Application _application;
    private List<ComponentReference> _references;
    private final String _implementationConfiguration;

    /**
     * Create a new BaseComponentService from a config model.
     * 
     * @param serviceConfig the component service configuration
     * @param componentConfig the component configuration
     * @param application the switchyard application
     */
    public BaseComponentService(ComponentServiceModel serviceConfig, ComponentModel componentConfig, Application application) {
        _name = serviceConfig.getQName();
        _implementation = getComponentImplementationType(componentConfig);
        _interface = getInterfaceName(serviceConfig.getInterface());
        _application = application;
        _references = new LinkedList<ComponentReference>();
        _implementationConfiguration = getComponentImplementationConfiguration(componentConfig);

        for (ComponentReferenceModel referenceModel : componentConfig.getReferences()) {
            _references.add(new BaseComponentReference(referenceModel.getQName(), 
                    getInterfaceName(referenceModel.getInterface())));
        }
    }

    @Override
    public QName getName() {
        return _name;
    }

    @Override
    public String getImplementation() {
        return _implementation;
    }

    @Override
    public String getInterface() {
        return _interface;
    }

    @Override
    public List<ComponentReference> getReferences() {
        if (_references == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(_references);
    }

    @Override
    public Application getApplication() {
        return _application;
    }

    @Override
    public String getImplementationConfiguration() {
        return _implementationConfiguration;
    }

    @Override
    public void resetMessageMetrics() {
        for (final ComponentReference reference : _references) {
            reference.resetMessageMetrics();
        }
        super.resetMessageMetrics();
    }

    private String getInterfaceName(InterfaceModel interfaceModel) {
        if (interfaceModel == null) {
            return null;
        }
        return interfaceModel.getInterface();
    }
    
    private String getComponentImplementationType(ComponentModel componentModel) {
        ComponentImplementationModel implementationModel = componentModel.getImplementation();
        if (implementationModel == null) {
            return null;
        }
        return implementationModel.getType();
    }

    private String getComponentImplementationConfiguration(ComponentModel componentModel) {
        return componentModel.getImplementation() != null 
                ? componentModel.getImplementation().toString() : null;
    }

}
