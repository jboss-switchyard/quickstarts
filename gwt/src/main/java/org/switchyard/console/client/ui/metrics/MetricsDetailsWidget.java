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
package org.switchyard.console.client.ui.metrics;

import org.switchyard.console.client.model.MessageMetrics;
import org.switchyard.console.client.model.ServiceMetrics;

import com.google.gwt.user.client.ui.Widget;

/**
 * MetricsDetailsWidget
 * 
 * <p/>
 * Widget for displaying details for a set of metrics (specifically service or reference).
 */
public interface MetricsDetailsWidget {

    /**
     * @param metrics the metrics being detailed
     * @param systemMetrics parent metrics to be used for ratios
     */
    public void setMetrics(ServiceMetrics metrics, MessageMetrics systemMetrics);

    /**
     * @return the widget displaying the details
     */
    public Widget asWidget();
}
