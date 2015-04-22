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


/**
 * Metrics
 */
public class Metrics {
    private final int successCount;
    private final int faultCount;
    private final int totalCount;
    private final double averageTime;
    private final long minTime;
    private final long maxTime;
    private final long totalTime;

    public Metrics(final int successCount, final int faultCount, final int totalCount,
            final double averageTime, final long minTime, final long maxTime,
            final long totalTime) {
        this.successCount = successCount;
        this.faultCount = faultCount;
        this.totalCount = totalCount;
        this.averageTime = averageTime;
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.totalTime = totalTime;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getFaultCount() {
        return faultCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public double getAverageTime() {
        return averageTime;
    }

    public long getMinTime() {
        return minTime;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public long getTotalTime() {
        return totalTime;
    }
}
