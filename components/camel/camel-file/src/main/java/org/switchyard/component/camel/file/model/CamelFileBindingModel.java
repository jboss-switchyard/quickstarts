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
