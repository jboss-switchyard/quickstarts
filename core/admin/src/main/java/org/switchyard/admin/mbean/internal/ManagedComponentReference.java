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

import org.switchyard.admin.ComponentReference;
import org.switchyard.admin.mbean.ComponentReferenceMXBean;

/**
 * Implementation of ComponentReferenceMXBean.
 */
public class ManagedComponentReference implements ComponentReferenceMXBean {
    
    private ComponentReference _reference;
    
    /**
     * Create a new ManagedComponentReference.
     * @param reference delegate reference to admin ComponentReference
     */
    public ManagedComponentReference(ComponentReference reference) {
        _reference = reference;
    }

    @Override
    public String getName() {
        return _reference.getName().toString();
    }

    @Override
    public String getInterface() {
        return _reference.getInterface();
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
