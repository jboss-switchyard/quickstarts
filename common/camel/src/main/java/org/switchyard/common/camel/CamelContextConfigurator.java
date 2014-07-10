/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.common.camel;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;
import org.apache.camel.ManagementStatisticsLevel;
import org.switchyard.ServiceDomain;
import org.switchyard.common.type.Classes;

/**
 * Utility class which encapsulates CamelContext configuration based on a 
 * set of properties.  Putting this in a distinct class keeps the property
 * constants in a single place and keeps the messy configuration code out 
 * of SwitchYardCamelContextImpl.
 */
public final class CamelContextConfigurator {
    
    /**
     * Domain property used to configure the timeout value for ShutdownStrategy.
     */
    public static final String SHUTDOWN_TIMEOUT = "org.switchyard.camel.ShutdownTimeout";
    
    /**
     * Domain property used to configure the statistics level for Camel management.  The 
     * value of this property corresponds to the string value of the ManagementStatisticsLevel enum.
     */
    public static final String PERFORMANCE_STATISTICS = 
            "org.switchyard.camel.PerformanceStatistics";
    
    /**
     * Property which contains the fully-qualified name of a class which implements 
     * CamelContextAware and can be used to configure the CamelContext used within SwitchYard.
     */
    public static final String CAMEL_CONTEXT_CONFIG = 
            "org.switchyard.camel.CamelContextConfiguration";
    
    private CamelContextConfigurator() {
        // prevent direct instantiation
    }
    
    /**
     * Configure CamelContext based on a ServiceDomain instance.
     * @param context CamelContext to configure
     * @param domain domain used to configure CamelContext
     */
    public static final void configure(CamelContext context, ServiceDomain domain) {
        configure(context, domain.getProperties());
    }

    /**
     * Configure CamelContext based on a map of properties.
     * @param context CamelContext to configure
     * @param properties properties to configure
     */
    public static final void configure(CamelContext context, Map<String, Object> properties) {
        for (Entry<String, Object> property : properties.entrySet()) {
            if (property.getValue() != null) {
                configure(context, property.getKey(), property.getValue());
            }
        }
    }
    
    /**
     * Configure CamelContext instance with a property name/value.
     * @param context CamelContext to configure
     * @param name config property name
     * @param value config property value
     */
    public static final void configure(CamelContext context, String name, Object value) {
        try {
            if (name.equals(SHUTDOWN_TIMEOUT)) {
                configureShutdownTimeout(context, value);
            } else if (name.equals(PERFORMANCE_STATISTICS)) {
                configurePerformanceStatistics(context, value);
            } else if (name.equals(CAMEL_CONTEXT_CONFIG)) {
                configureCamelContextAware(context, value);
            }
        } catch (Exception ex) {
            CommonCamelLogger.ROOT_LOGGER.camelContextConfigurationError(name, value, ex);
        }
    }
    
    private static void configureShutdownTimeout(CamelContext context, Object value) {
        int timeout = Integer.parseInt(value.toString());
        context.getShutdownStrategy().setTimeout(timeout);
    }
    
    private static void configurePerformanceStatistics(CamelContext context, Object value) {
        ManagementStatisticsLevel level = ManagementStatisticsLevel.valueOf(value.toString());
        context.getManagementStrategy().setStatisticsLevel(level);
    }
    
    private static void configureCamelContextAware(CamelContext context, Object value) throws Exception {
        Class<?> contextAwareClass = Classes.forName(value.toString());
        CamelContextAware contextAware = (CamelContextAware)contextAwareClass.newInstance();
        contextAware.setCamelContext(context);
    }
}
