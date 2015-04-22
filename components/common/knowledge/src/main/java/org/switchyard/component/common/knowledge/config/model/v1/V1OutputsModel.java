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
package org.switchyard.component.common.knowledge.config.model.v1;

import static org.switchyard.component.common.knowledge.config.model.OutputModel.OUTPUT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.common.knowledge.config.model.OutputModel;
import org.switchyard.component.common.knowledge.config.model.OutputsModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * A version 1 OutputsModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class V1OutputsModel extends BaseModel implements OutputsModel {

    private List<OutputModel> _outputs = new ArrayList<OutputModel>();

    /**
     * Creates a new V1OutputsModel in the specified namespace.
     * @param namespace the specified namespace
     */
    public V1OutputsModel(String namespace) {
        super(XMLHelper.createQName(namespace, OUTPUTS));
        setModelChildrenOrder(OUTPUT);
    }

    /**
     * Creates a new V1OutputsModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1OutputsModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration output_config : config.getChildren(OUTPUT)) {
            OutputModel output = (OutputModel)readModel(output_config);
            if (output != null) {
                _outputs.add(output);
            }
        }
        setModelChildrenOrder(OUTPUT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<OutputModel> getOutputs() {
        return Collections.unmodifiableList(_outputs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutputsModel addOutput(OutputModel output) {
        addChildModel(output);
        _outputs.add(output);
        return this;
    }

}
