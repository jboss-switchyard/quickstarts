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

import org.jboss.as.console.client.shared.viewframework.builder.OneToOneLayout;
import org.switchyard.console.client.model.MessageMetrics;
import org.switchyard.console.client.model.ServiceMetrics;

import com.google.gwt.user.client.ui.Widget;

/**
 * ReferenceDetailsWidget
 * 
 * Provides a widget for displaying a {@link ServiceMetrics}.
 */
public class ReferenceDetailsWidget implements MetricsDetailsWidget {

    private MessageMetricsViewer _referenceMetricsViewer;
    private ServiceOperationMetricsList _referenceOperationMetricsList;
    private GatewayMetricsList _gatewayMetricsList;

    /**
     * Create a new ReferenceDetailsWidget.
     */
    public ReferenceDetailsWidget() {
    }

    /**
     * @return the widget
     */
    @Override
    public Widget asWidget() {
        _referenceMetricsViewer = new MessageMetricsViewer(true);
        _referenceOperationMetricsList = new ServiceOperationMetricsList();
        _gatewayMetricsList = new GatewayMetricsList();

        OneToOneLayout serviceMetricsLayout = new OneToOneLayout().setPlain(true).setHeadline("Reference Metrics")
                .setDescription("Displays message metrics for a selected reference.")
                .setMaster(null, _referenceMetricsViewer.asWidget())
                .addDetail("Gateway Metrics", _gatewayMetricsList.asWidget())
                .addDetail("Operation Metrics", _referenceOperationMetricsList.asWidget());
        return serviceMetricsLayout.build();
    }

    /**
     * Updates the widget with the information for the specified service.
     * 
     * @param metrics the metrics for the selected service.
     * @param systemMetrics the metrics for the overall system.
     */
    @Override
    public void setMetrics(ServiceMetrics metrics, MessageMetrics systemMetrics) {
        if (metrics == null) {
            _referenceMetricsViewer.clear();
            _referenceOperationMetricsList.setServiceMetrics(null);
            _gatewayMetricsList.setServiceMetrics(null);
            return;
        }
        if (systemMetrics == null) {
            _referenceMetricsViewer.setMessageMetrics(metrics);
        } else {
            _referenceMetricsViewer.setMessageMetrics(metrics, systemMetrics.getTotalCount(),
                    systemMetrics.getTotalProcessingTime());
        }
        _referenceOperationMetricsList.setServiceMetrics(metrics);
        _gatewayMetricsList.setServiceMetrics(metrics);
    }

}
