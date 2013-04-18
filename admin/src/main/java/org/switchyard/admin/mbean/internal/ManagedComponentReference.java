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
