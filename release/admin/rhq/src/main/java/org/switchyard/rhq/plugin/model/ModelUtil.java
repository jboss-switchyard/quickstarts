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
package org.switchyard.rhq.plugin.model;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class ModelUtil {
    public static <T extends NamedResource> Map<String, T> createNamedResourceMap(final T[] resources) {
        if ((resources != null) && (resources.length > 0)) {
            final Map<String, T> resourceMap = new TreeMap<String, T>();
            for(T resource: resources) {
                resourceMap.put(resource.getName().toString(), resource);
            }
            return resourceMap;
        } else {
            return Collections.emptyMap();
        }
    }

    public static String toString(final Object obj) {
        return (obj == null ? null : obj.toString());
    }

    public static <T extends NamedMetric> Map<String, T> createNamedMetricMap(final T[] metrics) {
        if ((metrics != null) && (metrics.length > 0)) {
            final Map<String, T> metricMap = new TreeMap<String, T>();
            for(T resource: metrics) {
                metricMap.put(resource.getName(), resource);
            }
            return metricMap;
        } else {
            return Collections.emptyMap();
        }
    }

    public static <T extends ApplicationNamedMetric> Map<String, Map<String, T>> createApplicationNamedMetricMap(final T[] metrics) {
        if (metrics == null) {
            return Collections.emptyMap();
        } else {
            final Map<String, Map<String, T>> map = new TreeMap<String, Map<String, T>>();
            for (T metric: metrics) {
                final String applicationName = metric.getApplication();
                final String name = metric.getName();
                Map<String, T> serviceMap = map.get(applicationName);
                if (serviceMap == null) {
                    serviceMap = new TreeMap<String, T>();
                    map.put(applicationName, serviceMap);
                }
                serviceMap.put(name, metric);
            }
            return map;
        }
    }
}
