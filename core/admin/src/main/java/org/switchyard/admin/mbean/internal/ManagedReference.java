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
package org.switchyard.admin.mbean.internal;

import java.util.ArrayList;
import java.util.List;

import org.switchyard.admin.Reference;
import org.switchyard.admin.mbean.ApplicationMXBean;
import org.switchyard.admin.mbean.BindingMXBean;
import org.switchyard.admin.mbean.ReferenceMXBean;

/**
 * Implementation of ReferenceMXBean.
 */
public class ManagedReference implements ReferenceMXBean {
    
    private Reference _reference;
    private ManagedApplication _application;
    private List<BindingMXBean> _bindings = new ArrayList<BindingMXBean>();
    
    /**
     * Create a new ManagedReference.
     * @param reference delegate reference to admin Reference
     * @param application pointer to parent application
     */
    public ManagedReference(Reference reference, ManagedApplication application) {
        _reference = reference;
        _application = application;
    }

    @Override
    public String getName() {
        return _reference.getName().toString();
    }

    @Override
    public String getPromotedReference() {
        return _reference.getPromotedReference();
    }

    @Override
    public List<BindingMXBean> getBindings() {
        return _bindings;
    }
    
    /**
     * Adds a managed binding to this reference.
     * @param binding managed binding
     */
    public void addBinding(BindingMXBean binding) {
        _bindings.add(binding);
    }

    @Override
    public String getInterface() {
        return _reference.getInterface();
    }

    @Override
    public ApplicationMXBean getApplication() {
        return _application;
    }

    @Override
    public int getSuccessCount() {
        return _reference.getMessageMetrics().getSuccessCount();
    }

    @Override
    public int getFaultCount() {
        return _reference.getMessageMetrics().getFaultCount();
    }

    @Override
    public int getTotalCount() {
        return _reference.getMessageMetrics().getTotalCount();
    }

    @Override
    public long getTotalProcessingTime() {
        return _reference.getMessageMetrics().getTotalProcessingTime();
    }

    @Override
    public double getAverageProcessingTime() {
        return _reference.getMessageMetrics().getAverageProcessingTime();
    }

    @Override
    public int getMinProcessingTime() {
        return _reference.getMessageMetrics().getMinProcessingTime();
    }

    @Override
    public int getMaxProcessingTime() {
        return _reference.getMessageMetrics().getMaxProcessingTime();
    }

    @Override
    public void reset() {
        _reference.resetMessageMetrics();
    }
    
}
