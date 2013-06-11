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
package org.switchyard.component.camel.config.model.seda.v1;

import static junit.framework.Assert.assertEquals;

import org.apache.camel.component.seda.SedaEndpoint;
import org.switchyard.component.camel.config.test.v1.V1BaseCamelServiceBindingModelTest;
import org.switchyard.component.camel.core.model.seda.v1.V1CamelSedaBindingModel;
import org.switchyard.component.camel.core.model.v1.V1CamelBindingModel;

/**
 * Test for {@link V1CamelBindingModel}.
 *
 * @author Mario Antollini
 */
public class V1CamelSedaBindingModelTest extends V1BaseCamelServiceBindingModelTest<V1CamelSedaBindingModel, SedaEndpoint> {

    private static final String CAMEL_XML = "switchyard-seda-binding-beans.xml";

    private static final String NAME = "fooSedaName";
    private static final Integer SIZE = new Integer(55);
    private static final Integer CONCURRENT_CONSUMERS = new Integer(3);
    private static final String WAIT_FOR_TASK_TO_COMPLETE = "Always";
    private static final Long TIMEOUT = new Long(1000);
    private static final Boolean MULTIPLE_CONSUMERS = Boolean.TRUE;
    private static final Boolean LIMIT_CONCURRENT_CONSUMERS = Boolean.FALSE;

    private static final String CAMEL_URI = "seda://fooSedaName?size=55" +
        "&waitForTaskToComplete=Always&concurrentConsumers=3" +
        "&timeout=1000&multipleConsumers=true&limitConcurrentConsumers=false";

    public V1CamelSedaBindingModelTest() {
        super(SedaEndpoint.class, CAMEL_XML);
    }

    @Override
    protected void createModelAssertions(V1CamelSedaBindingModel model) {
        assertEquals(NAME, model.getEndpointName());
        assertEquals(SIZE, model.getSize());
        assertEquals(CONCURRENT_CONSUMERS, model.getConcurrentConsumers());
        assertEquals(WAIT_FOR_TASK_TO_COMPLETE, model.getWaitForTaskToComplete());
        assertEquals(TIMEOUT, model.getTimeout());
        assertEquals(MULTIPLE_CONSUMERS, model.isMultipleConsumers());
        assertEquals(LIMIT_CONCURRENT_CONSUMERS, model.isLimitConcurrentConsumers());
    }

    @Override
    protected V1CamelSedaBindingModel createTestModel() {
        return new V1CamelSedaBindingModel().setEndpointName(NAME)
            .setSize(SIZE)
            .setConcurrentConsumers(CONCURRENT_CONSUMERS)
            .setWaitForTaskToComplete(WAIT_FOR_TASK_TO_COMPLETE)
            .setTimeout(TIMEOUT)
            .setMultipleConsumers(MULTIPLE_CONSUMERS)
            .setLimitConcurrentConsumers(LIMIT_CONCURRENT_CONSUMERS);
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }

}