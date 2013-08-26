/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.switchyard.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.switchyard.Service;
import org.switchyard.ServiceReference;
import org.switchyard.spi.ServiceRegistry;

/**
 * Standalone implementation of ServiceRegistry.
 */
public class DefaultServiceRegistry implements ServiceRegistry {

    private static Logger _logger = Logger.getLogger(DefaultServiceRegistry.class);

    private final Map<QName, List<Service>> _services =
        new HashMap<QName, List<Service>>();

    private ConcurrentHashMap<QName, ServiceReference> _references =
            new ConcurrentHashMap<QName, ServiceReference>();

    @Override
    public synchronized List<Service> getServices() {
        List<Service> serviceList = new LinkedList<Service>();
        for (List<Service> services : _services.values()) {
            serviceList.addAll(services);
        }

        return serviceList;
    }

    @Override
    public synchronized List<Service> getServices(QName serviceName) {
        if (!_services.containsKey(serviceName)) {
            return Collections.emptyList();
        }

        return new LinkedList<Service>(_services.get(serviceName));
    }

    @Override
    public synchronized Service registerService(Service service) {
        if (!_services.containsKey(service.getName())) {
            _services.put(service.getName(), new LinkedList<Service>());
        }
        _services.get(service.getName()).add(service);

        if (_logger.isDebugEnabled()) {
            _logger.debug("Registered Service '" + service.getName() + "'.");
        }

        return service;
    }

    @Override
    public synchronized void unregisterService(Service service) {
        List<Service> serviceList =_services.get(service.getName());
        if (serviceList != null) {
            serviceList.remove(service);

            if (_logger.isDebugEnabled()) {

                QName serviceName = service.getName();
                String domainName = "";
                if ((service.getDomain() != null) && (service.getDomain().getName() != null)) {
                    domainName = service.getDomain().getName().toString();
                }
                _logger.debug("Unregistered Service '" + serviceName + "' from ServiceDomain '" + domainName + "'.");
            }
        }
    }
   
    @Override
    public void registerServiceReference(ServiceReference reference) {
        _references.putIfAbsent(reference.getName(), reference);

        if (_logger.isDebugEnabled()) {
            _logger.debug("Registered ServiceReference '" + reference.getName() + "'.");
        }
    }

    @Override
    public void unregisterServiceReference(ServiceReference reference) {
        ServiceReference removed = _references.remove(reference.getName());
        if (removed != null && _logger.isDebugEnabled()) {
            _logger.debug("Unregistered ServiceReference '" + reference.getName() + "'.");
        }
    }

    @Override
    public List<ServiceReference> getServiceReferences() {
        if (_references.isEmpty()) {
            return Collections.emptyList();
        } else {
            return new LinkedList<ServiceReference>(_references.values());
        }
    }

    @Override
    public ServiceReference getServiceReference(QName serviceName) {
        return _references.get(serviceName);
    }

}
