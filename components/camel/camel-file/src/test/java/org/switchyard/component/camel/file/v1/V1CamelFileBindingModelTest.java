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
package org.switchyard.component.camel.file.v1;

import static junit.framework.Assert.assertEquals;

import org.apache.camel.component.file.FileEndpoint;
import org.switchyard.component.camel.config.test.v1.V1BaseCamelServiceBindingModelTest;
import org.switchyard.component.camel.file.model.CamelFileNamespace;
import org.switchyard.component.camel.file.model.v1.V1CamelFileBindingModel;

/**
 * Test for {@link V1CamelBindingModel}.
 * 
 * @author Daniel Bevenius
 */
public class V1CamelFileBindingModelTest extends V1BaseCamelServiceBindingModelTest<V1CamelFileBindingModel, FileEndpoint> {

    private static final String CAMEL_XML = "/v1/switchyard-file-binding-beans.xml";
    private static final String DIRECTORY = "/input/directory";
    private static final Boolean AUTO_CREATE = false;
    private static final Integer BUFFER_SIZE = 2048;
    private static final String FILE_NAME = "fname";
    private static final String CHARSET = "cp1250";
    private static final Boolean FLATTEN = true;
    private static final String CAMEL_URI = "file:///input/directory?autoCreate=false&" +
        "bufferSize=2048&charset=cp1250&fileName=fname&flatten=true";

    public V1CamelFileBindingModelTest() {
        super(FileEndpoint.class, CAMEL_XML);
    }

    @Override
    protected void createModelAssertions(V1CamelFileBindingModel model) {
        assertEquals(DIRECTORY, model.getDirectory());
        assertEquals(AUTO_CREATE, model.isAutoCreate());
        assertEquals(BUFFER_SIZE, model.getBufferSize());
        assertEquals(FILE_NAME, model.getFileName());
        assertEquals(FLATTEN, model.isFlatten());
        assertEquals(CHARSET, model.getCharset());
    }

    @Override
    protected V1CamelFileBindingModel createTestModel() {
        return (V1CamelFileBindingModel) new V1CamelFileBindingModel(CamelFileNamespace.V_1_0.uri())
            .setDirectory(DIRECTORY)
            .setAutoCreate(AUTO_CREATE)
            .setBufferSize(BUFFER_SIZE)
            .setFileName(FILE_NAME)
            .setFlatten(FLATTEN)
            .setCharset(CHARSET);
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }

}
