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
package org.switchyard.component.camel.common.model.consumer;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;
import org.switchyard.component.camel.common.model.v1.V1CamelScheduledBatchPollConsumer;

/**
 * Test of scheduled batch poll consumer binding.
 * 
 * @author Lukasz Dywicki
 */
public class CamelScheduledBatchPollConsumerTest {

    private static final Integer MAX_MESSAGES_PER_POLL = 5;

    @Test
    public void testConfigOverride() {
        CamelScheduledBatchPollConsumer model = createModel();
        assertEquals(MAX_MESSAGES_PER_POLL, model.getMaxMessagesPerPoll());
    }

    private CamelScheduledBatchPollConsumer createModel() {
        return new V1CamelScheduledBatchPollConsumer("test", "test")
            .setMaxMessagesPerPoll(MAX_MESSAGES_PER_POLL);
    }
}
