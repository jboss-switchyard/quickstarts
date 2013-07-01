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
package org.switchyard.component.camel.jpa.model;

import org.switchyard.component.camel.common.model.consumer.CamelScheduledBatchPollConsumer;

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
