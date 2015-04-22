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

package org.switchyard.bus.camel;

import javax.xml.namespace.QName;

import org.switchyard.ExchangeHandler;
import org.switchyard.Service;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceMetadata;
import org.switchyard.metadata.InOnlyService;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.metadata.ServiceMetadataBuilder;

public class MockService implements Service {
    
    private QName _serviceName;
    private ServiceInterface _serviceInterface;
    private ExchangeHandler _handler;
    
    public MockService(QName serviceName, ExchangeHandler handler) {
        this(serviceName, new InOnlyService(), handler);
    }
    
    public MockService(QName serviceName, ServiceInterface serviceInterface, ExchangeHandler handler) {
        _serviceName = serviceName;
        _serviceInterface = serviceInterface;
        _handler = handler;
    }

    @Override
    public QName getName() {
        return _serviceName;
    }

    @Override
    public ServiceInterface getInterface() {
        return _serviceInterface;
    }

    @Override
    public void unregister() {
        
    }

    @Override
    public ServiceDomain getDomain() {
        return null;
    }

    @Override
    public ExchangeHandler getProviderHandler() {
        return _handler;
    }

    @Override
    public ServiceMetadata getServiceMetadata() {
        return ServiceMetadataBuilder.create().build();
    }

}
