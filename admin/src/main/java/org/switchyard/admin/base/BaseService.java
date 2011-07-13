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

package org.switchyard.admin.base;

import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.admin.Application;
import org.switchyard.admin.Component;
import org.switchyard.admin.Service;

/**
 * Base implementation for Service.
 */
public class BaseService implements Service {
    
    private QName _name;
    private String _serviceInterface;
    private Application _application;
    private Component _implementation;
    private List<Component> _gateways = new LinkedList<Component>();
    
    /**
     * Create a new BaseService.
     * 
     * @param name the name of the service.
     * @param serviceInterface the interface implemented by the service.
     * @param application the application containing the service.
     * @param implementation the implementation type of the service.
     * @param gateways the gateway types exposing the service.
     */
    public BaseService(QName name,
            String serviceInterface,
            Application application, 
            Component implementation,
            List<Component> gateways) {
        
        _name = name;
        _serviceInterface = serviceInterface;
        _application = application;
        _implementation = implementation;
        _gateways = gateways;
    }

    @Override
    public Application getApplication() {
        return _application;
    }

    @Override
    public List<Component> getGateways() {
        return _gateways;
    }

    @Override
    public Component getImplementation() {
       return _implementation;
    }

    @Override
    public String getInterface() {
        return _serviceInterface;
    }

    @Override
    public QName getName() {
        return _name;
    }
    
}
