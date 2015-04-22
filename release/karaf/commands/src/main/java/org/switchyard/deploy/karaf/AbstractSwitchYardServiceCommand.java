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
package org.switchyard.deploy.karaf;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.osgi.framework.ServiceReference;
import org.switchyard.admin.SwitchYard;

/**
 * Provides utility methods used by commands referencing the SwitchYard admin
 * service.
 */
public abstract class AbstractSwitchYardServiceCommand extends OsgiCommandSupport {

    @Override
    protected final Object doExecute() throws Exception {
        final ServiceReference<SwitchYard> serviceReference = getBundleContext().getServiceReference(SwitchYard.class);
        if (serviceReference == null) {
            System.out.println("SwitchYard admin service is unavailable.");
            return null;
        }
        try {
            final SwitchYard switchYard = getBundleContext().getService(serviceReference);
            if (switchYard == null) {
                System.out.println("SwitchYard admin service is unavailable.");
                return null;
            }
            return doExecute(switchYard);
        } finally {
            getBundleContext().ungetService(serviceReference);
        }
    }

    protected abstract Object doExecute(SwitchYard switchYard) throws Exception;

    /**
     * Aggregates the patterns in the list into a patterns[0]|patterns[1]|...
     * 
     * @param patterns list of patterns
     * @return a compiled Pattern
     */
    protected Pattern compilePattern(List<String> patterns, boolean isRegex) {
        if (patterns == null || patterns.size() == 0) {
            patterns = Collections.singletonList(".*");
            isRegex = true;
        }
        final StringBuffer regex = new StringBuffer();
        for (String name : patterns) {
            if (!isRegex) {
                regex.append("\\Q");
            }
            regex.append(name);
            if (!isRegex) {
                regex.append("\\E");
            }
            regex.append('|');
        }
        regex.deleteCharAt(regex.length() - 1);
        return Pattern.compile(regex.toString());
    }

}
