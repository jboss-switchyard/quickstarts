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

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
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
import org.switchyard.spi.Dispatcher;
import org.switchyard.spi.ExchangeBus;
import org.switchyard.spi.ServiceRegistry;
import org.switchyard.transform.TransformerRegistry;
import org.switchyard.validate.ValidatorRegistry;

/**
 * Implementation of ServiceDomain.
 */
public class DomainImpl implements ServiceDomain {

    private static Logger _logger = Logger.getLogger(DomainImpl.class);

    private final QName _name;
    private EventManager _eventManager;
    private ServiceRegistry _serviceRegistry;
    private ExchangeBus _exchangeBus;
    private TransformerRegistry _transformerRegistry;
    private ValidatorRegistry _validatorRegistry;
    private Map<String, Object> _properties = new ConcurrentHashMap<String, Object>();
    private Map<String, ServiceSecurity> _serviceSecurities = new ConcurrentHashMap<String, ServiceSecurity>();
    
    /**
     * Create a new ServiceDomain.
     * @param name name
     */
    public DomainImpl(QName name) {
        this(name,
            new DefaultServiceRegistry(),
            new LocalExchangeBus(),
            new BaseTransformerRegistry(),
            new BaseValidatorRegistry(),
            new EventManager(),
            null);
        // this constructor is used for tests, normally exchange bus can listen for domain events
        // ((LocalExchangeBus) _exchangeBus).init(this);
    }
    
    /**
     * Create a new ServiceDomain.
     * @param name name
     * @param serviceRegistry serviceRegistry
     * @param exchangeBus exchangeBus
     * @param transformerRegistry transformerRegistry
     * @param validatorRegistry validatorRegistry
     * @param eventManager eventManager
     * @param serviceSecurities serviceSecurities
     */
    public DomainImpl(QName name,
            ServiceRegistry serviceRegistry,
            ExchangeBus exchangeBus,
            TransformerRegistry transformerRegistry,
            ValidatorRegistry validatorRegistry,
            EventManager eventManager,
            Map<String, ServiceSecurity> serviceSecurities) {

        _name = name;
        _serviceRegistry = serviceRegistry;
        _exchangeBus  = exchangeBus;
        _transformerRegistry = transformerRegistry;
        _validatorRegistry = validatorRegistry;
        _eventManager = eventManager;

        if (serviceSecurities != null) {
            _serviceSecurities.putAll(serviceSecurities);
        }
        if (!_serviceSecurities.containsKey(ServiceSecurity.DEFAULT_NAME)) {
            _serviceSecurities.put(ServiceSecurity.DEFAULT_NAME, new DefaultServiceSecurity());
        }

        setEventPublisher(_transformerRegistry);
        setEventPublisher(_validatorRegistry);

        _eventManager.publish(new DomainStartupEvent(this));
        _exchangeBus.init(this);
        _exchangeBus.start();

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
        _properties.put(name, value);
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
        ServiceSecurity serviceSecurity = name != null ? _serviceSecurities.get(name) : null;
        if (serviceSecurity == null) {
            serviceSecurity = _serviceSecurities.get(ServiceSecurity.DEFAULT_NAME);
        }
        return serviceSecurity;
    }

    @Override
    public String toString() {
        return "ServiceDomain [name=" + _name + "]";
    }

}
