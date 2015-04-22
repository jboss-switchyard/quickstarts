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
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.shell.console.Completer;
import org.apache.karaf.shell.console.completer.ArgumentCompleter;
import org.apache.karaf.shell.console.completer.StringsCompleter;
import org.apache.karaf.shell.console.jline.CommandSessionHolder;
import org.switchyard.admin.Application;
import org.switchyard.admin.Binding;
import org.switchyard.admin.Reference;
import org.switchyard.admin.Service;
import org.switchyard.admin.SwitchYard;

/**
 * Generates completion set for Application arguments.
 */
public class BindingNameCompleter implements Completer {

    private SwitchYard _switchYard;

    @Override
    public int complete(String buffer, int cursor, List<String> candidates) {
        final StringsCompleter delegate = new StringsCompleter();
        final CommandSession session = CommandSessionHolder.getSession();
        if (session == null) {
            return delegate.complete(buffer, cursor, candidates);
        }
        final ArgumentCompleter.ArgumentList argList = (ArgumentCompleter.ArgumentList) session
                .get(ArgumentCompleter.ARGUMENTS_LIST);
        if (argList == null || argList.getArguments() == null || argList.getArguments().length == 4) {
            return delegate.complete(buffer, cursor, candidates);
        }
        final List<String> arguments = Arrays.asList(argList.getArguments());
        final Application application = _switchYard.getApplication(QName.valueOf(arguments.get(2)));
        if (application == null) {
            return delegate.complete(buffer, cursor, candidates);
        }
        final QName serviceOrReferenceName = QName.valueOf(arguments.get(3));
        final Service service = application.getService(serviceOrReferenceName);
        if (service == null) {
            final Reference reference = application.getReference(serviceOrReferenceName);
            if (reference == null) {
                return delegate.complete(buffer, cursor, candidates);
            }
            for (Binding binding : reference.getGateways()) {
                if (binding.getName() != null) {
                    delegate.getStrings().add(binding.getName());
                }
            }
        } else {
            for (Binding binding : service.getGateways()) {
                if (binding.getName() != null) {
                    delegate.getStrings().add(binding.getName());
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

}
