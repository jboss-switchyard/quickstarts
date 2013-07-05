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

import java.util.Set;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseNamedModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.InterfaceModel;

/**
 * A version 1 ComponentServiceModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1ComponentServiceModel extends BaseNamedModel implements ComponentServiceModel {

    private InterfaceModel _interface;

    /**
     * Constructs a new V1ComponentServiceModel.
     */
    public V1ComponentServiceModel() {
        super(new QName(CompositeModel.DEFAULT_NAMESPACE, ComponentServiceModel.SERVICE));
    }

    /**
     * Constructs a new V1ComponentServiceModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1ComponentServiceModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ComponentModel getComponent() {
        return (ComponentModel)getModelParent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InterfaceModel getInterface() {
        if (_interface == null) {
            _interface = (InterfaceModel)getFirstChildModelStartsWith(InterfaceModel.INTERFACE);
        }
        return _interface;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ComponentServiceModel setInterface(InterfaceModel interfaze) {
        setChildModel(interfaze);
        _interface = interfaze;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSecurity() {
        return getModelAttribute(ComponentServiceModel.SECURITY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ComponentServiceModel setSecurity(String security) {
        setModelAttribute(ComponentServiceModel.SECURITY, security);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPolicyRequirement(String policyName) {
        Set<String> requires = PolicyConfig.getRequires(this);
        requires.add(policyName);
        PolicyConfig.setRequires(this, requires);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getPolicyRequirements() {
        return PolicyConfig.getRequires(this);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasPolicyRequirement(String policyName) {
        return PolicyConfig.getRequires(this).contains(policyName);
    }
}
