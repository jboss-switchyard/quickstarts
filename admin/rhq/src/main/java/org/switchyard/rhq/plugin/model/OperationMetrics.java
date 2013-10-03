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

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * SwitchYard Operation Metrics
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class OperationMetrics extends Metrics implements NamedMetric {
    private final String name;
    
    @JsonCreator
    public OperationMetrics(@JsonProperty("name") String name,
            @JsonProperty("successCount") int successCount,
            @JsonProperty("faultCount") int faultCount,
            @JsonProperty("totalCount") int totalCount,
            @JsonProperty("averageTime") double averageTime,
            @JsonProperty("minTime") long minTime,
            @JsonProperty("maxTime") long maxTime,
            @JsonProperty("totalTime") long totalTime) {
        super(successCount, faultCount, totalCount, averageTime, minTime, maxTime, totalTime);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
