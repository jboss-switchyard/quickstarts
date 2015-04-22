/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.component.common.knowledge.config.builder;

import org.switchyard.component.common.knowledge.config.manifest.ContainerManifest;
import org.switchyard.component.common.knowledge.config.manifest.Manifest;
import org.switchyard.component.common.knowledge.config.manifest.RemoteManifest;
import org.switchyard.component.common.knowledge.config.manifest.ResourcesManifest;
import org.switchyard.component.common.knowledge.config.model.ContainerModel;
import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;
import org.switchyard.component.common.knowledge.config.model.ManifestModel;
import org.switchyard.component.common.knowledge.config.model.RemoteModel;
import org.switchyard.config.model.resource.ResourcesModel;

/**
 * ManifestBuilder.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class ManifestBuilder extends KnowledgeBuilder {

    private final Manifest _manifest;

    /**
     * Creates a new ManifestBuilder.
     * @param classLoader classLoader
     * @param manifestModel manifestModel
     */
    public ManifestBuilder(ClassLoader classLoader, ManifestModel manifestModel) {
        super(classLoader);
        Manifest manifest = null;
        if (manifestModel != null) {
            ContainerModel containerModel = manifestModel.getContainer();
            if (containerModel != null) {
                manifest = new ContainerManifest(
                        containerModel.getBaseName(),
                        containerModel.getReleaseId(),
                        containerModel.isScan(),
                        containerModel.getScanInterval(),
                        containerModel.getSessionName());
            } else {
                ResourcesModel resourcesModel = manifestModel.getResources();
                if (resourcesModel != null) {
                    manifest = new ResourcesManifest(ResourceBuilder.builders(getClassLoader(), resourcesModel));
                } else {
                    RemoteModel remoteModel = manifestModel.getRemote();
                    if (remoteModel != null) {
                        manifest = new RemoteManifest(new RemoteConfigurationBuilder(getClassLoader(), remoteModel));
                    }
                }
            }
        }
        if (manifest == null) {
            manifest = new ContainerManifest();
        }
        _manifest = manifest;
    }

    /**
     * Builds a Manifest.
     * @return a Manifest
     */
    public Manifest build() {
        return _manifest;
    }

    /**
     * Creates a ManifestBuilder.
     * @param classLoader classLoader
     * @param implementationModel implementationModel
     * @return a ManifestBuilder
     */
    public static ManifestBuilder builder(ClassLoader classLoader, KnowledgeComponentImplementationModel implementationModel) {
        ManifestModel manifestModel = implementationModel != null ? implementationModel.getManifest() : null;
        return new ManifestBuilder(classLoader, manifestModel);
    }

}
