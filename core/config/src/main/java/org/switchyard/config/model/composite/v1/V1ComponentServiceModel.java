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
import org.switchyard.config.model.composite.InterfaceModel;
import org.switchyard.config.model.composite.SCANamespace;

/**
 * A version 1 ComponentServiceModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1ComponentServiceModel extends BaseNamedModel implements ComponentServiceModel {

    private InterfaceModel _interface;
    private String _switchyardNamespace;

    /**
     * Constructs a new V1ComponentServiceModel.
     * @param switchyardNamespace switchyardNamespace
     */
    public V1ComponentServiceModel(String switchyardNamespace) {
        super(SCANamespace.DEFAULT.uri(), ComponentServiceModel.SERVICE);
        _switchyardNamespace = switchyardNamespace;
    }

    /**
     * Constructs a new V1ComponentServiceModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1ComponentServiceModel(Configuration config, Descriptor desc) {
        super(config, desc);
        _switchyardNamespace = getModelRootNamespace();
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
        return getModelAttribute(new QName(_switchyardNamespace, ComponentServiceModel.SECURITY));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ComponentServiceModel setSecurity(String security) {
        setModelAttribute(new QName(_switchyardNamespace, ComponentServiceModel.SECURITY), security);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPolicyRequirement(QName policyQName) {
        PolicyConfig.addRequirement(this, policyQName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<QName> getPolicyRequirements() {
        return PolicyConfig.getRequirements(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasPolicyRequirement(QName policyQName) {
        return PolicyConfig.hasRequirement(this, policyQName);
    }

}
