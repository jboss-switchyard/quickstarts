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

import org.switchyard.admin.Throttling;
import org.switchyard.admin.mbean.ThrottlingMXBean;

/**
 * ManagedThrottling
 * <p/>
 * Implementation for {@link ThrottlingMXBean}.
 */
public class ManagedThrottling implements ThrottlingMXBean {

    private final Throttling _delegate;
    /**
     * Create a new ManagedThrottling.
     * 
     * @param delegate the delegate admin object.
     */
    public ManagedThrottling(Throttling delegate) {
        _delegate = delegate;
    }

    @Override
    public boolean isEnabled() {
        return _delegate.isEnabled();
    }

    @Override
    public void enable() {
        _delegate.enable();
    }

    @Override
    public void disable() {
        _delegate.disable();
    }

    @Override
    public int getMaxRequests() {
        return _delegate.getMaxRequests();
    }

    @Override
    public void setMaxRequests(int maxRequests) {
        _delegate.setMaxRequests(maxRequests);
    }

    @Override
    public long getTimePeriod() {
        return _delegate.getTimePeriod();
    }

}
