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

import org.apache.felix.gogo.commands.Command;
import org.switchyard.admin.Application;
import org.switchyard.admin.SwitchYard;

/**
 * Shell command for list-applications.
 */
@Command(scope = "switchyard", name = "list-applications", description = "Returns a list of all SwitchYard applications deployed on the system.")
public class ListApplicationsCommand extends AbstractSwitchYardServiceCommand {

    @Override
    protected Object doExecute(final SwitchYard switchYard) throws Exception {
        System.out.println("  SwitchYard Applications  ");
        for (Application application : switchYard.getApplications()) {
            System.out.println(String.format("[ %s ]", application.getName()));
        }
        return null;
    }

}
