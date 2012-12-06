/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General License for more details. 
 * You should have received a copy of the GNU Lesser General License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
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
