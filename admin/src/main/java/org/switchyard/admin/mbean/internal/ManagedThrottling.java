/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
