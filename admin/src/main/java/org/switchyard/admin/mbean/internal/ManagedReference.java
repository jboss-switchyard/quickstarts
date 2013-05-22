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
