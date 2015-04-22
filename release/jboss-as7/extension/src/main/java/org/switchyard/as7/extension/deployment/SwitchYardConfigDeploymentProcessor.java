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

import org.jboss.as.ee.structure.DeploymentType;
import org.jboss.as.ee.structure.DeploymentTypeMarker;
import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.module.ResourceRoot;
import org.jboss.vfs.VirtualFile;
import org.switchyard.as7.extension.SwitchYardDeploymentMarker;

/**
 * DU processor that finds <literal>switchyard.xml</literal> file and attaches the information to the deployment.
 * 
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class SwitchYardConfigDeploymentProcessor implements DeploymentUnitProcessor {

    private static final String SWITCHYARD_XML = "META-INF/switchyard.xml";
    private static final String SWITCHYARD_XML_WAR = "WEB-INF/switchyard.xml";

    /* (non-Javadoc)
     * @see org.jboss.as.server.deployment.DeploymentUnitProcessor#deploy(org.jboss.as.server.deployment.DeploymentPhaseContext)
     */
    @Override
    public void deploy(DeploymentPhaseContext phaseContext) throws DeploymentUnitProcessingException {
        final DeploymentUnit deploymentUnit = phaseContext.getDeploymentUnit();
        final ResourceRoot deploymentRoot = deploymentUnit.getAttachment(Attachments.DEPLOYMENT_ROOT);
        final VirtualFile switchyardXml;
        
        if (DeploymentTypeMarker.isType(DeploymentType.WAR, deploymentUnit)) {
            // switchyard.xml file located in /WEB-INF
            final VirtualFile warSwitchYardXml = deploymentRoot.getRoot().getChild(SWITCHYARD_XML_WAR);
            if (warSwitchYardXml.exists()) {
                switchyardXml = warSwitchYardXml;
            } else {
                // fall back to original location
                switchyardXml = deploymentRoot.getRoot().getChild(SWITCHYARD_XML);
            }
        } else {
            switchyardXml = deploymentRoot.getRoot().getChild(SWITCHYARD_XML);
        }

        if (!switchyardXml.exists()) {
            return;
        }
        final String archiveName = deploymentUnit.getName();
        final String deploymentName = archiveName.substring(0, archiveName.lastIndexOf('.'));
        final SwitchYardMetaData switchYardMetaData = new SwitchYardMetaData(archiveName, deploymentName);
        switchYardMetaData.setSwitchYardFile(switchyardXml);

        deploymentUnit.putAttachment(SwitchYardMetaData.ATTACHMENT_KEY, switchYardMetaData);
        SwitchYardDeploymentMarker.mark(deploymentUnit);
    }

    /* (non-Javadoc)
     * @see org.jboss.as.server.deployment.DeploymentUnitProcessor#undeploy(org.jboss.as.server.deployment.DeploymentUnit)
     */
    @Override
    public void undeploy(DeploymentUnit context) {
    }

}
