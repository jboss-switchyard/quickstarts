/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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

package org.switchyard.test.mixins;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.junit.Assert;
import org.switchyard.deploy.internal.AbstractDeployment;
import org.switchyard.test.MockInitialContextFactory;
import org.switchyard.test.SimpleTestDeployment;

import javax.naming.NamingException;

/**
 * CDI Test Mix-In for deploying the Weld CDI Standalone Edition container.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class CDIMixIn extends AbstractTestMixIn {

    private Weld _weld;
    private AbstractDeployment _simpleCdiDeployment;

    @Override
    public void initialize() {
        // Deploy the weld container...
        _weld = new Weld();
        WeldContainer weldContainer = _weld.initialize();
        weldContainer.event().select(ContainerInitialized.class).fire(new ContainerInitialized());

        // And bind the BeanManager instance into java:comp...
        try {
            MockInitialContextFactory.getJavaComp().bind("BeanManager", weldContainer.getBeanManager());
        } catch (NamingException e) {
            Assert.fail("Failed to bind BeanManager into 'java:comp'.");
        }
    }

    @Override
    public void before(AbstractDeployment deployment) {
        if (deployment instanceof SimpleTestDeployment) {
            // Not a user defined configuration based test... deploy the Services and Transformers
            // found by the CDI discovery process...
            Class<? extends AbstractDeployment> simpleCdiDeploymentType = null;
            try {
                simpleCdiDeploymentType = (Class<? extends AbstractDeployment>) Class.forName("org.switchyard.component.bean.internal.SimpleCDIDeployment");
            } catch (ClassNotFoundException e) {
                Assert.fail("Failed to locate the SimpleCDIDeployment class on the classpath.  Module must include the SwitchYard Bean Component as one of its depedencies.");
            }
            try {
                _simpleCdiDeployment = simpleCdiDeploymentType.newInstance();
                _simpleCdiDeployment.setParentDeployment(deployment);
                _simpleCdiDeployment.init();
                _simpleCdiDeployment.start();
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail("Failed to manually deploy Bean Service.  Exception: " + e.getMessage());
            }

        }
    }

    @Override
    public void after(AbstractDeployment deployment) {
        if (_simpleCdiDeployment != null) {
            _simpleCdiDeployment.stop();
            _simpleCdiDeployment.destroy();
        }
    }

    @Override
    public void uninitialize() {
        _weld.shutdown();
    }
}
