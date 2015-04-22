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

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.switchyard.component.camel.common.model.v1.V1CamelScheduledPollConsumer;

/**
 * Test of scheduled poll consumer binding.
 * 
 * @author Lukasz Dywicki
 */
public class V1CamelScheduledPollConsumerTest {

    private static final Integer DELAY = 1000;
    private static final TimeUnit TIME_UNIT = TimeUnit.MINUTES;
    private static final Boolean SEND_EMPTY_MESSAGE_WHEN_IDLE = true;
    private static final Integer INITIAL_DELAY = 999;
    private static final Boolean USE_FIXED_DELAY = true;

    @Test
    public void testConfigOverride() {
        V1CamelScheduledPollConsumer model = createModel();
        assertEquals(DELAY, model.getDelay());
        assertEquals(INITIAL_DELAY, model.getInitialDelay());
        assertEquals(TIME_UNIT, model.getTimeUnit());
        assertEquals(SEND_EMPTY_MESSAGE_WHEN_IDLE, model.isSendEmptyMessageWhenIdle());
        assertEquals(USE_FIXED_DELAY, model.isUseFixedDelay());
        model.setDelay(750);
        assertEquals(new Integer(750), model.getDelay());
    }

    private V1CamelScheduledPollConsumer createModel() {
        return new V1CamelScheduledPollConsumer("test", "test")
            .setDelay(DELAY)
            .setInitialDelay(INITIAL_DELAY)
            .setTimeUnit(TIME_UNIT.name())
            .setSendEmptyMessageWhenIdle(SEND_EMPTY_MESSAGE_WHEN_IDLE)
            .setUseFixedDelay(USE_FIXED_DELAY);
    }
}
