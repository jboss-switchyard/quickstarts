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
package org.switchyard.as7.extension.deployment;

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

    /* (non-Javadoc)
     * @see org.jboss.as.server.deployment.DeploymentUnitProcessor#deploy(org.jboss.as.server.deployment.DeploymentPhaseContext)
     */
    @Override
    public void deploy(DeploymentPhaseContext phaseContext) throws DeploymentUnitProcessingException {
        final DeploymentUnit deploymentUnit = phaseContext.getDeploymentUnit();
        final ResourceRoot deploymentRoot = deploymentUnit.getAttachment(Attachments.DEPLOYMENT_ROOT);
        final VirtualFile switchyardXml = deploymentRoot.getRoot().getChild(SWITCHYARD_XML);

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
