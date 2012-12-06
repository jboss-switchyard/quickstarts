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
import org.switchyard.component.camel.file.model.CamelFileConsumerBindingModel;
import org.switchyard.component.camel.file.model.v1.V1CamelFileBindingModel;
import org.switchyard.component.camel.file.model.v1.V1CamelFileConsumerBindingModel;

/**
 * Test for {@link V1CamelBindingModel}.
 * 
 * @author Mario Antollini
 */
public class V1CamelFileConsumerBindingModelTest extends V1BaseCamelServiceBindingModelTest<V1CamelFileBindingModel, FileEndpoint> {

    private static final String CAMEL_XML = "/v1/switchyard-file-binding-consumer-beans.xml";

    private static final String DIRECTORY = "/input/directory";
    private static final Integer INITIAL_DELAY = 500;
    private static final Integer DELAY = 1000;
    private static final Integer MAX_MESSAGES_PER_POLL = 100;
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
    private static final String CAMEL_URI = "file:///input/directory?delay=1000&initialDelay=500&maxMessagesPerPoll=100&delete=false&" +
        "recursive=true&noop=false&preMove=.inProgress&move=.done&moveFailed=.failed&" +
        "include=*.csv&exclude=*.xml&idempotent=true&sortBy=file:name&" +
        "readLock=fileLock&readLockTimeout=10&readLockCheckInterval=1000&" +
        "startingDirectoryMustExist=false&directoryMustExist=true&doneFileName=done";

    public V1CamelFileConsumerBindingModelTest() {
        super(FileEndpoint.class, CAMEL_XML);
    }

    @Override
    protected V1CamelFileBindingModel createTestModel() {
        V1CamelFileBindingModel fileModel = new V1CamelFileBindingModel();
        fileModel.setDirectory(DIRECTORY);

        V1CamelFileConsumerBindingModel model = (V1CamelFileConsumerBindingModel) new V1CamelFileConsumerBindingModel()
            .setInitialDelay(INITIAL_DELAY)
            .setDelay(DELAY);
        model.setMaxMessagesPerPoll(MAX_MESSAGES_PER_POLL);
        model.setDelete(DELETE)
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
        return fileModel.setConsumer(model);
    }

    @Override
    protected void createModelAssertions(V1CamelFileBindingModel model) {
        CamelFileConsumerBindingModel consumer = model.getConsumer();

        assertEquals(INITIAL_DELAY, consumer.getInitialDelay());
        assertEquals(DELAY, consumer.getDelay());
        assertEquals(MAX_MESSAGES_PER_POLL, consumer.getMaxMessagesPerPoll());
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

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }

}
