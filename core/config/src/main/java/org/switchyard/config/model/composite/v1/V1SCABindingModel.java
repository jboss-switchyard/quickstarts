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

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.SCABindingModel;
import org.switchyard.config.model.composite.SCANamespace;

/**
 * V1 implementation in SCABindingModel.
 */
public class V1SCABindingModel extends V1BindingModel implements SCABindingModel {

    private String _switchyardNamespace;

    /**
     * Create a new V1SCABindingModel.
     * @param switchyardNamespace switchyardNamespace
     */
    public V1SCABindingModel(String switchyardNamespace) {
        super(SCABindingModel.SCA, SCANamespace.DEFAULT.uri());
        _switchyardNamespace = switchyardNamespace;
    }
    
    /**
     * Create a new V1SCABindingModel.
     * @param config raw config model
     * @param desc descriptor
     */
    public V1SCABindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
        _switchyardNamespace = getModelRootNamespace();
    }

    @Override
    public boolean isClustered() {
        return Boolean.valueOf(getModelAttribute(new QName(_switchyardNamespace, CLUSTERED)));
    }

    @Override
    public SCABindingModel setClustered(boolean clustered) {
        setModelAttribute(new QName(_switchyardNamespace, CLUSTERED), String.valueOf(clustered));
        return this;
    }

    @Override
    public boolean isPreferLocal() {
        return Boolean.valueOf(getModelAttribute(new QName(_switchyardNamespace, PREFER_LOCAL)));
    }

    @Override
    public SCABindingModel setPreferLocal(boolean preferLocal) {
        setModelAttribute(new QName(_switchyardNamespace, PREFER_LOCAL), String.valueOf(false));
        return this;
    }

    @Override
    public boolean isLoadBalanced() {
        return getLoadBalance() != null;
    }

    @Override
    public String getLoadBalance() {
        return getModelAttribute(new QName(_switchyardNamespace, LOAD_BALANCE));
    }

    @Override
    public SCABindingModel setLoadBalance(String loadBalance) {
        setModelAttribute(new QName(_switchyardNamespace, LOAD_BALANCE), loadBalance);
        return this;
    }

    @Override
    public boolean hasTarget() {
        return getTarget() != null;
    }

    @Override
    public String getTarget() {
        return getModelAttribute(new QName(_switchyardNamespace, TARGET));
    }

    @Override
    public SCABindingModel setTarget(String target) {
        setModelAttribute(new QName(_switchyardNamespace, TARGET), target);
        return this;
    }

    @Override
    public boolean hasTargetNamespace() {
        return getTargetNamespace() != null;
    }

    @Override
    public String getTargetNamespace() {
        return getModelAttribute(new QName(_switchyardNamespace, TARGET_NAMESPACE));
    }

    @Override
    public SCABindingModel setTargetNamespace(String namespace) {
        setModelAttribute(new QName(_switchyardNamespace, TARGET_NAMESPACE), namespace);
        return this;
    }
}
