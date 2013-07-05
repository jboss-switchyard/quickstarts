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

package org.switchyard.deploy;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.ServiceDomain;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.ComponentModel;

/**
 * Base implementation of Activator which provides a convenience implementation
 * for declaring activation types.
 */
public abstract class BaseActivator implements Activator {
    
    private List<String> _activationTypes = new LinkedList<String>();
    private ServiceDomain _serviceDomain;

    protected BaseActivator(String ... types) {
        if (types != null) {
            _activationTypes.addAll(Arrays.asList(types));
        }
    }

    @Override
    public ServiceHandler activateBinding(QName name, BindingModel config) {
        throw new UnsupportedOperationException(
                "activateBinding() not supported by " + getClass());
    }

    @Override
    public ServiceHandler activateService(QName name, ComponentModel config) {
        throw new UnsupportedOperationException(
                "deactivateService() not supported by " + getClass());
    }
    

    @Override
    public void deactivateBinding(QName name, ServiceHandler handler) {
        throw new UnsupportedOperationException(
                "deactivateBinding() not supported by " + getClass());
    }

    @Override
    public void deactivateService(QName name, ServiceHandler handler) {
        throw new UnsupportedOperationException(
                "deactivateService() not supported by " + getClass());
    }
    
    
    /**
     * Sets the service domain instance of this activator.
     * @param serviceDomain the service domain
     */
    public void setServiceDomain(ServiceDomain serviceDomain) {
        _serviceDomain = serviceDomain;
    }

    /**
     * Gets the service domain instance of this activator.
     * @return the service domain
     */
    public ServiceDomain getServiceDomain() {
        return _serviceDomain;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canActivate(String type) {
        return _activationTypes.contains(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getActivationTypes() {
        return Collections.unmodifiableList(_activationTypes);
    }

    @Override
    public void destroy() {
        // NOP so that Activator impls don't have to bother with this method
        // if they don't have anything to do in destroy().
    }
}
