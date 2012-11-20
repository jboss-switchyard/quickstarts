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

import org.kie.KieServices;
import org.kie.builder.KnowledgeBuilder;
import org.kie.io.KieResources;
import org.switchyard.common.io.resource.Resource;
import org.switchyard.common.io.resource.ResourceType;
import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;
import org.switchyard.component.common.knowledge.config.model.ManifestModel;
import org.switchyard.config.model.resource.ResourcesModel;

/**
 * Resource functions.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class Resources {

    /**
     * Installs resource types.
     * @param loader the class loader
     */
    public static void installTypes(ClassLoader loader) {
        ResourceType.install(loader);
    }

    /**
     * Adds resources.
     * @param model the model
     * @param loader the class loader
     * @param builder the knowledge builder
     */
    public static void addResources(KnowledgeComponentImplementationModel model, ClassLoader loader, KnowledgeBuilder builder) {
        ManifestModel manifestModel = model.getManifest();
        if (manifestModel != null) {
            ResourcesModel resourcesModel = manifestModel.getResources();
            if (resourcesModel != null) {
                KieResources kieResources = KieServices.Factory.get().getResources();
                for (Resource syResource : resourcesModel.getResources()) {
                    org.kie.io.Resource kieResource = kieResources.newUrlResource(syResource.getLocationURL(loader));
                    if (kieResource != null) {
                        org.kie.io.ResourceType kieResourceType = convertResourceType(syResource.getType());
                        if (kieResourceType != null) {
                            builder.add(kieResource, kieResourceType);
                        }
                    }
                }
            }
        }
    }

    /**
     * Adds a resource.
     * @param syResource the SwitchYard resource
     * @param loader the class loader
     * @param builder the knowledge builder
     */
    public static void addResource(Resource syResource, ClassLoader loader, KnowledgeBuilder builder) {
        KieResources kieResources = KieServices.Factory.get().getResources();
        org.kie.io.Resource kieResource = kieResources.newUrlResource(syResource.getLocationURL(loader));
        if (kieResource != null) {
            org.kie.io.ResourceType kieResourceType = convertResourceType(syResource.getType());
            if (kieResourceType != null) {
                builder.add(kieResource, kieResourceType);
            }
        }
    }

    /**
     * Converts a resource type.
     * @param syResourceType the SwitchYard resource type.
     * @return the KIE resource type
     */
    public static org.kie.io.ResourceType convertResourceType(ResourceType syResourceType) {
        if (syResourceType != null) {
            String resourceTypeName = syResourceType.getName();
            if ("BPMN".equals(resourceTypeName)) {
                // Drools ResourceType recognizes BPMN2, not BPMN
                resourceTypeName = "BPMN2";
            } else if ("XLS".equals(resourceTypeName)) {
                // Drools ResourceType recognizes DTABLE, not XLS
                resourceTypeName = "DTABLE";
            }
            return org.kie.io.ResourceType.getResourceType(resourceTypeName);
        }
        return null;
    }

    private Resources() {}

}
