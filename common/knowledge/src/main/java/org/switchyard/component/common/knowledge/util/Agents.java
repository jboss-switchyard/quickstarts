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

import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.kie.ChangeSet;
import org.kie.KieBaseConfiguration;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.agent.KnowledgeAgent;
import org.kie.agent.KnowledgeAgentConfiguration;
import org.kie.agent.KnowledgeAgentFactory;
import org.kie.builder.KnowledgeBuilderConfiguration;
import org.switchyard.common.io.resource.Resource;
import org.switchyard.common.lang.Strings;
import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;
import org.switchyard.component.common.knowledge.config.model.ManifestModel;
import org.switchyard.config.model.resource.ResourcesModel;

/**
 * Agent functions.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class Agents {

    private static AtomicInteger _nameCounter = new AtomicInteger();

    /**
     * Creates a new agent.
     * @param model the model
     * @param overrides any overrides
     * @param loader the class loader
     * @return the agent
     */
    public static KnowledgeAgent newAgent(KnowledgeComponentImplementationModel model, Properties overrides, ClassLoader loader) {
        String name;
        try {
            name = model.getComponent().getComposite().getName();
        } catch (NullPointerException npe) {
            name = null;
        }
        name = Strings.trimToNull(name);
        if (name == null) {
            name = Agents.class.getSimpleName();
        }
        name = name + "-" + _nameCounter.incrementAndGet();
        KieBaseConfiguration baseConfiguration = Configurations.getBaseConfiguration(model, overrides, loader);
        KnowledgeBase base = KnowledgeBaseFactory.newKnowledgeBase(baseConfiguration);
        KnowledgeAgentConfiguration agentConfiguration = Configurations.getAgentConfiguration(model, overrides);
        KnowledgeBuilderConfiguration builderConfiguration = Configurations.getBuilderConfiguration(model, overrides, loader);
        KnowledgeAgent agent = KnowledgeAgentFactory.newKnowledgeAgent(name, base, agentConfiguration, builderConfiguration);
        List<? extends Resource> resources = null;
        ManifestModel manifestModel = model.getManifest();
        if (manifestModel != null) {
            ResourcesModel resourcesModel = manifestModel.getResources();
            if (resourcesModel != null) {
                resources = resourcesModel.getResources();
            }
        }
        if (resources == null) {
            resources = Collections.emptyList();
        }
        ChangeSet changeSet = ChangeSets.newChangeSet(resources, loader);
        agent.applyChangeSet(changeSet);
        return agent;
    }

    private Agents() {}

}
