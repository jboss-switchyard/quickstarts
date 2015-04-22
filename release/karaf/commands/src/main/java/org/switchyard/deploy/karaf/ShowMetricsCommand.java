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
package org.switchyard.deploy.karaf;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.switchyard.admin.Application;
import org.switchyard.admin.ComponentService;
import org.switchyard.admin.Reference;
import org.switchyard.admin.Service;
import org.switchyard.admin.SwitchYard;

/**
 * Shell command for uses-artifact.
 */
@Command(scope = "switchyard", name = "show-metrics", description = "Displays metrics for the specified service/reference/component service.")
public class ShowMetricsCommand extends AbstractSwitchYardServiceCommand {

    /**
     * Specifies the search type: services, references or component services.
     */
    public static enum SearchType {
        /** show services. */
        service() {
            @Override
            public void printMetrics(Pattern pattern, Application application) {
                boolean printHeader = true;
                for (Service service : application.getServices()) {
                    final Matcher matcher = pattern.matcher(service.getName().toString());
                    if (matcher.find()) {
                        if (printHeader) {
                            System.out.println(application.getName() + " = [");
                            printHeader = false;
                        }
                        System.out.println(PrintUtil.printServiceMetrics(service, 1));
                    }
                }
                if (!printHeader) {
                    System.out.println("]");
                }
            };
        },
        /** show references. */
        reference() {
            @Override
            public void printMetrics(Pattern pattern, Application application) {
                boolean printHeader = true;
                for (Reference reference : application.getReferences()) {
                    final Matcher matcher = pattern.matcher(reference.getName().toString());
                    if (matcher.find()) {
                        if (printHeader) {
                            System.out.println(application.getName() + " = [");
                            printHeader = false;
                        }
                        System.out.println(PrintUtil.printReferenceMetrics(reference, 1));
                    }
                }
                if (!printHeader) {
                    System.out.println("]");
                }
            };
        },
        /** show component services. */
        component() {
            @Override
            public void printMetrics(Pattern pattern, Application application) {
                boolean printHeader = true;
                for (ComponentService service : application.getComponentServices()) {
                    final Matcher matcher = pattern.matcher(service.getName().toString());
                    if (matcher.find()) {
                        if (printHeader) {
                            System.out.println(application.getName() + " = [");
                            printHeader = false;
                        }
                        System.out.println(PrintUtil.printComponentServiceMetrics(service, 1));
                    }
                }
                if (!printHeader) {
                    System.out.println("]");
                }
            };
        },
        /** show system. */
        system() {
            @Override
            public void printMetrics(Pattern pattern, Application application) {
            };
        };

        /**
         * @param pattern the pattern
         * @param application the application to scan
         */
        public abstract void printMetrics(Pattern pattern, Application application);
    }

    @Argument(index = 0, name = "type", description = "Specifies the search type [service | reference | component | system].", required = true)
    private SearchType _type;

    @Argument(index = 1, name = "patterns", description = "Specifies the search pattern to use.", multiValued = true)
    private List<String> _patterns;

    @Option(name = "--regex", description = "If specified, treat the pattern(s) as a regular expression.")
    private boolean _regex;

    @Override
    protected Object doExecute(final SwitchYard switchYard) throws Exception {
        if (_type == SearchType.system) {
            System.out.println(PrintUtil.addMetricsToNode(switchYard.getMessageMetrics(), 1));
        } else {
            final Pattern pattern = compilePattern(_patterns, _regex);
            for (Application application : switchYard.getApplications()) {
                _type.printMetrics(pattern, application);
            }
        }
        return null;
    }

}
