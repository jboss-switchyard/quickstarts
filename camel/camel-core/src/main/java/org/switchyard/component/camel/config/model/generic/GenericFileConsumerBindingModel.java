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
package org.switchyard.component.camel.config.model.generic;

import org.switchyard.component.camel.config.model.CamelScheduledPollConsumer;

/**
 * Binding model for file based consumers.
 * 
 * @author Lukasz Dywicki
 */
public interface GenericFileConsumerBindingModel extends CamelScheduledPollConsumer {

    /**
     * If a directory, will look for files in all the sub-directories as well.
     * 
     * @return true if recursive; false otherwise
     */
    Boolean isRecursive();

    /**
     * Specify whether to look for files in all the sub-directories as well.
     * 
     * @param recursive
     *            true to look for files in all the sub-directories; false
     *            otherwise
     * @return a reference to this Camel File binding model
     */
    GenericFileConsumerBindingModel setRecursive(Boolean recursive);

    /**
     * Whether the file will be deleted after it is processed.
     * 
     * @return true if to be deleted; false otherwise
     */
    Boolean isDelete();

    /**
     * Specify whether the file will be deleted after it is processed.
     * 
     * @param delete
     *            true to get the file deleted; false otherwise
     * @return a reference to this Camel File binding model
     */
    GenericFileConsumerBindingModel setDelete(Boolean delete);

    /**
     * If true, the file is not moved or deleted in any way. This option is good
     * for readonly data, or for ETL type requirements. If noop=true, Camel will
     * set idempotent=true as well, to avoid consuming the same files over and
     * over again.
     * 
     * @return true if no operation is to be performed; false otherwise
     */
    Boolean isNoop();

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
    GenericFileConsumerBindingModel setNoop(Boolean noop);

    /**
     * Expression used to dynamically set the filename when moving it before
     * processing.
     * 
     * @return the expression to be used
     */
    String getPreMove();

    /**
     * Specify the expression used to dynamically set the filename when moving
     * it before processing.
     * 
     * @param preMove
     *            the expression
     * @return a reference to this Camel File binding model
     */
    GenericFileConsumerBindingModel setPreMove(String preMove);

    /**
     * Expression used to dynamically set the filename when moving it after
     * processing.
     * 
     * @return the expression to be used
     */
    String getMove();

    /**
     * Specify the expression used to dynamically set the filename when moving
     * it after processing.
     * 
     * @param move
     *            the expression
     * @return a reference to this Camel File binding model
     */
    GenericFileConsumerBindingModel setMove(String move);

    /**
     * Expression used to dynamically set a different target directory when
     * moving files after processing failed.
     * 
     * @return the expression to be used
     */
    String getMoveFailed();

    /**
     * Specify expression used to dynamically set a different target directory
     * when moving files after.
     * 
     * @param moveFailed
     *            the expression
     * @return a reference to this Camel File binding model
     */
    GenericFileConsumerBindingModel setMoveFailed(String moveFailed);

    /**
     * Used to include files, if filename matches the regex pattern.
     * 
     * @return the expression to be used
     */
    String getInclude();

    /**
     * Specify the expression used to include files, if filename matches the
     * regex pattern.
     * 
     * @param include
     *            the expression
     * @return a reference to this Camel File binding model
     */
    GenericFileConsumerBindingModel setInclude(String include);

    /**
     * Used to exclude files, if filename matches the regex pattern.
     * 
     * @return the expression to be used
     */
    String getExclude();

    /**
     * Specify the expression used to exclude files, if filename matches the
     * regex pattern.
     * 
     * @param exclude
     *            the expression
     * @return a reference to this Camel File binding model
     */
    GenericFileConsumerBindingModel setExclude(String exclude);

    /**
     * Option to use the Idempotent Consumer EIP pattern to let Camel skip
     * already processed files.
     * 
     * @return whether idempotent is enabled or not
     */
    Boolean isIdempotent();

    /**
     * Specify to use the Idempotent Consumer EIP pattern to let Camel skip
     * already processed files.
     * 
     * @param idempotent
     *            whether to use Idempotent Consumer EIP pattern or not
     * @return a reference to this Camel File binding model
     */
    GenericFileConsumerBindingModel setIdempotent(Boolean idempotent);

    /**
     * Pluggable repository.
     * 
     * @return the repository
     */
    String getIdempotentRepository();

    /**
     * Specify the Pluggable repository.
     * 
     * @param idempotentRepository
     *            the repository
     * @return a reference to this Camel File binding model
     */
    GenericFileConsumerBindingModel setIdempotentRepository(String idempotentRepository);

    /**
     * Pluggable in-progress repository.
     * 
     * @return the in-progress repository
     */
    String getInProgressRepository();

    /**
     * Specify the Pluggable in-progress repository.
     * 
     * @param inProgressRepository
     *            the in-progress repository
     * @return a reference to this Camel File binding model
     */
    GenericFileConsumerBindingModel setInProgressRepository(String inProgressRepository);

