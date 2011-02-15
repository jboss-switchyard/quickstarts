/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.switchyard;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.junit.After;
import org.junit.BeforeClass;
import org.switchyard.deploy.internal.AbstractDeployment;

/**
 * Base class for writing SwitchYard Bean @Service tests.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public abstract class SwitchYardCDITestCase extends SwitchYardTestCase {

    /**
     * Deployment runtime type.
     */
    private static Class<? extends AbstractDeployment> _cdiDeploymentTyoe;

    /**
     * Initialise the context and create teh deployment runtime type..
     * @throws ClassNotFoundException No class found.
     */
    @BeforeClass
    public static void installContextAndRuntimeDeploymentType() throws ClassNotFoundException {
        MockInitialContextFactory.install();
        // Performing this reflectively at runtime because we can't have a dependency on the bean component.
        _cdiDeploymentTyoe = (Class<? extends AbstractDeployment>) Class.forName("org.switchyard.component.bean.internal.SimpleCDIDeployment");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractDeployment createDeployment() throws InstantiationException, IllegalAccessException {
        deployWeldContainer();
        return _cdiDeploymentTyoe.newInstance();
    }

    /**
     * Clear the mock context.
     */
    @After
    public void clearMockContext() {
        MockInitialContextFactory.clear();
    }

    private void deployWeldContainer() {
        WeldContainer weld = new Weld().initialize();
        weld.event().select(ContainerInitialized.class).fire(new ContainerInitialized());
    }
}
