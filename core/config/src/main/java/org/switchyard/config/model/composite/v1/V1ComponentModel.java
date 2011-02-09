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

import org.switchyard.config.Configuration;
import org.switchyard.config.Descriptor;
import org.switchyard.config.model.BaseNamedModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.ImplementationModel;
import org.switchyard.config.model.composite.InternalServiceModel;
import org.switchyard.config.model.composite.ReferenceModel;

/**
 * V1ComponentModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1ComponentModel extends BaseNamedModel implements ComponentModel {

    private ImplementationModel _implementation;
    private List<InternalServiceModel> _services = new ArrayList<InternalServiceModel>();
    private List<ReferenceModel> _references = new ArrayList<ReferenceModel>();

    public V1ComponentModel() {
        super(ComponentModel.COMPONENT);
        setChildrenOrder(ImplementationModel.IMPLEMENTATION, InternalServiceModel.SERVICE, ReferenceModel.REFERENCE);
    }

    public V1ComponentModel(Configuration config, Descriptor desc) {
        super(config, desc);
        setChildrenOrder(ImplementationModel.IMPLEMENTATION, InternalServiceModel.SERVICE, ReferenceModel.REFERENCE);
        for (Configuration service_config : config.getChildren(InternalServiceModel.SERVICE)) {
            InternalServiceModel service = (InternalServiceModel)readModel(service_config);
            if (service != null) {
                _services.add(service);
            }
        }
        for (Configuration reference_config : config.getChildren(ReferenceModel.REFERENCE)) {
            ReferenceModel reference = (ReferenceModel)readModel(reference_config);
            if (reference != null) {
                _references.add(reference);
            }
        }
    }

    @Override
    public CompositeModel getComposite() {
        return (CompositeModel)getModelParent();
    }

    @Override
    public ImplementationModel getImplementation() {
        if (_implementation == null) {
            _implementation = (ImplementationModel)getFirstChildModelStartsWith(ImplementationModel.IMPLEMENTATION);
        }
        return _implementation;
    }

    @Override
    public ComponentModel setImplementation(ImplementationModel implementation) {
        setChildModel(implementation);
        _implementation = implementation;
        return this;
    }

    @Override
    public synchronized List<InternalServiceModel> getServices() {
        return Collections.unmodifiableList(_services);
    }

    @Override
    public synchronized ComponentModel addService(InternalServiceModel service) {
        addChildModel(service);
        _services.add(service);
        return this;
    }

    @Override
    public synchronized List<ReferenceModel> getReferences() {
        return Collections.unmodifiableList(_references);
    }

    @Override
    public synchronized ComponentModel addReference(ReferenceModel reference) {
        addChildModel(reference);
        _references.add(reference);
        return this;
    }

}
