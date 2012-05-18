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
package org.switchyard.component.camel.config.model.jpa;

import org.switchyard.component.camel.config.model.CamelScheduledBatchPollConsumer;

/**
 * Camel based jpa consumer configuration. 
 * 
 * @author Lukasz Dywicki
 */
public interface CamelJpaConsumerBindingModel extends CamelScheduledBatchPollConsumer {

    /**
     * If true, the entity is deleted after it is consumed; if false, the entity is not deleted.
     * 
     * @return Should entity be deleted.
     */
    Boolean isConsumeDelete();

    /**
     * Specify delete - or not behavior.
     * 
     * @param consumeDelete If true entity will be removed after consumption.
     * @return a reference to this binding model
     */
    CamelJpaConsumerBindingModel setConsumeDelete(Boolean consumeDelete);

    /**
     * Checks if the entity should be locked for processing.
     * 
     * @return True if lock should be acquired.
     */
    Boolean isConsumeLockEntity();

    /**
     * Specifies whether or not to set an exclusive lock on each entity bean while
     * processing the results from polling.
     * 
     * @param consumeLockEntity Should lock be acquired.
     * @return a reference to this binding model
     */
    CamelJpaConsumerBindingModel setConsumeLockEntity(Boolean consumeLockEntity);

    /**
     * Gets number of maximum results in one query execution.
     * 
     * @return Limit of query results.
     */
    Integer getMaximumResults();

    /**
     * Set the maximum number of results to retrieve on the Query.
     * 
     * @param maximumResults Number of maximum results in one poll
     * @return a reference to this binding model
     */
    CamelJpaConsumerBindingModel setMaximumResults(Integer maximumResults);

    /**
     * Custom query to use when consuming data.
     * 
     * @return JPA query to use for polling.
     */
    String getQuery();

    /**
     * Specify query to use for polling.
     * 
     * @param query JPA query.
     * @return a reference to this binding model
     */
    CamelJpaConsumerBindingModel setQuery(String query);

    /**
     * Named query to use when consuming data.
     * 
     * @return Named query.
     */
    String getNamedQuery();

    /**
     * Specify named query to use for polling.
     * 
     * @param namedQuery Named query to use.
     * @return a reference to this binding model
     */
    CamelJpaConsumerBindingModel setNamedQuery(String namedQuery);

    /**
     * Custom, native and database specific query to use for polling.
     * 
     * @return SQL query to use.
     */
    String getNativeQuery();

    /**
     * Specify native query to use for polling.
     * 
     * @param nativeQuery Native query to use.
     * @return a reference to this binding model
     */
    CamelJpaConsumerBindingModel setNativeQuery(String nativeQuery);

    /**
     * Type of result class.
     * 
     * @return Class name.
     */
    String getResultClass();

    /**
     * Defines the type of the returned payload. Without this option, we will return
     * an object array. Only has an affect when using in conjunction with native query
     * when consuming data.
     * 
     * @param resultClass Result class used for native call.
     * @return a reference to this binding model
     */
    CamelJpaConsumerBindingModel setResultClass(String resultClass);

    /**
     * Get the transaction behavior.
     * 
     * @return True if whole batch should be covered by one transaction.
     */
    Boolean isTransacted();

    /**
     * Whether to run the consumer in transacted mode, by which all messages will
     * either commit or rollback, when the entire batch has been processed. The default behavior
     * is to commit all the previously successfully processed messages, and only 
     * rollback the last failed message.
     * 
     * @param transacted Should whole processing be transactional.
     * @return a reference to this binding model
     */
    CamelJpaConsumerBindingModel setTransacted(Boolean transacted);

}
