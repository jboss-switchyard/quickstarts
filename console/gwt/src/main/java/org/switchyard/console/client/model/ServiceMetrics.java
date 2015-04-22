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

import java.util.List;

/**
 * ServiceMetrics
 * 
 * <p/>
 * Message metrics specific to a service/reference.
 * 
 * @author Rob Cernich
 */
public interface ServiceMetrics extends MessageMetrics, HasQName {

    /**
     * @return metrics for operations
     */
    List<OperationMetrics> getOperations();

    /**
     * @param value metrics for operations
     */
    void setOperations(List<OperationMetrics> value);

    /**
     * @return metrics for referenced services
     */
    List<ServiceMetrics> getReferences();

    /**
     * @param value metrics for referenced services
     */
    void setReferences(List<ServiceMetrics> value);

    /**
     * @return metrics for associated gateways
     */
    List<GatewayMetrics> getGateways();

    /**
     * @param value metrics for associated gateways
     */
    void setGateways(List<GatewayMetrics> value);

    /**
     * @return the containing application
     */
    String getApplication();

    /**
     * @param application the containing application
     */
    void setApplication(String application);

}
