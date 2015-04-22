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
import org.switchyard.config.model.BaseTypedModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.ComponentImplementationModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.SCANamespace;

/**
 * A version 1 ComponentImplementationModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1ComponentImplementationModel extends BaseTypedModel implements ComponentImplementationModel {

    /**
     * Constructs a new V1ComponentImplementationModel of the specified "type".
     * @param type the "type" of ComponentImplementationModel
     */
    public V1ComponentImplementationModel(String type) {
        this(type, SCANamespace.DEFAULT.uri());
    }

    /**
     * Constructs a new V1ComponentImplementationModel of the specified "type", and in the specified namespace.
     * @param type the "type" of ComponentImplementationModel
     * @param namespace the namespace
     */
    public V1ComponentImplementationModel(String type, String namespace) {
        super(new QName(namespace, ComponentImplementationModel.IMPLEMENTATION + '.' + type));
    }

    /**
     * Constructs a new V1ComponentImplementationModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1ComponentImplementationModel(Configuration config, Descriptor desc) {
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
