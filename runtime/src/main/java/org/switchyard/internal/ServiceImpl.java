/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2010-2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more 
 * details. You should have received a copy of the GNU Lesser General Public 
 * License, v.2.1 along with this distribution; if not, write to the Free 
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  
 * 02110-1301, USA.
 */

package org.switchyard.internal;

import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.Service;
import org.switchyard.ServiceDomain;
import org.switchyard.event.ServiceUnregistrationEvent;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.policy.Policy;

/**
 * A service registered in a SwitchYard domain.  This is an instance of the 
 * registered service itself and not a service reference (which is used to
 * invoke a service).
 */
public class ServiceImpl implements Service {

    private QName _name;
    private ServiceInterface _interface;
    private DomainImpl _domain;
    private List<Policy> _requires;

    /**
     * Creates a new reference to a service.
     * @param name name of the service reference
     * @param serviceInterface the service interface
     * @param requires list of policies required for this reference
     * @param domain domain in which the service is used 
     */
    public ServiceImpl(QName name, 
            ServiceInterface serviceInterface, 
            List<Policy> requires,
            DomainImpl domain) {
        
        _name = name;
        _interface = serviceInterface;
        _domain = domain;
        
        if (requires != null) {
            _requires = requires;
        } else {
            _requires = Collections.emptyList();
        }
    }

    @Override
    public ServiceInterface getInterface() {
        return _interface;
    }

    @Override
    public QName getName() {
        return _name;
    }
    
    @Override
    public ServiceDomain getDomain() {
        return _domain;
    }
    
    @Override
    public void unregister() {
        _domain.getServiceRegistry().unregisterService(this);
        _domain.getEventPublisher().publish(new ServiceUnregistrationEvent(this));
    }
    
    @Override
    public List<Policy> getRequiredPolicies() {
        return Collections.unmodifiableList(_requires);
    }
}
