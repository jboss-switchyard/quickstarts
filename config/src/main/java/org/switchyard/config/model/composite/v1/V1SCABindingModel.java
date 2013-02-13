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
