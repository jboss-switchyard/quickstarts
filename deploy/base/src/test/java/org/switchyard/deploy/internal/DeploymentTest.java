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
import org.switchyard.deploy.components.MockActivator;
import org.switchyard.deploy.components.config.MockBindingModel;
import org.switchyard.deploy.components.config.MockImplementationModel;
import org.switchyard.deploy.internal.transformers.ABTransformer;
import org.switchyard.deploy.internal.transformers.CDTransformer;
import org.switchyard.transform.Transformer;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class DeploymentTest {


    @Test
    public void testEmptySwitchYardConfiguration() {
        InputStream swConfigStream = getClass().getResourceAsStream("/switchyard-config-empty-01.xml");
        Deployment deployment = new Deployment(swConfigStream);

        deployment.init();
        deployment.destroy();
    }
    
    @Test
    public void testActivators() {
        InputStream swConfigStream = getClass().getResourceAsStream("/switchyard-config-mock-01.xml");
        Deployment deployment = new Deployment(swConfigStream);
        deployment.createGatewayActivator("mock", "org.switchyard.deploy.components.MockActivator");
        deployment.createComponentActivator("mock", "org.switchyard.deploy.components.MockActivator");
        deployment.init();
        
        // Grab a reference to our activators
        MockActivator gwActivator = (MockActivator)
            deployment.getGatewayActivator(MockBindingModel.TYPE);
        MockActivator implActivator = (MockActivator)
        deployment.getGatewayActivator(MockImplementationModel.TYPE);
        deployment.start();
        deployment.stop();
        deployment.destroy();

        // Verify the activators were poked
        Assert.assertTrue(gwActivator.initCalled());
        Assert.assertTrue(gwActivator.startCalled());
        Assert.assertTrue(gwActivator.stopCalled());
        Assert.assertTrue(gwActivator.destroyCalled());
        Assert.assertTrue(implActivator.initCalled());
        Assert.assertTrue(implActivator.startCalled());
        Assert.assertTrue(implActivator.stopCalled());
        Assert.assertTrue(implActivator.destroyCalled());
    }
    
    @Test
    public void test_transform_registration() {
        InputStream swConfigStream = getClass().getResourceAsStream("/switchyard-config-transform-01.xml");
        Deployment deployment = new Deployment(swConfigStream);

        deployment.init();

        // Check that the transformers are deployed...
        ServiceDomain domain = deployment.getDomain();
        Transformer<?,?> abTransformer = domain.getTransformerRegistry().getTransformer(new QName("http://www.switchyard.org", "a"), new QName("http://www.switchyard.org", "b"));
        Transformer<?,?> cdTransformer = domain.getTransformerRegistry().getTransformer(new QName("http://www.switchyard.org", "c"), new QName("http://www.switchyard.org", "d"));

        Assert.assertTrue(abTransformer instanceof ABTransformer);
        Assert.assertTrue(cdTransformer instanceof CDTransformer);

        deployment.destroy();

        // Check that the transformers are undeployed...
    }
}
