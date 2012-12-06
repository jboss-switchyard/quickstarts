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
package org.switchyard.component.camel.sql.model.v1;

import static junit.framework.Assert.assertEquals;

import org.apache.camel.component.sql.SqlEndpoint;
import org.switchyard.component.camel.config.test.v1.V1BaseCamelReferenceBindingModelTest;

/**
 * Test for {@link V1CamelSqlBindingModel}.
 *
 * @author Lukasz Dywicki
 */
public class V1CamelSqlBindingModelTest extends V1BaseCamelReferenceBindingModelTest<V1CamelSqlBindingModel, SqlEndpoint> {

    private static final String CAMEL_XML = "/v1/switchyard-sql-binding-beans.xml";
    private static final String CAMEL_URI = "sql://INSERT INTO events VALUES(?, ?, ?)?dataSourceRef=myDS&batch=false&placeholder=?";

    private static final String QUERY = "INSERT INTO events VALUES(?, ?, ?)";
    private static final String DATA_SOURCE_REF = "myDS";
    private static final Boolean BATCH = false;
    private static final String PLACEHOLDER = "?";

    public V1CamelSqlBindingModelTest() {
        super(SqlEndpoint.class, CAMEL_XML);

        setSkipCamelEndpointTesting(true);
    }

    @Override
    protected V1CamelSqlBindingModel createTestModel() {
        return new V1CamelSqlBindingModel()
            .setQuery(QUERY)
            .setDataSourceRef(DATA_SOURCE_REF)
            .setBatch(BATCH)
            .setPlaceholder(PLACEHOLDER);
    }

    @Override
    protected void createModelAssertions(V1CamelSqlBindingModel model) {
        assertEquals(QUERY, model.getQuery());
        assertEquals(DATA_SOURCE_REF, model.getDataSourceRef());
        assertEquals(BATCH, model.isBatch());
        assertEquals(PLACEHOLDER, model.getPlaceholder());
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }

}