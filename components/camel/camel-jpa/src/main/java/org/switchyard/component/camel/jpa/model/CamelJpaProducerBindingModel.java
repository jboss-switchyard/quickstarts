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

/**
 * JPA Producer configuration.
 * 
 * @author Lukasz Dywicki
 */
public interface CamelJpaProducerBindingModel {

    /**
     * If flush should be executed after persist call.
     * 
     * @return True if flush should be executed after every store operation.
     */
    Boolean isFlushOnSend();

    /**
     * Flushes the EntityManager after the entity bean has been persisted.
     * 
     * @param flushOnSend Should flush be executed after every persist/merge.
     * @return a reference to this binding model
     */
    CamelJpaProducerBindingModel setFlushOnSend(Boolean flushOnSend);

    /**
     * Get the persist flag.
     * 
     * @return True if persist will be used to store entity.
     */
    Boolean isUsePersist();

    /**
     * Indicates to use entityManager.persist(entity) instead of entityManager.merge(entity).
     * Note: entityManager.persist(entity) doesn't work for detached entities
     * (where the EntityManager has to execute an UPDATE instead of an INSERT query)!
     * 
     * @param usePersist Should camel call persist instead of merge for entities.
     * @return a reference to this binding model
     */
    CamelJpaProducerBindingModel setUsePersist(Boolean usePersist);

}
