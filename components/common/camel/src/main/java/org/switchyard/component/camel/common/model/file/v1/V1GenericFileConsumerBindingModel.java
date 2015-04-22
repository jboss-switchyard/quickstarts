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
package org.switchyard.component.camel.common.model.file.v1;

import org.switchyard.component.camel.common.model.file.GenericFileConsumerBindingModel;
import org.switchyard.component.camel.common.model.v1.V1CamelScheduledBatchPollConsumer;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * Implementation of 1st version for file consumer bindings.
 * 
 * @author Lukasz Dywicki
 */
public class V1GenericFileConsumerBindingModel extends V1CamelScheduledBatchPollConsumer 
    implements GenericFileConsumerBindingModel {

    /**
     * The name of the 'recursive' element.
     */
    public static final String RECURSIVE = "recursive";

    /**
     * The name of the 'delete' element.
     */
    public static final String DELETE = "delete";

    /**
     * The name of the 'noop' element.
     */
    public static final String NOOP = "noop";

    /**
     * The name of the 'preMove' element.
     */
    public static final String PRE_MOVE = "preMove";

    /**
     * The name of the 'move' element.
     */
    public static final String MOVE = "move";

    /**
     * The name of the 'moveFailed' element.
     */
    public static final String MOVE_FAILED = "moveFailed";

    /**
     * The name of the 'include' element.
     */
    public static final String INCLUDE = "include";

    /**
     * The name of the 'exclude' element.
     */
    public static final String EXCLUDE = "exclude";

    /**
     * The name of the 'idempotent' element.
     */
    public static final String IDEMPOTENT = "idempotent";

    /**
     * The name of the 'idempotentRepository' element.
     */
    public static final String IDEMPOTENT_REPOSITORY = "idempotentRepository";

    /**
     * The name of the 'inProgressRepository' element.
     */
    public static final String IN_PROGRESS_REPOSITORY = "inProgressRepository";

    /**
     * The name of the 'filter' element.
     */
    public static final String FILTER = "filter";

    /**
     * The name of the 'sorter' element.
     */
    public static final String SORTER = "sorter";

    /**
     * The name of the 'sortBy' element.
     */
    public static final String SORT_BY = "sortBy";

    /**
     * The name of the 'readLock' element.
     */
    public static final String READ_LOCK = "readLock";

    /**
     * The name of the 'readLockTimeout' element.
     */
    public static final String READ_LOCK_TIMEOUT = "readLockTimeout";

    /**
     * The name of the 'readLockCheckInterval' element.
     */
    public static final String READ_LOCK_CHECK_INTERVAL = "readLockCheckInterval";

    /**
     * The name of the 'exclusiveReadLockStrategy' element.
     */
    public static final String EXCLUSIVE_READ_LOCK_STRATEGY = "exclusiveReadLockStrategy";

    /**
     * The name of the 'processStrategy' element.
     */
    public static final String PROCESS_STRATEGY = "processStrategy";

    /**
     * The name of the 'startingDirectoryMustExist' element.
     */
    public static final String STARTING_DIRECTORY_MUST_EXIST = "startingDirectoryMustExist";

    /**
     * The name of the 'directoryMustExist' element.
     */
    public static final String DIRECTORY_MUST_EXIST = "directoryMustExist";

    /**
     * The name of the 'doneFileName' element.
     */
    public static final String DONE_FILE_NAME = "doneFileName";

    /**
     * Creates model bound to given namespace.
     * 
     * @param namespace Namespace to bound.
     * @param name Name of element.
     */
    public V1GenericFileConsumerBindingModel(String namespace, String name) {
        super(namespace, name);

        setModelChildrenOrder(
                DELETE, RECURSIVE, NOOP, PRE_MOVE, MOVE, MOVE_FAILED, INCLUDE, EXCLUDE,
                IDEMPOTENT, IDEMPOTENT_REPOSITORY, IN_PROGRESS_REPOSITORY,
                FILTER, SORTER, SORT_BY, READ_LOCK, READ_LOCK_TIMEOUT,
                READ_LOCK_CHECK_INTERVAL, EXCLUSIVE_READ_LOCK_STRATEGY,
                PROCESS_STRATEGY,
                STARTING_DIRECTORY_MUST_EXIST, DIRECTORY_MUST_EXIST,
                DONE_FILE_NAME);
    }

    /**
     * Create a binding model from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V1GenericFileConsumerBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public Boolean isRecursive() {
        return getBooleanConfig(RECURSIVE);
    }

    @Override
    public V1GenericFileConsumerBindingModel setRecursive(Boolean recursive) {
        return setConfig(RECURSIVE, recursive);
    }

    @Override
    public Boolean isDelete() {
        return getBooleanConfig(DELETE);
    }

    @Override
    public V1GenericFileConsumerBindingModel setDelete(Boolean delete) {
        return setConfig(DELETE, delete);
    }

    @Override
    public Boolean isNoop() {
        return getBooleanConfig(NOOP);
    }

    @Override
    public V1GenericFileConsumerBindingModel setNoop(Boolean noop) {
        return setConfig(NOOP, noop);
    }

    @Override
    public String getPreMove() {
        return getConfig(PRE_MOVE);
    }

    @Override
    public V1GenericFileConsumerBindingModel setPreMove(String preMove) {
        return setConfig(PRE_MOVE, preMove);
    }

    @Override
    public String getMove() {
        return getConfig(MOVE);
    }

    @Override
    public V1GenericFileConsumerBindingModel setMove(String move) {
        return setConfig(MOVE, move);
    }

    @Override
    public String getMoveFailed() {
        return getConfig(MOVE_FAILED);
    }

    @Override
    public V1GenericFileConsumerBindingModel setMoveFailed(String moveFailed) {
        return setConfig(MOVE_FAILED, moveFailed);
    }

    @Override
    public String getInclude() {
        return getConfig(INCLUDE);
    }

    @Override
    public V1GenericFileConsumerBindingModel setInclude(String include) {
        return setConfig(INCLUDE, include);
    }

    @Override
    public String getExclude() {
        return getConfig(EXCLUDE);
    }

    @Override
    public V1GenericFileConsumerBindingModel setExclude(String exclude) {
        return setConfig(EXCLUDE, exclude);
    }

    @Override
    public Boolean isIdempotent() {
        return getBooleanConfig(IDEMPOTENT);
    }

    @Override
    public V1GenericFileConsumerBindingModel setIdempotent(Boolean idempotent) {
        return setConfig(IDEMPOTENT, idempotent);
    }

    @Override
    public String getIdempotentRepository() {
        return getConfig(IDEMPOTENT_REPOSITORY);
    }

    @Override
    public V1GenericFileConsumerBindingModel setIdempotentRepository(String idempotentRepository) {
        return setConfig(IDEMPOTENT_REPOSITORY, idempotentRepository);
    }

    @Override
    public String getInProgressRepository() {
        return getConfig(IN_PROGRESS_REPOSITORY);
    }

    @Override
    public V1GenericFileConsumerBindingModel setInProgressRepository(String inProgressRepository) {
        return setConfig(IN_PROGRESS_REPOSITORY, inProgressRepository);
    }

    @Override
    public String getFilter() {
        return getConfig(FILTER);
    }

    @Override
    public V1GenericFileConsumerBindingModel setFilter(String filter) {
        return setConfig(FILTER, filter);
    }

    @Override
    public String getSorter() {
        return getConfig(SORTER);
    }

    @Override
    public V1GenericFileConsumerBindingModel setSorter(String sorter) {
        return setConfig(SORTER, sorter);
    }

    @Override
    public String getSortBy() {
        return getConfig(SORT_BY);
    }

    @Override
    public V1GenericFileConsumerBindingModel setSortBy(String sortBy) {
        return setConfig(SORT_BY, sortBy);
    }

    @Override
    public String getReadLock() {
        return getConfig(READ_LOCK);
    }

    @Override
    public V1GenericFileConsumerBindingModel setReadLock(String readLock) {
        return setConfig(READ_LOCK, readLock);
    }

    @Override
    public Long getReadLockTimeout() {
        return getLongConfig(READ_LOCK_TIMEOUT);
    }

    @Override
    public V1GenericFileConsumerBindingModel setReadLockTimeout(Long readLockTimeout) {
        return setConfig(READ_LOCK_TIMEOUT, readLockTimeout);
    }

    @Override
    public Integer getReadLockCheckInterval() {
        return getIntegerConfig(READ_LOCK_CHECK_INTERVAL);
    }

    @Override
    public V1GenericFileConsumerBindingModel setReadLockCheckInterval(Integer readLockCheckInterval) {
        return setConfig(READ_LOCK_CHECK_INTERVAL, readLockCheckInterval);
    }

    @Override
    public String getExclusiveReadLockStrategy() {
        return getConfig(EXCLUSIVE_READ_LOCK_STRATEGY);
    }

    @Override
    public V1GenericFileConsumerBindingModel setExclusiveReadLockStrategy(String exclusiveReadLockStrategy) {
        return setConfig(EXCLUSIVE_READ_LOCK_STRATEGY, exclusiveReadLockStrategy);
    }

    @Override
    public String getProcessStrategy() {
        return getConfig(PROCESS_STRATEGY);
    }

    @Override
    public V1GenericFileConsumerBindingModel setProcessStrategy(String processStrategy) {
        return setConfig(PROCESS_STRATEGY, processStrategy);
    }

    @Override
    public Boolean isStartingDirectoryMustExist() {
        return getBooleanConfig(STARTING_DIRECTORY_MUST_EXIST);
    }

    @Override
    public V1GenericFileConsumerBindingModel setStartingDirectoryMustExist(Boolean startingDirectoryMustExist) {
        return setConfig(STARTING_DIRECTORY_MUST_EXIST, startingDirectoryMustExist);
    }

    @Override
    public Boolean isDirectoryMustExist() {
        return getBooleanConfig(DIRECTORY_MUST_EXIST);
    }

    @Override
    public V1GenericFileConsumerBindingModel setDirectoryMustExist(Boolean directoryMustExist) {
        return setConfig(DIRECTORY_MUST_EXIST, directoryMustExist);
    }

    @Override
    public String getDoneFileName() {
        return getConfig(DONE_FILE_NAME);
    }

    @Override
    public V1GenericFileConsumerBindingModel setDoneFileName(String doneFileName) {
        return setConfig(DONE_FILE_NAME, doneFileName);
    }

}
