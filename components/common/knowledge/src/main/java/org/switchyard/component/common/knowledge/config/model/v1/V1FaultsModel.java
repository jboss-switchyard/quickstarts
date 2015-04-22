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

import static org.switchyard.component.common.knowledge.config.model.FaultModel.FAULT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.common.knowledge.config.model.FaultModel;
import org.switchyard.component.common.knowledge.config.model.FaultsModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * A version 1 FaultsModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class V1FaultsModel extends BaseModel implements FaultsModel {

    private List<FaultModel> _faults = new ArrayList<FaultModel>();

    /**
     * Creates a new V1FaultsModel in the specified namespace.
     * @param namespace the specified namespace
     */
    public V1FaultsModel(String namespace) {
        super(XMLHelper.createQName(namespace, FAULTS));
        setModelChildrenOrder(FAULT);
    }

    /**
     * Creates a new V1FaultsModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1FaultsModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration fault_config : config.getChildren(FAULT)) {
            FaultModel fault = (FaultModel)readModel(fault_config);
            if (fault != null) {
                _faults.add(fault);
            }
        }
        setModelChildrenOrder(FAULT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<FaultModel> getFaults() {
        return Collections.unmodifiableList(_faults);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FaultsModel addFault(FaultModel fault) {
        addChildModel(fault);
        _faults.add(fault);
        return this;
    }

}
