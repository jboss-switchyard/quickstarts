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
package org.switchyard.component.camel.deploy;

import static org.switchyard.deploy.ServiceDomainManager.ROOT_DOMAIN;

import java.util.List;

import org.junit.Test;
import org.switchyard.ServiceDomain;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.deploy.Activator;
import org.switchyard.deploy.ActivatorLoader;
import org.switchyard.deploy.ServiceDomainManager;
import org.switchyard.deploy.internal.Deployment;
import org.switchyard.SwitchYardException;

/**
 * Test for {@link CamelActivator} and verifies that an exception is thrown during
 * the initalization phase if a service is referenced in a Camel route but a 
 * sca:reference is missing from the SwitchYard component configuration.
 * 
 * @author Daniel Bevenius
 */
public class CamelImplementationMissingRefTest {

    private static final String SWITCHYARD_CONFIG = "switchyard-activator-impl-missing-ref.xml";

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
