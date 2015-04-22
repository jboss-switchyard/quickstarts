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

package org.switchyard.deploy.cdi;

import java.io.IOException;
import java.io.InputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.BeforeShutdown;
import javax.enterprise.inject.spi.Extension;

import org.switchyard.ServiceDomain;
import org.switchyard.common.type.Classes;
import org.switchyard.deploy.ActivatorLoader;
import org.switchyard.deploy.ServiceDomainManager;
import org.switchyard.deploy.internal.AbstractDeployment;
import org.switchyard.deploy.internal.Deployment;

/**
 * Deployer implemented as a CDI extension.  The deployer kicks in after all
 * switchyard services and references have been discovered by the Bean component
 * CDI extension.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@ApplicationScoped
public class SwitchYardCDIDeployer implements Extension {

    private Deployment _deployment;

    /**
     * {@link AfterDeploymentValidation} CDI event observer.
     *
     * @param event         CDI Event instance.
     */
    public void afterDeploymentValidation(@Observes AfterDeploymentValidation event) {
        InputStream swConfigStream;
        try {
            swConfigStream = Classes.getResourceAsStream(AbstractDeployment.SWITCHYARD_XML, getClass());
        } catch (IOException ioe) {
            swConfigStream = null;
        }

        if (swConfigStream != null) {
            try {
                _deployment = new Deployment(swConfigStream);
            } catch (java.io.IOException ioEx) {
                throw CDIDeployMessages.MESSAGES.failedReadingConfig(ioEx);
            } finally {
                try {
                    swConfigStream.close();
                } catch (IOException ioEx) {
                    ioEx.getMessage(); // keeps checkstyle happy
                }
            }
            ServiceDomain domain = new ServiceDomainManager().createDomain();
            _deployment.init(domain, ActivatorLoader.createActivators(domain));
            _deployment.start();
        }
    }

    /**
     * {@link BeforeShutdown} CDI event observer.
     *
     * @param event       CDI Event instance.
     */
    public void beforeShutdown(@Observes BeforeShutdown event) {
        if (_deployment != null) {
            _deployment.stop();
            _deployment.destroy();
        }
    }
}
