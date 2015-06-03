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

import static org.switchyard.rhq.plugin.SwitchYardConstants.OPERATION_RESET;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rhq.core.domain.configuration.Configuration;
import org.rhq.core.domain.measurement.AvailabilityType;
import org.rhq.core.domain.measurement.MeasurementDataNumeric;
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
import org.switchyard.rhq.plugin.model.Reference;
import org.switchyard.rhq.plugin.model.ReferenceMetrics;
import org.switchyard.rhq.plugin.operations.ResetReferenceMetrics;

/**
 * SwitchYard Reference Resource Component
 */
public class ReferenceResourceComponent extends BaseSwitchYardResourceComponent<ApplicationResourceComponent> implements MeasurementFacet, OperationFacet {
    /**
     * The logger instance.
     */
    private static Log LOG = LogFactory.getLog(ReferenceResourceComponent.class);
    
    protected Log getLog() {
        return LOG;
    }

    @Override
    public AvailabilityType getAvailability() {
        final Reference reference = getReference();
        return (reference != null) ? AvailabilityType.UP : AvailabilityType.DOWN;
    }

    public Map<String, Gateway> getGateways() {
        final Reference reference = getReference();
        if (reference == null) {
            return Collections.emptyMap();
        } else {
            return reference.getGateways();
        }
    }
    
    public Reference getReference() {
        final String referenceKey = getResourceContext().getResourceKey();
        return getApplication().getReferences().get(referenceKey);
    }
    
    public Application getApplication() {
        return getResourceContext().getParentResourceComponent().getApplication();
    }

    public ReferenceMetrics getReferenceMetrics() {
        final String operationKey = getResourceContext().getResourceKey();
        final Map<String, ReferenceMetrics> referenceMetrics = getResourceContext().getParentResourceComponent().getReferenceMetrics();
        return (referenceMetrics != null) ? referenceMetrics.get(operationKey) : null;
    }

    public Map<String, OperationMetrics> getOperationMetrics() {
        final ReferenceMetrics referenceMetrics = getReferenceMetrics();
        if (referenceMetrics != null) {
            return referenceMetrics.getOperationMetrics();
        } else {
            return Collections.emptyMap();
        }
    }

    public Map<String, GatewayMetrics> getGatewayMetrics() {
        final ReferenceMetrics serviceMetrics = getReferenceMetrics();
        if (serviceMetrics != null) {
            return serviceMetrics.getGatewayMetrics();
        } else {
            return Collections.emptyMap();
        }
    }

    public void clearReferenceMetrics() {
        getResourceContext().getParentResourceComponent().clearReferenceMetrics();
    }

    public void clearApplications() {
        getResourceContext().getParentResourceComponent().clearApplications();
    }

    @Override
    public void getValues(final MeasurementReport report, final Set<MeasurementScheduleRequest> requests) throws Exception {
        final ReferenceMetrics metrics = getReferenceMetrics();
        if (metrics != null) {
            for (MeasurementScheduleRequest request: requests) {
                final MeasurementDataNumeric measurementData = getCommonMetric(request, metrics);
                if (measurementData != null) {
                    report.addData(measurementData);
                } else if (LOG.isDebugEnabled()) {
                    LOG.debug("Unable to collect Reference measurement " + request.getName());
                }
            }
        }
    }

    @Override
    public OperationResult invokeOperation(final String name, final Configuration parameters) throws InterruptedException, Exception {
        if (OPERATION_RESET.equals(name)) {
            final Reference reference = getReference();
            final Application application = getApplication();
            if ((reference != null) && (application != null)) {
                final String referenceName = reference.getName().toString();
                final String applicationName = application.getName().toString();
                execute(new ResetReferenceMetrics(applicationName, referenceName), Void.class);
                clearReferenceMetrics();
            }
        } else if (LOG.isDebugEnabled()) {
            LOG.warn("Unknown Referencs operation " + name);
        }
        return null;
    }

    public <T> T execute(final Operation operation, Class<T> clazz) {
        return getResourceContext().getParentResourceComponent().execute(operation, clazz);
    }
}
