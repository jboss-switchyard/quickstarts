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
