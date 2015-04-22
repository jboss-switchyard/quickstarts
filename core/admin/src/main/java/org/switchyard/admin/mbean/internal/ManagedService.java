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

import org.switchyard.admin.Service;
import org.switchyard.admin.mbean.ApplicationMXBean;
import org.switchyard.admin.mbean.BindingMXBean;
import org.switchyard.admin.mbean.ServiceMXBean;
import org.switchyard.admin.mbean.ThrottlingMXBean;

/**
 * Implementation of ServiceMXBean.
 */
public class ManagedService implements ServiceMXBean {
    
    private Service _service;
    private ManagedApplication _parent;
    private List<BindingMXBean> _bindings = new ArrayList<BindingMXBean>();
    private ThrottlingMXBean _throttling;
    
    /**
     * Creates a new ManagedService.
     * @param service delegate reference to admin Service
     * @param parent pointer to parent application.
     */
    public ManagedService(Service service, ManagedApplication parent) {
        _service = service;
        _parent = parent;
        _throttling = new ManagedThrottling(service.getThrottling());
    }

    @Override
    public String getName() {
        return _service.getName().toString();
    }

    @Override
    public String getPromotedService() {
        return _service.getPromotedService().getName().toString();
    }

    @Override
    public List<BindingMXBean> getBindings() {
        return _bindings;
    }
    
    /**
     * Adds a managed binding to this service.
     * @param binding managed binding
     */
    public void addBinding(BindingMXBean binding) {
        _bindings.add(binding);
    }

    @Override
    public String getInterface() {
        return _service.getInterface();
    }

    @Override
    public ApplicationMXBean getApplication() {
        return _parent;
    }

    @Override
    public ThrottlingMXBean getThrottling() {
        return _throttling;
    }

}
