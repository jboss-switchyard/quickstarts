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

import static org.switchyard.component.common.knowledge.config.model.OperationModel.OPERATION;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.component.common.knowledge.config.model.OperationModel;
import org.switchyard.component.common.knowledge.config.model.OperationsModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * A version 1 OperationModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class V1OperationsModel extends BaseModel implements OperationsModel {

    private List<OperationModel> _operations = new ArrayList<OperationModel>();

    /**
     * Creates a new V1OperationsModel in the specified namespace.
     * @param namespace the specified namespace
     */
    public V1OperationsModel(String namespace) {
        super(new QName(namespace, OPERATIONS));
        setModelChildrenOrder(OPERATION);
    }

    /**
     * Creates a new V1OperationsModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1OperationsModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration operation_config : config.getChildren(OPERATION)) {
            OperationModel operation = (OperationModel)readModel(operation_config);
            if (operation != null) {
                _operations.add(operation);
            }
        }
        setModelChildrenOrder(OPERATION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<OperationModel> getOperations() {
        return Collections.unmodifiableList(_operations);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OperationsModel addOperation(OperationModel operation) {
        addChildModel(operation);
        _operations.add(operation);
        return this;
    }

}
