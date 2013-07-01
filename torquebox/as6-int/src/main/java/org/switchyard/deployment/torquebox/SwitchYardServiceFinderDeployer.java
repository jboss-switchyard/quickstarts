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
