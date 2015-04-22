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
package org.switchyard.component.resteasy.config.model.v2;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.switchyard.component.resteasy.config.model.ContextParamModel;
import org.switchyard.component.resteasy.config.model.ContextParamsModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * A version 2 ContextParamsModel.
 */
public class V2ContextParamsModel extends BaseModel implements ContextParamsModel {

    private List<ContextParamModel> _contextParams = new ArrayList<ContextParamModel>();

    /**
     * Creates a new ContextParamsModel in the specified namespace.
     * @param namespace the specified namespace
     */
    public V2ContextParamsModel(String namespace) {
        super(new QName(namespace, CONTEXT_PARAMS));
        setModelChildrenOrder(ContextParamModel.CONTEXT_PARAM);
    }

    /**
     * Creates a new ContextParamsModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V2ContextParamsModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration property_config : config.getChildren(ContextParamModel.CONTEXT_PARAM)) {
            ContextParamModel contextParam = (ContextParamModel) readModel(property_config);
            if (contextParam != null) {
                _contextParams.add(contextParam);
            }
        }
        setModelChildrenOrder(ContextParamModel.CONTEXT_PARAM);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<ContextParamModel> getContextParams() {
        return Collections.unmodifiableList(_contextParams);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> toMap() {
        List<ContextParamModel> contextParams = getContextParams();
        Map<String,String> map = new HashMap<String, String>(contextParams.size());

        for (ContextParamModel cp : contextParams) {
            String name = cp.getName();
            String value = cp.getValue();
            if (name != null && value != null) {
                map.put(name, value);
            }
        }
        return map;
    }

}
