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
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.drools.ChangeSet;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentConfiguration;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.switchyard.common.io.resource.Resource;
import org.switchyard.common.lang.Strings;
import org.switchyard.common.type.Classes;
import org.switchyard.component.common.rules.config.model.ComponentImplementationModel;

/**
 * Drools KnowledgeAgent Utilities.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class Agents {

    private static AtomicInteger _nameCounter = new AtomicInteger();

    /**
     * Creates a new KnowledgeAgent given the specified component implementation config.
     * @param cic the component implementation config
     * @param additionalResources any extra resources to add
     * @return the agent
     */
    public static KnowledgeAgent newAgent(ComponentImplementationConfig cic, Resource... additionalResources) {
        ComponentImplementationModel model = cic.getModel();
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
        KnowledgeBaseConfiguration kbaseConfig = Configs.getBaseConfiguration(cic);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kbaseConfig);
        KnowledgeAgentConfiguration kagentConfig = Configs.getAgentConfiguration(cic);
        KnowledgeBuilderConfiguration kbuilderConfig = Configs.getBuilderConfiguration(cic);
        KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent(name, kbase, kagentConfig, kbuilderConfig);
        List<Resource> resources = new ArrayList<Resource>();
        resources.addAll(model.getResources());
        if (additionalResources != null) {
            for (Resource additionalResource : additionalResources) {
                if (additionalResource != null) {
                    resources.add(additionalResource);
                }
            }
        }
        ClassLoader loader = cic.getLoader();
        if (loader == null) {
            loader = Classes.getClassLoader(Agents.class);
        }
        ChangeSet changeSet = ChangeSets.newChangeSet(resources, loader);
        kagent.applyChangeSet(changeSet);
        return kagent;
    }
 
    private Agents() {}

}
