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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.namespace.QName;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.ExchangePattern;
import org.switchyard.MockDomain;
import org.switchyard.Service;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.common.type.Classes;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.ComponentImplementationModel;
import org.switchyard.config.model.switchyard.EsbInterfaceModel;
import org.switchyard.deploy.ActivatorLoader;
import org.switchyard.deploy.components.MockActivator;
import org.switchyard.deploy.components.config.MockBindingModel;
import org.switchyard.deploy.internal.transformers.ABTransformer;
import org.switchyard.deploy.internal.transformers.CDTransformer;
import org.switchyard.deploy.internal.validators.AValidator;
import org.switchyard.deploy.internal.validators.BValidator;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.extensions.wsdl.WSDLService;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.metadata.ServiceOperation;
import org.switchyard.transform.Transformer;
import org.switchyard.validate.Validator;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class DeploymentTest {

    @Test
    public void testEmptySwitchYardConfiguration() throws Exception {
        InputStream swConfigStream = Classes.getResourceAsStream("/switchyard-config-empty-01.xml", getClass());
        Deployment deployment = new Deployment(swConfigStream);
        swConfigStream.close();

        MockDomain serviceDomain = new MockDomain();
        deployment.init(serviceDomain, ActivatorLoader.createActivators(serviceDomain));
        deployment.destroy();
    }
    
    @Test
    public void testComponentReferenceBinding() throws Exception {
        InputStream swConfigStream = Classes.getResourceAsStream("/switchyard-config-component-reference-binding-01.xml", getClass());
        Deployment deployment = new Deployment(swConfigStream);
        swConfigStream.close();

        MockDomain serviceDomain = new MockDomain();
        deployment.init(serviceDomain, ActivatorLoader.createActivators(serviceDomain));
        
        boolean result = false;
        try {
            deployment.start();
        } catch (SwitchYardException sye) {
            Assert.assertTrue(sye.getMessage().contains("Component Reference bindings are not allowed.   Found "));
            result = true;
        } catch (Exception e) {
            Assert.fail("Expected to catch a SwitchYardException on deploy.");
        }
        Assert.assertTrue("Expected to catch a SwitchYardException on deploy.", result);
    }

    @Test
    public void testComponentServiceBinding() throws Exception {
        InputStream swConfigStream = Classes.getResourceAsStream("/switchyard-config-component-service-binding-01.xml", getClass());
        Deployment deployment = new Deployment(swConfigStream);
        swConfigStream.close();

        MockDomain serviceDomain = new MockDomain();
        deployment.init(serviceDomain, ActivatorLoader.createActivators(serviceDomain));
        boolean result = false;
        try {
            deployment.start();
        } catch (SwitchYardException sye) { 
            Assert.assertTrue(sye.getMessage().contains("Component Service bindings are not allowed.   Found"));
            result = true;
        } catch (Exception e) {
            Assert.fail("Expected to catch a SwitchYardException on deploy.");
        }
        Assert.assertTrue("Expected to catch a SwitchYardException on deploy.", result);
    }

    
    @Test
    public void testRegistrants() throws Exception {
        InputStream swConfigStream = Classes.getResourceAsStream("/switchyard-config-mock-01.xml", getClass());
        Deployment deployment = new Deployment(swConfigStream);
        swConfigStream.close();

        MockDomain serviceDomain = new MockDomain();
        deployment.init(serviceDomain, ActivatorLoader.createActivators(serviceDomain));
        deployment.start();
        
        // check metadata is included for services and references
        Service implService = deployment.getDomain().getServices(
                new QName("urn:test:config-mock-binding:1.0", "TestService")).get(0);
        Assert.assertNotNull(implService.getServiceMetadata().getRegistrant());
        Assert.assertTrue(implService.getServiceMetadata().getRegistrant().getConfig() instanceof ComponentImplementationModel);
        
        Service bindingService = deployment.getDomain().getServices(
                new QName("urn:test:config-mock-binding:1.0", "TestReference")).get(0);
        Assert.assertNotNull(bindingService.getServiceMetadata().getRegistrant());
        Assert.assertTrue(bindingService.getServiceMetadata().getRegistrant().getConfig() instanceof BindingModel);
        
        ServiceReference implReference = deployment.getDomain().getServiceReference(
                new QName("urn:test:config-mock-binding:1.0", "TestService/TestReference"));
        Assert.assertNotNull(implReference.getServiceMetadata().getRegistrant());
        Assert.assertTrue(implReference.getServiceMetadata().getRegistrant().getConfig() instanceof ComponentImplementationModel);
        
        ServiceReference bindingReference = deployment.getDomain().getServiceReference(
                new QName("urn:test:config-mock-binding:1.0", "TestService"));
        Assert.assertNotNull(bindingReference.getServiceMetadata().getRegistrant());
        Assert.assertTrue(bindingReference.getServiceMetadata().getRegistrant().getConfig() instanceof BindingModel);
    }
    
    @Test
    public void testActivators() throws Exception {
        InputStream swConfigStream = Classes.getResourceAsStream("/switchyard-config-mock-01.xml", getClass());
        Deployment deployment = new Deployment(swConfigStream);
        swConfigStream.close();

        MockDomain serviceDomain = new MockDomain();
        deployment.init(serviceDomain, ActivatorLoader.createActivators(serviceDomain));
        
        // Grab a reference to our activators
        MockActivator activator = (MockActivator)
            deployment.findActivator(MockBindingModel.TYPE);
        deployment.start();
        deployment.stop();
        deployment.destroy();

        // Verify the activators were poked
        Assert.assertTrue(activator.activateServiceCalled());
        Assert.assertTrue(activator.activateBindingCalled());
        Assert.assertTrue(activator.startCalled());
        Assert.assertTrue(activator.stopCalled());
        Assert.assertTrue(activator.deactivateServiceCalled());
        Assert.assertTrue(activator.deactivateBindingCalled());
    }
    
    @Test
    public void testActivationTypes() throws Exception {
        InputStream swConfigStream = Classes.getResourceAsStream("/switchyard-config-mock-01.xml", getClass());
        Deployment deployment = new Deployment(swConfigStream);
        swConfigStream.close();

        List<String> types = deployment.getActivationTypes();
        Assert.assertEquals(1, types.size());
        Assert.assertEquals("mock", types.iterator().next());
    }
    
    @Test
    public void test_transform_registration() throws Exception {
        InputStream swConfigStream = Classes.getResourceAsStream("/switchyard-config-transform-01.xml", getClass());
        Deployment deployment = new Deployment(swConfigStream);
        swConfigStream.close();

        MockDomain serviceDomain = new MockDomain();
        deployment.init(serviceDomain, ActivatorLoader.createActivators(serviceDomain));

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
    public void test_validate_registration() throws Exception {
        InputStream swConfigStream = Classes.getResourceAsStream("/switchyard-config-validate-01.xml", getClass());
        Deployment deployment = new Deployment(swConfigStream);
        swConfigStream.close();

        MockDomain serviceDomain = new MockDomain();
        deployment.init(serviceDomain, ActivatorLoader.createActivators(serviceDomain));

        // Check that the validators are deployed...
        ServiceDomain domain = deployment.getDomain();
        Validator<?> aValidator = domain.getValidatorRegistry().getValidator(new QName("A"));
        Validator<?> bValidator = domain.getValidatorRegistry().getValidator(new QName("B"));
        
        Assert.assertTrue(aValidator instanceof AValidator);
        Assert.assertTrue(bValidator instanceof BValidator);
        
        deployment.destroy();

        // Check that the validators are undeployed...
    }

    @Test
    public void interfaceWSDL() throws Exception {
        InputStream swConfigStream = Classes.getResourceAsStream("/switchyard-config-interface-wsdl-01.xml", getClass());
        Deployment deployment = new Deployment(swConfigStream);
        swConfigStream.close();
        MockDomain serviceDomain = new MockDomain();
        deployment.init(serviceDomain, ActivatorLoader.createActivators(serviceDomain));
        deployment.start();

        Service service = serviceDomain.getServiceRegistry().getServices(
                new QName("urn:switchyard-interface-wsdl", "HelloService")).get(0);
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
    public void interfaceESB() throws Exception {
        InputStream swConfigStream = Classes.getResourceAsStream("/switchyard-config-interface-esb-01.xml", getClass());
        Deployment deployment = new Deployment(swConfigStream);
        swConfigStream.close();
        MockDomain serviceDomain = new MockDomain();
        deployment.init(serviceDomain, ActivatorLoader.createActivators(serviceDomain));
        deployment.start();

        // Test the service
        Service service = serviceDomain.getServiceRegistry().getServices(
                new QName("urn:switchyard-interface-esb", "HelloService")).get(0);
        Assert.assertNotNull(service);
        ServiceInterface iface = service.getInterface();
        Assert.assertEquals(EsbInterfaceModel.ESB, iface.getType());
        ServiceOperation op = iface.getOperation(ServiceInterface.DEFAULT_OPERATION);
        Assert.assertNotNull(op);
        Assert.assertEquals(ExchangePattern.IN_ONLY, op.getExchangePattern());
        
        // Test the reference
        ServiceReference reference = serviceDomain.getServiceReference(
                new QName("urn:switchyard-interface-esb", "HelloService/SomeOtherService"));
        Assert.assertNotNull(reference);
        Assert.assertEquals(EsbInterfaceModel.ESB, service.getInterface().getType());
        ServiceOperation rop = reference.getInterface().getOperation(ServiceInterface.DEFAULT_OPERATION);
        Assert.assertNotNull(rop);
        Assert.assertEquals(ExchangePattern.IN_OUT, rop.getExchangePattern());

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
            MockDomain serviceDomain = new MockDomain();
            deployment.init(serviceDomain, ActivatorLoader.createActivators(serviceDomain));
            deployment.start();
        } catch (SwitchYardException sye) {
            exception = sye;
            System.err.println(sye.toString());
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

        MockDomain serviceDomain = new MockDomain();
        deployment.init(serviceDomain, ActivatorLoader.createActivators(serviceDomain));
        try {
            deployment.start();
            Assert.fail("Expected SwitchYardException");
        } catch (SwitchYardException e) {
            Assert.assertEquals("Failed to load Service interface class 'org.acme.Blah'.", e.getMessage());
        }
    }
    
    @Test
    public void componentServiceMultiplePromotions() throws Exception {
        InputStream swConfigStream = Classes.getResourceAsStream("/switchyard-config-multiple-promotions.xml", getClass());
        Deployment deployment = new Deployment(swConfigStream);
        swConfigStream.close();

        MockDomain serviceDomain = new MockDomain();
        deployment.init(serviceDomain, ActivatorLoader.createActivators(serviceDomain));
        deployment.start();
        
        // verify that two promotions with different names have been registered
        List<Service> test1svcs = deployment.getDomain().getServices(
                new QName("urn:test:config-mock-binding:1.0", "Test1"));
        Assert.assertEquals(1, test1svcs.size());
        List<Service> test2svcs = deployment.getDomain().getServices(
                new QName("urn:test:config-mock-binding:1.0", "Test2"));
        Assert.assertEquals(1, test2svcs.size());
        
        // make sure there's only one registration when component service name equals composite service name
        List<Service> testsvcs = deployment.getDomain().getServices(
                new QName("urn:test:config-mock-binding:1.0", "TestService"));
        Assert.assertEquals(1, testsvcs.size());
    }

    @Test
    public void testDuplicateReference() throws Exception {
        deployWithFail("/naming/duplicate-reference.xml");
    }

    @Test
    public void testReferencePromotion() throws Exception {
        deployWithoutFail("/naming/reference-promotion.xml");
    }

    @Test
    public void testDuplicatePromotion() throws Exception {
        deployWithFail("/naming/duplicate-promotion.xml");
    }

    @Test
    public void testPromotedService() throws Exception {
        deployWithoutFail("/naming/promoted-service.xml");
    }

    // helper methods
    private void deployWithoutFail(String name) throws IOException {
        InputStream swConfigStream = Classes.getResourceAsStream(name, getClass());
        Deployment deployment = new Deployment(swConfigStream);
        swConfigStream.close();

        MockDomain serviceDomain = new MockDomain();
        deployment.init(serviceDomain, ActivatorLoader.createActivators(serviceDomain));
        deployment.start();
        deployment.stop();
    }

    private void deployWithFail(String name) {
        try {
            deployWithoutFail(name);
            Assert.fail("Unexpected success of deployment " + name);
        } catch (IOException e) {
            Assert.fail("Can not read configuration " + name);
        } catch (SwitchYardException e) {
            // this is expected
            e.getMessage();
        }
    }
}
