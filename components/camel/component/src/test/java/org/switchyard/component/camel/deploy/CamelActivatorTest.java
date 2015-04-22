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
package org.switchyard.component.camel.deploy;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.spi.PackageScanClassResolver;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.camel.deploy.support.CustomPackageScanResolver;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.test.MockHandler;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;

/**
 * Test for {@link CamelActivator}.
 * 
 * @author Daniel Bevenius
 * 
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(config = "switchyard-activator-test.xml", mixins = CDIMixIn.class)
public class CamelActivatorTest {

    private SwitchYardTestKit _testKit;
    private CamelContext _camelContext;

    @Test
    public void sendOneWayMessageThroughCamelToSwitchYardService() throws Exception {
        // remove the currently registered service for SimpleCamelService
        _testKit.removeService("SimpleCamelService");
        final MockHandler mockHandler = _testKit.registerInOnlyService("SimpleCamelService");
        final ProducerTemplate producerTemplate = _camelContext.createProducerTemplate();
        
        producerTemplate.sendBody("direct://input", "dummy payload");
        assertOneMessage(mockHandler, "dummy payload");
        
        producerTemplate.sendBody("direct://input2", "dummy payload");
        assertOneMessage(mockHandler, "dummy payload");
    }

    @Test
    public void setCustomClassPathResolver() {
        final PackageScanClassResolver p = _camelContext.getPackageScanClassResolver();
        assertThat(p, is(instanceOf(CustomPackageScanResolver.class)));
    }

    @Ignore
    @Test
    public void startStop() throws Exception {
        final MockHandler mockHandler = _testKit.registerInOnlyService("SimpleCamelService");
        final ProducerTemplate producerTemplate = _camelContext.createProducerTemplate();

        producerTemplate.sendBody("direct://input", "dummy payload");
        assertOneMessage(mockHandler, "dummy payload");

        // Stop the camel components for the service...

        try {
            producerTemplate.sendBody("direct://input2", "dummy payload");
            Assert.fail("Expected CamelExecutionException.");
        } catch (CamelExecutionException e) {
            // Expected....
        }

        // Restart the camel components for the service...
        producerTemplate.sendBody("direct://input", "dummy payload");
        assertOneMessage(mockHandler, "dummy payload");
    }

    private void assertOneMessage(final MockHandler mockHandler, final String expectedPayload) {
        mockHandler.waitForOKMessage();
        assertThat(mockHandler.getMessages().size(), is(1));
        final String content = mockHandler.getMessages().poll().getMessage().getContent(String.class);
        assertThat(content, is(equalTo("dummy payload")));
    }

}
