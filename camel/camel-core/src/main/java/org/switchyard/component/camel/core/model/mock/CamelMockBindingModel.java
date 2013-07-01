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
package org.switchyard.component.camel.core.model.mock;

import org.switchyard.component.camel.common.model.CamelBindingModel;

/**
 * Represents the configuration settings for a Mock endpoint in Camel. 
 * 
 * The Mock component provides a powerful declarative testing mechanism, 
 * which is similar to jMock in that it allows declarative expectations 
 * to be created on any Mock endpoint before a test begins. Then the test 
 * is run, which typically fires messages to one or more endpoints, and 
 * finally the expectations can be asserted in a test case to ensure the 
 * system worked as expected.
 * 
 * @author Mario Antollini
 * 
 */
public interface CamelMockBindingModel extends CamelBindingModel {

    /**
     * The name that uniquely identifies the endpoint.
     * @return The name that uniquely identifies the endpoint
     */
    String getEndpointName();

    /**
     * The name that uniquely identifies the endpoint.
     * @param name the name that uniquely identifies the endpoint
     * @return a reference to this Mock binding model
     */
    CamelMockBindingModel setEndpointName(String name);

    /**
     * An integer that specifies a group size for throughput logging. 
     * @return the group size for throughput logging 
     */
    Integer getReportGroup();

    /**
     * Specify a group size for throughput logging.
     * @param size the group size for throughput logging
     * @return a reference to this Mock binding model
     */
    CamelMockBindingModel setReportGroup(Integer size);

}
