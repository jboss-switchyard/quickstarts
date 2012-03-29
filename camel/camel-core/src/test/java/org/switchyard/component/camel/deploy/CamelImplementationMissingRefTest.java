/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.camel.deploy;

import static org.switchyard.deploy.ServiceDomainManager.ROOT_DOMAIN;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.switchyard.ServiceDomain;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.deploy.Activator;
import org.switchyard.deploy.ActivatorLoader;
import org.switchyard.deploy.ServiceDomainManager;
import org.switchyard.deploy.internal.Deployment;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.test.JBossASNamingServiceInstaller;
import org.switchyard.test.mixins.CDIMixIn;

/**
 * Test for {@link CamelActivator} and verifies that an exception is thrown during
 * the initalization phase if a service is referenced in a Camel route but a 
 * sca:reference is missing from the SwitchYard component configuration.
 * 
 * @author Daniel Bevenius
 * 
 */
public class CamelImplementationMissingRefTest {
    
    private static final String SWITCHYARD_CONFIG = "switchyard-activator-impl-missing-ref.xml";
    
    @BeforeClass
    public static void installContext() {
        JBossASNamingServiceInstaller.install();
        new CDIMixIn().initialize();
    }
    
    @AfterClass
    public static void clearContext() {
        JBossASNamingServiceInstaller.clear();
    }

    @Test (expected = SwitchYardException.class)
    public void should_throw_if_serviceReference_is_missing() throws Exception {
        final SwitchYardModel model = pullSwitchYardModel();
        final Deployment deployment = new Deployment(model);
        final ServiceDomain domain = new ServiceDomainManager().createDomain(ROOT_DOMAIN, deployment.getConfig());
        final List<Activator> activators = ActivatorLoader.createActivators(domain);
        deployment.init(domain, activators);
        deployment.start();
    }
    
    private SwitchYardModel pullSwitchYardModel() throws Exception {
        return new ModelPuller<SwitchYardModel>().pull(getClass().getResourceAsStream(SWITCHYARD_CONFIG));
    }
}
