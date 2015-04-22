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

import org.switchyard.component.camel.common.model.CamelBindingModel;

/**
 * Jpa binding model definition.
 * 
 * @author Lukasz Dywicki
 */
public interface CamelJpaBindingModel extends CamelBindingModel {

    /**
     * The entity class name.
     * 
     * @return Name of entity class.
     */
    String getEntityClassName();

    /**
     * Specify JPA entity class name.
     * 
     * @param entityClassName Class name.
     * @return a reference to this binding model
     */
    CamelJpaBindingModel setEntityClassName(String entityClassName);

    /**
     * Name of persistence unit.
     * 
     * @return Persistence unit.
     */
    String getPersistenceUnit();

    /**
     * Specify name of persistence unit used by camel.
     * 
     * @param persistenceUnit Persistence unit name.
     * @return a reference to this binding model
     */
    CamelJpaBindingModel setPersistenceUnit(String persistenceUnit);

    /**
     * Name of Spring platform transaction manager to use.
     * 
     * @return Transaction manager bean name.
     */
    String getTransactionManager();

    /**
     * Specify name of Spring transaction manager bean used for transaction
     * handling.
     * 
     * @param transactionManager Name of transaction manager bean.
     * @return a reference to this binding model
     */
    CamelJpaBindingModel setTransactionManager(String transactionManager);

    /**
     * The consumer's configurations.
     * 
     * @return an instance of the camel jpa consumer binding model
     */
    CamelJpaConsumerBindingModel getConsumer();

    /**
     * Specify the consumer binding model. 
     * 
     * @param consumer The consumer binding model
     * @return a reference to this binding model
     */
    CamelJpaBindingModel setConsumer(CamelJpaConsumerBindingModel consumer);

    /**
     * The producer's configurations.
     * 
     * @return an instance of the camel jpa producer binding model
     */
    CamelJpaProducerBindingModel getProducer();

    /**
     * Specify the producer binding model. 
     * 
     * @param producer The producer binding model
     * @return a reference to this binding model
     */
    CamelJpaBindingModel setProducer(CamelJpaProducerBindingModel producer);



}
