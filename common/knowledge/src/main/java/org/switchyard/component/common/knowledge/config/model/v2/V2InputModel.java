/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.component.common.knowledge.config.model.v2;

import org.switchyard.component.common.knowledge.config.model.InputModel;
import org.switchyard.component.common.knowledge.config.model.v1.V1InputModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * The 2nd version InputModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class V2InputModel extends V1InputModel {

    /**
     * Creates a new InputModel in the specified namespace and localName.
     * @param namespace the specified namespace
     */
    public V2InputModel(String namespace) {
        super(namespace);
    }

    /**
     * Creates a new InputModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V2InputModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOutput() {
        return getModelAttribute("output");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputModel setOutput(String output) {
        setModelAttribute("output", output);
        return this;
    }

}
