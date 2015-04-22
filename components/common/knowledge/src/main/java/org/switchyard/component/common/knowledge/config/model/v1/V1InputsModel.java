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

import static org.switchyard.component.common.knowledge.config.model.InputModel.INPUT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.common.knowledge.config.model.InputModel;
import org.switchyard.component.common.knowledge.config.model.InputsModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * A version 1 InputsModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class V1InputsModel extends BaseModel implements InputsModel {

    private List<InputModel> _inputs = new ArrayList<InputModel>();

    /**
     * Creates a new V1InputsModel in the specified namespace.
     * @param namespace the specified namespace
     */
    public V1InputsModel(String namespace) {
        super(XMLHelper.createQName(namespace, INPUTS));
        setModelChildrenOrder(INPUT);
    }

    /**
     * Creates a new V1InputsModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1InputsModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration input_config : config.getChildren(INPUT)) {
            InputModel input = (InputModel)readModel(input_config);
            if (input != null) {
                _inputs.add(input);
            }
        }
        setModelChildrenOrder(INPUT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<InputModel> getInputs() {
        return Collections.unmodifiableList(_inputs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputsModel addInput(InputModel input) {
        addChildModel(input);
        _inputs.add(input);
        return this;
    }

}
