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
package org.switchyard.admin;

import org.switchyard.Exchange;

/**
 * Message metric aware element which is interested in collecting them from
 * executed exchanges.
 */
public interface MessageMetricsAware {

    /**
     * Gets related message metrics.
     * @return Message metrics.
     */
    MessageMetrics getMessageMetrics();

    /**
     * Reset all collected metrics.
     */
    void resetMessageMetrics();

    /**
     * Collect metrics from given exchange.
     * @param exchange Completed exchange.
     */
    void recordMetrics(Exchange exchange);

}
