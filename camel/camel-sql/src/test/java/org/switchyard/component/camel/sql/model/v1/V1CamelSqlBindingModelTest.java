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