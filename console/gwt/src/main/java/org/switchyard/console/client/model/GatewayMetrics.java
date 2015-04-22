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
package org.switchyard.console.client.model;

/**
 * OperationMetrics
 * 
 * <p/>
 * Message metrics specific to a gateway/binding.
 */
public interface GatewayMetrics extends MessageMetrics {

    /**
     * @return binding name
     */
    String getName();

    /**
     * @param value binding name
     */
    void setName(String value);

    /**
     * @return binding type
     */
    String getType();

    /**
     * @param value binding type
     */
    void setType(String value);

}
