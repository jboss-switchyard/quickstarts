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
package org.switchyard.as7.extension.deployment;

import java.util.List;

import javax.enterprise.inject.spi.Extension;

import org.apache.log4j.Logger;
import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.weld.WeldDeploymentMarker;
import org.jboss.as.weld.deployment.WeldAttachments;
import org.jboss.as.weld.deployment.WeldPortableExtensions;
import org.jboss.modules.Module;
import org.jboss.weld.bootstrap.spi.Metadata;
import org.switchyard.as7.extension.ExtensionMessages;
import org.switchyard.as7.extension.SwitchYardDeploymentMarker;

/**
 * Deployment processor that installs the SwitchYard CDI extension.
 * 
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class SwitchYardCdiIntegrationProcessor implements DeploymentUnitProcessor {

    private static final String SWITCHYARD_CDI_EXTENSION = "org.switchyard.component.bean.SwitchYardCDIServiceDiscovery";
    private static final String DELTASPIKE_CDI_EXTENSION = "org.apache.deltaspike.core.api.provider.BeanManagerProvider";

    private static Logger _logger = Logger.getLogger(SwitchYardCdiIntegrationProcessor.class);

    /* (non-Javadoc)
     * @see org.jboss.as.server.deployment.DeploymentUnitProcessor#deploy(org.jboss.as.server.deployment.DeploymentPhaseContext)
     */
    @Override
    public void deploy(DeploymentPhaseContext phaseContext) throws DeploymentUnitProcessingException {
        final DeploymentUnit deploymentUnit = phaseContext.getDeploymentUnit();
        if (!SwitchYardDeploymentMarker.isSwitchYardDeployment(deploymentUnit)) {
            return;
        }

        if (WeldDeploymentMarker.isPartOfWeldDeployment(deploymentUnit)) {
            // Add the Weld portable extension
            final DeploymentUnit parent = deploymentUnit.getParent() == null ? deploymentUnit : deploymentUnit.getParent();
            synchronized (parent) {
                checkExtension(SWITCHYARD_CDI_EXTENSION, deploymentUnit, parent);
                checkExtension(DELTASPIKE_CDI_EXTENSION, deploymentUnit, parent);
            }
        } else {
            _logger.debug("SwitchYard Application for deployment unit '" + deploymentUnit.getName() + "' does not appear to contain CDI Beans "
                    + "(no META-INF/beans.xml file in unit).  Not attaching SwitchYard CDI Discovery Extension to deployment.");
        }
    }

    private void checkExtension(final String extensionName, DeploymentUnit deploymentUnit, DeploymentUnit parent) throws DeploymentUnitProcessingException {
        final Module module = deploymentUnit.getAttachment(Attachments.MODULE);
        Class<?> extensionClass = null;
        try {
            extensionClass = module.getClassLoader().loadClass(extensionName);
        } catch (ClassNotFoundException cnfe) {
            throw new DeploymentUnitProcessingException(cnfe);
        }
        if (extensionClass != null) {
            WeldPortableExtensions extensions = WeldPortableExtensions.getPortableExtensions(deploymentUnit);
            extensions.tryRegisterExtension(extensionClass, deploymentUnit);
        } else {
            throw ExtensionMessages.MESSAGES.extensionNotfound(extensionName);
        }
    }

    /* (non-Javadoc)
     * @see org.jboss.as.server.deployment.DeploymentUnitProcessor#undeploy(org.jboss.as.server.deployment.DeploymentUnit)
     */
    @Override
    public void undeploy(DeploymentUnit context) {
    }

}
