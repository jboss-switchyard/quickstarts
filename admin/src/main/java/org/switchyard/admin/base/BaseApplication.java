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
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.config.model.validate.ValidateModel;

/**
 * Base implementation of Application.
 */
public class BaseApplication implements Application {
    
    private QName _name;
    private Map<QName, Service> _services;
    private Map<QName, Reference> _references;
    private Map<QName, ComponentService> _componentServices;
    private List<Transformer> _transformers;
    private List<Validator> _validators;
    private SwitchYardModel _config;
    
    /**
     * Create a new BaseApplication.
     * @param name application name
     * @param config application descriptor model
     */
    public BaseApplication(QName name, SwitchYardModel config) {
        _name = name;
        _config = config;
        
        addTransformers();
        addValidators();
        addComponents();
        addServices();
        addReferences();
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
        return _config;
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


    private void addServices() {
        _services = new LinkedHashMap<QName, Service>();
        if (_config.getComposite().getServices() == null) {
            return;
        }
        
        for (CompositeServiceModel service : _config.getComposite().getServices()) {
            _services.put(service.getQName(), new BaseService(service, this));
        }
    }
    
    private void addReferences() {
        _references = new LinkedHashMap<QName, Reference>();
        if (_config.getComposite().getReferences() == null) {
            return;
        }
        
        for (CompositeReferenceModel ref : _config.getComposite().getReferences()) {
            _references.put(ref.getQName(), new BaseReference(ref, this));
        }
    }

    private void addTransformers() {
        _transformers = new LinkedList<Transformer>();
        if (_config.getTransforms() == null) {
            return;
        }
        for (TransformModel transformModel : _config.getTransforms().getTransforms()) {
            _transformers.add(new BaseTransformer(transformModel));
        }
    }
    
    private void addValidators() {
        _validators = new LinkedList<Validator>();
        if (_config.getValidates() == null) {
            return;
        }
        for (ValidateModel validateModel : _config.getValidates().getValidates()) {
            _validators.add(new BaseValidator(validateModel));
        }
    }

    private void addComponents() {
        _componentServices = new LinkedHashMap<QName, ComponentService>();
        if (_config.getComposite().getComponents() == null) {
            return;
        }
        for (ComponentModel component : _config.getComposite().getComponents()) {
            // TODO: we need a separate node for components, to support cases
            // where the component implements no services.  Should also consider
            // multiple services per component.
            if (component.getServices().size() > 0) {
                ComponentServiceModel service = component.getServices().get(0);
                if (service.getInterface() == null) { 
                    _componentServices.put(service.getQName(), new BaseNoopComponentService(service, component, this));
                } else if (InterfaceModel.JAVA.equals(service.getInterface().getType())) {
                    _componentServices.put(service.getQName(), new BaseJavaComponentService(service, component, this));
                } else if (InterfaceModel.WSDL.equals(service.getInterface().getType())) {
                    _componentServices.put(service.getQName(), new BaseWsdlComponentService(service, component, this));
                }
            }
        }
    }
}
