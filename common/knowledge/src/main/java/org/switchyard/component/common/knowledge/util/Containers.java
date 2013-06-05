/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.common.knowledge.util;

import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.switchyard.component.common.knowledge.config.model.ContainerModel;
import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;
import org.switchyard.component.common.knowledge.config.model.ManifestModel;

/**
 * Container functions.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class Containers {

    /**
     * Converts a colon (':') separated string into a ReleaseId.
     * @param releaseId the string
     * @return the ReleaseId
     */
    public static ReleaseId toReleaseId(String releaseId) {
        if (releaseId != null) {
            String[] gav = releaseId.split(":", 3);
            String groupId = gav.length > 0 ? gav[0] : null;
            String artifactId = gav.length > 1 ? gav[1] : null;
            String version = gav.length > 2 ? gav[2] : null;
            return KieServices.Factory.get().newReleaseId(groupId, artifactId, version);
        }
        return null;
    }

    /**
     * Gets a ContainerModel for the specified KnowledgeComponentImplementationModel.
     * @param model the specified KnowledgeComponentImplementationModel
     * @return the ContainerModel
     */
    public static ContainerModel getContainerModel(KnowledgeComponentImplementationModel model) {
        if (model != null) {
            ManifestModel manifestModel = model.getManifest();
            if (manifestModel != null) {
                return manifestModel.getContainer();
            }
        }
        return null;
    }

    /**
     * Gets a KieContainer for the specified ContainerModel.
     * @param model the specified ContainerModel
     * @return the KieContainer
     */
    public static KieContainer getContainer(ContainerModel model) {
        KieServices kieServices = KieServices.Factory.get();
        if (model != null) {
            ReleaseId releaseId = toReleaseId(model.getReleaseId());
            if (releaseId != null) {
                return kieServices.newKieContainer(releaseId);
            }
        }
        return kieServices.getKieClasspathContainer();
    }

    private Containers() {}

}
