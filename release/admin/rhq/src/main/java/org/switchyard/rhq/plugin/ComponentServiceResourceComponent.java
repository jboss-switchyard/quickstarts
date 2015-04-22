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
import org.switchyard.rhq.plugin.model.ComponentReference;
import org.switchyard.rhq.plugin.model.ComponentReferenceMetrics;
import org.switchyard.rhq.plugin.model.ComponentService;
import org.switchyard.rhq.plugin.model.ComponentServiceMetrics;
import org.switchyard.rhq.plugin.model.OperationMetrics;
import org.switchyard.rhq.plugin.operations.ResetComponentServiceMetrics;

/**
 * SwitchYard Component Service Resource Component
 */
public class ComponentServiceResourceComponent extends BaseSwitchYardResourceComponent<ApplicationResourceComponent> implements MeasurementFacet, OperationFacet {
    /**
     * The logger instance.
     */
    private static Log LOG = LogFactory.getLog(ComponentServiceResourceComponent.class);
    
    protected Log getLog() {
        return LOG;
    }

    @Override
    public AvailabilityType getAvailability() {
        final ComponentService componentService = getComponentService();
        return (componentService == null) ? AvailabilityType.DOWN : AvailabilityType.UP;
    }

    public Map<String, ComponentReference> getReferences() {
        final ComponentService componentService = getComponentService();
        if (componentService == null) {
            return Collections.emptyMap();
        } else {
            return componentService.getReferences();
        }
    }

    public ComponentService getComponentService() {
        final String componentServiceKey = getResourceContext().getResourceKey();
        return getApplication().getComponentServices().get(componentServiceKey);
    }
    
    public Application getApplication() {
        return getResourceContext().getParentResourceComponent().getApplication();
    }

    public <T> T execute(final Operation operation, Class<T> clazz) {
        return getResourceContext().getParentResourceComponent().execute(operation, clazz);
    }

    private ComponentServiceMetrics getComponentServiceMetrics() {
        final String componentServiceKey = getResourceContext().getResourceKey();
         final Map<String, ComponentServiceMetrics> componentServiceMetrics = getResourceContext().getParentResourceComponent().getComponentServiceMetrics();
         return (componentServiceMetrics != null) ? componentServiceMetrics.get(componentServiceKey) : null;
    }

    public Map<String, OperationMetrics> getOperationMetrics() {
        final ComponentServiceMetrics componentServiceMetrics = getComponentServiceMetrics();
        if (componentServiceMetrics != null) {
            return componentServiceMetrics.getOperationMetrics();
        } else {
            return Collections.emptyMap();
        }
    }

    public Map<String, ComponentReferenceMetrics> getComponentReferenceMetrics() {
        final ComponentServiceMetrics componentServiceMetrics = getComponentServiceMetrics();
        if (componentServiceMetrics != null) {
            return componentServiceMetrics.getComponentReferenceMetrics();
        } else {
            return Collections.emptyMap();
        }
    }

    public void clearComponentServiceMetrics() {
        getResourceContext().getParentResourceComponent().clearComponentServiceMetrics();
    }

    @Override
    public void getValues(final MeasurementReport report, final Set<MeasurementScheduleRequest> requests) throws Exception {
        final ComponentServiceMetrics metrics = getComponentServiceMetrics();
        if (metrics != null) {
            for (MeasurementScheduleRequest request: requests) {
                final MeasurementDataNumeric measurementData = getCommonMetric(request, metrics);
                if (measurementData != null) {
                    report.addData(measurementData);
                } else if (LOG.isDebugEnabled()) {
                    LOG.debug("Unable to collect Component Service measurement " + request.getName());
                }
            }
        }
    }

    @Override
    public OperationResult invokeOperation(final String name, final Configuration parameters) throws InterruptedException, Exception {
        if (OPERATION_RESET.equals(name)) {
            final ComponentService componentService = getComponentService();
            final Application application = getApplication();
            if ((componentService != null) && (application != null)) {
                final String componentServiceName = componentService.getName().toString();
                final String applicationName = application.getName().toString();
                execute(new ResetComponentServiceMetrics(applicationName, componentServiceName), Void.class);
                clearComponentServiceMetrics();
            }
        } else if (LOG.isDebugEnabled()) {
            LOG.warn("Unknown Component Service operation " + name);
        }
        return null;
    }
}
