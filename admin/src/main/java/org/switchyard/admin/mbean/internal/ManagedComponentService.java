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
