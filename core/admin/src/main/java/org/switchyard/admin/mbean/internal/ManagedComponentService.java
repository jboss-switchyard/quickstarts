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

import org.switchyard.admin.ComponentReference;
import org.switchyard.admin.ComponentService;
import org.switchyard.admin.ServiceOperation;
import org.switchyard.admin.mbean.ApplicationMXBean;
import org.switchyard.admin.mbean.ComponentServiceMXBean;

/**
 * Implementation of ComponentService.
 */
public class ManagedComponentService implements ComponentServiceMXBean {
    
    private ComponentService _service;
    private ManagedApplication _parent;
    
    /**
     * Create a new ManagedComponentService.
     * @param service delegate reference to admin ComponentService
     * @param parent pointer to parent application
     */
    public ManagedComponentService(ComponentService service, ManagedApplication parent) {
        _service = service;
        _parent = parent;
    }

    @Override
    public String getName() {
        return _service.getName().toString();
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
    public String getImplementation() {
        return _service.getImplementation();
    }

    @Override
    public String getImplementationConfiguration() {
        return _service.getImplementationConfiguration();
    }

    @Override
    public List<String> getServiceOperations() {
        List<String> ops = new ArrayList<String>();
        for (ServiceOperation op : _service.getServiceOperations()) {
            ops.add(op.getName());
        }
        return ops;
    }

    @Override
    public List<String> getReferences() {
        List<String> refs = new ArrayList<String>();
        for (ComponentReference ref : _service.getReferences()) {
            refs.add(ref.getName().toString());
        }
        return refs;
    }

    @Override
    public int getSuccessCount() {
        return _service.getMessageMetrics().getSuccessCount();
    }

    @Override
    public int getFaultCount() {
        return _service.getMessageMetrics().getFaultCount();
    }

    @Override
    public int getTotalCount() {
        return _service.getMessageMetrics().getTotalCount();
    }

    @Override
    public long getTotalProcessingTime() {
        return _service.getMessageMetrics().getTotalProcessingTime();
    }

    @Override
    public double getAverageProcessingTime() {
        return _service.getMessageMetrics().getAverageProcessingTime();
    }

    @Override
    public int getMinProcessingTime() {
        return _service.getMessageMetrics().getMinProcessingTime();
    }

    @Override
    public int getMaxProcessingTime() {
        return _service.getMessageMetrics().getMaxProcessingTime();
    }

    @Override
    public void reset() {
        _service.resetMessageMetrics();
    }
    
}
