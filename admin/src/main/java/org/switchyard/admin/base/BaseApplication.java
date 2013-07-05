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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.switchyard.admin.Application;
import org.switchyard.admin.ComponentService;
import org.switchyard.admin.Reference;
import org.switchyard.admin.Service;
import org.switchyard.admin.Transformer;
import org.switchyard.admin.Validator;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.config.model.composite.InterfaceModel;
import org.switchyard.config.model.property.PropertyModel;
import org.switchyard.config.model.switchyard.EsbInterfaceModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.config.model.validate.ValidateModel;
import org.switchyard.deploy.internal.AbstractDeployment;

/**
 * Base implementation of Application.
 */
public class BaseApplication implements Application {
    
    private final QName _name;
    private Map<QName, Service> _services;
    private Map<QName, Reference> _references;
    private Map<QName, ComponentService> _componentServices;
    private List<Transformer> _transformers;
    private List<Validator> _validators;
    private AbstractDeployment _deployment;
    private Map<String, String> _properties;
    
    /**
     * Create a new BaseApplication.
     * @param deployment the deployment representing this application
     */
    public BaseApplication(AbstractDeployment deployment) {
        _name = deployment.getName();
        _deployment = deployment;
        
        addTransformers();
        addValidators();
        addComponents();
        addServices();
        addReferences();
        addProperties();
    }

    @Override
    public QName getName() {
        return _name;
    }

    @Override
    public List<Service> getServices() {
        if (_services == null) {
            return Collections.emptyList();
        }
        return new ArrayList<Service>(_services.values());
    }
    
    @Override
    public List<Reference> getReferences() {
        if (_references == null) {
            return Collections.emptyList();
        }
        return new ArrayList<Reference>(_references.values());
    }
    
    @Override
    public Service getService(QName serviceName) {
        if (_services == null) {
            return null;
        }
        return _services.get(serviceName);
    }
    
    @Override
    public Reference getReference(QName referenceName) {
        if (_references == null) {
            return null;
        }
        return _references.get(referenceName);
    }
    
    @Override
    public SwitchYardModel getConfig() {
        return _deployment.getConfig();
    }

    @Override
    public List<ComponentService> getComponentServices() {
        if (_componentServices == null) {
            return Collections.emptyList();
        }
        return new ArrayList<ComponentService>(_componentServices.values());
    }

    @Override
    public ComponentService getComponentService(QName componentServiceName) {
        if (_componentServices == null) {
            return null;
        }
        return _componentServices.get(componentServiceName);
    }

    @Override
    public List<Transformer> getTransformers() {
        return Collections.unmodifiableList(_transformers);
    }

    @Override
    public List<Validator> getValidators() {
        return Collections.unmodifiableList(_validators);
    }


    @Override
    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(_properties);
    }

    /**
     * @return the deployment associated with this application.
     */
    public AbstractDeployment getDeployment() {
        return _deployment;
    }

    private void addServices() {
        _services = new LinkedHashMap<QName, Service>();
        if (getConfig().getComposite().getServices() == null) {
            return;
        }
        
        for (CompositeServiceModel service : getConfig().getComposite().getServices()) {
            _services.put(service.getQName(), new BaseService(service, this));
        }
    }
    
    private void addReferences() {
        _references = new LinkedHashMap<QName, Reference>();
        if (getConfig().getComposite().getReferences() == null) {
            return;
        }
        
        for (CompositeReferenceModel ref : getConfig().getComposite().getReferences()) {
            _references.put(ref.getQName(), new BaseReference(ref, this));
        }
    }

    private void addTransformers() {
        _transformers = new LinkedList<Transformer>();
        if (getConfig().getTransforms() == null) {
            return;
        }
        for (TransformModel transformModel : getConfig().getTransforms().getTransforms()) {
            _transformers.add(new BaseTransformer(transformModel));
        }
    }
    
    private void addValidators() {
        _validators = new LinkedList<Validator>();
        if (getConfig().getValidates() == null) {
            return;
        }
        for (ValidateModel validateModel : getConfig().getValidates().getValidates()) {
            _validators.add(new BaseValidator(validateModel));
        }
    }

    private void addComponents() {
        _componentServices = new LinkedHashMap<QName, ComponentService>();
        if (getConfig().getComposite().getComponents() == null) {
            return;
        }
        for (ComponentModel component : getConfig().getComposite().getComponents()) {
            // TODO: we need a separate node for components, to support cases
            // where the component implements no services.  Should also consider
            // multiple services per component.
            if (component.getServices().size() > 0) {
                ComponentServiceModel service = component.getServices().get(0);
                if (service.getInterface() == null || EsbInterfaceModel.ESB.equals(service.getInterface().getType())) { 
                    _componentServices.put(service.getQName(), new BaseNoopComponentService(service, component, this));
                } else if (InterfaceModel.JAVA.equals(service.getInterface().getType())) {
                    _componentServices.put(service.getQName(), new BaseJavaComponentService(service, component, this));
                } else if (InterfaceModel.WSDL.equals(service.getInterface().getType())) {
                    _componentServices.put(service.getQName(), new BaseWsdlComponentService(service, component, this));
                }
            }
        }
    }

    private void addProperties() {
        _properties = new LinkedHashMap<String, String>();
        if (getConfig().getComposite() == null) {
            return;
        }
        for (PropertyModel property : getConfig().getComposite().getProperties().values()) {
            _properties.put(property.getName(), property.getValue());
        }
    }

}
