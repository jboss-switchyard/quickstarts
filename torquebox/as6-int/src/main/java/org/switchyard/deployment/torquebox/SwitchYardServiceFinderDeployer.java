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

package org.switchyard.deployment.torquebox;

import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.switchyard.deploy.ServiceDomainManager;
import org.torquebox.base.metadata.RubyApplicationMetaData;
import org.torquebox.injection.AbstractRubyComponentDeployer;
import org.torquebox.mc.AttachmentUtils;

/**
 * Deployer for the deployment's {@link SwitchYardServiceFinder} bean instance.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SwitchYardServiceFinderDeployer extends AbstractRubyComponentDeployer {

    private ServiceDomainManager _domainManager;

    /**
     * Public constructor.
     */
    public SwitchYardServiceFinderDeployer() {
        setAllInputs(true);
        addInput(RubyApplicationMetaData.class);
        addOutput(BeanMetaData.class);
        setStage(DeploymentStages.REAL);
    }

    /**
     * Set the deployment's {@link ServiceDomainManager} instance.
     * @param domainManager The {@link ServiceDomainManager} instance.
     */
    public void setDomainManager(ServiceDomainManager domainManager) {
        this._domainManager = domainManager;
    }

    @Override
    public void deploy(DeploymentUnit unit) throws DeploymentException {
        RubyApplicationMetaData rubyAppMetaData = unit.getAttachment(RubyApplicationMetaData.class);

        if (rubyAppMetaData == null) {
            return;
        }

        String beanName = SwitchYardServiceFinder.beanName(unit.getName());
        BeanMetaDataBuilder beanBuilder = BeanMetaDataBuilder.createBuilder(beanName, SwitchYardServiceFinder.class.getName());

        beanBuilder.addPropertyMetaData("domainManager", _domainManager);

        BeanMetaData beanMetaData = beanBuilder.getBeanMetaData();

        AttachmentUtils.attach(unit, beanMetaData);
    }
}
