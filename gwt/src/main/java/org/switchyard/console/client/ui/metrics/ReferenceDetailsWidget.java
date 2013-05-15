/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
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
