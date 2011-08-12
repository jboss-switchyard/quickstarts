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

import java.util.List;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.switchyard.admin.Application;
import org.switchyard.admin.Binding;
import org.switchyard.admin.Component;
import org.switchyard.admin.ComponentService;
import org.switchyard.admin.ComponentType;
import org.switchyard.admin.Service;
import org.switchyard.config.model.composite.ComponentImplementationModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.config.model.composite.InterfaceModel;
import org.switchyard.deploy.internal.AbstractDeployment;

public class BaseSystemTest {

    private static final QName _testDeploymentName = new QName("TestDeployment");
    private BaseSwitchYard _switchYard;
    
    @Before
    public void setUp() {
        _switchYard = new BaseSwitchYard("1.0");
    }
    
    @Test
    public void testApplication() {
        BaseApplication app1 = new BaseApplication(_switchYard, new QName("app1"), null);
        BaseApplication app2 = new BaseApplication(_switchYard, new QName("app2"));
        
        _switchYard.addApplication(app1);
        app1.addService(new MockService(new QName("test1"), app1));
        Assert.assertEquals(1, app1.getServices().size());
        app1.addService(new MockService(new QName("test2"), app1));
        Assert.assertEquals(2, app1.getServices().size());
        Assert.assertEquals(2, _switchYard.getServices().size());

        _switchYard.addApplication(app2);
        app2.addService(new MockService(new QName("test3"), app2));
        Assert.assertEquals(2, _switchYard.getApplications().size());
        Assert.assertEquals(3, _switchYard.getServices().size());
        
        _switchYard.removeApplication(app1);
        Assert.assertEquals(app2, _switchYard.getApplications().get(0));
        Assert.assertEquals(1, _switchYard.getServices().size());
    }
    

    @Test
    public void testComponent() {
        Component c1 = new BaseComponent("c1", ComponentType.GATEWAY, null);
        Component c2 = new BaseComponent("c2", ComponentType.IMPLEMENTATION, null);
        
        _switchYard.addComponent(c1);
        _switchYard.addComponent(c2);
        Assert.assertEquals(2, _switchYard.getComponents().size());
        
        _switchYard.removeComponent(c1);
        Assert.assertEquals(c2, _switchYard.getComponents().get(0));
    }
    
    @Test
    public void testService() {
        Service s1 = new BaseService(new QName("s1"), null, null, null, null);
        Service s2 = new BaseService(new QName("s2"), null, null, null, null);
        
        _switchYard.addService(s1);
        _switchYard.addService(s2);
        Assert.assertEquals(2, _switchYard.getServices().size());
        
        _switchYard.removeService(s1);
        Assert.assertEquals(s2, _switchYard.getServices().get(0));
    }
    
    @Test
    public void testSwitchYardDeploymentListener() {
        AbstractDeployment deployment = new MockDeployment();
        SwitchYardBuilder listener = new SwitchYardBuilder(_switchYard);
        listener.initializing(deployment);
        Assert.assertEquals(1, _switchYard.getApplications().size());
        Assert.assertEquals(_testDeploymentName, _switchYard.getApplications().get(0).getName());

        QName serviceName = new QName("testService");
        CompositeServiceModel serviceModel = Mockito.mock(CompositeServiceModel.class);
        ComponentModel componentModel = Mockito.mock(ComponentModel.class);
        InterfaceModel interfaceModel = Mockito.mock(InterfaceModel.class);
        ComponentImplementationModel implementationModel = Mockito.mock(ComponentImplementationModel.class);
        Mockito.when(serviceModel.getQName()).thenReturn(serviceName);
        Mockito.when(serviceModel.getComponent()).thenReturn(componentModel);
        Mockito.when(serviceModel.getInterface()).thenReturn(interfaceModel);
        Mockito.when(componentModel.getImplementation()).thenReturn(implementationModel);
        Mockito.when(implementationModel.getType()).thenReturn("test");
        Mockito.when(interfaceModel.getInterface()).thenReturn("someInterface");

        listener.serviceDeployed(deployment, serviceModel);
        Assert.assertEquals(1, _switchYard.getApplications().get(0).getServices().size());
        Assert.assertEquals(serviceName, _switchYard.getApplications().get(0).getServices().get(0).getName());

        listener.serviceUndeployed(deployment, serviceName);
        Assert.assertEquals(0, _switchYard.getApplications().get(0).getServices().size());

        listener.destroyed(deployment);
        Assert.assertEquals(0, _switchYard.getApplications().size());
    }
    
    private static class MockService implements Service {

        private QName _name;
        private Application _application;

        public MockService(QName name, Application application) {
            _name = name;
            _application = application;
        }

        @Override
        public QName getName() {
            return _name;
        }

        @Override
        public ComponentService getPromotedService() {
            return null;
        }

        @Override
        public List<Binding> getGateways() {
            return null;
        }

        @Override
        public String getInterface() {
            return null;
        }

        @Override
        public Application getApplication() {
            return _application;
        }

    }

    private static class MockDeployment extends AbstractDeployment {

        public MockDeployment() {
            super(null);
        }

        @Override
        public QName getName() {
            return _testDeploymentName;
        }

        @Override
        protected void doInit() {
        }

        @Override
        protected void doStart() {
        }

        @Override
        protected void doStop() {
        }

        @Override
        protected void doDestroy() {
        }

    }
}
