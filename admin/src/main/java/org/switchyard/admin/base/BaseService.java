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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.switchyard.Exchange;
import org.switchyard.admin.Application;
import org.switchyard.admin.Binding;
import org.switchyard.admin.ComponentService;
import org.switchyard.admin.MessageMetrics;
import org.switchyard.admin.Service;
import org.switchyard.admin.Throttling;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.runtime.event.ExchangeCompletionEvent;

/**
 * Base implementation for Service.
 */
public class BaseService implements Service {
    
    private QName _name;
    private String _serviceInterface;
    private BaseApplication _application;
    private ComponentService _promotedService;
    private Map<String, Binding> _gateways = new LinkedHashMap<String, Binding>();
    private Throttling _throttling;
    
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
            BaseApplication application, 
            ComponentService implementation,
            Map<String, Binding> gateways) {
        
        _name = name;
        _serviceInterface = serviceInterface;
        _application = application;
        _promotedService = implementation;
        _gateways = gateways;
        _throttling = new ServiceThrottling(this, null);
    }
    
    /**
     * Create a new BaseService from the specified config model.
     * 
     * @param serviceConfig the composite service config.
     * @param application the application containing the service.
     */
    public BaseService(CompositeServiceModel serviceConfig, BaseApplication application) {
        _name = serviceConfig.getQName();
        _application = application;
        if (serviceConfig.getInterface() != null) {
            _serviceInterface = serviceConfig.getInterface().getInterface();
        }
        _promotedService = getPromotedService(application, serviceConfig);
        _gateways = new LinkedHashMap<String, Binding>();

        int idx = 0;
        for (BindingModel bindingModel : serviceConfig.getBindings()) {
            // Generate binding name for now until tooling and config are updated to expose it
            ++idx;
            String name = bindingModel.getName() == null ? "_" + _name.getLocalPart() + "_" + bindingModel.getType()
                    + "_" + idx : bindingModel.getName();
            _gateways.put(name, new BaseBinding(_application, _name, bindingModel.getType(), name, bindingModel.toString()));
        }
        _throttling = new ServiceThrottling(this, serviceConfig.getExtensions());
    }
    
    @Override
    public Application getApplication() {
        return _application;
    }

    @Override
    public List<Binding> getGateways() {
        return new ArrayList<Binding>(_gateways.values());
    }

    @Override
    public Binding getGateway(String gatewayName) {
        if (_gateways.containsKey(gatewayName)) {
            return _gateways.get(gatewayName);
        }
        return null;
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

    @Override
    public Throttling getThrottling() {
        return _throttling;
    }

    private ComponentService getPromotedService(Application application, CompositeServiceModel compositeService) {
        ComponentServiceModel componentServiceModel = compositeService.getComponentService();
        if (componentServiceModel == null) {
            return null;
        }
        return application.getComponentService(componentServiceModel.getQName());
    }

    @Override
    public MessageMetrics getMessageMetrics() {
        return _promotedService.getMessageMetrics();
    }

    @Override
    public void resetMessageMetrics() {
        for (final Binding binding : _gateways.values()) {
            binding.resetMessageMetrics();
        }
        _promotedService.resetMessageMetrics();
    }

    @Override
    public void recordMetrics(Exchange exchange) {
        final String gatewayName = exchange.getContext().getPropertyValue(ExchangeCompletionEvent.GATEWAY_NAME);
        if (gatewayName != null && _gateways.containsKey(gatewayName)) {
            _gateways.get(gatewayName).recordMetrics(exchange);
        }
        _promotedService.recordMetrics(exchange);
    }
}
