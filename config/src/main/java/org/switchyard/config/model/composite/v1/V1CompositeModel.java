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
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * V1CompositeModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1CompositeModel extends BaseNamedModel implements CompositeModel {

    private List<CompositeServiceModel> _services = new ArrayList<CompositeServiceModel>();
    private List<CompositeReferenceModel> _references = new ArrayList<CompositeReferenceModel>();
    private List<ComponentModel> _components = new ArrayList<ComponentModel>();

    public V1CompositeModel() {
        super(new QName(CompositeModel.DEFAULT_NAMESPACE, CompositeModel.COMPOSITE));
        setModelChildrenOrder(CompositeServiceModel.SERVICE, CompositeReferenceModel.REFERENCE, ComponentModel.COMPONENT);
    }

    public V1CompositeModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration service_config : config.getChildren(CompositeServiceModel.SERVICE)) {
            CompositeServiceModel service = (CompositeServiceModel)readModel(service_config);
            if (service != null) {
                _services.add(service);
            }
        }
        for (Configuration reference_config : config.getChildren(CompositeReferenceModel.REFERENCE)) {
            CompositeReferenceModel reference = (CompositeReferenceModel)readModel(reference_config);
            if (reference != null) {
                _references.add(reference);
            }
        }
        for (Configuration component_config : config.getChildren(ComponentModel.COMPONENT)) {
            ComponentModel component = (ComponentModel)readModel(component_config);
            if (component != null) {
                _components.add(component);
            }
        }
        setModelChildrenOrder(CompositeServiceModel.SERVICE, CompositeReferenceModel.REFERENCE, ComponentModel.COMPONENT);
    }

    public SwitchYardModel getSwitchYard() {
        return (SwitchYardModel)getModelParent();
    }

    @Override
    public synchronized List<CompositeReferenceModel> getReferences() {
        return Collections.unmodifiableList(_references);
    }

    @Override
    public synchronized CompositeModel addReference(CompositeReferenceModel reference) {
        addChildModel(reference);
        _references.add(reference);
        return this;
    }

    @Override
    public synchronized List<CompositeServiceModel> getServices() {
        return Collections.unmodifiableList(_services);
    }

    @Override
    public synchronized CompositeModel addService(CompositeServiceModel service) {
        addChildModel(service);
        _services.add(service);
        return this;
    }

    @Override
    public synchronized List<ComponentModel> getComponents() {
        return Collections.unmodifiableList(_components);
    }

    @Override
    public synchronized CompositeModel addComponent(ComponentModel component) {
        addChildModel(component);
        _components.add(component);
        return this;
    }

}
