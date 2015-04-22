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

import java.util.Collections;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rhq.core.domain.measurement.AvailabilityType;
import org.rhq.modules.plugins.jbossas7.json.Operation;
import org.switchyard.rhq.plugin.model.Application;
import org.switchyard.rhq.plugin.model.ComponentService;
import org.switchyard.rhq.plugin.model.ComponentServiceMetrics;
import org.switchyard.rhq.plugin.model.Reference;
import org.switchyard.rhq.plugin.model.ReferenceMetrics;
import org.switchyard.rhq.plugin.model.Service;
import org.switchyard.rhq.plugin.model.ServiceMetrics;

/**
 * SwitchYard Application Resource Component
 */
public class ApplicationResourceComponent extends BaseSwitchYardResourceComponent<SwitchYardResourceComponent> {
    /**
     * The logger instance.
     */
    private static Log LOG = LogFactory.getLog(ApplicationResourceComponent.class);
    
    protected Log getLog() {
        return LOG;
    }

    @Override
    public AvailabilityType getAvailability() {
        final Application application = getApplication();
        return (application == null ? AvailabilityType.DOWN : AvailabilityType.UP);
    }

    public Map<String, Service> getServices() {
        final Application application = getApplication();
        if (application == null) {
            return Collections.emptyMap();
        } else {
            return application.getServices();
        }
    }

    public Map<String, Reference> getReferences() {
        final Application application = getApplication();
        if (application == null) {
            return Collections.emptyMap();
        } else {
            return application.getReferences();
        }
    }

    public Map<String, ComponentService> getComponentServices() {
        final Application application = getApplication();
        if (application == null) {
            return Collections.emptyMap();
        } else {
            return application.getComponentServices();
        }
    }
    
    public Application getApplication() {
        final String applicationKey = getResourceContext().getResourceKey();
        return getResourceContext().getParentResourceComponent().getApplication(applicationKey);
    }

    public <T> T execute(final Operation operation, Class<T> clazz) {
        return getResourceContext().getParentResourceComponent().execute(operation, clazz);
    }

    public Map<String, ServiceMetrics> getServiceMetrics() {
        final String applicationKey = getResourceContext().getResourceKey();
        final Map<String, Map<String, ServiceMetrics>> serviceMetricsMap = getResourceContext().getParentResourceComponent().getServiceMetrics();
        return serviceMetricsMap.get(applicationKey);
    }

    public Map<String, ReferenceMetrics> getReferenceMetrics() {
        final String applicationKey = getResourceContext().getResourceKey();
        final Map<String, Map<String, ReferenceMetrics>> referenceMetricsMap = getResourceContext().getParentResourceComponent().getReferenceMetrics();
        return referenceMetricsMap.get(applicationKey);
    }

    public Map<String, ComponentServiceMetrics> getComponentServiceMetrics() {
        final String applicationKey = getResourceContext().getResourceKey();
        final Map<String, Map<String, ComponentServiceMetrics>> componentServiceMetricsMap = getResourceContext().getParentResourceComponent().getComponentServiceMetrics();
        return componentServiceMetricsMap.get(applicationKey);
    }

    public void clearApplications() {
        getResourceContext().getParentResourceComponent().clearApplications();
    }

    public void clearServiceMetrics() {
        getResourceContext().getParentResourceComponent().clearServiceMetrics();
    }

    public void clearReferenceMetrics() {
        getResourceContext().getParentResourceComponent().clearReferenceMetrics();
    }

    public void clearComponentServiceMetrics() {
        getResourceContext().getParentResourceComponent().clearComponentServiceMetrics();
    }
}
