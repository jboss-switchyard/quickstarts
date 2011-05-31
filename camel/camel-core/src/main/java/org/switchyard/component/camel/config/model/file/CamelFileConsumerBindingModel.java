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

package org.switchyard.component.camel.config.model.file;

/**
 * Represents the configuration settings for a File Consumer binding in Camel.
 * 
 *  @author Mario Antollini
 */
public interface CamelFileConsumerBindingModel {

    /**
     * Milliseconds before polling the file/directory starts.
     * 
     * @return the initial delay setting or null if it has not been specified
     */
    public Integer getInitialDelay();

    /**
     * Specify the time (in milliseconds) before polling the file/directory
     * starts.
     * 
     * @param initialDelay
     *            the time in milliseconds
     * @return a reference to this Camel File binding model
     */
    public CamelFileConsumerBindingModel setInitialDelay(Integer initialDelay);

    /**
     * Milliseconds before the next poll of the file/directory.
     * 
     * @return the delay setting or null if it has not been specified
     */
    public Integer getDelay();

    /**
     * Specify the time (in milliseconds) before next poll of the file/directory.
     * 
     * @param delay
     *            the time in milliseconds
     * @return a reference to this Camel File binding model
     */
    public CamelFileConsumerBindingModel setDelay(Integer delay);

    /**
     * Milliseconds before the next poll of the file/directory.
     * 
     * @return the delay setting or null if it has not been specified
     */
    public Boolean isUseFixedDelay();

    /**
     * Specify whether to use fixed delay between pools, otherwise fixed rate is
     * used.
     * 
     * @param useFixedDelay
     *            true: fixed delay between pools. False: fixed rate is used
     * @return a reference to this Camel File binding model
     */
    public CamelFileConsumerBindingModel setUseFixedDelay(Boolean useFixedDelay);

    /**
     * If a directory, will look for files in all the sub-directories as well.
     * 
     * @return true if recursive; false otherwise
     */
    public Boolean isRecursive();

    /**
     * Specify whether to look for files in all the sub-directories as well.
     * 
     * @param recursive
     *            true to look for files in all the sub-directories; false
     *            otherwise
     * @return a reference to this Camel File binding model
     */
    public CamelFileConsumerBindingModel setRecursive(Boolean recursive);

    /**
     * Whether the file will be deleted after it is processed.
     * 
     * @return true if to be deleted; false otherwise
     */
    public Boolean isDelete();

    /**
     * Specify whether the file will be deleted after it is processed.
     * 
     * @param delete
     *            true to get the file deleted; false otherwise
     * @return a reference to this Camel File binding model
     */
    public CamelFileConsumerBindingModel setDelete(Boolean delete);

    /**
     * If true, the file is not moved or deleted in any way. This option is good
     * for readonly data, or for ETL type requirements. If noop=true, Camel will
     * set idempotent=true as well, to avoid consuming the same files over and
     * over again.
     * 
     * @return true if no operation is to be performed; false otherwise
     */
    public Boolean isNoop();

    /**
     * Specify whether the file is not to be moved or deleted in any way. This
     * option is good for readonly data, or for ETL type requirements. If
     * noop=true, Camel will set idempotent=true as well, to avoid consuming the
     * same files over and over again.
     * 
     * @param noop
     *            true to avoid moving or deleting the file; false otherwise
     * @return a reference to this Camel File binding model
     */
    public CamelFileConsumerBindingModel setNoop(Boolean noop);

    /**
     * Expression used to dynamically set the filename when moving it before
     * processing.
     * 
     * @return the expression to be used
     */
    public String getPreMove();

    /**
     * Specify the expression used to dynamically set the filename when moving
     * it before processing.
     * 
     * @param preMove
     *            the expression
     * @return a reference to this Camel File binding model
     */
    public CamelFileConsumerBindingModel setPreMove(String preMove);

    /**
     * Expression used to dynamically set the filename when moving it after
     * processing.
     * 
     * @return the expression to be used
     */
    public String getMove();

    /**
     * Specify the expression used to dynamically set the filename when moving
     * it after processing.
     * 
     * @param move
     *            the expression
     * @return a reference to this Camel File binding model
     */
    public CamelFileConsumerBindingModel setMove(String move);

