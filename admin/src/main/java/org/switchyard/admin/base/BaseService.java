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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.admin.Application;
import org.switchyard.admin.Binding;
import org.switchyard.admin.ComponentService;
import org.switchyard.admin.Service;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;

/**
 * Base implementation for Service.
 */
public class BaseService implements Service {
    
    private QName _name;
    private String _serviceInterface;
    private Application _application;
    private ComponentService _promotedService;
    private List<Binding> _gateways = new LinkedList<Binding>();
    
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
            ComponentService implementation,
            List<Binding> gateways) {
        
        _name = name;
        _serviceInterface = serviceInterface;
        _application = application;
        _promotedService = implementation;
        _gateways = gateways;
    }
    
    /**
     * Create a new BaseService from the specified config model.
     * 
     * @param serviceConfig the composite service config.
     * @param application the application containing the service.
     */
    public BaseService(CompositeServiceModel serviceConfig, Application application) {
        _name = serviceConfig.getQName();
        _application = application;
        if (serviceConfig.getInterface() != null) {
            _serviceInterface = serviceConfig.getInterface().getInterface();
        }
        _promotedService = getPromotedService(application, serviceConfig);
        _gateways = new ArrayList<Binding>();

        for (BindingModel bindingModel : serviceConfig.getBindings()) {
            _gateways.add(new BaseBinding(bindingModel.getType(), bindingModel.toString()));
        }
    }
    
    @Override
    public Application getApplication() {
        return _application;
    }

    @Override
    public List<Binding> getGateways() {
        return _gateways;
    }

    @Override
    public ComponentService getPromotedService() {
       return _promotedService;
    }

    @Override
    public String getInterface() {
        return _serviceInterface;
    }

    @Override
    public QName getName() {
        return _name;
    }

    private ComponentService getPromotedService(Application application, CompositeServiceModel compositeService) {
        ComponentServiceModel componentServiceModel = compositeService.getComponentService();
        if (componentServiceModel == null) {
            return null;
        }
        return application.getComponentService(componentServiceModel.getQName());
    }
}
