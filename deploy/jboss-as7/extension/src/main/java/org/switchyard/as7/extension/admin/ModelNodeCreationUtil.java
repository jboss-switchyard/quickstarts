/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.as7.extension.admin;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.INTERFACE;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.NAME;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.TYPE;
import static org.switchyard.as7.extension.SwitchYardModelConstants.APPLICATION;
import static org.switchyard.as7.extension.SwitchYardModelConstants.CONFIG_SCHEMA;
import static org.switchyard.as7.extension.SwitchYardModelConstants.GATEWAYS;
import static org.switchyard.as7.extension.SwitchYardModelConstants.IMPLEMENTATION;
import static org.switchyard.as7.extension.SwitchYardModelConstants.SERVICES;

import org.jboss.dmr.ModelNode;
import org.switchyard.admin.Application;
import org.switchyard.admin.Component;
import org.switchyard.admin.Service;

/**
 * ModelNodeCreationUtil
 * 
 * Utility class for creating AS7 management {@link ModelNode} object from
 * {@link org.switchyard.admin.SwitchYard} admin objects.
 * 
 * @author Rob Cernich
 */
final public class ModelNodeCreationUtil {

    /**
     * Creates a new {@link ModelNode} tree from the {@link Application}. The
     * tree has the form: <br>
     * <code><pre>
     *      "name" => "name",
     *      "services" =&gt; [
     *          ("serviceName" =&gt; {
     *              "name" =&gt; "serviceName",
     *              "application" =&gt; "name",
     *              "interface" =&gt; "interfaceName",
     *              "implementation" =&gt; "implementationTypeName",
     *              "gateways" =&gt; [
     *                  "gatewayTypeName",
     *                  ...
     *              ]
     *          }),
     *          ...
     *      ]
     * </pre></code>
     * 
     * @param application the {@link Application} used to populate the node.
     * @return a new {@link ModelNode}
     */
    public static ModelNode createApplicationNode(Application application) {
        ModelNode applicationNode = new ModelNode();
        ModelNode servicesNode = new ModelNode();
        for (Service service : application.getServices()) {
            servicesNode.get(service.getName().toString()).set(createServiceNode(service));
        }

        applicationNode.get(NAME).set(application.getName().toString());
        applicationNode.get(SERVICES).set(servicesNode);

        return applicationNode;
    }

    /**
     * Creates a new {@link ModelNode} tree from the {@link Application}. The
     * tree has the form: <br>
     * <code><pre>
     *      "name" =&gt; "name",
     *      "services" =&gt; [
     *          "service1", "service2", ...
     *      ]
     * </pre></code>
     * 
     * @param application the {@link Application} used to populate the node.
     * @return a new {@link ModelNode}
     */
    public static ModelNode createSimpleApplicationNode(Application application) {
        ModelNode applicationNode = new ModelNode();
        ModelNode servicesNode = new ModelNode();
        for (Service service : application.getServices()) {
            servicesNode.add(service.getName().toString());
        }

        applicationNode.get(NAME).set(application.getName().toString());
        applicationNode.get(SERVICES).set(servicesNode);

        return applicationNode;
    }

    /**
     * Creates a new {@link ModelNode} tree from the {@link Service}. The tree
     * has the form: <br>
     * <code><pre>
     *      "name" =&gt; "serviceName",
     *      "application =&gt; "applicationName",
     *      "interface" =&gt; "interfaceName",
     *      "implementation" =&gt; "implementationTypeName",
     *      "gateways" =&gt; [
     *          "gatewayTypeName",
     *          ...
     *      ]
     * </pre></code>
     * 
     * @param service the {@link Service} used to populate the node.
     * @return a new {@link ModelNode}
     */
    public static ModelNode createServiceNode(Service service) {
        ModelNode serviceNode = new ModelNode();

        String serviceName = service.getName().toString();
        serviceNode.get(NAME).set(serviceName);

        serviceNode.get(APPLICATION).set(service.getApplication().getName().toString());

        String interfaceName = service.getInterface();
        if (interfaceName == null) {
            serviceNode.get(INTERFACE);
        } else {
            serviceNode.get(INTERFACE).set(interfaceName);
        }

        Component implementation = service.getImplementation();
        if (implementation == null) {
            serviceNode.get(IMPLEMENTATION);
        } else {
            serviceNode.get(IMPLEMENTATION).set(implementation.getName());
        }

        ModelNode gatewaysNode = new ModelNode();
        for (Component gateway : service.getGateways()) {
            gatewaysNode.add(gateway.getName());
        }
        serviceNode.get(GATEWAYS).set(gatewaysNode);

        return serviceNode;
    }

    /**
     * Creates a new {@link ModelNode} tree from the {@link Component}. The tree
     * has the form: <br>
     * <code><pre>
     *      "name" =&gt; "componentName"
     *      "type" =&gt; "GATEWAY"
     *      "config-schema" =&gt; "&lt;?xml ..."
     * </pre></code>
     * 
     * @param component the {@link Component} used to populate the node.
     * @return a new {@link ModelNode}
     */
    public static ModelNode createComponentNode(Component component) {
        ModelNode componentNode = new ModelNode();
        componentNode.get(NAME).set(component.getName());
        componentNode.get(TYPE).set(component.getType().toString());
        componentNode.get(CONFIG_SCHEMA).set(component.getConfigSchema());
        return componentNode;
    }

    private ModelNodeCreationUtil() {
    }

}
