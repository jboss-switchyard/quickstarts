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
