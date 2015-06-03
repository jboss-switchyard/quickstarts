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
package org.switchyard.component.rules.config.model.v1;

import static org.switchyard.component.common.knowledge.config.model.OperationModel.OPERATION;

import org.switchyard.component.common.knowledge.config.model.v1.V1KnowledgeMarshaller;
import org.switchyard.component.rules.config.model.RulesComponentImplementationModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composite.ComponentImplementationModel;

/**
 * A CompositeMarshaller which can also create knowledge models.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class V1RulesMarshaller extends V1KnowledgeMarshaller {

    /**
     * The complete local name ("implementation.rules").
     */
    private static final String IMPLEMENTATION_RULES = ComponentImplementationModel.IMPLEMENTATION + "." + RulesComponentImplementationModel.RULES;

    /**
     * Required constructor called via reflection.
     *
     * @param desc the Descriptor
     */
    public V1RulesMarshaller(Descriptor desc) {
        super(desc);
    }

    /**
     * Reads in the Configuration, looking for various knowledge models.
     * If not found, it falls back to the super class (V1CompositeMarshaller).
     *
     * @param config the Configuration
     * @return the Model
     */
    @Override
    public Model read(Configuration config) {
        String name = config.getName();
        Descriptor desc = getDescriptor();
        if (IMPLEMENTATION_RULES.equals(name)) {
            return new V1RulesComponentImplementationModel(config, desc);
        } else if (OPERATION.equals(name)) {
            return new V1RulesOperationModel(config, desc);
        }
        return super.read(config);
    }

}
