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

package org.switchyard;

import javax.xml.namespace.QName;

import org.switchyard.internal.DomainImpl;
import org.switchyard.internal.EventManager;
import org.switchyard.internal.LocalExchangeBus;
import org.switchyard.internal.transform.BaseTransformerRegistry;
import org.switchyard.internal.validate.BaseValidatorRegistry;
import org.switchyard.metadata.InOnlyService;
import org.switchyard.metadata.InOutService;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.spi.ServiceRegistry;

public class MockDomain extends DomainImpl {
    
    public static final QName DEFAULT_DOMAIN = 
        new QName("urn:switchyard:test", "MockDomain");
    
    public MockDomain() {
        super(DEFAULT_DOMAIN);
    }
    
    public MockDomain(ServiceRegistry registry) {
        super(DEFAULT_DOMAIN,
                registry,
                new LocalExchangeBus(),
                new BaseTransformerRegistry(),
                new BaseValidatorRegistry(),
                new EventManager(),
                null);
    }
    
    public ServiceReference createInOnlyService(QName serviceName) {
        ServiceInterface inOnly = new InOnlyService();
        registerService(serviceName, inOnly, new MockHandler());
        ServiceReference reference = registerServiceReference(serviceName, inOnly);
        return reference;
    }
    

    public ServiceReference createInOnlyService(QName serviceName, ExchangeHandler handler) {
        ServiceInterface inOnly = new InOnlyService();
        registerService(serviceName, inOnly, handler);
        ServiceReference reference = registerServiceReference(serviceName, inOnly);
        return reference;
    }

    public ServiceReference createInOutService(QName serviceName) {
        ServiceInterface inOut = new InOutService();
        registerService(serviceName, inOut, new MockHandler());
        ServiceReference reference = registerServiceReference(serviceName, inOut);
        return reference;
    }

    public ServiceReference createInOutService(QName serviceName, ExchangeHandler handler) {
        ServiceInterface inOut = new InOutService();
        registerService(serviceName, inOut, handler);
        ServiceReference reference = registerServiceReference(serviceName, inOut);
        return reference;
    }
}
