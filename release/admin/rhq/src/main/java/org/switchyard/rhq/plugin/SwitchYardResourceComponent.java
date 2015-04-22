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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

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
import org.rhq.modules.plugins.jbossas7.BaseServerComponent;
import org.rhq.modules.plugins.jbossas7.json.Operation;
import org.switchyard.rhq.plugin.model.Application;
import org.switchyard.rhq.plugin.model.ComponentServiceMetrics;
import org.switchyard.rhq.plugin.model.ModelUtil;
import org.switchyard.rhq.plugin.model.ReferenceMetrics;
import org.switchyard.rhq.plugin.model.ServiceMetrics;
import org.switchyard.rhq.plugin.model.SwitchYardMetrics;
import org.switchyard.rhq.plugin.operations.ReadApplication;
import org.switchyard.rhq.plugin.operations.ReadComponentServiceMetrics;
import org.switchyard.rhq.plugin.operations.ReadReferenceMetrics;
import org.switchyard.rhq.plugin.operations.ReadServiceMetrics;
import org.switchyard.rhq.plugin.operations.ReadSwitchYardMetrics;
import org.switchyard.rhq.plugin.operations.ResetMetrics;

import static org.switchyard.rhq.plugin.SwitchYardConstants.OPERATION_RESET;
/**
 * SwitchYard Component
 */
public class SwitchYardResourceComponent extends BaseSwitchYardResourceComponent<BaseServerComponent<?>> implements MeasurementFacet, OperationFacet {
    /**
     * The logger instance.
     */
    private static Log LOG = LogFactory.getLog(SwitchYardResourceComponent.class);
    
    // TODO, make configurable
    private static long REFRESH = 30*1000;
    
    /**
     * The current application map
     */
    private AtomicReference<Map<String, Application>> applications = new AtomicReference<Map<String, Application>>();
    /**
     * The timestamp of the last application refresh.
     */
    private AtomicLong applicationTimestamp = new AtomicLong();
    
    /**
     * The global metrics
     */
    private AtomicReference<SwitchYardMetrics> switchYardMetrics = new AtomicReference<SwitchYardMetrics>();
    /**
     * The timestamp of the last global metric refresh.
     */
    private AtomicLong switchYardMetricsTimestamp = new AtomicLong();
    
    /**
     * The service metrics
     */
    private AtomicReference<Map<String, Map<String, ServiceMetrics>>> serviceMetrics = new AtomicReference<Map<String, Map<String, ServiceMetrics>>>();
    /**
     * The timestamp of the last service metric refresh.
     */
    private AtomicLong serviceMetricsTimestamp = new AtomicLong();
    
    /**
     * The reference metrics
     */
    private AtomicReference<Map<String, Map<String, ReferenceMetrics>>> referenceMetrics = new AtomicReference<Map<String, Map<String, ReferenceMetrics>>>();
    /**
     * The timestamp of the last reference metric refresh.
     */
    private AtomicLong referenceMetricsTimestamp = new AtomicLong();
    
    /**
     * The component service metrics
     */
    private AtomicReference<Map<String, Map<String, ComponentServiceMetrics>>> componentServiceMetrics = new AtomicReference<Map<String, Map<String, ComponentServiceMetrics>>>();
    /**
     * The timestamp of the last component service metric refresh.
     */
    private AtomicLong componentServiceMetricsTimestamp = new AtomicLong();
    

    @Override
    public AvailabilityType getAvailability() {
        final SwitchYardMetrics metrics = getSwitchYardMetrics();
        return (metrics == null) ? AvailabilityType.DOWN : AvailabilityType.UP;
    }

    protected Log getLog() {
        return LOG;
    }

    public <T> T execute(final Operation operation, Class<T> clazz) {
        return SwitchYardDiscoveryComponent.execute(getResourceContext()
                .getParentResourceComponent(), operation, clazz);
    }
    
    public Map<String, Application> getApplications() {
        return getApplicationMap();
    }
    
    public Application getApplication(final String name) {
        return getApplicationMap().get(name);
    }

    public SwitchYardMetrics getSwitchYardMetrics() {
        return getCachedSwitchYardMetrics();
    }

    public Map<String, Map<String, ServiceMetrics>> getServiceMetrics() {
        return getCachedServiceMetrics();
    }

    public Map<String, Map<String, ReferenceMetrics>> getReferenceMetrics() {
        return getCachedReferenceMetrics();
    }

    public Map<String, Map<String, ComponentServiceMetrics>> getComponentServiceMetrics() {
        return getCachedComponentServiceMetrics();
    }

    public void clearApplications() {
        applicationTimestamp.set(0);
    }

    public void clearServiceMetrics() {
        serviceMetricsTimestamp.set(0);
    }

    public void clearReferenceMetrics() {
        referenceMetricsTimestamp.set(0);
    }

    public void clearComponentServiceMetrics() {
        componentServiceMetricsTimestamp.set(0);
    }

    private Map<String, Application> getApplicationMap() {
        Map<String, Application> applicationMap = applications.get();
        long now = System.currentTimeMillis();
        if (now - applicationTimestamp.get() > REFRESH) {
            synchronized(this) {
                if (now - applicationTimestamp.get() > REFRESH) {
                    applicationMap = ModelUtil.createNamedResourceMap(loadApplications());
                    applications.set(applicationMap);
                    applicationTimestamp.set(System.currentTimeMillis());
                }
            }
            applicationMap = applications.get();
        }
        return applicationMap;
    }

