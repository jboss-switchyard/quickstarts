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
import org.switchyard.deploy.osgi.NamespaceHandler;

/**
 * Shell commands for namespaces.
 */
@Command(scope = "switchyard", name = "namespace-list", description = "List switchyard namespaces.")
public class NamespaceList extends OsgiCommandSupport {

    @Override
    protected Object doExecute() throws Exception {
        Collection<ServiceReference<NamespaceHandler>> refs =
                getBundleContext().getServiceReferences(
                        NamespaceHandler.class, "(" + NamespaceHandler.NAMESPACES + "=*)");
        if (refs != null) {
            Set<URI> namespaces = new TreeSet<URI>();
            for (ServiceReference<NamespaceHandler> ref : refs) {
                namespaces.addAll(getNamespaces(ref.getProperty(NamespaceHandler.NAMESPACES)));
            }
            for (URI namespace : namespaces) {
                System.out.println(namespace.toString());
            }
        }
        return null;
    }

    private static List<URI> getNamespaces(Object ns) {
        if (ns == null) {
            throw new IllegalArgumentException("NamespaceHandler service does not have an associated "
                    + NamespaceHandler.NAMESPACES + " property defined");
        } else if (ns instanceof URI[]) {
            return Arrays.asList((URI[]) ns);
        } else if (ns instanceof URI) {
            return Collections.singletonList((URI) ns);
        } else if (ns instanceof String) {
            return Collections.singletonList(URI.create((String) ns));
        } else if (ns instanceof String[]) {
            String[] strings = (String[]) ns;
            List<URI> namespaces = new ArrayList<URI>(strings.length);
            for (String string : strings) {
                namespaces.add(URI.create(string));
            }
            return namespaces;
        } else if (ns instanceof Collection) {
            Collection<?> col = (Collection<?>) ns;
            List<URI> namespaces = new ArrayList<URI>(col.size());
            for (Object o : col) {
                namespaces.add(toURI(o));
            }
            return namespaces;
        } else if (ns instanceof Object[]) {
            Object[] array = (Object[]) ns;
            List<URI> namespaces = new ArrayList<URI>(array.length);
            for (Object o : array) {
                namespaces.add(toURI(o));
            }
            return namespaces;
        } else {
            throw new IllegalArgumentException("NamespaceHandler service has an associated "
                    + NamespaceHandler.NAMESPACES + " property defined which can not be converted to an array of URI");
        }
    }

    private static URI toURI(Object o) {
        if (o instanceof URI) {
            return (URI) o;
        } else if (o instanceof String) {
            return URI.create((String) o);
        } else {
            throw new IllegalArgumentException("NamespaceHandler service has an associated "
                    + NamespaceHandler.NAMESPACES + " property defined which can not be converted to an array of URI");
        }
    }

}
