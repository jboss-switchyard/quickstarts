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

package org.switchyard.deploy.cdi;

import java.io.IOException;
import java.io.InputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.BeforeShutdown;
import javax.enterprise.inject.spi.Extension;

import org.switchyard.common.type.Classes;
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
                _deployment = new Deployment(ServiceDomainManager.createDomain(), swConfigStream);
            } catch (java.io.IOException ioEx) {
                throw new RuntimeException("Failed while reading config stream.", ioEx);
            }
            _deployment.init();
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
