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
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.common.type.Classes;
import org.switchyard.deploy.ServiceDomainManager;
import org.switchyard.deploy.components.MockActivator;
import org.switchyard.deploy.components.config.MockBindingModel;
import org.switchyard.deploy.internal.transformers.ABTransformer;
import org.switchyard.deploy.internal.transformers.CDTransformer;
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

        deployment.init(ServiceDomainManager.createDomain());
        deployment.destroy();
    }
    
    @Test
    public void testActivators() throws Exception {
        InputStream swConfigStream = Classes.getResourceAsStream("/switchyard-config-mock-01.xml", getClass());
        Deployment deployment = new Deployment(swConfigStream);
        deployment.init(ServiceDomainManager.createDomain());
        
        // Grab a reference to our activators
        MockActivator activator = (MockActivator)
            deployment.getActivator(MockBindingModel.TYPE);
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
}
