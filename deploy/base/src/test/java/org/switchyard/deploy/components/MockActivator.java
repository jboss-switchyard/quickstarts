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
