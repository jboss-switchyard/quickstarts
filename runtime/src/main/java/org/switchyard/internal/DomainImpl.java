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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.switchyard.ExchangeHandler;
import org.switchyard.Service;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.event.DomainShutdownEvent;
import org.switchyard.event.DomainStartupEvent;
import org.switchyard.event.EventObserver;
import org.switchyard.event.EventPublisher;
import org.switchyard.event.ReferenceRegistrationEvent;
import org.switchyard.event.ReferenceUnregistrationEvent;
import org.switchyard.event.ServiceRegistrationEvent;
import org.switchyard.internal.transform.BaseTransformerRegistry;
import org.switchyard.internal.validate.BaseValidatorRegistry;
import org.switchyard.metadata.InOutService;
import org.switchyard.metadata.Registrant;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.policy.Policy;
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
    private ServiceRegistry _registry;
    private ExchangeBus _exchangeBus;
    private TransformerRegistry _transformerRegistry;
    private ValidatorRegistry _validatorRegistry;
    private List<ExchangeHandler> _userHandlers = new LinkedList<ExchangeHandler>();
    private Map<String, Object> _attachements = Collections.synchronizedMap(new LinkedHashMap<String, Object>());
    private Map<QName, ServiceReference> _references =
            new ConcurrentHashMap<QName, ServiceReference>();

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
            new EventManager());
        // this constructor is used for tests, normally exchange bus can listen
        // for domain events
//        ((LocalExchangeBus) _exchangeBus).init(this);
    }
    
    /**
     * Create a new ServiceDomain.
     * @param name name
     * @param registry registry
     * @param exchangeBus message exchange bus
     * @param transformerRegistry transformerRegistry
     * @param validatorRegistry validatorRegistry
     * @param eventManager event manager
     */
    public DomainImpl(QName name,
            ServiceRegistry registry,
            ExchangeBus exchangeBus,
            TransformerRegistry transformerRegistry,
            ValidatorRegistry validatorRegistry,
            EventManager eventManager) {

        _name = name;
        _registry = registry;
        _exchangeBus  = exchangeBus;
        _transformerRegistry = transformerRegistry;
        _validatorRegistry = validatorRegistry;
        _eventManager = eventManager;

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
    public Service registerService(QName serviceName, ServiceInterface metadata, 
            ExchangeHandler handler) {
        List<Policy> requires = Collections.emptyList();
        return registerService(serviceName, metadata, handler, requires, null);
    }

    @Override
    public Service registerService(QName serviceName,
            ServiceInterface metadata, ExchangeHandler handler, List<Policy> requires, Registrant owner) {
        
        // If no service interface is provided, we default to InOutService
        if (metadata == null) {
            metadata = new InOutService();
        }
        // Create the service 
        Service service = new ServiceImpl(serviceName, metadata, this, handler, requires, owner);
        // register the service
        _registry.registerService(service);
        _eventManager.publish(new ServiceRegistrationEvent(service));
        return service;
    }

    @Override
    public ServiceReference registerServiceReference(QName serviceName,
            ServiceInterface metadata) {
        return registerServiceReference(serviceName, metadata, null, null, null);
    }

    @Override
    public ServiceReference registerServiceReference(QName serviceName,
            ServiceInterface metadata, ExchangeHandler handler) {
        return registerServiceReference(serviceName, metadata, handler, null, null);
    }
    
    @Override
    public ServiceReference registerServiceReference(QName serviceName,
            ServiceInterface metadata, ExchangeHandler handler, List<Policy> provides, Registrant owner) {
        ServiceReferenceImpl reference = new ServiceReferenceImpl(serviceName, metadata, this, handler, provides, owner);
        Dispatcher dispatch = _exchangeBus.createDispatcher(reference);
        reference.setDispatcher(dispatch);
        _references.put(serviceName, reference);
        _eventManager.publish(new ReferenceRegistrationEvent(reference));
        
        return reference;
    }
    
    @Override
    public ServiceReference getServiceReference(QName serviceName) {
        return _references.get(serviceName);
    }
    
    /**
     * Unregisters a service reference from the domain.
     * @param reference the reference to unregister
     */
    public void unregisterServiceReference(ServiceReference reference) {
        _references.remove(reference.getName());
        _eventManager.publish(new ReferenceUnregistrationEvent(reference));
    }

    @Override
    public void wireReference(ServiceReference reference,
            org.switchyard.Service service) {
        // TODO Auto-generated method stub
        
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
         return _registry.getServices();
     }
       

     @Override
     public List<Service> getServices(QName serviceName) {
         return _registry.getServices(serviceName);
     }
    
    /**
     * Convenient access to the domain's service registry.
     * @return service registry
     */
    public ServiceRegistry getServiceRegistry() {
        return _registry;
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
        _references.clear();
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
    public List<ExchangeHandler> getHandlers() {
        return _userHandlers;
    }

    @Override
    public Map<String, Object> getProperties() {
        return _attachements;
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
}
