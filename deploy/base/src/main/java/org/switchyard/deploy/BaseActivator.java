/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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

package org.switchyard.deploy;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.ServiceDomain;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.ComponentModel;

/**
 * Base implementation of Activator which provides a convenience implementation
 * for declaring activation types.
 */
public abstract class BaseActivator implements Activator {
    
    private List<String> _activationTypes = new LinkedList<String>();
    private ServiceDomain _serviceDomain;

    protected BaseActivator(String ... types) {
        if (types != null) {
            _activationTypes.addAll(Arrays.asList(types));
        }
    }

    @Override
    public ServiceHandler activateBinding(QName name, BindingModel config) {
        throw new UnsupportedOperationException(
                "activateBinding() not supported by " + getClass());
    }

    @Override
    public ServiceHandler activateService(QName name, ComponentModel config) {
        throw new UnsupportedOperationException(
                "deactivateService() not supported by " + getClass());
    }
    

    @Override
    public void deactivateBinding(QName name, ServiceHandler handler) {
        throw new UnsupportedOperationException(
                "deactivateBinding() not supported by " + getClass());
    }

    @Override
    public void deactivateService(QName name, ServiceHandler handler) {
        throw new UnsupportedOperationException(
                "deactivateService() not supported by " + getClass());
    }
    
    
    /**
     * Sets the service domain instance of this activator.
     * @param serviceDomain the service domain
     */
    public void setServiceDomain(ServiceDomain serviceDomain) {
        _serviceDomain = serviceDomain;
    }

    /**
     * Gets the service domain instance of this activator.
     * @return the service domain
     */
    public ServiceDomain getServiceDomain() {
        return _serviceDomain;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canActivate(String type) {
        return _activationTypes.contains(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getActivationTypes() {
        return Collections.unmodifiableList(_activationTypes);
    }
}
