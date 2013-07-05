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

import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.SCABindingModel;

/**
 * V1 implementation in SCABindingModel.
 */
public class V1SCABindingModel extends V1BindingModel implements SCABindingModel {
    
    /**
     * Create a new V1SCABindingModel.
     */
    public V1SCABindingModel() {
        super(SCABindingModel.SCA, CompositeModel.DEFAULT_NAMESPACE);
    }
    
    /**
     * Create a new V1SCABindingModel.
     * @param config raw config model
     * @param desc descriptor
     */
    public V1SCABindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public boolean isClustered() {
        return Boolean.valueOf(getModelAttribute(CLUSTERED));
    }

    @Override
    public SCABindingModel setClustered(boolean clustered) {
        setModelAttribute(CLUSTERED, String.valueOf(clustered));
        return this;
    }

    @Override
    public boolean isLoadBalanced() {
        return getLoadBalance() != null;
    }

    @Override
    public String getLoadBalance() {
        return getModelAttribute(LOAD_BALANCE);
    }

    @Override
    public SCABindingModel setLoadBalance(String loadBalance) {
        setModelAttribute(LOAD_BALANCE, loadBalance);
        return this;
    }

    @Override
    public boolean hasTarget() {
        return getTarget() != null;
    }

    @Override
    public String getTarget() {
        return getModelAttribute(TARGET);
    }

    @Override
    public SCABindingModel setTarget(String target) {
        setModelAttribute(TARGET, target);
        return this;
    }

    @Override
    public boolean hasTargetNamespace() {
        return getTargetNamespace() != null;
    }

    @Override
    public String getTargetNamespace() {
        return getModelAttribute(TARGET_NAMESPACE);
    }

    @Override
    public SCABindingModel setTargetNamespace(String namespace) {
        setModelAttribute(TARGET_NAMESPACE, namespace);
        return this;
    }

}
