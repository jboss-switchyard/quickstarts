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
package org.switchyard.component.camel.quartz.model.v2;

import static junit.framework.Assert.assertEquals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import org.apache.camel.component.quartz.QuartzEndpoint;
import org.junit.Test;
import org.switchyard.component.camel.config.test.v1.V1BaseCamelServiceBindingModelTest;
import org.switchyard.component.camel.quartz.model.CamelQuartzNamespace;

/**
 * Test for {@link V2CamelQuartzBindingModel}.
 *
 * @author Lukasz Dywicki
 */
public class V2CamelQuartzBindingModelTest extends V1BaseCamelServiceBindingModelTest<V2CamelQuartzBindingModel, QuartzEndpoint> {

    private static final String CAMEL_XML = "/v2/switchyard-quartz-binding-beans.xml";

    // Used for dateTime fields
    private static DateFormat _dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private static final String NAME = "MyJob";
    private static final String CRON = "0 0 12 * * ?";
    private static final Integer REPEAT_COUNT = new Integer(3);
    private static final Long REPEAT_INTERVAL = new Long(100000);
    private static final Boolean STATEFUL = true;
    private static Date START_TIME;
    private static Date END_TIME;
    private static String TIMEZONE;

    private static final String CAMEL_URI = "quartz://MyJob?cron=0 0 12 * * ?&stateful=true" +
        "&trigger.startTime=2011-01-01T12:00:00&trigger.endTime=2011-01-01T12:00:00&trigger.timeZone=America/New_York";

    static {
        try {
            START_TIME = _dateFormat.parse("2011-01-01T12:00:00");
            END_TIME = _dateFormat.parse("2011-01-01T12:00:00");
            TIMEZONE = "America/New_York";
        } catch (Exception e) { /* ignore */ }
    }

    public V2CamelQuartzBindingModelTest() {
        super(QuartzEndpoint.class, CAMEL_XML);

        setSkipCamelEndpointTesting(true);
    }

    @Override
    protected V2CamelQuartzBindingModel createTestModel() {
        final V2CamelQuartzBindingModel model = new V2CamelQuartzBindingModel(CamelQuartzNamespace.V_2_0.uri());
        model.setAdditionalUriParameters(createAdditionalUriParametersModel(CamelQuartzNamespace.V_2_0.uri(), Collections.singletonMap("trigger.timeZone", "GMT")));
        return (V2CamelQuartzBindingModel) model
            .setTimerName(NAME)
            .setCron(CRON)
            .setStateful(STATEFUL)
            .setStartTime(START_TIME)
            .setEndTime(END_TIME)
            .setTimeZone(TIMEZONE);
    }

    protected V2CamelQuartzBindingModel createAlternateTestModel() {
        final V2CamelQuartzBindingModel model = new V2CamelQuartzBindingModel(CamelQuartzNamespace.V_2_0.uri());
        model.setAdditionalUriParameters(createAdditionalUriParametersModel(CamelQuartzNamespace.V_2_0.uri(), Collections.singletonMap("trigger.timeZone", "GMT")));
        return (V2CamelQuartzBindingModel) model
            .setTimerName(NAME)
            .setRepeatCount(REPEAT_COUNT)
            .setRepeatInterval(REPEAT_INTERVAL)
            .setStateful(STATEFUL)
            .setStartTime(START_TIME)
            .setEndTime(END_TIME)
            .setTimeZone(TIMEZONE);
    }

    /**
     * Verify correctness of model created by test.
     *
     * @throws Exception If model can not be read
     */
    @Test
    public void testAlternateModelIsValid() throws Exception {
        validModel(createAlternateTestModel());
    }

    /**
     * Verify assertions with model binding.
     */
    @Test
    public void testModelAssertionsFromAlternateModel() {
        createAlternateModelAssertions(createAlternateTestModel());
    }

    @Override
    protected void createModelAssertions(V2CamelQuartzBindingModel model) {
        assertEquals(NAME, model.getTimerName());
        assertEquals(CRON, model.getCron());
        assertEquals(STATEFUL, model.isStateful());
        assertEquals(START_TIME, model.getStartTime());
        assertEquals(END_TIME, model.getEndTime());
        assertEquals(TIMEZONE, model.getTimeZone());
    }

    protected void createAlternateModelAssertions(V2CamelQuartzBindingModel model) {
        assertEquals(NAME, model.getTimerName());
        assertEquals(REPEAT_INTERVAL, model.getRepeatInterval());
        assertEquals(REPEAT_COUNT, model.getRepeatCount());
        assertEquals(STATEFUL, model.isStateful());
        assertEquals(START_TIME, model.getStartTime());
        assertEquals(END_TIME, model.getEndTime());
        assertEquals(TIMEZONE, model.getTimeZone());
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }

}
