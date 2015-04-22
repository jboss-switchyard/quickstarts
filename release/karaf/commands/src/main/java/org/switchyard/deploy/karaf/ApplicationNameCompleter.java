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

import java.util.List;

import org.apache.karaf.shell.console.Completer;
import org.apache.karaf.shell.console.completer.StringsCompleter;
import org.switchyard.admin.Application;
import org.switchyard.admin.SwitchYard;

/**
 * Generates completion set for Application arguments.
 */
public class ApplicationNameCompleter implements Completer {

    private SwitchYard _switchYard;

    @Override
    public int complete(String buffer, int cursor, List<String> candidates) {
        final StringsCompleter delegate = new StringsCompleter();
        final List<Application> applications = _switchYard.getApplications();
        for (Application application : applications) {
            delegate.getStrings().add(application.getName().toString());
        }
        return delegate.complete(buffer, cursor, candidates);
    }

    /**
     * @param switchYard the SwitchYard admin service
     */
    public void setSwitchYard(SwitchYard switchYard) {
        _switchYard = switchYard;
    }

}
