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
import org.switchyard.component.camel.config.test.v1.V1BaseCamelServiceBindingModelTest;
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
        return (V1CamelFileBindingModel) new V1CamelFileBindingModel()
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