    /**
     * Expression used to dynamically set a different target directory when
     * moving files after processing failed.
     * 
     * @return the expression to be used
     */
    public String getMoveFailed();

    /**
     * Specify expression used to dynamically set a different target directory
     * when moving files after.
     * 
     * @param moveFailed
     *            the expression
     * @return a reference to this Camel File binding model
     */
    public CamelFileConsumerBindingModel setMoveFailed(String moveFailed);

    /**
     * Used to include files, if filename matches the regex pattern.
     * 
     * @return the expression to be used
     */
    public String getInclude();

    /**
     * Specify the expression used to include files, if filename matches the
     * regex pattern.
     * 
     * @param include
     *            the expression
     * @return a reference to this Camel File binding model
     */
    public CamelFileConsumerBindingModel setInclude(String include);

    /**
     * Used to exclude files, if filename matches the regex pattern.
     * 
     * @return the expression to be used
     */
    public String getExclude();

    /**
     * Specify the expression used to exclude files, if filename matches the
     * regex pattern.
     * 
     * @param exclude
     *            the expression
     * @return a reference to this Camel File binding model
     */
    public CamelFileConsumerBindingModel setExclude(String exclude);

    /**
     * Option to use the Idempotent Consumer EIP pattern to let Camel skip
     * already processed files.
     * 
     * @return whether idempotent is enabled or not
     */
    public Boolean isIdempotent();

    /**
     * Specify to use the Idempotent Consumer EIP pattern to let Camel skip
     * already processed files.
     * 
     * @param idempotent
     *            whether to use Idempotent Consumer EIP pattern or not
     * @return a reference to this Camel File binding model
     */
    public CamelFileConsumerBindingModel setIdempotent(Boolean idempotent);

    /**
     * Pluggable repository.
     * 
     * @return the repository
     */
    public String getIdempotentRepository();

    /**
     * Specify the Pluggable repository.
     * 
     * @param idempotentRepository
     *            the repository
     * @return a reference to this Camel File binding model
     */
    public CamelFileConsumerBindingModel setIdempotentRepository(
            String idempotentRepository);

    /**
     * Pluggable in-progress repository.
     * 
     * @return the in-progress repository
     */
    public String getInProgressRepository();

    /**
     * Specify the Pluggable in-progress repository.
     * 
     * @param inProgressRepository
     *            the in-progress repository
     * @return a reference to this Camel File binding model
     */
    public CamelFileConsumerBindingModel setInProgressRepository(
            String inProgressRepository);

    /**
     * Pluggable filter.
     * 
     * @return the filter
     */
    public String getFilter();

    /**
     * Specify the Pluggable filter.
     * 
     * @param filter
     *            the filter
     * @return a reference to this Camel File binding model
     */
    public CamelFileConsumerBindingModel setFilter(String filter);

    /**
     * Pluggable sorter.
     * 
     * @return the sorter
     */
    public String getSorter();

    /**
     * Specify the Pluggable sorter.
     * 
     * @param sorter
     *            the sorter
     * @return a reference to this Camel File binding model
     */
    public CamelFileConsumerBindingModel setSorter(String sorter);

    /**
     * Built-in sort. group sort by modified date.
     * 
     * @return the sort
     */
    public String getSortBy();

    /**
     * Specify the built-in sort to use. Supports nested sorts, so you can have
     * a sort by file name and as a 2nd group sort by modified date.
     * 
     * @param sortBy
     *            the built-in sort
     * @return a reference to this Camel File binding model
     */
    public CamelFileConsumerBindingModel setSortBy(String sortBy);

    /**
     * Used by consumer, to only poll the files if it has exclusive read-lock on
     * the file.
     * 
     * @return the read lock
     */
    public String getReadLock();

    /**
     * Used by consumer, to specify only polling the files if it has exclusive
     * read-lock on the file (i.e. the file is not in-progress or being
     * written). Camel will wait until the file lock is granted.
     * 
     * @param readLock
     *            the read lock
     * @return a reference to this Camel File binding model
     */
    public CamelFileConsumerBindingModel setReadLock(String readLock);

