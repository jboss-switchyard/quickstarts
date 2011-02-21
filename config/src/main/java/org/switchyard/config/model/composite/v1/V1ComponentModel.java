/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
package org.switchyard.config.model.composite.v1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseNamedModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.ComponentImplementationModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.config.model.composite.CompositeModel;

/**
 * V1ComponentModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1ComponentModel extends BaseNamedModel implements ComponentModel {

    private ComponentImplementationModel _implementation;
    private List<ComponentServiceModel> _services = new ArrayList<ComponentServiceModel>();
    private List<ComponentReferenceModel> _references = new ArrayList<ComponentReferenceModel>();

    public V1ComponentModel() {
        super(new QName(CompositeModel.DEFAULT_NAMESPACE, ComponentModel.COMPONENT));
        setModelChildrenOrder(ComponentImplementationModel.IMPLEMENTATION, ComponentServiceModel.SERVICE, ComponentReferenceModel.REFERENCE);
    }

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
        setModelChildrenOrder(ComponentImplementationModel.IMPLEMENTATION, ComponentServiceModel.SERVICE, ComponentReferenceModel.REFERENCE);
    }

    @Override
    public CompositeModel getComposite() {
        return (CompositeModel)getModelParent();
    }

    @Override
    public ComponentImplementationModel getImplementation() {
        if (_implementation == null) {
            _implementation = (ComponentImplementationModel)getFirstChildModelStartsWith(ComponentImplementationModel.IMPLEMENTATION);
        }
        return _implementation;
    }

    @Override
    public ComponentModel setImplementation(ComponentImplementationModel implementation) {
        setChildModel(implementation);
        _implementation = implementation;
        return this;
    }

    @Override
    public synchronized List<ComponentServiceModel> getServices() {
        return Collections.unmodifiableList(_services);
    }

    @Override
    public synchronized ComponentModel addService(ComponentServiceModel service) {
        addChildModel(service);
        _services.add(service);
        return this;
    }

    @Override
    public synchronized List<ComponentReferenceModel> getReferences() {
        return Collections.unmodifiableList(_references);
    }

    @Override
    public synchronized ComponentModel addReference(ComponentReferenceModel reference) {
        addChildModel(reference);
        _references.add(reference);
        return this;
    }

}
