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
import org.switchyard.component.camel.common.model.file.GenericFileProducerBindingModel;
import org.switchyard.component.camel.config.test.v1.V1BaseCamelReferenceBindingModelTest;
import org.switchyard.component.camel.file.model.v1.V1CamelFileBindingModel;
import org.switchyard.component.camel.file.model.v1.V1CamelFileProducerBindingModel;

/**
 * Test for {@link V1CamelBindingModel}.
 * 
 * @author Mario Antollini
 */
public class V1CamelFileProducerBindingModelTest extends V1BaseCamelReferenceBindingModelTest<V1CamelFileBindingModel, FileEndpoint> {

    private static final String CAMEL_XML = "/v1/switchyard-file-binding-producer-beans.xml";

    private static final String DIRECTORY = "/input/directory";
    private static final Boolean AUTO_CREATE = Boolean.FALSE;
    private static final String FILE_EXIST = "Override";
    private static final String TEMP_PREFIX = "prefix_";
    private static final Boolean KEEP_LAST_MODIFIED = Boolean.FALSE;
    private static final Boolean EAGER_DELETE_TARGET_FLE = Boolean.TRUE;
    private static final String DONE_FILENAME = "processed";
    private static final String CAMEL_URI = 
        "file:///input/directory?autoCreate=false&fileExist=Override" +
        "&tempPrefix=prefix_&keepLastModified=false" +
        "&eagerDeleteTargetFile=true&doneFileName=processed";

    public V1CamelFileProducerBindingModelTest() {
        super(FileEndpoint.class, CAMEL_XML);
    }

    @Override
    protected void createModelAssertions(V1CamelFileBindingModel model) {
        GenericFileProducerBindingModel producer = model.getProducer();
        assertEquals(FILE_EXIST, producer.getFileExist());
        assertEquals(TEMP_PREFIX, producer.getTempPrefix());
        assertEquals(KEEP_LAST_MODIFIED, producer.isKeepLastModified());
        assertEquals(EAGER_DELETE_TARGET_FLE, producer.isEagerDeleteTargetFile());
        assertEquals(DONE_FILENAME, producer.getDoneFileName());
    }

    @Override
    protected V1CamelFileBindingModel createTestModel() {
        V1CamelFileBindingModel fileModel = (V1CamelFileBindingModel) new V1CamelFileBindingModel()
            .setDirectory(DIRECTORY)
            .setAutoCreate(AUTO_CREATE);

        GenericFileProducerBindingModel producer = new V1CamelFileProducerBindingModel()
            .setFileExist(FILE_EXIST)
            .setTempPrefix(TEMP_PREFIX)
            .setKeepLastModified(KEEP_LAST_MODIFIED)
            .setEagerDeleteTargetFile(EAGER_DELETE_TARGET_FLE)
            .setDoneFileName(DONE_FILENAME);
        return fileModel.setProducer(producer);
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }

}
