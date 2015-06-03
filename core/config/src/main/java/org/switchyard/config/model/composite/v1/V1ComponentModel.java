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

package org.switchyard.config.model.composite.v1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.switchyard.common.property.CompoundPropertyResolver;
import org.switchyard.common.property.SystemAndTestPropertyResolver;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseNamedModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.ComponentImplementationModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.SCANamespace;
import org.switchyard.config.model.property.PropertyModel;

/**
 * A version 1 ComponentModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1ComponentModel extends BaseNamedModel implements ComponentModel {

    private ComponentImplementationModel _implementation;
    private List<ComponentServiceModel> _services = new ArrayList<ComponentServiceModel>();
    private List<ComponentReferenceModel> _references = new ArrayList<ComponentReferenceModel>();
    private Map<String, PropertyModel> _properties = new HashMap<String, PropertyModel>();

    /**
     * Constructs a new V1ComponentModel.
     */
    public V1ComponentModel() {
        super(SCANamespace.DEFAULT.uri(), ComponentModel.COMPONENT);
        setModelChildrenOrder(ComponentImplementationModel.IMPLEMENTATION + ".*", ComponentServiceModel.SERVICE, ComponentReferenceModel.REFERENCE, PropertyModel.PROPERTY);
        setComponentPropertyResolver();
    }

    /**
     * Constructs a new V1ComponentModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1ComponentModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration service_config : config.getChildren(ComponentServiceModel.SERVICE)) {
            ComponentServiceModel service = (ComponentServiceModel)readModel(service_config);
            if (service != null) {
                _services.add(service);
            }
        }
        for (Configuration reference_config : config.getChildren(ComponentReferenceModel.REFERENCE)) {
            ComponentReferenceModel reference = (ComponentReferenceModel)readModel(reference_config);
            if (reference != null) {
                _references.add(reference);
            }
        }
        for (Configuration property_config : config.getChildren(PropertyModel.PROPERTY)) {
            PropertyModel property = (PropertyModel)readModel(property_config);
            if (property != null) {
                _properties.put(property.getName(), property);
            }
        }

        setModelChildrenOrder(ComponentImplementationModel.IMPLEMENTATION + ".*", ComponentServiceModel.SERVICE, ComponentReferenceModel.REFERENCE, PropertyModel.PROPERTY);
        setComponentPropertyResolver();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompositeModel getComposite() {
        return (CompositeModel)getModelParent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ComponentImplementationModel getImplementation() {
        if (_implementation == null) {
            _implementation = (ComponentImplementationModel)getFirstChildModelStartsWith(ComponentImplementationModel.IMPLEMENTATION);
        }
        return _implementation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ComponentModel setImplementation(ComponentImplementationModel implementation) {
        setChildModel(implementation);
        _implementation = implementation;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<ComponentServiceModel> getServices() {
        return Collections.unmodifiableList(_services);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized ComponentModel addService(ComponentServiceModel service) {
        addChildModel(service);
        _services.add(service);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<ComponentReferenceModel> getReferences() {
        return Collections.unmodifiableList(_references);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized ComponentModel addReference(ComponentReferenceModel reference) {
        addChildModel(reference);
        _references.add(reference);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized PropertyModel getProperty(String name) {
        return _properties.get(name);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized Map<String, PropertyModel> getProperties() {
        return Collections.unmodifiableMap(_properties);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized V1ComponentModel addProperty(PropertyModel property) {
        addChildModel(property);
        _properties.put(property.getName(), property);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object resolveProperty(String key) {
        Object value = null;
        if (key != null) {
            PropertyModel property = getProperty(key);
            value = property != null ? property.getValue() : null;
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setComponentPropertyResolver() {
        Configuration config = getModelConfiguration();
        Configuration parent = config.getParent();
        if (parent != null) {
            config.setPropertyResolver(CompoundPropertyResolver.compact(parent.getPropertyResolver(), this));
        } else {
            config.setPropertyResolver(CompoundPropertyResolver.compact(SystemAndTestPropertyResolver.INSTANCE, this));
        }
    }
}
