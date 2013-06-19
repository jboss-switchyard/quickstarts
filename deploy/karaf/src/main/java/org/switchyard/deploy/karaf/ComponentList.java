/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.switchyard.deploy.karaf;

import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.osgi.framework.ServiceReference;
import org.switchyard.deploy.Component;
import org.switchyard.deploy.osgi.NamespaceHandler;
import org.switchyard.deploy.osgi.internal.ComponentExtension;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
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
