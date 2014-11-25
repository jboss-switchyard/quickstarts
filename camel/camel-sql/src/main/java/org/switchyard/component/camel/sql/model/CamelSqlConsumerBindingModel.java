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
package org.switchyard.component.camel.sql.model;

/**
 * Represents the consumer configuration settings for a SQL endpoint in Camel.
 * 
 * @author David Virgil Naranjo
 */
public interface CamelSqlConsumerBindingModel {
    /**
     * Delay in milliseconds.
     *
     * @return Delay.
     */
    Long getDelay();

    /**
     * Set the delay in milliseconds.
     *
     * @param delay
     *            Delay to use.
     * @return a reference to this binding model
     */
    CamelSqlConsumerBindingModel setDelay(Long delay);

    /**
     * Initial delay in milliseconds.
     *
     * @return the initial delay.
     */
    Long getInitialDelay();

    /**
     * Set the delay in milliseconds.
     *
     * @param initialDelay
     *            Initial delay to use.
     * @return a reference to this binding model
     */
    CamelSqlConsumerBindingModel setInitialDelay(Long initialDelay);
}
