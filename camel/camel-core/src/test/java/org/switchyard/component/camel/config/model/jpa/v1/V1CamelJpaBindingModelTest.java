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
package org.switchyard.component.camel.config.model.jpa.v1;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import org.apache.camel.component.jpa.JpaEndpoint;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;
import org.switchyard.component.camel.config.model.jpa.CamelJpaBindingModel;
import org.switchyard.component.camel.config.model.v1.V1BaseCamelModelTest;
import org.switchyard.config.model.Validation;

/**
 * Test for {@link V1CamelJpaBindingModel}.
 * 
 * @author Lukasz Dywicki
 */
public class V1CamelJpaBindingModelTest extends V1BaseCamelModelTest<V1CamelJpaBindingModel> {

    private static final String CAMEL_XML = "switchyard-jpa-binding-beans.xml";
    private static final String COMPONENT_URI = "jpa://some.clazz.Name?persistenceUnit=MyPU";
    static final String ENTITY_CLASS_NAME = "some.clazz.Name";
    static final String PERSISTENCE_UNIT = "MyPU";

    @Test
    public void validateCamelBindingModelWithBeanElement() throws Exception {
        final V1CamelJpaBindingModel bindingModel = getFirstCamelBinding(CAMEL_XML);
        final Validation validateModel = bindingModel.validateModel();

        validateModel.assertValid();
        assertTrue(validateModel.isValid());
        assertModel(bindingModel);
        assertEquals(COMPONENT_URI, bindingModel.getComponentURI().toString());
    }

    @Test
    public void compareWriteConfig() throws Exception {
        String refXml = getFirstCamelBinding(CAMEL_XML).toString();
        String newXml = createModel().toString();
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(refXml, newXml);
        assertTrue(diff.toString(), diff.similar());
    }

    @Test
    public void testCamelEndpoint()  throws Exception {
        CamelJpaBindingModel model = getFirstCamelBinding(CAMEL_XML);

        JpaEndpoint endpoint = getEndpoint(model, JpaEndpoint.class);
        assertEquals(COMPONENT_URI, endpoint.getEndpointUri().toString());
    }

    private V1CamelJpaBindingModel createModel() {
        return (V1CamelJpaBindingModel) new V1CamelJpaBindingModel()
            .setEntityClassName(ENTITY_CLASS_NAME)
            .setPersistenceUnit(PERSISTENCE_UNIT);
    }

    private void assertModel(CamelJpaBindingModel model) {
        assertEquals(ENTITY_CLASS_NAME, model.getEntityClassName());
        assertEquals(PERSISTENCE_UNIT, model.getPersistenceUnit());
    }
}