    /**
     * Optional timeout in milliseconds for the read-lock, if supported by the
     * read-lock.
     * 
     * @return the timeout in milliseconds
     */
    public Long getReadLockTimeout();

    /**
     * Specify an optional timeout in milliseconds for the read-lock, if
     * supported by the read-lock.
     * 
     * @param readLockTimeout
     *            the timeout
     * @return a reference to this Camel File binding model
     */
    public CamelFileConsumerBindingModel setReadLockTimeout(Long readLockTimeout);

    /**
     * Camel 2.6: Interval in millis for the read-lock, if supported by the read
     * lock. This interval is used for sleeping between attempts to acquire the
     * read lock.
     * 
     * @return the interval in millis
     */
    public Integer getReadLockCheckInterval();

    /**
     * Specify the interval in millis for the read-lock, if supported by the
     * read lock.
     * 
     * @param readLockCheckInterval
     *            the interval in millis
     * @return a reference to this Camel File binding model
     */
    public CamelFileConsumerBindingModel setReadLockCheckInterval(
            Integer readLockCheckInterval);

    /**
     * Pluggable read-lock.
     * 
     * @return the read-lock
     */
    public String getExclusiveReadLockStrategy();

    /**
     * Specify the Pluggable read-lock.
     * 
     * @param exclusiveReadLockStrategy
     *            the read-lock
     * @return a reference to this Camel File binding model
     */
    public CamelFileConsumerBindingModel setExclusiveReadLockStrategy(
            String exclusiveReadLockStrategy);

    /**
     * A pluggable File Process Strategy allowing you to implement your own
     * readLock option or similar.
     * 
     * @return file process strategy
     */
    public String getProcessStrategy();

    /**
     * Specify a pluggable File Process Strategy allowing you to implement your
     * own readLock option or similar.
     * 
     * @param processStrategy
     *            the process strategy
     * @return a reference to this Camel File binding model
     */
    public CamelFileConsumerBindingModel setProcessStrategy(
            String processStrategy);

    /**
     * An integer that defines the maximum number of messages to gather per poll.
     * 
     * @return maximum number og messages per poll
     */
    public Integer getMaxMessagesPerPoll();

    /**
     * Specify the maximum number of messages to gather per poll.
     * 
     * @param maxMessagesPerPoll
     *            the maximum number of messages to gather per poll
     * @return a reference to this Camel File binding model
     */
    public CamelFileConsumerBindingModel setMaxMessagesPerPoll(
            Integer maxMessagesPerPoll);

    /**
     * Camel 2.5: Whether the starting directory must exist.
     * 
     * @return true if the starting directory must exist; false otherwise
     */
    public Boolean isStartingDirectoryMustExist();

    /**
     * Specify whether the starting directory must exist.
     * 
     * @param startingDirectoryMustExist
     *            true if the directory must exist; false otherwise
     * @return a reference to this Camel File binding model
     */
    public CamelFileConsumerBindingModel setStartingDirectoryMustExist(
            Boolean startingDirectoryMustExist);

    /**
     * Camel 2.5: Similar to startingDirectoryMustExist but this applies during
     * polling recursive sub directories.
     * 
     * @return true if the starting directory must exist; false otherwise
     */
    public Boolean isDirectoryMustExist();

    /**
     * Camel 2.5: Similar to startingDirectoryMustExist but this applies during
     * polling recursive sub directories.
     * 
     * @param directoryMustExist
     *            true if the directory must exist; false otherwise
     * @return a reference to this Camel File binding model
     */
    public CamelFileConsumerBindingModel setDirectoryMustExist(
            Boolean directoryMustExist);

    /**
     * Camel 2.6: If provided, Camel will only consume files if a done file
     * exists.
     * 
     * @return the file name to use
     */
    public String getDoneFileName();

    /**
     * Camel 2.6: If provided, Camel will only consume files if a done file
     * exists.
     * 
     * @param doneFileName
     *            the file name to use
     * @return a reference to this Camel File binding model
     */
    public CamelFileConsumerBindingModel setDoneFileName(String doneFileName);

}
