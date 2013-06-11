/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.camel.quartz.model.v1;

import static junit.framework.Assert.assertEquals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.component.quartz.QuartzEndpoint;
import org.switchyard.component.camel.config.test.v1.V1BaseCamelServiceBindingModelTest;

/**
 * Test for {@link V1CamelQuartzBindingModel}.
 *
 * @author Lukasz Dywicki
 */
public class V1CamelQuartzBindingModelTest extends V1BaseCamelServiceBindingModelTest<V1CamelQuartzBindingModel, QuartzEndpoint> {

    private static final String CAMEL_XML = "/v1/switchyard-quartz-binding-beans.xml";

    // Used for dateTime fields
    private static DateFormat _dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private static final String NAME = "MyJob";
    private static final String CRON = "0 0 12 * * ?";
    private static final Boolean STATEFUL = true;
    private static Date START_TIME;
    private static Date END_TIME;

    private static final String CAMEL_URI = "quartz://MyJob?cron=0 0 12 * * ?&stateful=true" +
        "&trigger.startTime=2011-01-01T12:00:00&trigger.endTime=2011-01-01T12:00:00";

    static {
        try {
            START_TIME = _dateFormat.parse("2011-01-01T12:00:00");
            END_TIME = _dateFormat.parse("2011-01-01T12:00:00");
        } catch (Exception e) { /* ignore */ }
    }

    public V1CamelQuartzBindingModelTest() {
        super(QuartzEndpoint.class, CAMEL_XML);

        setSkipCamelEndpointTesting(true);
    }

    @Override
    protected V1CamelQuartzBindingModel createTestModel() {
        return (V1CamelQuartzBindingModel) new V1CamelQuartzBindingModel()
            .setTimerName(NAME)
            .setCron(CRON)
            .setStateful(STATEFUL)
            .setStartTime(START_TIME)
            .setEndTime(END_TIME);
    }

    @Override
    protected void createModelAssertions(V1CamelQuartzBindingModel model) {
        assertEquals(NAME, model.getTimerName());
        assertEquals(CRON, model.getCron());
        assertEquals(STATEFUL, model.isStateful());
        assertEquals(START_TIME, model.getStartTime());
        assertEquals(END_TIME, model.getEndTime());
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }

}