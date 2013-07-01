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
package org.switchyard.component.camel.common.model.v1;

import org.switchyard.component.camel.common.model.consumer.CamelScheduledBatchPollConsumer;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * Implementation of 1st batching poll consumer interface.
 * 
 * @author Lukasz Dywicki
 */
public class V1CamelScheduledBatchPollConsumer extends V1CamelScheduledPollConsumer 
    implements CamelScheduledBatchPollConsumer {

    /**
     * The name of the 'maxMessagesPerPoll' element.
     */
    private static final String MAX_MESSAGES_PER_POLL = "maxMessagesPerPoll";

    /**
     * Creates model bound to given namespace.
     * 
     * @param name Name of element.
     * @param namespace Namespace to bound.
     */
    public V1CamelScheduledBatchPollConsumer(String name, String namespace) {
        super(name, namespace);

        setModelChildrenOrder(MAX_MESSAGES_PER_POLL);
    }

    /**
     * Constructor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V1CamelScheduledBatchPollConsumer(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public Integer getMaxMessagesPerPoll() {
        return getIntegerConfig(MAX_MESSAGES_PER_POLL);
    }

    @Override
    public V1CamelScheduledBatchPollConsumer setMaxMessagesPerPoll(Integer maxMessagesPerPoll) {
        return setConfig(MAX_MESSAGES_PER_POLL, maxMessagesPerPoll);
    }

}
