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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.switchyard.common.io.resource.Resource;
import org.switchyard.common.type.Classes;
import org.switchyard.component.common.rules.config.model.ComponentImplementationModel;

/**
 * Drools KnowledgeBase Utilities.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class Bases {

    /**
     * Creates a new KnowledgeBase given the specified component implementation model.
     * @param model the model
     * @return the base
     */
    public static KnowledgeBase newBase(ComponentImplementationModel model) {
        return newBase(model, null);
    }

    /**
     * Creates a new KnowledgeBase given the specified component implementation model and classloader.
     * @param model the model
     * @param loader the classloader
     * @param additionalResources any extra resources to add
     * @return the base
     */
    public static KnowledgeBase newBase(ComponentImplementationModel model, ClassLoader loader, Resource... additionalResources) {
        if (loader == null) {
            loader = Classes.getClassLoader(Bases.class);
        }
        KnowledgeBaseConfiguration kbaseConfig = Configs.getBaseConfiguration(model, loader);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kbaseConfig);
        KnowledgeBuilderConfiguration kbuilderConfig = Configs.getBuilderConfiguration(loader);
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(kbase, kbuilderConfig);
        List<Resource> resources = new ArrayList<Resource>();
        resources.addAll(model.getResources());
        if (additionalResources != null) {
            resources.addAll(Arrays.asList(additionalResources));
        }
        for (Resource resource : resources) {
            Resources.add(resource, kbuilder, loader);
        }
        return kbuilder.newKnowledgeBase();
    }

    private Bases() {}

}
