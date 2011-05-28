/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */

package org.switchyard.component.camel.config.model.file.v1;

import javax.xml.namespace.QName;

import org.switchyard.component.camel.config.model.file.CamelFileConsumerBindingModel;
import org.switchyard.component.camel.config.model.v1.NameValueModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * A binding for Camel's file component, for consumer configs.
 * 
 * @author Mario Antollini
 */
public class V1CamelFileConsumerBindingModel extends BaseModel implements
        CamelFileConsumerBindingModel {

    /**
     * The name of the 'initialDelay' element.
     */
    public static final String INITIAL_DELAY = "initialDelay";

    /**
     * The name of the 'delay' element.
     */
    public static final String DELAY = "delay";

    /**
     * The name of the 'useFixedDelay' element.
     */
    public static final String USE_FIXED_DELAY = "useFixedDelay";

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
     * The name of the 'maxMessagesPerPoll' element.
     */
    public static final String MAX_MESSAGES_PER_POLL = "maxMessagesPerPoll";

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
     * Create a new V1CamelFileConsumerBindingModel.
     */
    public V1CamelFileConsumerBindingModel() {
        super(new QName(V1CamelFileBindingModel.CONSUME));
        setModelChildrenOrder(INITIAL_DELAY, DELAY, USE_FIXED_DELAY, RECURSIVE,
                DELETE, NOOP, PRE_MOVE, MOVE, MOVE_FAILED, INCLUDE, EXCLUDE,
                IDEMPOTENT, IDEMPOTENT_REPOSITORY, IN_PROGRESS_REPOSITORY,
                FILTER, SORTER, SORT_BY, READ_LOCK, READ_LOCK_TIMEOUT,
                READ_LOCK_CHECK_INTERVAL, EXCLUSIVE_READ_LOCK_STRATEGY,
                PROCESS_STRATEGY, MAX_MESSAGES_PER_POLL,
                STARTING_DIRECTORY_MUST_EXIST, DIRECTORY_MUST_EXIST,
                DONE_FILE_NAME);
    }

    /**
     * Create a V1CamelFileConsumerBindingModel from the specified configuration
     * and descriptor.
     * 
     * @param config
     *            The switchyard configuration instance.
     * @param desc
     *            The switchyard descriptor instance.
     */
    public V1CamelFileConsumerBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public Integer getInitialDelay() {
        return getIntegerConfig(INITIAL_DELAY);
    }

    @Override
    public V1CamelFileConsumerBindingModel setInitialDelay(Integer initialDelay) {
        setConfig(INITIAL_DELAY, String.valueOf(initialDelay));
        return this;
    }

    @Override
    public Integer getDelay() {
        return getIntegerConfig(DELAY);
    }

    @Override
    public V1CamelFileConsumerBindingModel setDelay(Integer delay) {
        setConfig(DELAY, String.valueOf(delay));
        return this;
    }

    @Override
    public Boolean isUseFixedDelay() {
        return getBooleanConfig(USE_FIXED_DELAY);
    }

    @Override
    public V1CamelFileConsumerBindingModel setUseFixedDelay(
            Boolean useFixedDelay) {
        setConfig(USE_FIXED_DELAY, String.valueOf(useFixedDelay));
        return this;
    }

    @Override
    public Boolean isRecursive() {
        return getBooleanConfig(RECURSIVE);
    }

    @Override
    public V1CamelFileConsumerBindingModel setRecursive(Boolean recursive) {
        setConfig(RECURSIVE, String.valueOf(recursive));
        return this;
    }

    @Override
    public Boolean isDelete() {
        return getBooleanConfig(DELETE);
    }

    @Override
    public V1CamelFileConsumerBindingModel setDelete(Boolean delete) {
        setConfig(DELETE, String.valueOf(delete));
        return this;
    }

    @Override
    public Boolean isNoop() {
        return getBooleanConfig(NOOP);
    }

    @Override
    public V1CamelFileConsumerBindingModel setNoop(Boolean noop) {
        setConfig(NOOP, String.valueOf(noop));
        return this;
    }

    @Override
    public String getPreMove() {
        return getConfig(PRE_MOVE);
    }

    @Override
    public V1CamelFileConsumerBindingModel setPreMove(String preMove) {
        setConfig(PRE_MOVE, preMove);
        return this;
    }

    @Override
    public String getMove() {
        return getConfig(MOVE);
    }

    @Override
    public V1CamelFileConsumerBindingModel setMove(String move) {
        setConfig(MOVE, move);
        return this;
    }

    @Override
    public String getMoveFailed() {
        return getConfig(MOVE_FAILED);
    }

    @Override
    public V1CamelFileConsumerBindingModel setMoveFailed(String moveFailed) {
        setConfig(MOVE_FAILED, moveFailed);
        return this;
    }

    @Override
    public String getInclude() {
        return getConfig(INCLUDE);
    }

    @Override
    public V1CamelFileConsumerBindingModel setInclude(String include) {
        setConfig(INCLUDE, include);
        return this;
    }

    @Override
    public String getExclude() {
        return getConfig(EXCLUDE);
    }

    @Override
    public V1CamelFileConsumerBindingModel setExclude(String exclude) {
        setConfig(EXCLUDE, exclude);
        return this;
    }

    @Override
    public Boolean isIdempotent() {
        return getBooleanConfig(IDEMPOTENT);
    }

    @Override
    public V1CamelFileConsumerBindingModel setIdempotent(Boolean idempotent) {
        setConfig(IDEMPOTENT, String.valueOf(idempotent));
        return this;
    }

    @Override
    public String getIdempotentRepository() {
        return getConfig(IDEMPOTENT_REPOSITORY);
    }

    @Override
    public V1CamelFileConsumerBindingModel setIdempotentRepository(
            String idempotentRepository) {
        setConfig(IDEMPOTENT_REPOSITORY, idempotentRepository);
        return this;
    }

    @Override
    public String getInProgressRepository() {
        return getConfig(IN_PROGRESS_REPOSITORY);
    }

    @Override
    public V1CamelFileConsumerBindingModel setInProgressRepository(
            String inProgressRepository) {
        setConfig(inProgressRepository, inProgressRepository);
        return this;
    }

    @Override
    public String getFilter() {
        return getConfig(FILTER);
    }

    @Override
    public V1CamelFileConsumerBindingModel setFilter(String filter) {
        setConfig(FILTER, filter);
        return this;
    }

    @Override
    public String getSorter() {
        return getConfig(SORTER);
    }

    @Override
    public V1CamelFileConsumerBindingModel setSorter(String sorter) {
        setConfig(SORTER, sorter);
        return this;
    }

    @Override
    public String getSortBy() {
        return getConfig(SORT_BY);
    }

    @Override
    public V1CamelFileConsumerBindingModel setSortBy(String sortBy) {
        setConfig(SORT_BY, sortBy);
        return this;
    }

    @Override
    public String getReadLock() {
        return getConfig(READ_LOCK);
    }

    @Override
    public V1CamelFileConsumerBindingModel setReadLock(String readLock) {
        setConfig(READ_LOCK, readLock);
        return this;
    }

    @Override
    public Long getReadLockTimeout() {
        return getLongConfig(READ_LOCK_TIMEOUT);
    }

    @Override
    public V1CamelFileConsumerBindingModel setReadLockTimeout(
            Long readLockTimeout) {
        setConfig(READ_LOCK_TIMEOUT, String.valueOf(readLockTimeout));
        return this;
    }

    @Override
    public Integer getReadLockCheckInterval() {
        return getIntegerConfig(READ_LOCK_CHECK_INTERVAL);
    }

    @Override
    public V1CamelFileConsumerBindingModel setReadLockCheckInterval(
            Integer readLockCheckInterval) {
        setConfig(READ_LOCK_CHECK_INTERVAL,
                String.valueOf(readLockCheckInterval));
        return this;
    }

    @Override
    public String getExclusiveReadLockStrategy() {
        return getConfig(EXCLUSIVE_READ_LOCK_STRATEGY);
    }

    @Override
    public V1CamelFileConsumerBindingModel setExclusiveReadLockStrategy(
            String exclusiveReadLockStrategy) {
        setConfig(EXCLUSIVE_READ_LOCK_STRATEGY, exclusiveReadLockStrategy);
        return this;
    }

    @Override
    public String getProcessStrategy() {
        return getConfig(PROCESS_STRATEGY);
    }

    @Override
    public V1CamelFileConsumerBindingModel setProcessStrategy(
            String processStrategy) {
        setConfig(PROCESS_STRATEGY, processStrategy);
        return this;
    }

    @Override
    public Integer getMaxMessagesPerPoll() {
        return getIntegerConfig(MAX_MESSAGES_PER_POLL);
    }

    @Override
    public V1CamelFileConsumerBindingModel setMaxMessagesPerPoll(
            Integer maxMessagesPerPoll) {
        setConfig(MAX_MESSAGES_PER_POLL, String.valueOf(maxMessagesPerPoll));
        return this;
    }

    @Override
    public Boolean isStartingDirectoryMustExist() {
        return getBooleanConfig(STARTING_DIRECTORY_MUST_EXIST);
    }

    @Override
    public V1CamelFileConsumerBindingModel setStartingDirectoryMustExist(
            Boolean startingDirectoryMustExist) {
        setConfig(STARTING_DIRECTORY_MUST_EXIST,
                String.valueOf(startingDirectoryMustExist));
        return this;
    }

    @Override
    public Boolean isDirectoryMustExist() {
        return getBooleanConfig(DIRECTORY_MUST_EXIST);
    }

    @Override
    public V1CamelFileConsumerBindingModel setDirectoryMustExist(
            Boolean directoryMustExist) {
        setConfig(DIRECTORY_MUST_EXIST, String.valueOf(directoryMustExist));
        return this;
    }

    @Override
    public String getDoneFileName() {
        return getConfig(DONE_FILE_NAME);
    }

    @Override
    public V1CamelFileConsumerBindingModel setDoneFileName(String doneFileName) {
        setConfig(DONE_FILE_NAME, doneFileName);
        return this;
    }

    private Integer getIntegerConfig(String configName) {
        String value = getConfig(configName);
        return value != null ? Integer.parseInt(value) : null;
    }

    private Long getLongConfig(String configName) {
        String value = getConfig(configName);
        return value != null ? Long.parseLong(value) : null;
    }

    private Boolean getBooleanConfig(String configName) {
        String value = getConfig(configName);
        return value != null ? Boolean.valueOf(value) : null;
    }

    private String getConfig(String configName) {
        Configuration config = getModelConfiguration()
                .getFirstChild(configName);
        if (config != null) {
            return config.getValue();
        } else {
            return null;
        }
    }

    private void setConfig(String name, String value) {
        Configuration config = getModelConfiguration().getFirstChild(name);
        if (config != null) {
            // set an existing config value
            config.setValue(value);
        } else {
            // create the config model and set the value
            NameValueModel model = new NameValueModel(name);
            model.setValue(value);
            setChildModel(model);
        }
    }

}
