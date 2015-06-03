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

package org.switchyard.admin.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.switchyard.Exchange;
import org.switchyard.admin.Application;
import org.switchyard.admin.Binding;
import org.switchyard.admin.Reference;
import org.switchyard.admin.ServiceOperation;
import org.switchyard.common.type.Classes;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.config.model.composite.InterfaceModel;
import org.switchyard.config.model.switchyard.EsbInterfaceModel;
import org.switchyard.extensions.java.JavaService;
import org.switchyard.extensions.wsdl.WSDLReaderException;
import org.switchyard.extensions.wsdl.WSDLService;
import org.switchyard.runtime.event.ExchangeCompletionEvent;

/**
 * Base implementation for Reference.
 */
public class BaseReference extends BaseMessageMetricsAware implements Reference {
    
    private QName _name;
    private String _referenceInterface;
    private BaseApplication _application;
    private String _promotedReference;
    private Map<String, Binding> _gateways = new LinkedHashMap<String, Binding>();
    private List<ServiceOperation> _operations = new LinkedList<ServiceOperation>();;
    
    /**
     * Create a new BaseReference.
     * 
     * @param name the name of the reference.
     * @param referenceInterface the interface implemented by the reference.
     * @param application the application containing the reference.
     * @param reference the component reference.
     * @param gateways the gateway types exposing the reference.
     */
    public BaseReference(QName name,
            String referenceInterface,
            BaseApplication application, 
            String reference,
            Map<String, Binding> gateways) {
        
        _name = name;
        _referenceInterface = referenceInterface;
        _application = application;
        _promotedReference = reference;
        _gateways = gateways;
    }
    
    /**
     * Create a new BaseReference from the specified config model.
     * 
     * @param referenceConfig the composite reference config.
     * @param application the application containing the service.
     */
    public BaseReference(CompositeReferenceModel referenceConfig, BaseApplication application) {
        _name = referenceConfig.getQName();
        _application = application;
        if (referenceConfig.getInterface() != null) {
            _referenceInterface = referenceConfig.getInterface().getInterface();
        }
        _promotedReference = referenceConfig.getPromote();
        _gateways = new LinkedHashMap<String, Binding>();

        int idx = 0;
        for (BindingModel bindingModel : referenceConfig.getBindings()) {
            // Generate binding name for now until tooling and config are updated to expose it
            ++idx;
            String name = bindingModel.getName() == null ? "_" + _name.getLocalPart() + "_" + bindingModel.getType()
                    + "_" + idx : bindingModel.getName();
            _gateways.put(name, new BaseBinding(_application, _name, bindingModel.getType(), name, bindingModel.toString()));
        }
        _operations = new LinkedList<ServiceOperation>();
        for (org.switchyard.metadata.ServiceOperation so : getInterfaceOperations(referenceConfig.getInterface())) {
            _operations.add(new BaseServiceOperation(so.getName(), so.getExchangePattern().name(), so.getInputType(), so.getOutputType(), so.getFaultType()));
        }
    }

    private Collection<org.switchyard.metadata.ServiceOperation> getInterfaceOperations(InterfaceModel interfaceModel) {
        if (interfaceModel == null || EsbInterfaceModel.ESB.equals(interfaceModel.getType())) { 
            return Collections.emptySet();
        } else if (InterfaceModel.JAVA.equals(interfaceModel.getType())) {
            return JavaService.fromClass(Classes.forName(interfaceModel.getInterface())).getOperations();
        } else if (InterfaceModel.WSDL.equals(interfaceModel.getType())) {
            try {
                return WSDLService.fromWSDL(interfaceModel.getInterface()).getOperations();
            } catch (WSDLReaderException e) {
                // not sure what should do here.. - service should not be deployed
                // so we won't collect metrics for it anyway
                return Collections.emptySet();
            }
        }
        return Collections.emptySet();
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
    public String getPromotedReference() {
       return _promotedReference;
    }

    @Override
    public String getInterface() {
        return _referenceInterface;
    }

    @Override
    public QName getName() {
        return _name;
    }
    
    @Override
    public List<ServiceOperation> getServiceOperations() {
        return Collections.unmodifiableList(_operations);
    }

    @Override
    public ServiceOperation getServiceOperation(String operation) {
        for (ServiceOperation serviceOperation : _operations) {
            if (serviceOperation.getName().equals(operation)) {
                return serviceOperation;
            }
        }
        return null;
    }

    @Override
    public void resetMessageMetrics() {
        for (final Binding binding : _gateways.values()) {
            binding.resetMessageMetrics();
        }
        for (final ServiceOperation operation : _operations) {
            operation.resetMessageMetrics();
        }
        super.resetMessageMetrics();
    }

    @Override
    public void recordMetrics(Exchange exchange) {
        final String gatewayName = exchange.getContext().getPropertyValue(ExchangeCompletionEvent.GATEWAY_NAME);
        if (gatewayName != null && _gateways.containsKey(gatewayName)) {
            _gateways.get(gatewayName).recordMetrics(exchange);
        }
        final String operationName = exchange.getContract().getProviderOperation().getName();
        if (operationName != null) {
            for (final ServiceOperation operation : _operations) {
                if (operationName.equals(operation.getName())) {
                    operation.recordMetrics(exchange);
                    break;
                }
            }
        }
        super.recordMetrics(exchange);
    }
}
