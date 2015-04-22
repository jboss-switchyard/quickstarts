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

import static org.switchyard.rhq.plugin.SwitchYardConstants.METRIC_STATE;
import static org.switchyard.rhq.plugin.SwitchYardConstants.OPERATION_START;
import static org.switchyard.rhq.plugin.SwitchYardConstants.OPERATION_STOP;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rhq.core.domain.configuration.Configuration;
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
import org.switchyard.rhq.plugin.model.Reference;
import org.switchyard.rhq.plugin.operations.StartGateway;
import org.switchyard.rhq.plugin.operations.StopGateway;

/**
 * SwitchYard Reference Binding Resource Component
 */
public class ReferenceBindingResourceComponent extends BaseSwitchYardResourceComponent<ReferenceResourceComponent> implements MeasurementFacet, OperationFacet {
    /**
     * The logger instance.
     */
    private static Log LOG = LogFactory.getLog(ReferenceBindingResourceComponent.class);
    
    protected Log getLog() {
        return LOG;
    }

    @Override
    public AvailabilityType getAvailability() {
        final Gateway gateway = getGateway();
        return (gateway == null) ? AvailabilityType.DOWN : AvailabilityType.UP;
    }

    public Gateway getGateway() {
        final String gatewayKey = getResourceContext().getResourceKey();
        return getResourceContext().getParentResourceComponent().getGateways().get(gatewayKey);
    }

    public Reference getReference() {
        return getResourceContext().getParentResourceComponent().getReference();
    }

    public Application getApplication() {
        return getResourceContext().getParentResourceComponent().getApplication();
    }

    public GatewayMetrics getGatewayMetrics() {
        final String operationKey = getResourceContext().getResourceKey();
        return getResourceContext().getParentResourceComponent().getGatewayMetrics().get(operationKey);
    }

    public void clearApplications() {
        getResourceContext().getParentResourceComponent().clearApplications();
    }

    @Override
    public void getValues(final MeasurementReport report, final Set<MeasurementScheduleRequest> requests) throws Exception {
        final GatewayMetrics metrics = getGatewayMetrics();
        final Gateway gateway = getGateway();
        if ((metrics != null) && (gateway != null)) {
            for (MeasurementScheduleRequest request: requests) {
                final MeasurementDataNumeric measurementData = getCommonMetric(request, metrics);
                if (measurementData != null) {
                    report.addData(measurementData);
                } else {
                    final String name = request.getName();
                    if (METRIC_STATE.equals(name)) {
                        report.addData(new MeasurementDataTrait(request, gateway.getState()));
                    } else if (LOG.isDebugEnabled()) {
                        LOG.debug("Unable to collect Reference Binding measurement " + request.getName());
                    }
                }
            }
        }
    }

    @Override
    public OperationResult invokeOperation(final String name, final Configuration parameters) throws InterruptedException, Exception {
        if (OPERATION_START.equals(name)) {
            final String bindingKey = getResourceContext().getResourceKey();
            final Reference reference = getReference();
            if (reference != null) {
                final Application application = getApplication();
                if (application != null) {
                    final String referenceName = reference.getName().toString();
                    final String applicationName = application.getName().toString();
                    execute(new StartGateway(applicationName, referenceName, bindingKey, "reference"), Void.class);
                    clearApplications();
                }
            }
        } else if (OPERATION_STOP.equals(name)) {
            final String bindingKey = getResourceContext().getResourceKey();
            final Reference reference = getReference();
            if (reference != null) {
                final Application application = getApplication();
                if (application != null) {
                    final String referenceName = reference.getName().toString();
                    final String applicationName = application.getName().toString();
                    execute(new StopGateway(applicationName, referenceName, bindingKey, "reference"), Void.class);
                    clearApplications();
                }
            }
        } else if (LOG.isDebugEnabled()) {
            LOG.warn("Unknown Reference Binding operation " + name);
        }
        return null;
    }

    public <T> T execute(final Operation operation, Class<T> clazz) {
        return getResourceContext().getParentResourceComponent().execute(operation, clazz);
    }
}
