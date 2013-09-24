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

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.switchyard.ExchangeHandler;
import org.switchyard.Service;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceMetadata;
import org.switchyard.ServiceReference;
import org.switchyard.ServiceSecurity;
import org.switchyard.event.DomainShutdownEvent;
import org.switchyard.event.DomainStartupEvent;
import org.switchyard.event.EventObserver;
import org.switchyard.event.EventPublisher;
import org.switchyard.event.ReferenceRegistrationEvent;
import org.switchyard.event.ServiceRegistrationEvent;
import org.switchyard.internal.transform.BaseTransformerRegistry;
import org.switchyard.internal.validate.BaseValidatorRegistry;
import org.switchyard.metadata.InOutService;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.security.service.DefaultServiceDomainSecurity;
import org.switchyard.security.service.SecureServiceDomain;
import org.switchyard.security.service.ServiceDomainSecurity;
import org.switchyard.spi.Dispatcher;
import org.switchyard.spi.ExchangeBus;
import org.switchyard.spi.ServiceRegistry;
import org.switchyard.transform.TransformerRegistry;
import org.switchyard.validate.ValidatorRegistry;

/**
 * Implementation of ServiceDomain.
 */
public class DomainImpl implements SecureServiceDomain {

    private static Logger _logger = Logger.getLogger(DomainImpl.class);

    private final QName _name;
    private EventManager _eventManager;
    private ServiceRegistry _serviceRegistry;
    private ExchangeBus _exchangeBus;
    private TransformerRegistry _transformerRegistry;
    private ValidatorRegistry _validatorRegistry;
    private Map<String, Object> _properties = new ConcurrentHashMap<String, Object>();
    private ServiceDomainSecurity _serviceDomainSecurity;
    
    /**
     * Create a new ServiceDomain.  This is a convenience constructor which uses default
     * resources and initializes via init() in the constructor.  Do not use this in runtime
     * code!  This is for test/embedded use only.
     * @param name name
     */
    protected DomainImpl(QName name) {
        this(name,
            new DefaultServiceRegistry(),
            new LocalExchangeBus(),
            new BaseTransformerRegistry(),
            new BaseValidatorRegistry(),
            new EventManager(),
            new DefaultServiceDomainSecurity());
        init();
    }
    
    /**
     * Create a new ServiceDomain.
     * @param name name
     * @param serviceRegistry serviceRegistry
     * @param exchangeBus exchangeBus
     * @param transformerRegistry transformerRegistry
     * @param validatorRegistry validatorRegistry
     * @param eventManager eventManager
     * @param serviceDomainSecurity serviceDomainSecurity
     */
    public DomainImpl(QName name,
            ServiceRegistry serviceRegistry,
            ExchangeBus exchangeBus,
            TransformerRegistry transformerRegistry,
            ValidatorRegistry validatorRegistry,
            EventManager eventManager,
            ServiceDomainSecurity serviceDomainSecurity) {

        _name = name;
        _serviceRegistry = serviceRegistry;
        _exchangeBus  = exchangeBus;
        _transformerRegistry = transformerRegistry;
        _validatorRegistry = validatorRegistry;
        _eventManager = eventManager;
        _serviceDomainSecurity = serviceDomainSecurity;

        setEventPublisher(_transformerRegistry);
        setEventPublisher(_validatorRegistry);

        if (_logger.isDebugEnabled()) {
            _logger.debug("Created SwitchYard ServiceDomain instance '" + name + "'.");
        }
    }

    @Override
    public Service registerService(QName serviceName, ServiceInterface contract, 
            ExchangeHandler handler) {
        return registerService(serviceName, contract, handler, null);
    }

