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
package org.switchyard.deployment;

import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.deployers.vfs.spi.deployer.AbstractSimpleVFSRealDeployer;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * AS6 Deployer for SwitchYard.
 */
public class SwitchYardDeployer extends AbstractSimpleVFSRealDeployer<SwitchYardMetaData> {
    
    private static final String BEAN_PREFIX = "switchyard";
    
    /**
     * No args constructor.
     */
    public SwitchYardDeployer() {
        super(SwitchYardMetaData.class);
        setOutput(BeanMetaData.class);
        setStage(DeploymentStages.REAL);
    }

    @Override
    public void deploy(VFSDeploymentUnit unit, SwitchYardMetaData metaData)
    throws DeploymentException {
        BeanMetaData beanMetaData = createBeanMetaData(unit, metaData);
        unit.addAttachment(BeanMetaData.class, beanMetaData);
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

        bmdBuilder.addDependency(deploymentUnit.getName());
        bmdBuilder.addDependency(deploymentUnit.getName() + "_WeldBootstrapBean");
        return bmdBuilder.getBeanMetaData();
    }

}
