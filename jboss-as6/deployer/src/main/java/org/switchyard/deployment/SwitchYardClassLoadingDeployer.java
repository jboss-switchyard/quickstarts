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

package org.switchyard.deployment;

import org.apache.log4j.Logger;
import org.jboss.classloading.spi.metadata.ClassLoadingMetaData;
import org.jboss.classloading.spi.version.Version;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractRealDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;

/**
 * Class loading deployer for SwitchYard apps.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SwitchYardClassLoadingDeployer extends AbstractRealDeployer {

    private static Logger _logger = Logger.getLogger(SwitchYardClassLoadingDeployer.class);

    /**
     * Public default constructor.
     */
    public SwitchYardClassLoadingDeployer() {
        addInput(SwitchYardMetaData.class);
        addInput(ClassLoadingMetaData.class);
        addOutput(ClassLoadingMetaData.class);
        setStage(DeploymentStages.POST_PARSE);
    }

    @Override
    protected void internalDeploy(DeploymentUnit unit) throws DeploymentException {
        if (unit.getAttachment(SwitchYardMetaData.class) != null) {
            configureModuleClassloading(unit);
        }
    }

    private void configureModuleClassloading(DeploymentUnit unit) {
        ClassLoadingMetaData classLoadingMetaData = unit.getAttachment(ClassLoadingMetaData.class);

        if (classLoadingMetaData == null) {
            // By default, import/export nothing in/out of the module...
            classLoadingMetaData = new ClassLoadingMetaData();
            classLoadingMetaData.setName(unit.getName());
            classLoadingMetaData.setDomain(unit.getName());
            classLoadingMetaData.setImportAll(false);
            classLoadingMetaData.setVersion(Version.DEFAULT_VERSION);

            unit.addAttachment(ClassLoadingMetaData.class, classLoadingMetaData);
        }
    }
}