    private SwitchYardMetrics getCachedSwitchYardMetrics() {
        SwitchYardMetrics metrics = switchYardMetrics.get();
        long now = System.currentTimeMillis();
        if (now - switchYardMetricsTimestamp.get() > REFRESH) {
            synchronized(this) {
                if (now - switchYardMetricsTimestamp.get() > REFRESH) {
                    metrics = loadSwitchYardMetrics();
                    if (metrics != null) {
                        switchYardMetrics.set(metrics);
                        switchYardMetricsTimestamp.set(System.currentTimeMillis());
                    }
                }
            }
            metrics = switchYardMetrics.get();
        }
        return metrics;
    }

    private Map<String, Map<String, ServiceMetrics>> getCachedServiceMetrics() {
        Map<String, Map<String, ServiceMetrics>> serviceMetricsMap = serviceMetrics.get();
        long now = System.currentTimeMillis();
        if (now - serviceMetricsTimestamp.get() > REFRESH) {
            synchronized(this) {
                if (now - serviceMetricsTimestamp.get() > REFRESH) {
                    serviceMetricsMap = ModelUtil.createApplicationNamedMetricMap(loadServiceMetrics());
                    serviceMetrics.set(serviceMetricsMap);
                    serviceMetricsTimestamp.set(System.currentTimeMillis());
                }
            }
            serviceMetricsMap = serviceMetrics.get();
        }
        return serviceMetricsMap;
    }

    private Map<String, Map<String, ReferenceMetrics>> getCachedReferenceMetrics() {
        Map<String, Map<String, ReferenceMetrics>> referenceMetricsMap = referenceMetrics.get();
        long now = System.currentTimeMillis();
        if (now - referenceMetricsTimestamp.get() > REFRESH) {
            synchronized(this) {
                if (now - referenceMetricsTimestamp.get() > REFRESH) {
                    referenceMetricsMap = ModelUtil.createApplicationNamedMetricMap(loadReferenceMetrics());
                    referenceMetrics.set(referenceMetricsMap);
                    referenceMetricsTimestamp.set(System.currentTimeMillis());
                }
            }
            referenceMetricsMap = referenceMetrics.get();
        }
        return referenceMetricsMap;
    }

    private Map<String, Map<String, ComponentServiceMetrics>> getCachedComponentServiceMetrics() {
        Map<String, Map<String, ComponentServiceMetrics>> componentServiceMetricsMap = componentServiceMetrics.get();
        long now = System.currentTimeMillis();
        if (now - componentServiceMetricsTimestamp.get() > REFRESH) {
            synchronized(this) {
                if (now - componentServiceMetricsTimestamp.get() > REFRESH) {
                    componentServiceMetricsMap = ModelUtil.createApplicationNamedMetricMap(loadComponentServiceMetrics());
                    componentServiceMetrics.set(componentServiceMetricsMap);
                    componentServiceMetricsTimestamp.set(System.currentTimeMillis());
                }
            }
            componentServiceMetricsMap = componentServiceMetrics.get();
        }
        return componentServiceMetricsMap;
    }
    
    private Application[] loadApplications() {
        final Application[] applications = execute(new ReadApplication(), Application[].class);
        return (applications == null ? new Application[0] : applications);
    }
    
    private SwitchYardMetrics loadSwitchYardMetrics() {
        final SwitchYardMetrics[] metrics = execute(new ReadSwitchYardMetrics(), SwitchYardMetrics[].class);
        return (metrics == null ? null : metrics[0]);
    }
    
    private ServiceMetrics[] loadServiceMetrics() {
        final ServiceMetrics[] serviceMetrics = execute(new ReadServiceMetrics(), ServiceMetrics[].class);
        return (serviceMetrics == null ? new ServiceMetrics[0] : serviceMetrics);
    }
    
    private ReferenceMetrics[] loadReferenceMetrics() {
        final ReferenceMetrics[] referenceMetrics = execute(new ReadReferenceMetrics(), ReferenceMetrics[].class);
        return (referenceMetrics == null ? new ReferenceMetrics[0] : referenceMetrics);
    }
    
    private ComponentServiceMetrics[] loadComponentServiceMetrics() {
        final ComponentServiceMetrics[] componentServiceMetrics = execute(new ReadComponentServiceMetrics(), ComponentServiceMetrics[].class);
        return (componentServiceMetrics == null ? new ComponentServiceMetrics[0] : componentServiceMetrics);
    }

    @Override
    public void getValues(final MeasurementReport report, final Set<MeasurementScheduleRequest> requests) throws Exception {
        final SwitchYardMetrics metrics = getSwitchYardMetrics();
        if (metrics != null) {
            for (MeasurementScheduleRequest request: requests) {
                final MeasurementDataNumeric measurementData = getCommonMetric(request, metrics);
                if (measurementData != null) {
                    report.addData(measurementData);
                } else if (LOG.isDebugEnabled()) {
                    LOG.debug("Unable to collect SwitchYard measurement " + request.getName());
                }
            }
        }
    }

    @Override
    public OperationResult invokeOperation(final String name, final Configuration parameters) throws InterruptedException, Exception {
        if (OPERATION_RESET.equals(name)) {
            execute(new ResetMetrics(), Void.class);
            clearServiceMetrics();
            clearReferenceMetrics();
            clearComponentServiceMetrics();
        } else if (LOG.isDebugEnabled()) {
            LOG.warn("Unknown SwitchYard operation " + name);
        }
        return null;
    }
}
