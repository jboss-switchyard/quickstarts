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
package org.switchyard.component.camel.ftp.model.v1;

import static org.junit.Assert.assertEquals;

import org.apache.camel.component.file.remote.FtpsEndpoint;
import org.switchyard.component.camel.config.test.v1.V1BaseCamelServiceBindingModelTest;
import org.switchyard.component.camel.ftp.model.CamelFtpNamespace;
import org.switchyard.component.camel.ftp.model.v1.V1CamelFtpsBindingModel;

/**
 * Test for {@link V1CamelFtpsBindingModel}.
 * 
 * @author Lukasz Dywicki
 */
public class V1CamelFtpsBindingModelTest extends V1BaseCamelServiceBindingModelTest<V1CamelFtpsBindingModel, FtpsEndpoint> {

    private static final String CAMEL_XML = "/v1/switchyard-ftps-binding-beans.xml";
    private static final String DIRECTORY = "test";
    private static final String HOST = "localhost";
    private static final Boolean IMPLICT = false;
    private static final String CAMEL_URI = "ftps://localhost/test?isImplicit=false";

    public V1CamelFtpsBindingModelTest() {
        super(FtpsEndpoint.class, CAMEL_XML);
    }

    @Override
    protected V1CamelFtpsBindingModel createTestModel() {
        V1CamelFtpsBindingModel model = (V1CamelFtpsBindingModel) new V1CamelFtpsBindingModel(CamelFtpNamespace.V_1_0.uri())
            .setDirectory(DIRECTORY)
            .setHost(HOST);

        return model.setImplict(IMPLICT);
    }

    @Override
    protected void createModelAssertions(V1CamelFtpsBindingModel model) {
        assertEquals(DIRECTORY, model.getDirectory());
        assertEquals(HOST, model.getHost());
        assertEquals(IMPLICT, model.isImplict());
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }

}
