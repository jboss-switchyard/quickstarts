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
package org.switchyard.component.bean.internal;

import java.lang.annotation.Annotation;
import java.util.Set;

import org.switchyard.Exchange;
import org.switchyard.ServiceReference;
import org.switchyard.component.bean.ReferenceInvocation;
import org.switchyard.component.bean.ReferenceInvoker;
import org.switchyard.metadata.ServiceInterface;

/**
 * Bean class used to inject a ReferenceInvoker for an @Reference in a bean class.
 */
public class ReferenceInvokerBean extends InternalBean implements ReferenceInvoker {

    private final String _serviceName;
    private ServiceReference _reference;
    
    /**
     * Creates a new ReferenceInvokerBean.
     * @param serviceName name of the service reference
     * @param qualifiers any qualifiers associated with the injection point
     */
    public ReferenceInvokerBean(String serviceName, Set<Annotation> qualifiers) {
        super(ReferenceInvoker.class, qualifiers);
        setProxyObject(this);
        _serviceName = serviceName;
    }
    
    /**
     * Get the name of the service used by the invoker.
     * @return service name
     */
    public String getServiceName() {
        return _serviceName;
    }
    
    /**
     * Set the service reference for the target service.
     * @param reference The target service.
     */
    public void setReference(ServiceReference reference) {
        _reference = reference;
    }
    
    /**
     * Returns the service reference used by this invoker.
     * @return target reference
     */
    public ServiceReference getReference() {
        return _reference;
    }
    
    @Override
    public ReferenceInvocation newInvocation() {
        assertReference();
        ServiceInterface intf = _reference.getInterface();
        if (intf.getOperations().size() != 1) {
            throw new IllegalStateException("Operation name is required to create ReferenceInvocation for " + _serviceName);
        }
        
        return newInvocation(intf.getOperations().iterator().next().getName());
    }

    @Override
    public ReferenceInvocation newInvocation(String operation) {
        assertReference();
        InvocationResponseHandler handler = new InvocationResponseHandler();
        Exchange exchange = _reference.createExchange(operation, handler);
        return new ExchangeInvocation(exchange, handler);
    }

    @Override
    public ServiceInterface getContract() {
        return _reference.getInterface();
    }
    
    private void assertReference() throws IllegalStateException {
        if (_reference == null) {
            throw new IllegalStateException("Reference does not exist for service: " + _serviceName);
        }
    }

}

