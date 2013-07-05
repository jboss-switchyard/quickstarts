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

package org.switchyard.deploy.components;

import javax.xml.namespace.QName;

import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.deploy.BaseActivator;
import org.switchyard.deploy.BaseServiceHandler;
import org.switchyard.deploy.ServiceHandler;

public class MockActivator extends BaseActivator {

    public static final String ACTIVATION_TYPE = "mock";
    
    private boolean _activateBindingCalled;
    private boolean _activateServiceCalled;
    private boolean _deactivateBindingCalled;
    private boolean _deactivateServiceCalled;
    private boolean _startCalled;
    private boolean _stopCalled;
    
    private ServiceHandler _handler = new MockServiceHandler();
    
    public MockActivator() {
        super(ACTIVATION_TYPE);
    }
    
    @Override
    public ServiceHandler activateBinding(QName serviceName, BindingModel config) {
        _activateBindingCalled = true;
        return _handler;
    }
    
    @Override
    public ServiceHandler activateService(QName serviceName, ComponentModel config) {
        _activateServiceCalled = true;
        return _handler;
    }
    
    
    @Override
    public void deactivateBinding(QName name, ServiceHandler handler) {
        _deactivateBindingCalled = true;
    }

    @Override
    public void deactivateService(QName name, ServiceHandler handler) {
        _deactivateServiceCalled = true;
    }

    public boolean startCalled() {
        return _startCalled;
    }
    
    public boolean stopCalled() {
        return _stopCalled;
    }
    
    public boolean activateBindingCalled() {
        return _activateBindingCalled;
    }
    
    public boolean activateServiceCalled() {
        return _activateServiceCalled;
    }
    
    public boolean deactivateBindingCalled() {
        return _deactivateBindingCalled;
    }
    
    public boolean deactivateServiceCalled() {
        return _deactivateServiceCalled;
    }
    
    class MockServiceHandler extends BaseServiceHandler {
        @Override
        public void doStart() {
            _startCalled = true;
        }
        
        @Override
        public void doStop() {
            _stopCalled = true;
        }
    }
}
