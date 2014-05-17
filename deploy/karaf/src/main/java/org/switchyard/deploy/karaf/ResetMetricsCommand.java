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
@Command(scope = "switchyard", name = "reset-metrics", description = "Reset metrics for the specified service/reference/application service.")
public class ResetMetricsCommand extends AbstractSwitchYardServiceCommand {

    /**
     * Specifies the type: services, references, application or all.
     */
    public static enum SearchType {
        /** service metrics. */
        service() {
            @Override
            public void resetMetrics(Pattern pattern, SwitchYard switchYard) {
                for (Service service : switchYard.getServices()) {
                    if (pattern.matcher(service.getName().toString()).find()) {
                        service.resetMessageMetrics();
                    }
                }
            }
        },
        /** reference metrics. */
        reference() {
            @Override
            public void resetMetrics(Pattern pattern, SwitchYard switchYard) {
                for (Reference reference : switchYard.getReferences()) {
                    if (pattern.matcher(reference.getName().toString()).find()) {
                        reference.resetMessageMetrics();
                    }
                }
            }
        },
        /** application metrics. */
        application() {
            @Override
            public void resetMetrics(Pattern pattern, SwitchYard switchYard) {
                for (Application application : switchYard.getApplications()) {
                    if (pattern.matcher(application.getName().toString()).find()) {
                        for (Service service : application.getServices()) {
                            service.resetMessageMetrics();
                        }
                        for (Reference reference : application.getReferences()) {
                            reference.resetMessageMetrics();
                        }
                        for (ComponentService service : application.getComponentServices()) {
                            service.resetMessageMetrics();
                        }
                    }
                }
            }
        },
        /** all metrics. */
        all() {
            @Override
            public void resetMetrics(Pattern pattern, SwitchYard switchYard) {
                switchYard.resetMessageMetrics();
            }
        };

        /**
         * @param pattern the pattern
         * @param switchYard the SwitchYard admin service
         */
        public abstract void resetMetrics(Pattern pattern, SwitchYard switchYard);
    }

    @Argument(index = 0, name = "type", description = "Specifies the type [service | reference | application | all] of metrics to reset.", required = true)
    private SearchType _type;

    @Argument(index = 1, name = "patterns", description = "Specifies the search pattern to use.", multiValued = true)
    private List<String> _patterns;

    @Option(name = "--regex", description = "If specified, treat the pattern(s) as a regular expression.")
    private boolean _regex;

    @Override
    protected Object doExecute(final SwitchYard switchYard) throws Exception {
        final Pattern pattern = compilePattern(_patterns, _regex);
        _type.resetMetrics(pattern, switchYard);
        return null;
    }

}
