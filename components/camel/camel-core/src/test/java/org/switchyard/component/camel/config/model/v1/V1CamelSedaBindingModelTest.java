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
package org.switchyard.component.camel.config.model.v1;

import static junit.framework.Assert.assertEquals;

import org.apache.camel.component.seda.SedaEndpoint;
import org.switchyard.component.camel.config.test.v1.V1BaseCamelServiceBindingModelTest;
import org.switchyard.component.camel.core.model.CamelCoreNamespace;
import org.switchyard.component.camel.core.model.v1.V1CamelUriBindingModel;
import org.switchyard.component.camel.core.model.v1.V1CamelSedaBindingModel;

/**
 * Test for {@link V1CamelUriBindingModel}.
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
        return new V1CamelSedaBindingModel(CamelCoreNamespace.V_1_0.uri()).setEndpointName(NAME)
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