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

package org.switchyard.tools.forge;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.seam.forge.project.dependencies.Dependency;
import org.jboss.seam.forge.project.dependencies.DependencyBuilder;
import org.jboss.seam.forge.project.facets.BaseFacet;
import org.jboss.seam.forge.project.facets.DependencyFacet;
import org.jboss.seam.forge.project.facets.PackagingFacet;
import org.jboss.seam.forge.project.packaging.PackagingType;
import org.jboss.seam.forge.shell.Shell;

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

    @Inject
    private Shell _shell;
    
    protected AbstractFacet(String ... dependencies) {
        if (dependencies != null && dependencies.length > 0) {
            _depends = Arrays.asList(dependencies);
        }
    }
    
    @Override
    public boolean install() {
        DependencyFacet deps = project.getFacet(DependencyFacet.class);
        if (!_depends.isEmpty()) {
            // Version choice can be driven off of first dependency
            List<Dependency> versions = deps.resolveAvailableVersions(_depends.get(0) + ":[,]");
            Dependency version = _shell.promptChoiceTyped("Please select a version to install:", versions);
            // Set the version property in the pom so it can be changed easily
            deps.setProperty(VERSION, version.getVersion());
            // Add base required dependencies
            for (String artifact : _depends) {
                DependencyBuilder dep = DependencyBuilder.create(artifact + ":${" + VERSION  + "}");
                deps.addDependency(dep);
            }
        }
        
        return true;
    }

    @Override
    public boolean isInstalled() {
        boolean installed = false;
        // If the first dependency is present then we assume the facet is installed
        if (!_depends.isEmpty()) {
            Dependency dep = DependencyBuilder.create(_depends.get(0));
            PackagingType packagingType = project.getFacet(PackagingFacet.class).getPackagingType();
            installed = project.getFacet(DependencyFacet.class).hasDependency(dep)
                    && PackagingType.JAR.equals(packagingType);
        }
        return installed;
    }
    
    protected Shell getShell() {
        return _shell;
    }
}
