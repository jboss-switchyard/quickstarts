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
import org.switchyard.ServiceDomain;
import org.switchyard.component.camel.deploy.support.CustomPackageScanResolver;
import org.switchyard.deploy.internal.Deployment;
import org.switchyard.test.MockHandler;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;

/**
 * Test for {@link CamelActivator}.
 * 
 * @author Daniel Bevenius
 * 
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(config = "switchyard-activator-test.xml", mixins = CDIMixIn.class)
public class CamelActivatorTest {

    private ServiceDomain _domain;
    private SwitchYardTestKit _testKit;

    @Test
    public void sendOneWayMessageThroughCamelToSwitchYardService() throws Exception {
        // remove the currently registered service for SimpleCamelService
        _testKit.removeService("SimpleCamelService");
        final MockHandler mockHandler = _testKit.registerInOnlyService("SimpleCamelService");
        final CamelContext camelContext = getCamelContext();
        final ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        
        producerTemplate.sendBody("direct://input", "dummy payload");
        assertOneMessage(mockHandler, "dummy payload");
        
        producerTemplate.sendBody("direct://input2", "dummy payload");
        assertOneMessage(mockHandler, "dummy payload");
    }
    
    @Test
    public void setCustomClassPathResolver() {
        final CamelContext camelContext = getCamelContext();
        final PackageScanClassResolver p = camelContext.getPackageScanClassResolver();
        assertThat(p, is(instanceOf(CustomPackageScanResolver.class)));
    }

    @Ignore
    @Test
    public void startStop() throws Exception {
        final MockHandler mockHandler = _testKit.registerInOnlyService("SimpleCamelService");
        final CamelActivator activator = getCamelActivator();
        final CamelContext camelContext = activator.getCamelContext();
        final ProducerTemplate producerTemplate = camelContext.createProducerTemplate();

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

    private void assertOneMessage(final MockHandler mockHandler, final String expectedPayload)
    {
        mockHandler.waitForOKMessage();
        assertThat(mockHandler.getMessages().size(), is(1));
        final String content = mockHandler.getMessages().poll().getMessage().getContent(String.class);
        assertThat(content, is(equalTo("dummy payload")));
    }
    
    private CamelActivator getCamelActivator() {
        final Deployment deployment = (Deployment) _testKit.getDeployment();
        return (CamelActivator) deployment.findActivator("camel");
    }

    private CamelContext getCamelContext() {
        final CamelActivator activator = getCamelActivator();
        return activator.getCamelContext();
    }

}
