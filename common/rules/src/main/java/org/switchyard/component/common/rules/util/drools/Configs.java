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

import java.util.Properties;

import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.agent.KnowledgeAgentConfiguration;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.conf.EventProcessingOption;
import org.drools.conf.MaxThreadsOption;
import org.drools.conf.MultithreadEvaluationOption;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.conf.ClockTypeOption;
import org.switchyard.common.type.Classes;
import org.switchyard.component.common.rules.ClockType;
import org.switchyard.component.common.rules.EventProcessingType;
import org.switchyard.component.common.rules.config.model.ComponentImplementationModel;

/**
 * Drools Configuration Utilities.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class Configs {

    /**
     * Creates a new KnowledgeBaseConfiguration given the specified component implementation config.
     * @param cic the component implementation config
     * @return the KnowledgeBaseConfiguration
     */
    public static KnowledgeBaseConfiguration getBaseConfiguration(ComponentImplementationConfig cic) {
        KnowledgeBaseConfiguration kbaseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration(getProperties(cic), getLoader(cic));
        ComponentImplementationModel model = cic.getModel();
        Boolean multithreadEvaluation = model.getMultithreadEvaluation();
        if (multithreadEvaluation != null) {
            boolean me = multithreadEvaluation.booleanValue();
            MultithreadEvaluationOption meo = me ? MultithreadEvaluationOption.YES : MultithreadEvaluationOption.NO;
            kbaseConfig.setOption(meo);
            if (me) {
                Integer maxThreads = model.getMaxThreads();
                if (maxThreads != null) {
                    kbaseConfig.setOption(MaxThreadsOption.get(maxThreads.intValue()));
                }
            }
        }
        EventProcessingType eventProcessing = model.getEventProcessing();
        if (eventProcessing != null) {
            switch (eventProcessing) {
                case CLOUD:
                    kbaseConfig.setOption(EventProcessingOption.CLOUD);
                    break;
                case STREAM:
                    kbaseConfig.setOption(EventProcessingOption.STREAM);
                    break;
            }
        }
        return kbaseConfig;
    }

    /**
     * Creates a KnowledgeAgentConfiguration given the specified component implementation config.
     * @param cic the component implementation config
     * @return the config
     */
    public static KnowledgeAgentConfiguration getAgentConfiguration(ComponentImplementationConfig cic) {
        return KnowledgeAgentFactory.newKnowledgeAgentConfiguration(getProperties(cic));
    }

    /**
     * Creates a KnowledgeBuilderConfiguration given the specified component implementation config.
     * @param cic the component implementation config
     * @return the config
     */
    public static KnowledgeBuilderConfiguration getBuilderConfiguration(ComponentImplementationConfig cic) {
        return  KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(getProperties(cic), getLoader(cic));
    }

    /**
     * Creates a new KnowledgeSessionConfiguration given the specified component implementation config.
     * @param cic the component implementation config
     * @return the config
     */
    public static KnowledgeSessionConfiguration getSessionConfiguration(ComponentImplementationConfig cic) {
        KnowledgeSessionConfiguration ksessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(getProperties(cic));
        ClockType clock = cic.getModel().getClock();
        if (clock != null) {
            switch (clock) {
                case REALTIME:
                    ksessionConfig.setOption(ClockTypeOption.get(org.drools.ClockType.REALTIME_CLOCK.getId()));
                    break;
                case PSEUDO:
                    ksessionConfig.setOption(ClockTypeOption.get(org.drools.ClockType.PSEUDO_CLOCK.getId()));
                    break;
            }
        }
        return ksessionConfig;
    }

    private static Properties getProperties(ComponentImplementationConfig cic) {
        return Environments.getProperties(cic);
    }

    private static ClassLoader getLoader(ComponentImplementationConfig cic) {
        ClassLoader loader = cic.getLoader();
        if (loader == null) {
            loader = Classes.getClassLoader(Configs.class);
        }
        return loader;
    }
 
    private Configs() {}

}
