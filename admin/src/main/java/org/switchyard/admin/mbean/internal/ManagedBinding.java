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

}
