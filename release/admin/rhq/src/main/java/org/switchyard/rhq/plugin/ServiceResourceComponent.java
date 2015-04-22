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

import static org.switchyard.rhq.plugin.SwitchYardConstants.METRIC_MAX_REQUESTS;
import static org.switchyard.rhq.plugin.SwitchYardConstants.METRIC_THROTTLING_ENABLED;
import static org.switchyard.rhq.plugin.SwitchYardConstants.METRIC_TIME_PERIOD;
import static org.switchyard.rhq.plugin.SwitchYardConstants.OPERATION_PARAMETER_ENABLED;
import static org.switchyard.rhq.plugin.SwitchYardConstants.OPERATION_PARAMETER_MAX_REQUESTS;
import static org.switchyard.rhq.plugin.SwitchYardConstants.OPERATION_RESET;
import static org.switchyard.rhq.plugin.SwitchYardConstants.OPERATION_SET_THROTTLING;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rhq.core.domain.configuration.Configuration;
import org.rhq.core.domain.configuration.PropertySimple;
import org.rhq.core.domain.measurement.AvailabilityType;
import org.rhq.core.domain.measurement.MeasurementDataNumeric;
import org.rhq.core.domain.measurement.MeasurementDataTrait;
import org.rhq.core.domain.measurement.MeasurementReport;
import org.rhq.core.domain.measurement.MeasurementScheduleRequest;
import org.rhq.core.pluginapi.measurement.MeasurementFacet;
import org.rhq.core.pluginapi.operation.OperationFacet;
import org.rhq.core.pluginapi.operation.OperationResult;
import org.rhq.modules.plugins.jbossas7.json.Operation;
import org.switchyard.rhq.plugin.model.Application;
import org.switchyard.rhq.plugin.model.Gateway;
import org.switchyard.rhq.plugin.model.GatewayMetrics;
import org.switchyard.rhq.plugin.model.OperationMetrics;
import org.switchyard.rhq.plugin.model.Service;
import org.switchyard.rhq.plugin.model.ServiceMetrics;
import org.switchyard.rhq.plugin.model.Throttling;
import org.switchyard.rhq.plugin.operations.ResetServiceMetrics;
import org.switchyard.rhq.plugin.operations.UpdateThrottling;

/**
 * SwitchYard Service Resource Component
 */
public class ServiceResourceComponent extends BaseSwitchYardResourceComponent<ApplicationResourceComponent> implements MeasurementFacet, OperationFacet {
    /**
     * The logger instance.
     */
    private static Log LOG = LogFactory.getLog(ServiceResourceComponent.class);
    
    protected Log getLog() {
        return LOG;
    }

    @Override
    public AvailabilityType getAvailability() {
        final Service service = getService();
        return (service == null ? AvailabilityType.DOWN : AvailabilityType.UP);
    }

    public Map<String, Gateway> getGateways() {
        final Service service = getService();
        if (service == null) {
            return Collections.emptyMap();
        } else {
            return service.getGateways();
        }
    }
    
    public Service getService() {
        final String serviceKey = getResourceContext().getResourceKey();
        return getApplication().getServices().get(serviceKey);
    }
    
    public Application getApplication() {
        return getResourceContext().getParentResourceComponent().getApplication();
    }
    
    public void clearServiceMetrics() {
        getResourceContext().getParentResourceComponent().clearServiceMetrics();
    }

    private ServiceMetrics getServiceMetrics() {
        final String serviceKey = getResourceContext().getResourceKey();
        final Map<String, ServiceMetrics> serviceMetrics = getResourceContext().getParentResourceComponent().getServiceMetrics();
        return (serviceMetrics != null) ? serviceMetrics.get(serviceKey) : null;
    }

    public Map<String, OperationMetrics> getOperationMetrics() {
        final ServiceMetrics serviceMetrics = getServiceMetrics();
        if (serviceMetrics != null) {
            return serviceMetrics.getOperationMetrics();
        } else {
            return Collections.emptyMap();
        }
    }

    public Map<String, GatewayMetrics> getGatewayMetrics() {
        final ServiceMetrics serviceMetrics = getServiceMetrics();
        if (serviceMetrics != null) {
            return serviceMetrics.getGatewayMetrics();
        } else {
            return Collections.emptyMap();
        }
    }

    public void clearApplications() {
        getResourceContext().getParentResourceComponent().clearApplications();
    }

    @Override
    public void getValues(final MeasurementReport report, final Set<MeasurementScheduleRequest> requests) throws Exception {
        final ServiceMetrics metrics = getServiceMetrics();
        final Service service = getService();
        if ((metrics != null) && (service != null)) {
            for (MeasurementScheduleRequest request: requests) {
                final MeasurementDataNumeric measurementData = getCommonMetric(request, metrics);
                if (measurementData != null) {
                    report.addData(measurementData);
                } else {
                    final String name = request.getName();
                    final Throttling throttling = service.getThrottling();
                    if (METRIC_THROTTLING_ENABLED.equals(name)) {
                        report.addData(new MeasurementDataTrait(request, Boolean.toString(throttling.isEnabled())));
                    } else if (METRIC_TIME_PERIOD.equals(name)) {
                        report.addData(new MeasurementDataTrait(request, Long.toString(throttling.getTimePeriod())));
                    } else if (METRIC_MAX_REQUESTS.equals(name)) {
                        report.addData(new MeasurementDataTrait(request, Integer.toString(throttling.getMaxRequests())));
                    } else if (LOG.isDebugEnabled()) {
                        LOG.debug("Unable to collect Service measurement " + request.getName());
                    }
                }
            }
        }
    }

    @Override
    public OperationResult invokeOperation(final String name, final Configuration parameters) throws InterruptedException, Exception {
        if (OPERATION_RESET.equals(name)) {
            final Service service = getService();
            final Application application = getApplication();
            if ((service != null) && (application != null)) {
                final String serviceName = service.getName().toString();
                final String applicationName = application.getName().toString();
                execute(new ResetServiceMetrics(applicationName, serviceName), Void.class);
                clearServiceMetrics();
            }
        } else if (OPERATION_SET_THROTTLING.equals(name)) {
            final PropertySimple enabled = (PropertySimple) parameters.get(OPERATION_PARAMETER_ENABLED);
            final PropertySimple maxRequests = (PropertySimple) parameters.get(OPERATION_PARAMETER_MAX_REQUESTS);
            final Service service = getService();
            final Application application = getApplication();
            if ((service != null) && (application != null)) {
                final String serviceName = service.getName().toString();
                final String applicationName = application.getName().toString();
                execute(new UpdateThrottling(applicationName, serviceName,
                        (enabled != null ? enabled.getBooleanValue() : null),
                        (maxRequests != null ? maxRequests.getIntegerValue() : null)),
                        Void.class);
                clearApplications();
            }
        } else if (LOG.isDebugEnabled()) {
            LOG.warn("Unknown Service operation " + name);
        }
        return null;
    }

    public <T> T execute(final Operation operation, Class<T> clazz) {
        return getResourceContext().getParentResourceComponent().execute(operation, clazz);
    }
}
