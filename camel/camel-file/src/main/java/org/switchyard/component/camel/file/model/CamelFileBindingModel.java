/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.camel.file.model;

import org.switchyard.component.camel.common.model.file.GenericFileBindingModel;
import org.switchyard.component.camel.common.model.file.GenericFileProducerBindingModel;

/**
 * Represents the configuration settings for Camel File binding.
 */
public interface CamelFileBindingModel extends GenericFileBindingModel {

    /**
     * The consumer's configurations.
     * @return an instance of the camel file consumer binding model
     */
    CamelFileConsumerBindingModel getConsumer();

    /**
     * Specify the consumer binding model. 
     * @param consumer the consumer binding model
     * @return a reference to this Camel File binding model
     */
    GenericFileBindingModel setConsumer(CamelFileConsumerBindingModel consumer);

    /**
     * The producers's configurations.
     * @return an instance of the camel file producer binding model
     */
    GenericFileProducerBindingModel getProducer();

    /**
     * Specify the producer binding model.
     * @param producer the producer binding model
     * @return a reference to this Camel File binding model
     */
    GenericFileBindingModel setProducer(GenericFileProducerBindingModel producer);

}
