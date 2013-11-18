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
            return toReleaseId(groupId, artifactId, version);
        }
        return null;
    }

    /**
     * Creates a ReleaseId from the specified groupId, artifactId and version.
     * @param groupId the groupId
     * @param artifactId the artifactId
     * @param version the version
     * @return the ReleaseId
     */
    public static ReleaseId toReleaseId(String groupId, String artifactId, String version) {
        return KieServices.Factory.get().newReleaseId(groupId, artifactId, version);
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
        return kieServices.newKieClasspathContainer();
    }

    private Containers() {}

}
