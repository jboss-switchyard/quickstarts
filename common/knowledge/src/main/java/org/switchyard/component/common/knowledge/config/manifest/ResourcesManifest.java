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
package org.switchyard.component.common.knowledge.config.manifest;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.io.Resource;
import org.kie.api.runtime.Environment;
import org.switchyard.component.common.knowledge.config.builder.ResourceBuilder;

/**
 * ResourcesManifest.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public final class ResourcesManifest extends Manifest {

    private List<ResourceBuilder> _resourceBuilders = new ArrayList<ResourceBuilder>();

    /**
     * Creates a new ResourcesManifest.
     * @param resourceBuilders the ResourceBuilders
     */
    public ResourcesManifest(List<ResourceBuilder> resourceBuilders) {
        if (resourceBuilders != null) {
            _resourceBuilders.addAll(resourceBuilders);
        }
    }

    /**
     * Builds the Resources.
     * @return the Resources
     */
    public List<Resource> buildResources() {
        List<Resource> resources = new ArrayList<Resource>();
        for (ResourceBuilder builder : _resourceBuilders) {
            Resource resource = builder.build();
            if (resource != null) {
                resources.add(resource);
            }
        }
        return resources;
    }

    /**
     * Removes a ResourcesManifest from the Environment.
     * @param environment the Environment
     * @return the ResourcesManifest
     */
    public static ResourcesManifest removeFromEnvironment(Environment environment) {
        return Manifest.removeFromEnvironment(environment, ResourcesManifest.class);
    }

}
