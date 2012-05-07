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
import org.junit.Before;
import org.junit.Test;
import org.switchyard.component.camel.config.model.OperationSelector;
import org.switchyard.component.camel.config.model.file.CamelFileBindingModel;
import org.switchyard.component.camel.config.model.file.CamelFileConsumerBindingModel;
import org.switchyard.component.camel.config.model.v1.V1BaseCamelModelTest;
import org.switchyard.component.camel.config.model.v1.V1CamelBindingModel;
import org.switchyard.component.camel.config.model.v1.V1OperationSelector;
import org.switchyard.config.model.Validation;

/**
 * Test for {@link V1CamelBindingModel}.
 * 
 * @author Mario Antollini
 */
public class V1CamelFileConsumerBindingModelTest extends V1BaseCamelModelTest<V1CamelFileBindingModel> {

    private static final String CAMEL_XML = "switchyard-file-binding-consumer-beans.xml";

    private static final String OPERATION_NAME = "print";
    private static final String TARGET_DIR = "/input/directory";
    private static final Boolean AUTO_CREATE = true;
    private static final Integer BUFFER_SIZE = new Integer(1024);
    private static final Integer INITIAL_DELAY = new Integer(10);
    private static final Boolean DELETE = Boolean.FALSE;
    private static final Integer READ_LOCK_CHECK_INTERVAL = new Integer(1000);
    private static final Boolean DIRECTORY_MUST_EXIST = Boolean.TRUE;
    private static final String CAMEL_URI = 
        "file:///input/directory?autoCreate=true&bufferSize=1024&initialDelay=10" +
        "&delete=false&readLockCheckInterval=1000&directoryMustExist=true";

    private static final String NEW_CAMEL_URI = 
        "file:///input/directory?autoCreate=true&bufferSize=1024&initialDelay=10" +
        "&readLockCheckInterval=1000&delete=false&directoryMustExist=true";

    private static final String CAMEL_ENDPOINT_URI = 
        "file:///input/directory?autoCreate=true&bufferSize=1024&delete=false&" +
        "directoryMustExist=true&initialDelay=10&readLockCheckInterval=1000";

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testConfigOverride() {
        // set a value on an existing config element
        CamelFileBindingModel bindingModel = createFileConsumerModel();
        assertEquals(READ_LOCK_CHECK_INTERVAL, bindingModel.getConsumer().getReadLockCheckInterval());
        bindingModel.getConsumer().setReadLockCheckInterval(2500);
        assertEquals(new Integer(2500), bindingModel.getConsumer().getReadLockCheckInterval());
    }

    @Test
    public void testReadConfig() throws Exception {
        final V1CamelFileBindingModel bindingModel = getFirstCamelBinding(CAMEL_XML);
        final Validation validateModel = bindingModel.validateModel();
        //Valid Model?
        assertEquals(validateModel.isValid(), true);
        //Camel File
        assertEquals(bindingModel.getOperationSelector().getOperationName(), OPERATION_NAME);
        assertEquals(bindingModel.getTargetDir(), TARGET_DIR);
        assertEquals(bindingModel.isAutoCreate(), AUTO_CREATE);
        assertEquals(bindingModel.getBufferSize(), BUFFER_SIZE);
        //Camel File Consumer
        assertEquals(bindingModel.getConsumer().getInitialDelay(), INITIAL_DELAY);
        assertEquals(bindingModel.getConsumer().isDelete(), DELETE);
        assertEquals(bindingModel.getConsumer().getReadLockCheckInterval(), READ_LOCK_CHECK_INTERVAL);
        assertEquals(bindingModel.getConsumer().isDirectoryMustExist(), DIRECTORY_MUST_EXIST);
        assertEquals(bindingModel.getComponentURI().toString(), CAMEL_URI);
    }

    @Test
    public void testWriteConfig() throws Exception {
        CamelFileBindingModel bindingModel = createFileConsumerModel();
        assertEquals(bindingModel.getOperationSelector().getOperationName(), OPERATION_NAME);
        assertEquals(bindingModel.getTargetDir(), TARGET_DIR);
        assertEquals(bindingModel.isAutoCreate(), AUTO_CREATE);
        assertEquals(bindingModel.getBufferSize(), BUFFER_SIZE);
        //Camel File Consumer
        assertEquals(bindingModel.getConsumer().getInitialDelay(), INITIAL_DELAY);
        assertEquals(bindingModel.getConsumer().isDelete(), DELETE);
        assertEquals(bindingModel.getConsumer().getReadLockCheckInterval(), READ_LOCK_CHECK_INTERVAL);
        assertEquals(bindingModel.getConsumer().isDirectoryMustExist(), DIRECTORY_MUST_EXIST);
        assertEquals(bindingModel.getComponentURI().toString(), NEW_CAMEL_URI);
    }

    /**
     * This test fails because of namespace prefix
     * 
     */
    @Test
    public void compareWriteConfig() throws Exception {
        String refXml = getFirstCamelBinding(CAMEL_XML).toString();
        String newXml = createFileConsumerModel().toString();
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(refXml, newXml);
        assertTrue(diff.toString(), diff.similar());
    }

    @Test
    public void testComponentURI() {
        CamelFileBindingModel bindingModel = createFileConsumerModel();
        assertEquals(NEW_CAMEL_URI, bindingModel.getComponentURI().toString());
    }

    @Test
    public void testCamelEndpoint() {
        CamelFileBindingModel model = createFileConsumerModel();
        FileEndpoint endpoint = getEndpoint(model, FileEndpoint.class);
        //assertEquals(endpoint.getId(), OPERATION_NAME); //No way to get the operation name
        assertEquals(endpoint.getConfiguration().getDirectory(), 
            TARGET_DIR.replace("/", File.separator));
        assertEquals(endpoint.isAutoCreate(), AUTO_CREATE.booleanValue());
        assertEquals(endpoint.getBufferSize(), BUFFER_SIZE.intValue());
        assertEquals(endpoint.getConsumerProperties().get(
            V1CamelFileConsumerBindingModel.INITIAL_DELAY), INITIAL_DELAY.toString());
        assertEquals(endpoint.isDelete(), DELETE.booleanValue());
        assertEquals(endpoint.getReadLockCheckInterval(), READ_LOCK_CHECK_INTERVAL.longValue());
        assertEquals(endpoint.isDirectoryMustExist(), DIRECTORY_MUST_EXIST.booleanValue());
        assertEquals(endpoint.getEndpointUri().toString(), CAMEL_ENDPOINT_URI);
    }

    private CamelFileBindingModel createFileConsumerModel() {
        OperationSelector operationSelector = new V1OperationSelector();
        operationSelector.setOperationName(OPERATION_NAME);

        V1CamelFileBindingModel fileModel = new V1CamelFileBindingModel();
        fileModel.setOperationSelector(operationSelector);

        fileModel.setAutoCreate(AUTO_CREATE)
            .setBufferSize(BUFFER_SIZE)
            .setTargetDir(TARGET_DIR);

        CamelFileConsumerBindingModel consumer = new V1CamelFileConsumerBindingModel()
            .setInitialDelay(INITIAL_DELAY)
            .setReadLockCheckInterval(READ_LOCK_CHECK_INTERVAL)
            .setDelete(DELETE)
            .setDirectoryMustExist(DIRECTORY_MUST_EXIST);

        fileModel.setConsumer(consumer);
        fileModel.setOperationSelector(operationSelector);

        return fileModel;
    }

}