    /**
     * Pluggable filter.
     * 
     * @return the filter
     */
    String getFilter();

    /**
     * Specify the Pluggable filter.
     * 
     * @param filter
     *            the filter
     * @return a reference to this Camel File binding model
     */
    GenericFileConsumerBindingModel setFilter(String filter);

    /**
     * Pluggable sorter.
     * 
     * @return the sorter
     */
    String getSorter();

    /**
     * Specify the Pluggable sorter.
     * 
     * @param sorter
     *            the sorter
     * @return a reference to this Camel File binding model
     */
    GenericFileConsumerBindingModel setSorter(String sorter);

    /**
     * Built-in sort. group sort by modified date.
     * 
     * @return the sort
     */
    String getSortBy();

    /**
     * Specify the built-in sort to use. Supports nested sorts, so you can have
     * a sort by file name and as a 2nd group sort by modified date.
     * 
     * @param sortBy
     *            the built-in sort
     * @return a reference to this Camel File binding model
     */
    GenericFileConsumerBindingModel setSortBy(String sortBy);

    /**
     * Used by consumer, to only poll the files if it has exclusive read-lock on
     * the file.
     * 
     * @return the read lock
     */
    String getReadLock();

    /**
     * Used by consumer, to specify only polling the files if it has exclusive
     * read-lock on the file (i.e. the file is not in-progress or being
     * written). Camel will wait until the file lock is granted.
     * 
     * @param readLock
     *            the read lock
     * @return a reference to this Camel File binding model
     */
    GenericFileConsumerBindingModel setReadLock(String readLock);

    /**
     * Optional timeout in milliseconds for the read-lock, if supported by the
     * read-lock.
     * 
     * @return the timeout in milliseconds
     */
    Long getReadLockTimeout();

    /**
     * Specify an optional timeout in milliseconds for the read-lock, if
     * supported by the read-lock.
     * 
     * @param readLockTimeout
     *            the timeout
     * @return a reference to this Camel File binding model
     */
    GenericFileConsumerBindingModel setReadLockTimeout(Long readLockTimeout);

    /**
     * Camel 2.6: Interval in millis for the read-lock, if supported by the read
     * lock. This interval is used for sleeping between attempts to acquire the
     * read lock.
     * 
     * @return the interval in millis
     */
    Integer getReadLockCheckInterval();

    /**
     * Specify the interval in millis for the read-lock, if supported by the
     * read lock.
     * 
     * @param readLockCheckInterval
     *            the interval in millis
     * @return a reference to this Camel File binding model
     */
    GenericFileConsumerBindingModel setReadLockCheckInterval(Integer readLockCheckInterval);

    /**
     * Pluggable read-lock.
     * 
     * @return the read-lock
     */
    String getExclusiveReadLockStrategy();

    /**
     * Specify the Pluggable read-lock.
     * 
     * @param exclusiveReadLockStrategy
     *            the read-lock
     * @return a reference to this Camel File binding model
     */
    GenericFileConsumerBindingModel setExclusiveReadLockStrategy(String exclusiveReadLockStrategy);

    /**
     * A pluggable File Process Strategy allowing you to implement your own
     * readLock option or similar.
     * 
     * @return file process strategy
     */
    String getProcessStrategy();

    /**
     * Specify a pluggable File Process Strategy allowing you to implement your
     * own readLock option or similar.
     * 
     * @param processStrategy
     *            the process strategy
     * @return a reference to this Camel File binding model
     */
    GenericFileConsumerBindingModel setProcessStrategy(String processStrategy);

    /**
     * Camel 2.5: Whether the starting directory must exist.
     * 
     * @return true if the starting directory must exist; false otherwise
     */
    Boolean isStartingDirectoryMustExist();

    /**
     * Specify whether the starting directory must exist.
     * 
     * @param startingDirectoryMustExist
     *            true if the directory must exist; false otherwise
     * @return a reference to this Camel File binding model
     */
    GenericFileConsumerBindingModel setStartingDirectoryMustExist(Boolean startingDirectoryMustExist);

    /**
     * Camel 2.5: Similar to startingDirectoryMustExist but this applies during
     * polling recursive sub directories.
     * 
     * @return true if the starting directory must exist; false otherwise
     */
    Boolean isDirectoryMustExist();

    /**
     * Camel 2.5: Similar to startingDirectoryMustExist but this applies during
     * polling recursive sub directories.
     * 
     * @param directoryMustExist
     *            true if the directory must exist; false otherwise
     * @return a reference to this Camel File binding model
     */
    GenericFileConsumerBindingModel setDirectoryMustExist(Boolean directoryMustExist);

    /**
     * Camel 2.6: If provided, Camel will only consume files if a done file
     * exists.
     * 
     * @return the file name to use
     */
    String getDoneFileName();

    /**
     * Camel 2.6: If provided, Camel will only consume files if a done file
     * exists.
     * 
     * @param doneFileName
     *            the file name to use
     * @return a reference to this Camel File binding model
     */
    GenericFileConsumerBindingModel setDoneFileName(String doneFileName);

}
