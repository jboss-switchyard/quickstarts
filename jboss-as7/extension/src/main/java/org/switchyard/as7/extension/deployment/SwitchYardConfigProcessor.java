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

import java.io.IOException;
import java.io.InputStream;

import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.logging.Logger;
import org.jboss.modules.Module;
import org.switchyard.as7.extension.SwitchYardDeploymentMarker;
import org.switchyard.common.property.CompoundPropertyResolver;
import org.switchyard.common.property.PropertyResolver;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class SwitchYardConfigProcessor implements DeploymentUnitProcessor {

    private static final Logger LOG = Logger.getLogger("org.switchyard");

    /* (non-Javadoc)
     * @see org.jboss.as.server.deployment.DeploymentUnitProcessor#deploy(org.jboss.as.server.deployment.DeploymentPhaseContext)
     */
    @Override
    public void deploy(DeploymentPhaseContext phaseContext) throws DeploymentUnitProcessingException {
        final DeploymentUnit deploymentUnit = phaseContext.getDeploymentUnit();

        if (!SwitchYardDeploymentMarker.isSwitchYardDeployment(deploymentUnit)) {
            LOG.debug("Ignoring deployment unit '" + deploymentUnit.getName() + "' as it is not recognized as being a SwitchYard Application.");
            return;
        }
        SwitchYardMetaData switchYardMetaData = deploymentUnit.getAttachment(SwitchYardMetaData.ATTACHMENT_KEY);

        ClassLoader origCL = Thread.currentThread().getContextClassLoader();
        InputStream is = null;
        try {
            final Module module = deploymentUnit.getAttachment(Attachments.MODULE);
            Thread.currentThread().setContextClassLoader(module.getClassLoader());
            is = switchYardMetaData.getSwitchYardFile().openStream();
            SwitchYardModel switchyardModel = new ModelPuller<SwitchYardModel>().pull(is);
            JBossPropertyResolver.set(deploymentUnit, switchyardModel);
            switchYardMetaData.setSwitchYardModel(switchyardModel);
            LOG.debug("Successfully parsed SwitchYard configuration for deployment unit '" + deploymentUnit.getName() + "'.");
        } catch (IOException ioe) {
            throw new DeploymentUnitProcessingException(ioe);
        } finally {
            Thread.currentThread().setContextClassLoader(origCL);
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ioe) {
                    LOG.error(ioe);
                }
            }
        }

    }

    /* (non-Javadoc)
     * @see org.jboss.as.server.deployment.DeploymentUnitProcessor#undeploy(org.jboss.as.server.deployment.DeploymentUnit)
     */
    @Override
    public void undeploy(DeploymentUnit context) {
        // TODO Auto-generated method stub

    }

    private static final class JBossPropertyResolver implements PropertyResolver {

        private final org.jboss.metadata.property.PropertyResolver _wrapped;

        private JBossPropertyResolver(org.jboss.metadata.property.PropertyResolver wrapped) {
            _wrapped = wrapped;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object resolveProperty(String key) {
            return key != null ? _wrapped.resolve(key) : null;
        }

        private static void set(DeploymentUnit deploymentUnit, SwitchYardModel switchyardModel) {
            org.jboss.metadata.property.PropertyResolver wrapped = deploymentUnit.getAttachment(org.jboss.as.ee.metadata.property.Attachments.FINAL_PROPERTY_RESOLVER);
            if (wrapped != null) {
                Configuration config = switchyardModel.getModelConfiguration();
                config.setPropertyResolver(CompoundPropertyResolver.compact(config.getPropertyResolver(), new JBossPropertyResolver(wrapped)));
            }
        }

    }

}
