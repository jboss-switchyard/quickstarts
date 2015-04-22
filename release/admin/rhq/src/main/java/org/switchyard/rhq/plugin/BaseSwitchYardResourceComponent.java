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
package org.switchyard.rhq.plugin;

import static org.switchyard.rhq.plugin.SwitchYardConstants.METRIC_AVERAGE_PROCESSING_TIME;
import static org.switchyard.rhq.plugin.SwitchYardConstants.METRIC_FAULT_COUNT;
import static org.switchyard.rhq.plugin.SwitchYardConstants.METRIC_MAX_PROCESSING_TIME;
import static org.switchyard.rhq.plugin.SwitchYardConstants.METRIC_MIN_PROCESSING_TIME;
import static org.switchyard.rhq.plugin.SwitchYardConstants.METRIC_SUCCESS_COUNT;
import static org.switchyard.rhq.plugin.SwitchYardConstants.METRIC_TOTAL_COUNT;
import static org.switchyard.rhq.plugin.SwitchYardConstants.METRIC_TOTAL_PROCESSING_TIME;

import org.apache.commons.logging.Log;
import org.rhq.core.domain.measurement.MeasurementDataNumeric;
import org.rhq.core.domain.measurement.MeasurementScheduleRequest;
import org.rhq.core.pluginapi.inventory.InvalidPluginConfigurationException;
import org.rhq.core.pluginapi.inventory.ResourceComponent;
import org.rhq.core.pluginapi.inventory.ResourceContext;
import org.switchyard.rhq.plugin.model.Metrics;

/**
 * Base SwitchYard Component
 */
abstract class BaseSwitchYardResourceComponent<T extends ResourceComponent<?>> implements ResourceComponent<T> {
    /**
     * The current resource context.
     */
    private ResourceContext<T> resourceContext;

    @Override
    public void start(ResourceContext<T> context)
            throws InvalidPluginConfigurationException, Exception {
        this.resourceContext = context;
    }

    protected ResourceContext<T> getResourceContext() {
        return resourceContext;
    }

    @Override
    public void stop() {
        return;
    }

    protected abstract Log getLog();
    
    protected MeasurementDataNumeric getCommonMetric(final MeasurementScheduleRequest request, final Metrics metrics) {
        final String name = request.getName();
        if (METRIC_SUCCESS_COUNT.equals(name)) {
            return new MeasurementDataNumeric(request, Double.valueOf(metrics.getSuccessCount()));
        } else if (METRIC_FAULT_COUNT.equals(name)) {
            return new MeasurementDataNumeric(request, Double.valueOf(metrics.getFaultCount()));
        } else if (METRIC_TOTAL_COUNT.equals(name)) {
            return new MeasurementDataNumeric(request, Double.valueOf(metrics.getTotalCount()));
        } else if (METRIC_TOTAL_PROCESSING_TIME.equals(name)) {
            return new MeasurementDataNumeric(request, Double.valueOf(metrics.getTotalTime()));
        } else if (METRIC_AVERAGE_PROCESSING_TIME.equals(name)) {
            return new MeasurementDataNumeric(request, Double.valueOf(metrics.getAverageTime()));
        } else if (METRIC_MIN_PROCESSING_TIME.equals(name)) {
            return new MeasurementDataNumeric(request, Double.valueOf(metrics.getMinTime()));
        } else if (METRIC_MAX_PROCESSING_TIME.equals(name)) {
            return new MeasurementDataNumeric(request, Double.valueOf(metrics.getMaxTime()));
        } else {
            return null;
        }
    }
}
