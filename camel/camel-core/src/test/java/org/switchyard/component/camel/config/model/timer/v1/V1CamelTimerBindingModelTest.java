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

package org.switchyard.component.camel.config.model.timer.v1;

import static junit.framework.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.component.timer.TimerEndpoint;
import org.switchyard.component.camel.config.test.v1.V1BaseCamelServiceBindingModelTest;
import org.switchyard.component.camel.core.model.timer.v1.V1CamelTimerBindingModel;
import org.switchyard.component.camel.core.model.v1.V1CamelBindingModel;

/**
 * Test for {@link V1CamelBindingModel}.
 * 
 * @author Mario Antollini
 */
public class V1CamelTimerBindingModelTest extends V1BaseCamelServiceBindingModelTest<V1CamelTimerBindingModel, TimerEndpoint> {

    private static final String CAMEL_XML = "switchyard-timer-binding-beans.xml";

    private static final String NAME = "fooTimer";
    private static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    private static final Long PERIOD = new Long(555);
    private static final Long DELAY = new Long(100);
    private static final Boolean FIXED_RATE = Boolean.TRUE;
    private static final Boolean DAEMON = Boolean.FALSE;

    private static final String CAMEL_URI = 
        "timer://fooTimer?time=2011-01-01T12:00:00&pattern=yyyy-MM-dd'T'HH:mm:ss&" +
        "period=555&delay=100&fixedRate=true&daemon=false";

    private Date referenceDate;

    public V1CamelTimerBindingModelTest() throws ParseException {
        super(TimerEndpoint.class, CAMEL_XML);

        referenceDate = new SimpleDateFormat(PATTERN).parse("2011-01-01T12:00:00");
    }

    @Override
    protected void createModelAssertions(V1CamelTimerBindingModel model) {
        assertEquals(NAME, model.getTimerName());
        assertEquals(referenceDate.toString(), model.getTime().toString());
        assertEquals(PATTERN, model.getPattern());
        assertEquals(PERIOD, model.getPeriod());
        assertEquals(DELAY, model.getDelay());
        assertEquals(FIXED_RATE, model.isFixedRate());
        assertEquals(DAEMON, model.isDaemon());
    }

    @Override
    protected V1CamelTimerBindingModel createTestModel() {
        return new V1CamelTimerBindingModel().setTimerName(NAME)
            .setTime(referenceDate)
            .setPattern(PATTERN)
            .setPeriod(PERIOD)
            .setDelay(DELAY)
            .setFixedRate(FIXED_RATE)
            .setDaemon(DAEMON);
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }

}
