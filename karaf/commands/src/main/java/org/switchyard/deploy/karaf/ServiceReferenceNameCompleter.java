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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.shell.console.Completer;
import org.apache.karaf.shell.console.completer.ArgumentCompleter;
import org.apache.karaf.shell.console.completer.StringsCompleter;
import org.apache.karaf.shell.console.jline.CommandSessionHolder;
import org.switchyard.admin.Application;
import org.switchyard.admin.Reference;
import org.switchyard.admin.Service;
import org.switchyard.admin.SwitchYard;

/**
 * Generates completion set for Application arguments.
 */
public class ServiceReferenceNameCompleter implements Completer {

    private SwitchYard _switchYard;
    private Integer _applicationArgumentIndex;
    private String[] _applicationOptions;

    @Override
    public int complete(String buffer, int cursor, List<String> candidates) {
        final StringsCompleter delegate = new StringsCompleter();
        final Pattern applicationNamePattern = getApplicationNamePattern();
        final List<Application> applications = _switchYard.getApplications();
        for (Application application : applications) {
            if (applicationNamePattern.matcher(application.getName().toString()).find()) {
                for (Service service : application.getServices()) {
                    delegate.getStrings().add(service.getName().toString());
                }
                for (Reference reference : application.getReferences()) {
                    delegate.getStrings().add(reference.getName().toString());
                }
            }
        }
        return delegate.complete(buffer, cursor, candidates);
    }

    /**
     * @param switchYard the SwitchYard admin service
     */
    public void setSwitchYard(SwitchYard switchYard) {
        _switchYard = switchYard;
    }

    /**
     * @param applicationArgumentIndex the index for the application argument
     */
    public void setApplicationArgumentIndex(Integer applicationArgumentIndex) {
        _applicationArgumentIndex = applicationArgumentIndex;
    }

    /**
     * @param applicationOptions option strings for application name
     */
    public void setApplicationOptions(String[] applicationOptions) {
        _applicationOptions = applicationOptions;
    }

    private Pattern getApplicationNamePattern() {
        final CommandSession session = CommandSessionHolder.getSession();
        if (session == null) {
            return compilePattern(null, false);
        }
        final ArgumentCompleter.ArgumentList argList = (ArgumentCompleter.ArgumentList) session
                .get(ArgumentCompleter.ARGUMENTS_LIST);
        if (argList == null || argList.getArguments() == null || argList.getArguments().length == 0) {
            return compilePattern(null, false);
        }
        final List<String> arguments = Arrays.asList(argList.getArguments());
        boolean isRegex = arguments.indexOf("--regex") > 0;
        if (_applicationArgumentIndex == null) {
            if (_applicationOptions == null || _applicationOptions.length == 0) {
                return compilePattern(null, false);
            }
            for (String option : _applicationOptions) {
                final int index = arguments.indexOf(option);
                if (index >= 0) {
                    if (arguments.size() - 1 > index) {
                        // XXX: do we need to worry about commas delimiting a
                        // list?
                        return compilePattern(Collections.singletonList(arguments.get(index + 1)), isRegex);
                    }
                }
            }
        } else {
            int argumentOffset = 1; // command is first argument
            for (int index = 0, count = arguments.size(); index < count; ++index) {
                if (arguments.get(index).startsWith("-")) {
                    argumentOffset = index;
                }
            }
            // XXX: assuming the last option does not accept a value here
            if (_applicationArgumentIndex + argumentOffset < arguments.size()) {
                return compilePattern(
                        Collections.singletonList(arguments.get(_applicationArgumentIndex + argumentOffset)), isRegex);
            }
        }
        return compilePattern(null, false);
    }

    private Pattern compilePattern(List<String> patterns, boolean isRegex) {
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
