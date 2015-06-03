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
import org.switchyard.admin.SwitchYard;
import org.switchyard.config.model.switchyard.ArtifactModel;

/**
 * Shell command for uses-artifact.
 */
@Command(scope = "switchyard", name = "uses-artifact", description = "Returns a list of all SwitchYard applications referencing the specified artifact.")
public class UsesArtifactCommand extends AbstractSwitchYardServiceCommand {

    /**
     * Specifies the search type: by name or by url.
     */
    public static enum SearchType {
        /** by name. */
        name() {
            @Override
            public boolean matches(Pattern pattern, ArtifactModel artifact) {
                final Matcher matcher = pattern.matcher(artifact.getName());
                return matcher.find();
            };
        },
        /** by url. */
        url() {
            @Override
            public boolean matches(Pattern pattern, ArtifactModel artifact) {
                final Matcher matcher = pattern.matcher(artifact.getURL());
                return matcher.find();
            };
        };

        /**
         * @param pattern the pattern
         * @param artifact the artifact
         * @return true if the artifact matches the pattern
         */
        public abstract boolean matches(Pattern pattern, ArtifactModel artifact);
    }

    @Argument(index = 0, name = "type", description = "Specifies the search type [name | url].", required = true)
    private SearchType _type;

    @Argument(index = 1, name = "patterns", description = "Specifies the search pattern to use.", multiValued = true)
    private List<String> _patterns;

    @Option(name = "--regex", description = "If specified, treat the pattern(s) as a regular expression.")
    private boolean _regex;

    @Override
    protected Object doExecute(final SwitchYard switchYard) throws Exception {
        final Pattern pattern = compilePattern(_patterns, _regex);
        for (Application application : switchYard.getApplications()) {
            if (application.getConfig().getArtifacts() == null) {
                continue;
            }
            boolean printHeader = true;
            for (ArtifactModel artifact : application.getConfig().getArtifacts().getArtifacts()) {
                if (_type.matches(pattern, artifact)) {
                    if (printHeader) {
                        System.out.println(application.getName() + " = [");
                        printHeader = false;
                    }
                    System.out.println(PrintUtil.printArtifact(artifact, 1));
                }
            }
            if (!printHeader) {
                System.out.println("]");
            }
        }
        return null;
    }

}
