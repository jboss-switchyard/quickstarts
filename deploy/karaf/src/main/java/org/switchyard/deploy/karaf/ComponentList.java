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

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.osgi.framework.ServiceReference;
import org.switchyard.deploy.Component;
import org.switchyard.deploy.osgi.internal.ComponentExtension;

/**
 * Shell commands for components.
 */
@Command(scope = "switchyard", name = "component-list", description = "List switchyard components.")
public class ComponentList extends OsgiCommandSupport {

    @Override
    protected Object doExecute() throws Exception {
        Collection<ServiceReference<Component>> refs =
                getBundleContext().getServiceReferences(
                        Component.class, "(" + ComponentExtension.SWITCHYARD_TYPES + "=*)");
        if (refs != null) {
            Set<String> types = new TreeSet<String>();
            for (ServiceReference<Component> ref : refs) {
                types.addAll(getTypes(ref.getProperty(ComponentExtension.SWITCHYARD_TYPES)));
            }
            for (String type : types) {
                System.out.println(type);
            }
        }
        return null;
    }

    private static List<String> getTypes(Object ns) {
        if (ns == null) {
            throw new IllegalArgumentException("Component service does not have an associated "
                    + ComponentExtension.SWITCHYARD_TYPES + " property defined");
        } else if (ns instanceof String[]) {
            return Arrays.asList((String[]) ns);
        } else if (ns instanceof URI) {
            return Collections.singletonList((String) ns);
        } else if (ns instanceof String) {
            return Collections.singletonList((String) ns);
        } else if (ns instanceof Collection) {
            Collection col = (Collection) ns;
            List<String> types = new ArrayList<String>(col.size());
            for (Object o : col) {
                types.add(o.toString());
            }
            return types;
        } else if (ns instanceof Object[]) {
            Object[] array = (Object[]) ns;
            List<String> types = new ArrayList<String>(array.length);
            for (Object o : array) {
                types.add(o.toString());
            }
            return types;
        } else {
            throw new IllegalArgumentException("Component service has an associated "
                    + ComponentExtension.SWITCHYARD_TYPES + " property defined which can not be converted to an array of String");
        }
    }

}
