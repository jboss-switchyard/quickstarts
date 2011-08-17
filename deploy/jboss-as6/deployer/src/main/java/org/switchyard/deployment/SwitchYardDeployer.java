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

package org.switchyard.deployment;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.deployers.vfs.spi.deployer.AbstractSimpleVFSRealDeployer;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.deploy.ServiceDomainManager;

/**
 * AS6 Deployer for SwitchYard.
 */
public class SwitchYardDeployer extends AbstractSimpleVFSRealDeployer<SwitchYardMetaData> {

    private Logger _log = Logger.getLogger(SwitchYardConfigParser.class);

    private static final String BEAN_PREFIX = "switchyard";

    private ServiceDomainManager _domainManager;

    /**
     * No args constructor.
     */
    public SwitchYardDeployer() {
        super(SwitchYardMetaData.class);
        setOutput(BeanMetaData.class);
        setStage(DeploymentStages.REAL);
    }

    /**
     * Set the {@link ServiceDomainManager} instance for the deployment.
     * @param domainManager The domain manager.
     */
    public void setDomainManager(ServiceDomainManager domainManager) {
        this._domainManager = domainManager;
    }

    @Override
    public void deploy(VFSDeploymentUnit unit, SwitchYardMetaData metaData)
        throws DeploymentException {
        try {
            parseSwitchYardConfig(metaData);
            _log.debug("Successfully parsed SwitchYard configuration for deployment unit '" + unit.getName() + "'.");
        } catch (IOException ioe) {
            throw new DeploymentException(ioe);
        }
        BeanMetaData beanMetaData = createBeanMetaData(unit, metaData);
        unit.addAttachment(BeanMetaData.class, beanMetaData);
    }
    
    /**
     * Parse the SwitchYard configuration
     * @param metaData SwitchYard MetaData
     * @throws IOException IOException
     */
    private void parseSwitchYardConfig(SwitchYardMetaData metaData) throws IOException {
        InputStream is = metaData.getSwitchYardFile().openStream();
        try {
            SwitchYardModel switchyardModel = new ModelPuller<SwitchYardModel>().pull(is);
            metaData.setSwitchYardModel(switchyardModel);
        } finally {
            is.close();
        }
    }
    
    /**
     * Creates a {@link BeanMetaData} that describes the {@link SwitchYardDeployment} class.
     *
     * @param deploymentUnit The deployment unit to deploy.
     * @param metadata The SwitchYard MetaData that is associated with the deployment unit.
     * @return BeanMetaData The {@link BeanMetaData} describing the SwitchYardDeployment class
     */
    private BeanMetaData createBeanMetaData(final DeploymentUnit deploymentUnit, final SwitchYardMetaData metadata)
    {
        BeanMetaDataBuilder bmdBuilder = BeanMetaDataBuilder.createBuilder(BEAN_PREFIX + "." + deploymentUnit.getName(), SwitchYardDeployment.class.getName());
        // Setup the first constructor argument.
        bmdBuilder.addConstructorParameter(String.class.getName(), metadata.getArchiveName());
        // Setup the second constructor argument.
        bmdBuilder.addConstructorParameter(VFSDeploymentUnit.class.getName(), deploymentUnit);
        // Setup the third constructor argument.
        bmdBuilder.addConstructorParameter(SwitchYardModel.class.getName(), metadata.getSwitchYardModel());
        // Setup the fourth constructor argument.
        bmdBuilder.addConstructorParameter(ServiceDomainManager.class.getName(), _domainManager);

        bmdBuilder.addDependency(deploymentUnit.getName());
        bmdBuilder.addDependency(deploymentUnit.getName() + "_WeldBootstrapBean");

        _log.debug("Successfully created AS6 BeanMetaData instance for deployment unit '" + deploymentUnit.getName() + "'.");

        return bmdBuilder.getBeanMetaData();
    }

}
