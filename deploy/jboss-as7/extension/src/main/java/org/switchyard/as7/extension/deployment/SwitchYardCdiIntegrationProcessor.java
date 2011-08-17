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
import org.jboss.modules.Module;
import org.jboss.weld.bootstrap.spi.Metadata;
import org.switchyard.as7.extension.SwitchYardDeploymentMarker;

/**
 * Deployment processor that installs the SwitchYard CDI extension.
 * 
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class SwitchYardCdiIntegrationProcessor implements DeploymentUnitProcessor {

    private static final String SWITCHYARD_CDI_EXTENSION = "org.switchyard.component.bean.SwitchYardCDIServiceDiscovery";

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
                boolean found = false;
                final List<Metadata<Extension>> extensions = parent.getAttachmentList(WeldAttachments.PORTABLE_EXTENSIONS);
                for (Metadata<Extension> extension : extensions) {
                    if (extension.getLocation().equals(SWITCHYARD_CDI_EXTENSION)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    _logger.debug("SwitchYard Application for deployment unit '" + deploymentUnit.getName() + "' contains CDI Beans.  "
                            + "Attaching SwitchYard CDI Discovery Extension to deployment.");

                    try {
                        final Module module = deploymentUnit.getAttachment(Attachments.MODULE);
                        Class<? extends Extension> clazz = (Class<? extends Extension>) module.getClassLoader().loadClass(SWITCHYARD_CDI_EXTENSION);
                        final Extension ext = clazz.newInstance();
                        Metadata<Extension> metadata = new Metadata<Extension>() {
                            @Override
                            public Extension getValue() {
                                return ext;
                            }

                            @Override
                            public String getLocation() {
                                return SWITCHYARD_CDI_EXTENSION;
                            }
                        };
                        parent.addToAttachmentList(WeldAttachments.PORTABLE_EXTENSIONS, metadata);
                    } catch (InstantiationException ie) {
                        throw new DeploymentUnitProcessingException(ie);
                    } catch (IllegalAccessException iae) {
                        throw new DeploymentUnitProcessingException(iae);
                    } catch (ClassNotFoundException cnfe) {
                        throw new DeploymentUnitProcessingException(cnfe);
                    }
                }
            }
        } else {
            _logger.debug("SwitchYard Application for deployment unit '" + deploymentUnit.getName() + "' does not appear to contain CDI Beans "
                    + "(no META-INF/beans.xml file in unit).  Not attaching SwitchYard CDI Discovery Extension to deployment.");
        }
    }

    /* (non-Javadoc)
     * @see org.jboss.as.server.deployment.DeploymentUnitProcessor#undeploy(org.jboss.as.server.deployment.DeploymentUnit)
     */
    @Override
    public void undeploy(DeploymentUnit context) {
    }

}
