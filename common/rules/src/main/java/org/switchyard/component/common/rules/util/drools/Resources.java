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
package org.switchyard.component.common.rules.util.drools;

import org.drools.builder.KnowledgeBuilder;
import org.drools.io.ResourceFactory;
import org.switchyard.common.io.resource.Resource;
import org.switchyard.common.io.resource.ResourceType;

/**
 * Drools Resource Utilities.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class Resources {

    /**
     * Converts a SwitchYard Resource into a Drools Resource.
     * @param switchyardResource the original
     * @return the converted
     */
    public static org.drools.io.Resource convert(Resource switchyardResource) {
        return convert(switchyardResource, null);
    }

    /**
     * Converts a SwitchYard Resource into a Drools Resource.
     * @param switchyardResource the original
     * @param loader the ClassLoader to use
     * @return the converted
     */
    public static org.drools.io.Resource convert(Resource switchyardResource, ClassLoader loader) {
        if (switchyardResource != null) {
            return ResourceFactory.newUrlResource(switchyardResource.getLocationURL(loader));
        }
        return null;
    }

    /**
     * Converts a SwitchYard ResourceType into a Drools ResourceType.
     * @param switchyardResourceType the original
     * @return the converted
     */
    public static org.drools.builder.ResourceType convert(ResourceType switchyardResourceType) {
        if (switchyardResourceType != null) {
            String resourceName = switchyardResourceType.getName();
            if ("BPMN".equals(resourceName)) {
                // Drools ResourceType recognizes BPMN2, not BPMN
                resourceName = "BPMN2";
            } else if ("XLS".equals(resourceName)) {
                // Drools ResourceType recognizes DTABLE, not XLS
                resourceName = "DTABLE";
            }
            return org.drools.builder.ResourceType.getResourceType(resourceName);
        }
        return null;
    }

    /**
     * Adds a SwitchYard Resource to a Drools KnowledgeBuilder.
     * @param switchyardResource the resource
     * @param kbuilder the builder
     */
    public static void add(Resource switchyardResource, KnowledgeBuilder kbuilder) {
        add(switchyardResource, kbuilder, null);
    }

    /**
     * Adds a SwitchYard Resource to a Drools KnowledgeBuilder.
     * @param switchyardResource the resource
     * @param kbuilder the builder
     * @param loader the ClassLoader to use
     */
    public static void add(Resource switchyardResource, KnowledgeBuilder kbuilder, ClassLoader loader) {
        org.drools.io.Resource droolsResource = convert(switchyardResource, loader);
        if (droolsResource != null) {
            org.drools.builder.ResourceType droolsResourceType = convert(switchyardResource.getType());
            if (droolsResourceType != null) {
                kbuilder.add(droolsResource, droolsResourceType);
            }
        }
    }

    private Resources() {}

}
