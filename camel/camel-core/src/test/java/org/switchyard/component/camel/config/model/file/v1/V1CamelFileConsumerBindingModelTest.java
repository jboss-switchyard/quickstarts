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
import org.switchyard.component.camel.config.model.file.CamelFileBindingModel;
import org.switchyard.component.camel.config.model.file.CamelFileConsumerBindingModel;
import org.switchyard.component.camel.config.model.v1.V1BaseCamelModelTest;
import org.switchyard.component.camel.config.model.v1.V1CamelBindingModel;
import org.switchyard.config.model.Validation;

/**
 * Test for {@link V1CamelBindingModel}.
 * 
 * @author Mario Antollini
 */
public class V1CamelFileConsumerBindingModelTest extends V1BaseCamelModelTest<V1CamelFileBindingModel> {

    private static final String CAMEL_XML = "switchyard-file-binding-consumer-beans.xml";

    private static final String DIRECTORY = "/input/directory";
    private static final Boolean DELETE = Boolean.FALSE;
    private static final Boolean RECURSIVE = true;
    private static final Boolean NOOP = false;
    private static final String PRE_MOVE = ".inProgress";
    private static final String MOVE = ".done";
    private static final String MOVE_FAILED = ".failed";
    private static final String INCLUDE = "*.csv";
    private static final String EXCLUDE = "*.xml";
    private static final Boolean IDEMPOTENT = true;
    private static final String SORT_BY = "file:name";
    private static final String READ_LOCK = "fileLock";
    private static final Long READ_LOCK_TIMEOUT = 10L;
    private static final Integer READ_LOCK_CHECK_INTERVAL = new Integer(1000);
    private static final Boolean STARTING_DIRECTORY_MUST_EXIST = false;
    private static final Boolean DIRECTORY_MUST_EXIST = Boolean.TRUE;
    private static final String DONE_FILE_NAME = "done";
    private static final String CAMEL_URI = "file:///input/directory?delete=false&" +
        "recursive=true&noop=false&preMove=.inProgress&move=.done&moveFailed=.failed&" +
        "include=*.csv&exclude=*.xml&idempotent=true&sortBy=file:name&" +
        "readLock=fileLock&readLockTimeout=10&readLockCheckInterval=1000&" +
        "startingDirectoryMustExist=false&directoryMustExist=true&doneFileName=done";

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
        assertTrue(validateModel.isValid());
        //Camel File
        assertEquals(DIRECTORY, bindingModel.getDirectory());
        //Camel File Consumer
        assertConsumerModel(bindingModel.getConsumer());
        assertEquals(CAMEL_URI, bindingModel.getComponentURI().toString());
    }

    @Test
    public void testWriteConfig() throws Exception {
        CamelFileBindingModel bindingModel = createFileConsumerModel();
        assertEquals(DIRECTORY, bindingModel.getDirectory());
        //Camel File Consumer
        assertConsumerModel(bindingModel.getConsumer());
        assertEquals(CAMEL_URI, bindingModel.getComponentURI().toString());
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
        assertEquals(CAMEL_URI, bindingModel.getComponentURI().toString());
    }

    @Test
    public void testCamelEndpoint() {
        CamelFileBindingModel model = createFileConsumerModel();
        FileEndpoint endpoint = getEndpoint(model, FileEndpoint.class);
        //assertEquals(endpoint.getId(), OPERATION_NAME); //No way to get the operation name
        assertEquals(DIRECTORY.replace("/", File.separator), endpoint.getConfiguration().getDirectory());
        assertEquals(DELETE.booleanValue(), endpoint.isDelete());
        assertEquals(READ_LOCK_CHECK_INTERVAL.longValue(), endpoint.getReadLockCheckInterval());
        assertEquals(DIRECTORY_MUST_EXIST.booleanValue(), endpoint.isDirectoryMustExist());
    }

    private CamelFileBindingModel createFileConsumerModel() {
        V1CamelFileBindingModel fileModel = new V1CamelFileBindingModel();
        fileModel.setDirectory(DIRECTORY);

        CamelFileConsumerBindingModel consumer = (CamelFileConsumerBindingModel) new V1CamelFileConsumerBindingModel()
            .setDelete(DELETE)
            .setRecursive(RECURSIVE)
            .setNoop(NOOP)
            .setPreMove(PRE_MOVE)
            .setMove(MOVE)
            .setMoveFailed(MOVE_FAILED)
            .setInclude(INCLUDE)
            .setExclude(EXCLUDE)
            .setIdempotent(IDEMPOTENT)
            .setSortBy(SORT_BY)
            .setReadLock(READ_LOCK)
            .setReadLockTimeout(READ_LOCK_TIMEOUT)
            .setReadLockCheckInterval(READ_LOCK_CHECK_INTERVAL)
            .setStartingDirectoryMustExist(STARTING_DIRECTORY_MUST_EXIST)
            .setDirectoryMustExist(DIRECTORY_MUST_EXIST)
            .setDoneFileName(DONE_FILE_NAME);
        fileModel.setConsumer(consumer);

        return fileModel;
    }

    private void assertConsumerModel(CamelFileConsumerBindingModel consumer) {
        assertEquals(RECURSIVE, consumer.isRecursive());
        assertEquals(DELETE, consumer.isDelete());
        assertEquals(NOOP, consumer.isNoop());
        assertEquals(PRE_MOVE, consumer.getPreMove());
        assertEquals(MOVE, consumer.getMove());
        assertEquals(MOVE_FAILED, consumer.getMoveFailed());
        assertEquals(INCLUDE, consumer.getInclude());
        assertEquals(EXCLUDE, consumer.getExclude());
        assertEquals(IDEMPOTENT, consumer.isIdempotent());
        assertEquals(SORT_BY, consumer.getSortBy());
        assertEquals(READ_LOCK, consumer.getReadLock());
        assertEquals(READ_LOCK_TIMEOUT, consumer.getReadLockTimeout());
        assertEquals(READ_LOCK_CHECK_INTERVAL, consumer.getReadLockCheckInterval());
        assertEquals(STARTING_DIRECTORY_MUST_EXIST, consumer.isStartingDirectoryMustExist());
        assertEquals(DIRECTORY_MUST_EXIST, consumer.isDirectoryMustExist());
        assertEquals(DONE_FILE_NAME, consumer.getDoneFileName());
    }
}
