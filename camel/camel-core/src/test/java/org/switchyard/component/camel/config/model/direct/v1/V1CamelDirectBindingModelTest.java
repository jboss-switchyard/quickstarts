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
package org.switchyard.component.camel.config.model.direct.v1;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import org.apache.camel.component.direct.DirectEndpoint;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.component.camel.config.model.direct.CamelDirectBindingModel;
import org.switchyard.component.camel.config.model.v1.V1BaseCamelModelTest;
import org.switchyard.component.camel.config.model.v1.V1CamelBindingModel;
import org.switchyard.config.model.Validation;

/**
 * Test for {@link V1CamelBindingModel}.
 *
 * @author Mario Antollini
 */
public class V1CamelDirectBindingModelTest extends V1BaseCamelModelTest<V1CamelDirectBindingModel> {

    private static final String CAMEL_XML = "switchyard-direct-binding-beans.xml";

    private static final String NAME = "fooDirectName";

    private static final String CAMEL_URI = "direct://fooDirectName";

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testConfigOverride() {
        // Set a value on an existing config element
        CamelDirectBindingModel bindingModel = createDirectModel();
        assertEquals(NAME, bindingModel.getName());
        bindingModel.setName("newFooDirectName");
        assertEquals("newFooDirectName", bindingModel.getName());
    }

    @Test
    public void testReadConfig() throws Exception {
        final CamelDirectBindingModel bindingModel = getFirstCamelBinding(CAMEL_XML);
        final Validation validateModel = bindingModel.validateModel();
        //Valid Model?
        assertEquals(validateModel.isValid(), true);
        //Camel Direct
        assertEquals(bindingModel.getName(), NAME);
        //URI
        assertEquals(bindingModel.getComponentURI().toString(), CAMEL_URI);
    }

    @Test
    public void testWriteConfig() throws Exception {
        CamelDirectBindingModel bindingModel = createDirectModel();
        final Validation validateModel = bindingModel.validateModel();
        //Valid Model?
        assertEquals(validateModel.isValid(), true);
        //Camel Direct
        assertEquals(bindingModel.getName(), NAME);
        //URI
        assertEquals(bindingModel.getComponentURI().toString(), CAMEL_URI);
    }

    @Test
    public void compareWriteConfig() throws Exception {
        String refXml = getFirstCamelBinding(CAMEL_XML).toString();
        String newXml = createDirectModel().toString();
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(refXml, newXml);
        assertTrue(diff.toString(), diff.similar());
    }

    @Test
    public void testCamelEndpoint() {
        DirectEndpoint endpoint = getEndpoint(createDirectModel(), DirectEndpoint.class);
        //assertEquals(endpoint.getId(), NAME);
        assertEquals(endpoint.getEndpointUri().toString(), CAMEL_URI);
    }

    private CamelDirectBindingModel createDirectModel() {
        return new V1CamelDirectBindingModel().setName(NAME);
    }

}