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
package org.switchyard.rhq.plugin.model;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * SwitchYard Reference Metrics
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ReferenceMetrics extends Metrics implements ApplicationNamedMetric {
    private final String name;
    private final String application;
    private final Map<String, OperationMetrics> operationMetrics;
    private final Map<String, GatewayMetrics> gatewayMetrics;
    
    @JsonCreator
    public ReferenceMetrics(@JsonProperty("name") String name,
            @JsonProperty("application") String application,
            @JsonProperty("successCount") int successCount,
            @JsonProperty("faultCount") int faultCount,
            @JsonProperty("totalCount") int totalCount,
            @JsonProperty("averageTime") double averageTime,
            @JsonProperty("minTime") long minTime,
            @JsonProperty("maxTime") long maxTime,
            @JsonProperty("totalTime") long totalTime,
            @JsonProperty("operations") OperationMetrics[] operations,
            @JsonProperty("gateways") GatewayMetrics[] gateways) {
        super(successCount, faultCount, totalCount, averageTime, minTime, maxTime, totalTime);
        this.name = name;
        this.application = application;
        this.operationMetrics = ModelUtil.createNamedMetricMap(operations);
        this.gatewayMetrics = ModelUtil.createNamedMetricMap(gateways);
    }

    public String getName() {
        return name;
    }

    public String getApplication() {
        return application;
    }

    public Map<String, OperationMetrics> getOperationMetrics() {
        return operationMetrics;
    }

    public Map<String, GatewayMetrics> getGatewayMetrics() {
        return gatewayMetrics;
    }
}
