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
package org.switchyard.config.model.switchyard.v1;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.switchyard.ThrottlingModel;

/**
 * Implementation of ThrottlingModel : v1.
 */
public class V1ThrottlingModel extends BaseModel implements ThrottlingModel {

    /**
     * Constructs a new V1ThrottlingModel.
     */
    public V1ThrottlingModel() {
        super(new QName(SwitchYardModel.DEFAULT_NAMESPACE, ThrottlingModel.THROTTLING));
    }

    /**
     * Constructs a new V1ThrottlingModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1ThrottlingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getTimePeriod() {
        final String value = getModelAttribute(ThrottlingModel.TIME_PERIOD);
        return value == null ? null : Long.valueOf(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ThrottlingModel setTimePeriod(Long timePeriod) {
        setModelAttribute(ThrottlingModel.TIME_PERIOD, timePeriod == null ? null : timePeriod.toString());
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxRequests() {
        return Integer.valueOf(getModelAttribute(ThrottlingModel.MAX_REQUESTS));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ThrottlingModel setMaxRequests(int maxRequests) {
        setModelAttribute(ThrottlingModel.MAX_REQUESTS, String.valueOf(maxRequests));
        return this;
    }

}
