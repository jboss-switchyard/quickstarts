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

package org.switchyard.deploy.internal;

import java.io.InputStream;

import javax.xml.namespace.QName;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.common.type.Classes;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.deploy.ServiceDomainManager;
import org.switchyard.deploy.components.MockActivator;
import org.switchyard.deploy.components.config.MockBindingModel;
import org.switchyard.deploy.internal.transformers.ABTransformer;
import org.switchyard.deploy.internal.transformers.CDTransformer;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.extensions.wsdl.WSDLService;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.metadata.ServiceOperation;
import org.switchyard.transform.Transformer;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class DeploymentTest {

    @Test
    public void testEmptySwitchYardConfiguration() throws Exception {
        InputStream swConfigStream = Classes.getResourceAsStream("/switchyard-config-empty-01.xml", getClass());
        Deployment deployment = new Deployment(swConfigStream);
        swConfigStream.close();

        deployment.init(ServiceDomainManager.createDomain());
        deployment.destroy();
    }
    
    @Test
    public void testActivators() throws Exception {
        InputStream swConfigStream = Classes.getResourceAsStream("/switchyard-config-mock-01.xml", getClass());
        Deployment deployment = new Deployment(swConfigStream);
        swConfigStream.close();

        deployment.init(ServiceDomainManager.createDomain());
        
        // Grab a reference to our activators
        MockActivator activator = (MockActivator)
            deployment.findActivator(MockBindingModel.TYPE);
        deployment.start();
        deployment.stop();
        deployment.destroy();

        // Verify the activators were poked
        Assert.assertTrue(activator.initCalled());
        Assert.assertTrue(activator.startCalled());
        Assert.assertTrue(activator.stopCalled());
        Assert.assertTrue(activator.destroyCalled());
    }
    
    @Test
    public void test_transform_registration() throws Exception {
        InputStream swConfigStream = Classes.getResourceAsStream("/switchyard-config-transform-01.xml", getClass());
        Deployment deployment = new Deployment(swConfigStream);
        swConfigStream.close();

        deployment.init(ServiceDomainManager.createDomain());

        // Check that the transformers are deployed...
        ServiceDomain domain = deployment.getDomain();
        Transformer<?,?> abTransformer = domain.getTransformerRegistry().getTransformer(new QName("A"), new QName("B"));
        Transformer<?,?> cdTransformer = domain.getTransformerRegistry().getTransformer(new QName("C"), new QName("D"));

        Assert.assertTrue(abTransformer instanceof ABTransformer);
        Assert.assertTrue(cdTransformer instanceof CDTransformer);

        deployment.destroy();

        // Check that the transformers are undeployed...
    }

    @Test
    public void interfaceWSDL() throws Exception {
        InputStream swConfigStream = Classes.getResourceAsStream("/switchyard-config-interface-wsdl-01.xml", getClass());
        Deployment deployment = new Deployment(swConfigStream);
        swConfigStream.close();
        deployment.init(ServiceDomainManager.createDomain());
        deployment.start();

        // FIXME: new QName("urn:switchyard-interface-wsdl", "HelloService") does not work
        ServiceReference service = deployment.getDomain().getService(new QName("HelloService"));
        Assert.assertNotNull(service);
        ServiceInterface iface = service.getInterface();
        Assert.assertEquals(WSDLService.TYPE, iface.getType());
        ServiceOperation op = iface.getOperation("sayHello");
        Assert.assertNotNull(op);
        Assert.assertEquals(new QName("urn:switchyard-interface-wsdl", "sayHello"), op.getInputType());
        Assert.assertEquals(new QName("urn:switchyard-interface-wsdl", "sayHelloResponse"), op.getOutputType());

        deployment.stop();
        deployment.destroy();

    }
    
    @Test
    public void nonExistentActivatorThrowsException() throws Exception {
        InputStream swConfigStream = null;
        SwitchYardException exception = null;
        
        // Load an app config which references a mock component, but provide no activator
        try {
            swConfigStream = Classes.getResourceAsStream("/switchyard-config-activator-01.xml", getClass());
            Deployment deployment = new Deployment(swConfigStream);
            deployment.init(ServiceDomainManager.createDomain());
            deployment.start();
        } catch (SwitchYardException sye) {
            exception = sye;
            System.out.println(sye.toString());
        } finally {
            if (swConfigStream != null) {
                swConfigStream.close();
            }
        }
        
        Assert.assertNotNull("Missing activator did not trigger SwitchYardException!", exception);
    }

    @Test
    public void testUnknownInterfaceClassName() throws Exception {
        InputStream swConfigStream = Classes.getResourceAsStream("/switchyard-config-unknown-interface.xml", getClass());
        Deployment deployment = new Deployment(swConfigStream);
        swConfigStream.close();

        deployment.init(ServiceDomainManager.createDomain());
        try {
            deployment.start();
            Assert.fail("Expected SwitchYardException");
        } catch (SwitchYardException e) {
            Assert.assertEquals("Failed to load Service interface class 'org.acme.Blah'.", e.getMessage());
        }
    }

    @Test
    public void testNotifications() throws Exception {
        InputStream swConfigStream = Classes.getResourceAsStream("/switchyard-config-mock-01.xml", getClass());
        Deployment deployment = new Deployment(swConfigStream);
        swConfigStream.close();

        DeploymentListener listener = Mockito.mock(DeploymentListener.class);
        deployment.addDeploymentListener(listener);

        deployment.init(ServiceDomainManager.createDomain());
        deployment.start();
        deployment.stop();
        deployment.destroy();

        Mockito.verify(listener).initializing(deployment);
        Mockito.verify(listener).initialized(deployment);
        Mockito.verify(listener).starting(deployment);
        Mockito.verify(listener, Mockito.times(1)).serviceDeployed(deployment,
                deployment.getConfig().getComposite().getServices().get(0));
        Mockito.verify(listener).started(deployment);
        Mockito.verify(listener).stopping(deployment);
        Mockito.verify(listener).stopped(deployment);
        Mockito.verify(listener).destroying(deployment);
        Mockito.verify(listener).destroyed(deployment);
    }
    
    @Test
    public void testListenerThrowing() throws Exception {
        InputStream swConfigStream = Classes.getResourceAsStream("/switchyard-config-mock-01.xml", getClass());
        Deployment deployment = new Deployment(swConfigStream);
        swConfigStream.close();

        DeploymentListener listener = Mockito.mock(DeploymentListener.class);
        Mockito.doThrow(new RuntimeException("error")).when(listener).initializing(deployment);
        Mockito.doThrow(new RuntimeException("error")).when(listener).initialized(deployment);
        Mockito.doThrow(new RuntimeException("error")).when(listener).starting(deployment);
        Mockito.doThrow(new RuntimeException("error")).when(listener)
                .serviceDeployed(Mockito.any(Deployment.class), Mockito.any(CompositeServiceModel.class));
        Mockito.doThrow(new RuntimeException("error")).when(listener).started(deployment);
        Mockito.doThrow(new RuntimeException("error")).when(listener).stopping(deployment);
        Mockito.doThrow(new RuntimeException("error")).when(listener).stopped(deployment);
        Mockito.doThrow(new RuntimeException("error")).when(listener).destroying(deployment);
        Mockito.doThrow(new RuntimeException("error")).when(listener).destroyed(deployment);
        deployment.addDeploymentListener(listener);

        deployment.init(ServiceDomainManager.createDomain());
        deployment.start();
        deployment.stop();
        deployment.destroy();

        Mockito.verify(listener).initializing(deployment);
        Mockito.verify(listener).initialized(deployment);
        Mockito.verify(listener).starting(deployment);
        Mockito.verify(listener, Mockito.times(1)).serviceDeployed(deployment,
                deployment.getConfig().getComposite().getServices().get(0));
        Mockito.verify(listener).started(deployment);
        Mockito.verify(listener).stopping(deployment);
        Mockito.verify(listener).stopped(deployment);
        Mockito.verify(listener).destroying(deployment);
        Mockito.verify(listener).destroyed(deployment);
    }

    @Test
    public void testMockExtenders() throws Exception {
        AbstractDeployment mock = new AbstractDeployment(null) {
            
            @Override
            protected void doStop() {
            }
            
            @Override
            protected void doStart() {
            }
            
            @Override
            protected void doInit() {
            }
            
            @Override
            protected void doDestroy() {
            }
        };
        
        mock.init(ServiceDomainManager.createDomain());
        mock.start();
        mock.stop();
        mock.destroy();
    }
}
