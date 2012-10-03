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

package org.switchyard.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.switchyard.Service;
import org.switchyard.spi.ServiceRegistry;

/**
 * Standalone implementation of ServiceRegistry.
 */
public class DefaultServiceRegistry implements ServiceRegistry {

    private static Logger _logger = Logger.getLogger(DefaultServiceRegistry.class);

    private final Map<QName, List<Service>> _services =
        new HashMap<QName, List<Service>>();

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
                _logger.debug("Unregistered Service '" + service.getName() + "' from ServiceDomain '" + service.getDomain().getName() + "'.");
            }
        }
    }

}
