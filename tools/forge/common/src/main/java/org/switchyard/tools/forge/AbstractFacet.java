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

package org.switchyard.tools.forge;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.project.packaging.PackagingType;

/**
 * Base implementation for SwitchYard facets.
 */
public abstract class AbstractFacet extends BaseFacet {

    /**
     * Property name used in POMs to identify SwitchYard version.
     */
    public static final String VERSION = "switchyard.version";
    
    // List of dependencies added to every SwitchYard application
    private List<String> _depends = new LinkedList<String>();

    protected AbstractFacet(String ... dependencies) {
        if (dependencies != null && dependencies.length > 0) {
            _depends = Arrays.asList(dependencies);
        }
    }
    
    protected void installDependencies() {
        DependencyFacet deps = project.getFacet(DependencyFacet.class);
        if (!_depends.isEmpty()) {
            // Add base required dependencies
            for (String artifact : _depends) {
                DependencyBuilder dep = DependencyBuilder.create(artifact + ":${" + VERSION  + "}");
                deps.addDirectDependency(dep);
            }
        }
    }

    @Override
    public boolean isInstalled() {
        boolean installed = false;
        // If the first dependency is present then we assume the facet is installed
        if (!_depends.isEmpty()) {
            Dependency dep = DependencyBuilder.create(_depends.get(0));
            PackagingType packagingType = project.getFacet(PackagingFacet.class).getPackagingType();
            installed = project.getFacet(DependencyFacet.class).hasDirectDependency(dep)
                    && PackagingType.JAR.equals(packagingType);
        }
        return installed;
    }
    
    /**
     * Get the version of SwitchYard used by the application.
     * @return SwitchYard version
     */
    public String getVersion() {
        return project.getFacet(DependencyFacet.class).getProperty(VERSION);
    }
    
    /**
     * Get the version of SwitchYard used by the application.
     * @param version SwitchYard version
     */
    public void setVersion(String version) {
        project.getFacet(DependencyFacet.class).setProperty(VERSION, version);
    }
}
