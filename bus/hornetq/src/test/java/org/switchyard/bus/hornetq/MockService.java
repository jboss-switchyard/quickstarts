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

package org.switchyard.bus.hornetq;

import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.Service;
import org.switchyard.ServiceDomain;
import org.switchyard.metadata.InOnlyService;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.policy.Policy;

public class MockService implements Service {
    
    private QName _serviceName;
    private ServiceInterface _serviceInterface;
    
    public MockService(QName serviceName) {
        this(serviceName, new InOnlyService());
    }
    
    public MockService(QName serviceName, ServiceInterface serviceInterface) {
        _serviceName = serviceName;
        _serviceInterface = serviceInterface;
    }

    @Override
    public ServiceInterface getInterface() {
        return _serviceInterface;
    }

    @Override
    public QName getName() {
        return _serviceName;
    }

    @Override
    public void unregister() {
        
    }

    @Override
    public ServiceDomain getDomain() {
        return null;
    }

    @Override
    public List<Policy> getRequiredPolicy() {
        return null;
    }

}
