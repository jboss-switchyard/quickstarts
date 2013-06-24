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
