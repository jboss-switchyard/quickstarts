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
package org.switchyard.component.camel.config.model.v1;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.net.URI;
import java.net.URISyntaxException;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.switchyard.config.model.composite.BindingModel;

/**
 * Test for {@link V1CamelBindingModel}.
 * 
 * @author Daniel Bevenius
 */
public class V1CamelBindingModelTest extends V1BaseCamelModelTest<V1CamelBindingModel> {

    private static boolean oldIgnoreWhitespace;

    @BeforeClass
    public static void setup() {
        oldIgnoreWhitespace = XMLUnit.getIgnoreWhitespace();
        XMLUnit.setIgnoreWhitespace(true);
    }

    @AfterClass
    public static void cleanup() {
        XMLUnit.setIgnoreWhitespace(oldIgnoreWhitespace);
    }

    @Test
    public void validateProgrammaticConfig() throws Exception {
        final V1CamelBindingModel bindingModel = createModel();
        validateDefaults(bindingModel);
    }

    @Test
    public void validateXmlConfig() throws Exception {
        final V1CamelBindingModel bindingModel = getFirstCamelModelBinding("switchyard-camel-binding-beans.xml");
        validateDefaults(bindingModel);
    }

    @Test
    public void validateCamelBindingModelWithReference() throws Exception {
        final V1CamelBindingModel bindingModel = getFirstCamelModelBinding("switchyard-camel-ref-beans.xml");
        validateModel(bindingModel);
        assertThat(bindingModel, is(instanceOf(V1CamelBindingModel.class)));
    }

    @Test
    public void readWriteConfig() throws Exception {
        final String control = getFirstCamelModelBinding("switchyard-camel-binding-beans.xml").toString();
        final String test = createModel().toString();
        XMLAssert.assertXMLEqual(control, test);
    }

    private V1CamelBindingModel createModel() throws URISyntaxException {
        final V1CamelBindingModel bindingModel = new V1CamelBindingModel()
        .setConfigURI(new URI("direct://input"));
        return bindingModel;
    }

    private void validateDefaults(final V1CamelBindingModel model) {
        validateModel(model);
        assertThat(model.getType(), is("camel"));
        assertThat(model.getConfigURI().getScheme(), is("direct"));
    }

    private void validateModel(final BindingModel model) {
    	model.validateModel().assertValid();
        assertThat(model.validateModel().isValid(), is(true));
    }

}