    @Override
    public Service registerService(QName serviceName,
            ServiceInterface contract, ExchangeHandler handler, ServiceMetadata metadata) {
        
        // If no service interface is provided, we default to InOutService
        if (contract == null) {
            contract = new InOutService();
        }
        // Create the service 
        Service service = new ServiceImpl(serviceName, contract, this, handler, metadata);
        // register the service
        _serviceRegistry.registerService(service);
        _eventManager.publish(new ServiceRegistrationEvent(service));
        return service;
    }

    @Override
    public ServiceReference registerServiceReference(QName serviceName,
            ServiceInterface contract) {
        return registerServiceReference(serviceName, contract, null);
    }

    @Override
    public ServiceReference registerServiceReference(QName serviceName,
            ServiceInterface contract, ExchangeHandler handler) {
        return registerServiceReference(serviceName, contract, handler, null);
    }
    
    @Override
    public ServiceReference registerServiceReference(QName serviceName,
            ServiceInterface contract, ExchangeHandler handler,  ServiceMetadata metadata) {
        ServiceReferenceImpl reference = new ServiceReferenceImpl(serviceName, contract, this, handler, metadata);
        Dispatcher dispatch = _exchangeBus.createDispatcher(reference);
        reference.setDispatcher(dispatch);
        _serviceRegistry.registerServiceReference(reference);
        _eventManager.publish(new ReferenceRegistrationEvent(reference));
        
        return reference;
    }
    
    @Override
    public ServiceReference getServiceReference(QName serviceName) {
        return _serviceRegistry.getServiceReference(serviceName);
    }
    
    @Override
    public QName getName() {
        return _name;
    }
    
    @Override
    public TransformerRegistry getTransformerRegistry() {
        return _transformerRegistry;
    }
    
    @Override
    public ValidatorRegistry getValidatorRegistry() {
        return _validatorRegistry;
    }
    
    @Override
    public List<Service> getServices() {
        return _serviceRegistry.getServices();
    }
       

    @Override
    public List<Service> getServices(QName serviceName) {
        return _serviceRegistry.getServices(serviceName);
    }
    
    /**
     * Convenient access to the domain's service registry.
     * @return service registry
     */
    public ServiceRegistry getServiceRegistry() {
        return _serviceRegistry;
    }
    
    /**
     * Convenient access to the domain's exchange bus.
     * @return exchange bus
     */
    public ExchangeBus getBus() {
        return _exchangeBus;
    }
    
    @Override
    public void init() {
        _eventManager.publish(new DomainStartupEvent(this));
        _exchangeBus.init(this);
        _exchangeBus.start();
    }
    
    @Override
    public void destroy() {
        _exchangeBus.stop();
        _eventManager.publish(new DomainShutdownEvent(this));
    }

    @Override
    public ServiceDomain addEventObserver(EventObserver observer, Class<? extends EventObject> eventType) {
        _eventManager.addObserver(observer, eventType);
        return this;
    }

    @Override
    public EventPublisher getEventPublisher() {
        return _eventManager;
    }

    @Override
    public Map<String, Object> getProperties() {
        return Collections.unmodifiableMap(_properties);
    }

    @Override
    public Object getProperty(String name) {
        return _properties.get(name);
    }

    @Override
    public ServiceDomain setProperty(String name, Object value) {
        if (value != null) {
            _properties.put(name, value);
        } else {
            _properties.remove(name);
        }
        return this;
    }

    private void setEventPublisher(Object target) {
        if (target == null) {
            return;
        }
        
        try {
            Method setter = target.getClass().getMethod("setEventPublisher", EventPublisher.class);
            setter.invoke(target, _eventManager);
        } catch (Exception ex) {
            _logger.debug("Attempt to set EventPublisher failed on " 
                    + target.getClass().getCanonicalName(), ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServiceSecurity getServiceSecurity(String name) {
        return getServiceDomainSecurity().getServiceSecurity(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServiceDomainSecurity getServiceDomainSecurity() {
        return _serviceDomainSecurity;
    }

    @Override
    public String toString() {
        return "ServiceDomain [name=" + _name + "]";
    }

}
