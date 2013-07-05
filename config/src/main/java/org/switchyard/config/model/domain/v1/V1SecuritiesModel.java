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
package org.switchyard.config.model.domain.v1;

import static org.switchyard.config.model.domain.SecurityModel.SECURITY;
import static org.switchyard.config.model.switchyard.SwitchYardModel.DEFAULT_NAMESPACE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.domain.DomainModel;
import org.switchyard.config.model.domain.SecuritiesModel;
import org.switchyard.config.model.domain.SecurityModel;

/**
 * A version 1 SecuritiesModel.
 */
public class V1SecuritiesModel extends BaseModel implements SecuritiesModel {

    private List<SecurityModel> _securities = new ArrayList<SecurityModel>();

    /**
     * Constructs a new V1SecuritiesModel.
     */
    public V1SecuritiesModel() {
        super(new QName(DEFAULT_NAMESPACE, SECURITIES));
        setModelChildrenOrder(SECURITY);
    }

    /**
     * Constructs a new V1SecuritiesModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1SecuritiesModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration security_config : config.getChildrenStartsWith(SECURITY)) {
            SecurityModel security = (SecurityModel)readModel(security_config);
            if (security != null) {
                _securities.add(security);
            }
        }
        setModelChildrenOrder(SECURITY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DomainModel getDomain() {
        return (DomainModel)getModelParent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<SecurityModel> getSecurities() {
        return Collections.unmodifiableList(_securities);
    }

}
