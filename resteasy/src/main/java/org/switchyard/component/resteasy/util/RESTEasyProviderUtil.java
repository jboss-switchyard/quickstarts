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
package org.switchyard.component.resteasy.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.switchyard.common.type.Classes;

/**
 * Utility class that recognizes RESTEasy providers from context params.
 */
public final class RESTEasyProviderUtil {

    private RESTEasyProviderUtil() {
    }

    /**
     * Finds the RESTEasy providers parameter and return its value as list of class names.
     *
     * @param contextParams the map of all context parameters
     * @return a List of RESTEasy provider class names or null if not present
     */
    public static List<String> getProviders(Map<String, String> contextParams) {
        if (contextParams != null) {
            String providers = contextParams.get(ResteasyContextParameters.RESTEASY_PROVIDERS);
            if (providers != null) {
                return Arrays.asList(providers.split(","));
            }
        }
        return null;
    }

    /**
     * Finds the RESTEasy providers parameter and return its value as list of classes.
     *
     * @param contextParams the map of all context parameters
     * @return a List of RESTEasy provider classes or null if not present
     */
    public static List<Class<?>> getProviderClasses(Map<String, String> contextParams) {
        List<String> providers = getProviders(contextParams);
        if (providers != null) {
            List<Class<?>> providerClasses = new ArrayList<Class<?>>(providers.size());
            for (String provider : providers) {
                Class<?> pc = Classes.forName(provider.trim());
                if (pc != null) {
                    providerClasses.add(pc);
                }
            }
            return providerClasses.isEmpty() ? null : providerClasses;
        }
        return null;
    }

}
