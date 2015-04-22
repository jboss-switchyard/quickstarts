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

import org.switchyard.admin.Binding;
import org.switchyard.admin.mbean.BindingMXBean;

/**
 * Implementation of BindingMXBean.
 */
public class ManagedBinding implements BindingMXBean {
    
    private Binding _binding;
    
    /**
     * Create a new ManagedBinding.
     * @param binding delegate to admin Binding instance
     */
    public ManagedBinding(Binding binding) {
        _binding = binding;
    }

    @Override
    public String getType() {
        return _binding.getType();
    }

    @Override
    public String getConfiguration() {
        return _binding.getConfiguration();
    }

    @Override
    public String getName() {
        return _binding.getName();
    }

    @Override
    public void start() {
        _binding.start();
    }

    @Override
    public void stop() {
        _binding.stop();
    }

    @Override
    public State getState() {
        return State.valueOf(_binding.getState().toString());
    }

    @Override
    public int getSuccessCount() {
        return _binding.getMessageMetrics().getSuccessCount();
    }

    @Override
    public int getFaultCount() {
        return _binding.getMessageMetrics().getFaultCount();
    }

    @Override
    public int getTotalCount() {
        return _binding.getMessageMetrics().getTotalCount();
    }

    @Override
    public long getTotalProcessingTime() {
        return _binding.getMessageMetrics().getTotalProcessingTime();
    }

    @Override
    public double getAverageProcessingTime() {
        return _binding.getMessageMetrics().getAverageProcessingTime();
    }

    @Override
    public int getMinProcessingTime() {
        return _binding.getMessageMetrics().getMinProcessingTime();
    }

    @Override
    public int getMaxProcessingTime() {
        return _binding.getMessageMetrics().getMaxProcessingTime();
    }

    @Override
    public void reset() {
        _binding.resetMessageMetrics();
    }
    
}
