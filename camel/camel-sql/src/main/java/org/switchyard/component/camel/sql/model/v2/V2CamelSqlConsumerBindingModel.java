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
package org.switchyard.component.camel.sql.model.v2;

import org.switchyard.component.camel.common.model.v1.V1BaseCamelModel;
import org.switchyard.component.camel.sql.model.CamelSqlConsumerBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * Implementation of sql consumer configuration binding.
 *
 * @author David Virgil Naranjo
 */
public class V2CamelSqlConsumerBindingModel extends V1BaseCamelModel implements CamelSqlConsumerBindingModel {

    private static final String DELAY = "consumer.delay";
    private static final String INITIAL_DELAY = "consumer.initialDelay";

    protected V2CamelSqlConsumerBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
        setModelChildrenOrder(INITIAL_DELAY, DELAY);
    }

    /**
     * Creates new binding model.
     *
     * @param namespace
     *            namespace
     */
    public V2CamelSqlConsumerBindingModel(String namespace) {
        super(namespace, V2CamelSqlBindingModel.CONSUME);
    }

    @Override
    public Long getDelay() {
        return getLongConfig(DELAY);
    }

    @Override
    public V2CamelSqlConsumerBindingModel setDelay(Long delay) {
        return setConfig(DELAY, delay);
    }

    @Override
    public Long getInitialDelay() {
        return getLongConfig(INITIAL_DELAY);
    }

    @Override
    public V2CamelSqlConsumerBindingModel setInitialDelay(Long initialDelay) {
        return setConfig(INITIAL_DELAY, initialDelay);
    }




}
