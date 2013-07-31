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
package org.switchyard.component.common.knowledge.util;

import java.util.Properties;

import org.drools.compiler.compiler.PackageBuilderConfiguration;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.SessionConfiguration;
import org.drools.core.common.ProjectClassLoader;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.switchyard.common.type.reflect.FieldAccess;
import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;

/**
 * Configuration functions.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class Configurations {

    /**
     * Gets a base configuration.
     * @param model the model
     * @param loader the class loader
     * @param overrides any overrides
     * @return the base configuration
     */
    public static KieBaseConfiguration getBaseConfiguration(KnowledgeComponentImplementationModel model, ClassLoader loader, Properties overrides) {
        RuleBaseConfiguration baseConfiguration = (RuleBaseConfiguration)KnowledgeBaseFactory.newKnowledgeBaseConfiguration(Propertys.getProperties(model, overrides));
        ClassLoader baseLoader = baseConfiguration.getClassLoader();
        if (baseLoader instanceof ProjectClassLoader) {
            ((ProjectClassLoader)baseLoader).setDroolsClassLoader(loader);
        }
        return baseConfiguration;
    }

    /**
     * Gets a builder configuration.
     * @param model the model
     * @param loader the class loader
     * @param overrides any overrides
     * @return the builder configuration
     */
    public static KnowledgeBuilderConfiguration getBuilderConfiguration(KnowledgeComponentImplementationModel model, ClassLoader loader, Properties overrides) {
        PackageBuilderConfiguration builderConfiguration = (PackageBuilderConfiguration)KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(Propertys.getProperties(model, overrides));
        ClassLoader builderLoader = builderConfiguration.getClassLoader();
        if (builderLoader instanceof ProjectClassLoader) {
            ((ProjectClassLoader)builderLoader).setDroolsClassLoader(loader);
        }
        return builderConfiguration;
    }

    /**
     * Gets a session configuration.
     * @param model the model
     * @param loader the class loader
     * @param overrides any overrides
     * @return the session configuration
     */
    public static KieSessionConfiguration getSessionConfiguration(KnowledgeComponentImplementationModel model, ClassLoader loader, Properties overrides) {
        SessionConfiguration sessionConfiguration = (SessionConfiguration)KnowledgeBaseFactory.newKnowledgeSessionConfiguration(Propertys.getProperties(model, overrides));
        new FieldAccess<ClassLoader>(SessionConfiguration.class, "classLoader").write(sessionConfiguration, loader);
        return sessionConfiguration;
    }

    private Configurations() {}

}
