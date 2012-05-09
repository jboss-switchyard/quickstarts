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
package org.switchyard.component.camel.config.model.file.v1;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.io.File;

import org.apache.camel.component.file.FileEndpoint;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;
import org.switchyard.component.camel.config.model.file.CamelFileBindingModel;
import org.switchyard.component.camel.config.model.v1.V1BaseCamelModelTest;
import org.switchyard.component.camel.config.model.v1.V1CamelBindingModel;
import org.switchyard.component.camel.config.model.v1.V1OperationSelector;
import org.switchyard.config.model.Validation;

/**
 * Test for {@link V1CamelBindingModel}.
 * 
 * @author Daniel Bevenius
 */
public class V1CamelFileBindingModelTest extends V1BaseCamelModelTest<V1CamelFileBindingModel> {

    private static final String CAMEL_XML = "switchyard-file-binding-beans.xml";
    private static final String DIRECTORY = "/input/directory";
    private static final Boolean AUTO_CREATE = false;
    private static final Integer BUFFER_SIZE = 2048;
    private static final String FILE_NAME = "fname";
    private static final String CHARSET = "cp1250";
    private static final Boolean FLATTEN = true;
    private static final String CAMEL_URI = "file:///input/directory?autoCreate=false&" +
        "bufferSize=2048&charset=cp1250&fileName=fname&flatten=true";

    @Test
    public void validateCamelBindingModelWithBeanElement() throws Exception {
        final V1CamelFileBindingModel bindingModel = getFirstCamelBinding(CAMEL_XML);
        final Validation validateModel = bindingModel.validateModel();

        validateModel.assertValid();
        assertTrue(validateModel.isValid());
        assertEquals(DIRECTORY, bindingModel.getDirectory());
        assertEquals(AUTO_CREATE, bindingModel.isAutoCreate());
        assertEquals(BUFFER_SIZE, bindingModel.getBufferSize());
        assertEquals(FILE_NAME, bindingModel.getFileName());
        assertEquals(FLATTEN, bindingModel.isFlatten());
        assertEquals(CHARSET, bindingModel.getCharset());
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
        CamelFileBindingModel model = getFirstCamelBinding(CAMEL_XML);

        FileEndpoint endpoint = getEndpoint(model, FileEndpoint.class);
        assertEquals(DIRECTORY.replace("/", File.separator), endpoint.getConfiguration().getDirectory());
        assertEquals(CAMEL_URI, endpoint.getEndpointUri().toString());
    }

    private V1CamelFileBindingModel createModel() {
        V1OperationSelector selector = new V1OperationSelector();
        selector.setOperationName("print");
        return (V1CamelFileBindingModel) new V1CamelFileBindingModel()
            .setDirectory(DIRECTORY)
            .setAutoCreate(AUTO_CREATE)
            .setBufferSize(BUFFER_SIZE)
            .setFileName(FILE_NAME)
            .setFlatten(FLATTEN)
            .setCharset(CHARSET)
            .setOperationSelector(selector);
    }

}
