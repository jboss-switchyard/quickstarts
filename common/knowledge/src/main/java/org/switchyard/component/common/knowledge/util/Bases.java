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

import java.util.Properties;

import org.kie.KieBase;
import org.kie.KieBaseConfiguration;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderConfiguration;
import org.kie.builder.KnowledgeBuilderFactory;
import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;

/**
 * Base functions.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class Bases {

    /**
     * Creates a new base.
     * @param model the model
     * @param overrides any overrides
     * @param loader the class loader
     * @return the base
     */
    public static KieBase newBase(KnowledgeComponentImplementationModel model, Properties overrides, ClassLoader loader) {
        KieBaseConfiguration baseConfiguration = Configurations.getBaseConfiguration(model, overrides, loader);
        KnowledgeBase base = KnowledgeBaseFactory.newKnowledgeBase(baseConfiguration);
        KnowledgeBuilderConfiguration builderConfiguration = Configurations.getBuilderConfiguration(model, overrides, loader);
        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder(base, builderConfiguration);
        Resources.addResources(model, loader, builder);
        return builder.newKnowledgeBase();
    }

    private Bases() {}

}
