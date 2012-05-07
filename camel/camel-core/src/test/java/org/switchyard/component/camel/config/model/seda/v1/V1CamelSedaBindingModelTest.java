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
import static junit.framework.Assert.assertTrue;

import org.apache.camel.component.seda.SedaEndpoint;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.component.camel.config.model.seda.CamelSedaBindingModel;
import org.switchyard.component.camel.config.model.v1.V1BaseCamelModelTest;
import org.switchyard.component.camel.config.model.v1.V1CamelBindingModel;
import org.switchyard.config.model.Validation;

/**
 * Test for {@link V1CamelBindingModel}.
 *
 * @author Mario Antollini
 */
public class V1CamelSedaBindingModelTest extends V1BaseCamelModelTest<V1CamelSedaBindingModel> {

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

    private static final String CAMEL_ENDPOINT_URI = "seda://fooSedaName?" +
        "concurrentConsumers=3&limitConcurrentConsumers=false&multipleConsumers=true&" +
        "size=55&timeout=1000&waitForTaskToComplete=Always";

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testConfigOverride() {
        // Set a value on an existing config element
        CamelSedaBindingModel bindingModel = createSedaModel();
        assertEquals(NAME, bindingModel.getName());
        bindingModel.setName("newFooSedaName");
        assertEquals("newFooSedaName", bindingModel.getName());
    }

    @Test
    public void testReadConfig() throws Exception {
        final CamelSedaBindingModel bindingModel = getFirstCamelBinding(CAMEL_XML);
        final Validation validateModel = bindingModel.validateModel();
        //Valid Model?
        assertEquals(validateModel.isValid(), true);
        //Camel Seda
        assertEquals(bindingModel.getName(), NAME);
        assertEquals(bindingModel.getSize(), SIZE);
        assertEquals(bindingModel.getConcurrentConsumers(), CONCURRENT_CONSUMERS);
        assertEquals(bindingModel.getWaitForTaskToComplete(), WAIT_FOR_TASK_TO_COMPLETE);
        assertEquals(bindingModel.getTimeout(), TIMEOUT);
        assertEquals(bindingModel.isMultipleConsumers(), MULTIPLE_CONSUMERS);
        assertEquals(bindingModel.isLimitConcurrentConsumers(), LIMIT_CONCURRENT_CONSUMERS);
        //URI
        assertEquals(bindingModel.getComponentURI().toString(), CAMEL_URI);
    }

    @Test
    public void testWriteConfig() throws Exception {
        CamelSedaBindingModel bindingModel = createSedaModel();
        final Validation validateModel = bindingModel.validateModel();
        //Valid Model?
        assertEquals(validateModel.isValid(), true);
        //Camel Seda
        assertEquals(bindingModel.getName(), NAME);
        assertEquals(bindingModel.getSize(), SIZE);
        assertEquals(bindingModel.getConcurrentConsumers(), CONCURRENT_CONSUMERS);
        assertEquals(bindingModel.getWaitForTaskToComplete(), WAIT_FOR_TASK_TO_COMPLETE);
        assertEquals(bindingModel.getTimeout(), TIMEOUT);
        assertEquals(bindingModel.isMultipleConsumers(), MULTIPLE_CONSUMERS);
        assertEquals(bindingModel.isLimitConcurrentConsumers(), LIMIT_CONCURRENT_CONSUMERS);
        //URI
        assertEquals(bindingModel.getComponentURI().toString(), CAMEL_URI);
    }

    @Test
    public void compareWriteConfig() throws Exception {
        String refXml = getFirstCamelBinding(CAMEL_XML).toString();
        String newXml = createSedaModel().toString();
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(refXml, newXml);
        assertTrue(diff.toString(), diff.similar());
    }

    @Test
    public void testCamelEndpoint() {
        CamelSedaBindingModel model = createSedaModel();
        SedaEndpoint endpoint = getEndpoint(model, SedaEndpoint.class);
        //assertEquals(endpoint.getId(), NAME); //No way to get the endpoint name
        assertEquals(endpoint.getSize(), SIZE.intValue());
        assertEquals(endpoint.getConcurrentConsumers(), CONCURRENT_CONSUMERS.intValue());
        assertEquals(endpoint.getWaitForTaskToComplete().toString(), WAIT_FOR_TASK_TO_COMPLETE);
        assertEquals(endpoint.getTimeout(), TIMEOUT.longValue());
        assertEquals(endpoint.isMultipleConsumers(), MULTIPLE_CONSUMERS.booleanValue());
        assertEquals(endpoint.isMultipleConsumersSupported() , !LIMIT_CONCURRENT_CONSUMERS.booleanValue());
        assertEquals(endpoint.getEndpointUri().toString(), CAMEL_ENDPOINT_URI);
    }

    private CamelSedaBindingModel createSedaModel() {
        return new V1CamelSedaBindingModel().setName(NAME)
            .setSize(SIZE)
            .setConcurrentConsumers(CONCURRENT_CONSUMERS)
            .setWaitForTaskToComplete(WAIT_FOR_TASK_TO_COMPLETE)
            .setTimeout(TIMEOUT)
            .setMultipleConsumers(MULTIPLE_CONSUMERS)
            .setLimitConcurrentConsumers(LIMIT_CONCURRENT_CONSUMERS);
    }

}