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
package org.switchyard.component.camel.config.model.sql.v1;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import org.apache.camel.util.UnsafeUriCharactersEncoder;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;
import org.switchyard.component.camel.config.model.sql.CamelSqlBindingModel;
import org.switchyard.component.camel.config.model.v1.V1BaseCamelModelTest;
import org.switchyard.config.model.Validation;

/**
 * Test for {@link V1CamelSqlBindingModel}.
 *
 * @author Lukasz Dywicki
 */
public class V1CamelSqlBindingModelTest extends V1BaseCamelModelTest<V1CamelSqlBindingModel> {

    private static final String CAMEL_XML = "switchyard-sql-binding-beans.xml";


    private static final String COMPONENT_URI = UnsafeUriCharactersEncoder.encode(
        "sql://INSERT INTO events VALUES(?, ?, ?)?dataSourceRef=myDS&batch=false&placeholder=?"
    );

    private static final String QUERY = "INSERT INTO events VALUES(?, ?, ?)";
    private static final String DATA_SOURCE_REF = "myDS";
    private static final Boolean BATCH = false;
    private static final String PLACEHOLDER = "?";

    @Test
    public void validateCamelBinding() throws Exception {
        final CamelSqlBindingModel bindingModel = getFirstCamelReferenceBinding(CAMEL_XML);
        final Validation validateModel = bindingModel.validateModel();

        assertTrue(validateModel.isValid());
        assertModel(bindingModel);
        assertEquals(COMPONENT_URI, bindingModel.getComponentURI().toString());
    }

    @Test
    public void verifyProtocol() {
        V1CamelSqlBindingModel model = new V1CamelSqlBindingModel();
        model.setQuery(QUERY);
        model.setDataSourceRef(DATA_SOURCE_REF);

        String uri = model.getComponentURI().toString();
        assertTrue(uri.startsWith(V1CamelSqlBindingModel.SQL + "://"));
        assertTrue(model.validateModel().isValid());
    }


    @Test
    public void compareWriteConfig() throws Exception {
        String refXml = getFirstCamelReferenceBinding(CAMEL_XML).toString();
        String newXml = createModel().toString();
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(refXml, newXml);
        assertTrue(diff.toString(), diff.similar());
    }

    private V1CamelSqlBindingModel createModel() {
        return (V1CamelSqlBindingModel) new V1CamelSqlBindingModel()
            .setQuery(QUERY)
            .setDataSourceRef(DATA_SOURCE_REF)
            .setBatch(BATCH)
            .setPlaceholder(PLACEHOLDER);
    }

    private void assertModel(CamelSqlBindingModel model) {
        assertEquals(QUERY, model.getQuery());
        assertEquals(DATA_SOURCE_REF, model.getDataSourceRef());
        assertEquals(BATCH, model.isBatch());
        assertEquals(PLACEHOLDER, model.getPlaceholder());
    }

}