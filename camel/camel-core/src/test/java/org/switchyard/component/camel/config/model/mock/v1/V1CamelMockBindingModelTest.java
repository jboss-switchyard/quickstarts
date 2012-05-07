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
package org.switchyard.component.camel.config.model.mock.v1;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.processor.ThroughputLogger;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.component.camel.config.model.mock.CamelMockBindingModel;
import org.switchyard.component.camel.config.model.v1.V1BaseCamelModelTest;
import org.switchyard.component.camel.config.model.v1.V1CamelBindingModel;
import org.switchyard.config.model.Validation;

/**
 * Test for {@link V1CamelBindingModel}.
 *
 * @author Mario Antollini
 */
public class V1CamelMockBindingModelTest extends V1BaseCamelModelTest<V1CamelMockBindingModel> {

    private static final String CAMEL_XML = "switchyard-mock-binding-beans.xml";

    private static final String NAME = "fooMockName";
    private static final Integer REPORT_GROUP = new Integer(999);

    private static final String CAMEL_URI = "mock://fooMockName?reportGroup=999";

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testConfigOverride() {
        // Set a value on an existing config element
        CamelMockBindingModel bindingModel = createMockModel();
        assertEquals(NAME, bindingModel.getName());
        bindingModel.setName("newFooMockName");
        assertEquals("newFooMockName", bindingModel.getName());
    }

    @Test
    public void testReadConfig() throws Exception {
        final CamelMockBindingModel bindingModel = getFirstCamelBinding(CAMEL_XML);
        final Validation validateModel = bindingModel.validateModel();
        //Valid Model?
        assertEquals(validateModel.isValid(), true);
        //Camel Mock
        assertEquals(bindingModel.getName(), NAME);
        assertEquals(bindingModel.getReportGroup(), REPORT_GROUP);
        //URI
        assertEquals(bindingModel.getComponentURI().toString(), CAMEL_URI);
    }

    @Test
    public void testWriteConfig() throws Exception {
        CamelMockBindingModel bindingModel = createMockModel();
        final Validation validateModel = bindingModel.validateModel();
        //Valid Model?
        assertEquals(validateModel.isValid(), true);
        //Camel Mock
        assertEquals(bindingModel.getName(), NAME);
        assertEquals(bindingModel.getReportGroup(), REPORT_GROUP);
        //URI
        assertEquals(bindingModel.getComponentURI().toString(), CAMEL_URI);
    }

    @Test
    public void compareWriteConfig() throws Exception {
        String refXml = getFirstCamelBinding(CAMEL_XML).toString();
        String newXml = createMockModel().toString();
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(refXml, newXml);
        assertTrue(diff.toString(), diff.similar());
    }

    @Test
    public void testCamelEndpoint() {
        CamelMockBindingModel model = createMockModel();
        MockEndpoint endpoint = getEndpoint(model, MockEndpoint.class);
        //assertEquals(endpoint.getId(), NAME); //No way to get the endpoint name
        ThroughputLogger logger = (ThroughputLogger)endpoint.getReporter();
        assertEquals(logger.getGroupSize(), REPORT_GROUP);
        assertEquals(endpoint.getEndpointUri().toString(), CAMEL_URI);
    }

    private CamelMockBindingModel createMockModel() {
        return new V1CamelMockBindingModel().setName(NAME).setReportGroup(REPORT_GROUP);
    }

}