/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.switchyard.internal;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.HandlerChain;
import org.switchyard.Service;
import org.switchyard.ServiceDomain;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.spi.Endpoint;
import org.switchyard.spi.ServiceRegistry;

public class MockServiceRegistry implements ServiceRegistry {        
    public MockServiceRegistry() {
    }
    
    @Override
    public Service registerService(QName serviceName, ServiceInterface serviceInterface, Endpoint endpoint,
            HandlerChain handlers, ServiceDomain domain) {
        QName mockName = new QName("mockServiceName");
        return new ServiceRegistration(mockName, serviceInterface, endpoint, handlers, this, domain);
    }

    @Override
    public void unregisterService(Service service) { 
    }

    @Override
    public List<Service> getServices() {
        return new ArrayList<Service>();
    }

    @Override
    public List<Service> getServices(QName serviceName) {
        return new ArrayList<Service>();
    }

    @Override
    public List<Service> getServicesForDomain(String domainName) {
        return new ArrayList<Service>();
    }
}
