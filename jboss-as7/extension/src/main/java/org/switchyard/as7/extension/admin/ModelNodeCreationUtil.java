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
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.PROPERTIES;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.TYPE;
import static org.switchyard.as7.extension.SwitchYardModelConstants.ACTIVATION_TYPES;
import static org.switchyard.as7.extension.SwitchYardModelConstants.APPLICATION;
import static org.switchyard.as7.extension.SwitchYardModelConstants.ARTIFACTS;
import static org.switchyard.as7.extension.SwitchYardModelConstants.AVERAGE_TIME;
import static org.switchyard.as7.extension.SwitchYardModelConstants.COMPONENT_SERVICES;
import static org.switchyard.as7.extension.SwitchYardModelConstants.CONFIGURATION;
import static org.switchyard.as7.extension.SwitchYardModelConstants.FAULT_COUNT;
import static org.switchyard.as7.extension.SwitchYardModelConstants.FROM;
import static org.switchyard.as7.extension.SwitchYardModelConstants.GATEWAYS;
import static org.switchyard.as7.extension.SwitchYardModelConstants.IMPLEMENTATION;
import static org.switchyard.as7.extension.SwitchYardModelConstants.IMPLEMENTATION_CONFIGURATION;
import static org.switchyard.as7.extension.SwitchYardModelConstants.MAX_TIME;
import static org.switchyard.as7.extension.SwitchYardModelConstants.MIN_TIME;
import static org.switchyard.as7.extension.SwitchYardModelConstants.PROMOTED_SERVICE;
import static org.switchyard.as7.extension.SwitchYardModelConstants.REFERENCES;
import static org.switchyard.as7.extension.SwitchYardModelConstants.SERVICES;
import static org.switchyard.as7.extension.SwitchYardModelConstants.SUCCESS_COUNT;
import static org.switchyard.as7.extension.SwitchYardModelConstants.TO;
import static org.switchyard.as7.extension.SwitchYardModelConstants.TOTAL_COUNT;
import static org.switchyard.as7.extension.SwitchYardModelConstants.TOTAL_TIME;
import static org.switchyard.as7.extension.SwitchYardModelConstants.TRANSFORMERS;
import static org.switchyard.as7.extension.SwitchYardModelConstants.URL;
import static org.switchyard.as7.extension.SwitchYardModelConstants.VALIDATORS;

import java.util.Map;
import java.util.Set;

import org.jboss.dmr.ModelNode;
import org.switchyard.admin.Application;
import org.switchyard.admin.Binding;
import org.switchyard.admin.Component;
import org.switchyard.admin.ComponentReference;
import org.switchyard.admin.ComponentService;
import org.switchyard.admin.MessageMetrics;
import org.switchyard.admin.Service;
import org.switchyard.admin.Transformer;
import org.switchyard.admin.Validator;
import org.switchyard.config.model.switchyard.ArtifactModel;

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
     *          {
     *              "name" =&gt; "serviceName",
     *              "application" =&gt; "name",
     *              "interface" =&gt; "interfaceName",
     *              "promotedService" =&gt; "promotedServiceName",
     *              "gateways" =&gt; [
     *                  {
     *                      "type" =&gt; "typeName",
     *                      "configuration" =&gt; "&lt;?binding.foo ..."
     *                  },
     *                  ...
     *              ],
     *          },
     *          ...
     *      ]
     *      "componentServices" =&gt; [
     *          {
     *              "name" =&gt; "serviceName",
     *              "application" =&gt; "name",
     *              "interface" =&gt; "interfaceName",
     *              "implementation" =&gt; "implementationTypeName",
     *              "references" =&gt; [
     *                  {
     *                      "name" =&gt; "referenceName",
     *                      "interface" =&gt; "interfaceName"
     *                  },
     *                  ...
     *              ],
     *          },
     *          ...
     *      ]
     *      "transformers" =&gt; [
     *          {
     *              "from" =&gt; "fromType",
     *              "to" =&gt; "toType",
     *              "type" =&gt; "transformerType",
     *          },
     *          ...
     *      ]
     *      "artifacts" =&gt; [
     *          {
     *              "name" =&gt; "nameType",
     *              "url" =&gt; "urlType",
     *          },
     *          ...
     *      ]
     *      "validators" =&gt; [
     *          {
     *              "name" =&gt; "typeName",
     *              "type" =&gt; "XML",
     *          },
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
            servicesNode.add(createServiceNode(service));
        }

        ModelNode componentServicesNode = new ModelNode();
        for (ComponentService componentService : application.getComponentServices()) {
            componentServicesNode.add(createComopnentServiceNode(componentService));
        }

        ModelNode transformersNode = new ModelNode();
        for (Transformer transformer : application.getTransformers()) {
            transformersNode.add(createTransformerNode(transformer));
        }

        ModelNode artifactsNode = new ModelNode();
        if (application.getConfig().getArtifacts() != null) {
            for (ArtifactModel artifact : application.getConfig().getArtifacts().getArtifacts()) {
                artifactsNode.add(createArtifactNode(artifact));
            }
        }

        ModelNode validatorsNode = new ModelNode();
        if (application.getValidators() != null) {
            for (Validator validator : application.getValidators()) {
                validatorsNode.add(createValidatorNode(validator));
            }
        }

        applicationNode.get(NAME).set(application.getName().toString());
        applicationNode.get(SERVICES).set(servicesNode);
        applicationNode.get(COMPONENT_SERVICES).set(componentServicesNode);
        applicationNode.get(TRANSFORMERS).set(transformersNode);
        applicationNode.get(ARTIFACTS).set(artifactsNode);
        applicationNode.get(VALIDATORS).set(validatorsNode);

        return applicationNode;
    }

    /**
     * Creates a new {@link ModelNode} tree from the {@link Service}. The tree
     * has the form: <br>
     * <code><pre>
     *      "name" =&gt; "serviceName",
     *      "application" =&gt; "name",
     *      "interface" =&gt; "interfaceName",
     *      "promotedService" =&gt; "promotedServiceName",
     *      "gateways" =&gt; [
     *          {
     *              "type" =&gt; "typeName",
     *              "configuration" =&gt; "&lt;?binding.foo ..."
     *          },
     *          ...
     *      ]
     * </pre></code>
     * 
     * @param service the {@link Service} used to populate the node.
     * @return a new {@link ModelNode}
     */
    public static ModelNode createServiceNode(Service service) {
        ModelNode serviceNode = new ModelNode();

        serviceNode.get(NAME).set(service.getName().toString());

        serviceNode.get(APPLICATION).set(service.getApplication().getName().toString());

        String interfaceName = service.getInterface();
        if (interfaceName == null) {
            serviceNode.get(INTERFACE);
        } else {
            serviceNode.get(INTERFACE).set(interfaceName);
        }

        ComponentService promotedService = service.getPromotedService();
        if (promotedService == null) {
            serviceNode.get(PROMOTED_SERVICE);
        } else {
            serviceNode.get(PROMOTED_SERVICE).set(promotedService.getName().toString());
        }

        ModelNode gatewaysNode = new ModelNode();
        for (Binding gateway : service.getGateways()) {
            gatewaysNode.add(createGateway(gateway));
        }
        serviceNode.get(GATEWAYS).set(gatewaysNode);

        return serviceNode;
    }

    /**
     * Creates a new {@link ModelNode} tree from the {@link ComponentService}.
     * The tree has the form: <br>
     * <code><pre>
     *      "name" =&gt; "serviceName",
     *      "application" =&gt; "name",
     *      "interface" =&gt; "interfaceName",
     *      "implementation" =&gt; "implementationTypeName",
     *      "references" =&gt; [
     *          {
     *              "name" =&gt; "referenceName",
     *              "interface" =&gt; "interfaceName"
     *          },
     *          ...
     *      ]
     * </pre></code>
     * 
     * @param service the {@link ComponentService} used to populate the node.
     * @return a new {@link ModelNode}
     */
    public static ModelNode createComopnentServiceNode(ComponentService service) {
        ModelNode serviceNode = new ModelNode();

        serviceNode.get(NAME).set(service.getName().toString());

        serviceNode.get(APPLICATION).set(service.getApplication().getName().toString());

        String interfaceName = service.getInterface();
        if (interfaceName == null) {
            serviceNode.get(INTERFACE);
        } else {
            serviceNode.get(INTERFACE).set(interfaceName);
        }

        String implementation = service.getImplementation();
        if (implementation == null) {
            serviceNode.get(IMPLEMENTATION);
        } else {
            serviceNode.get(IMPLEMENTATION).set(implementation);
        }

        String implementationConfiguration = service.getImplementationConfiguration();
        if (implementationConfiguration == null) {
            serviceNode.get(IMPLEMENTATION_CONFIGURATION);
        } else {
            serviceNode.get(IMPLEMENTATION_CONFIGURATION).set(implementationConfiguration);
        }

        ModelNode referencesNode = new ModelNode();
        for (ComponentReference reference : service.getReferences()) {
            referencesNode.add(createComponentReferenceNode(reference));
        }
        serviceNode.get(REFERENCES).set(referencesNode);

        return serviceNode;
    }

    /**
     * Creates a new {@link ModelNode} tree from the {@link Service}. The tree
     * has the form: <br>
     * <code><pre>
     *      "name" =&gt; "serviceName",
     *      "application =&gt; "applicationName",
     * </pre></code>
     * 
     * @param service the {@link Service} used to populate the node.
     * @return a new {@link ModelNode}
     */
    public static ModelNode createSimpleServiceNode(Service service) {
        ModelNode serviceNode = new ModelNode();

        serviceNode.get(NAME).set(service.getName().toString());

        serviceNode.get(APPLICATION).set(service.getApplication().getName().toString());

        return serviceNode;
    }

    /**
     * Creates a new {@link ModelNode} tree from the {@link Component}. The tree
     * has the form: <br>
     * <code><pre>
     *      "name" =&gt; "componentName"
     *      "activationTypes" =&gt; ["soap"]
     *      "configSchema" =&gt; "&lt;?xml ..."
     * </pre></code>
     * 
     * @param component the {@link Component} used to populate the node.
     * @return a new {@link ModelNode}
     */
    public static ModelNode createComponentNode(Component component) {
        ModelNode componentNode = new ModelNode();
        componentNode.get(NAME).set(component.getName());

        Set<String> types = component.getTypes();
        if (types == null || types.isEmpty()) {
            componentNode.get(ACTIVATION_TYPES).addEmptyList();
        } else {
            for (String type : types) {
                componentNode.get(ACTIVATION_TYPES).add(type);
            }
        }

        Map<String, String> properties = component.getProperties();
        if (properties == null || properties.isEmpty()) {
            componentNode.get(PROPERTIES);
        } else {
            for (Map.Entry<String, String> property : properties.entrySet()) {
                componentNode.get(PROPERTIES).get(property.getKey()).set(property.getValue());
            }
        }
        return componentNode;
    }

    /**
     * Creates a new {@link ModelNode} tree from the {@link Service}. The tree
     * has the form: <br>
     * <code><pre>
     *      "type" =&gt; "typeName",
     *      "configuration" =&gt; "&lt;?binding.foo ..."
     * </pre></code>
     * 
     * @param binding the {@link Binding} used to populate the node.
     * @return a new {@link ModelNode}
     */
    public static ModelNode createGateway(Binding binding) {
        ModelNode gatewayNode = new ModelNode();

        if (binding.getType() == null) {
            gatewayNode.get(TYPE);
        } else {
            gatewayNode.get(TYPE).set(binding.getType());
        }

        if (binding.getConfiguration() == null) {
            gatewayNode.get(CONFIGURATION);
        } else {
            gatewayNode.get(CONFIGURATION).set(binding.getConfiguration());
        }

        return gatewayNode;
    }

    /**
     * Creates a new {@link ModelNode} tree from the {@link ComponentReference}.
     * The tree has the form: <br>
     * <code><pre>
     *      "name" =&gt; "referenceName",
     *      "interface" =&gt; "interfaceName"
     * </pre></code>
     * 
     * @param reference the {@link ComponentReference} used to populate the
     *            node.
     * @return a new {@link ModelNode}
     */
    public static ModelNode createComponentReferenceNode(ComponentReference reference) {
        ModelNode referenceNode = new ModelNode();

        if (reference.getName() == null) {
            referenceNode.get(NAME);
        } else {
            referenceNode.get(NAME).set(reference.getName().toString());
        }

        if (reference.getInterface() == null) {
            referenceNode.get(INTERFACE);
        } else {
            referenceNode.get(INTERFACE).set(reference.getInterface());
        }

        return referenceNode;
    }

    /**
     * Creates a new {@link ModelNode} tree from the {@link Transformer}. The
     * tree has the form: <br>
     * <code><pre>
     *      "from" =&gt; "fromType",
     *      "to" =&gt; "toType",
     *      "type" =&gt; "transformerType",
     * </pre></code>
     * 
     * @param transformation the {@link Transformer} used to populate the node.
     * @return a new {@link ModelNode}
     */
    public static ModelNode createTransformerNode(Transformer transformation) {
        ModelNode transformationNode = new ModelNode();

        if (transformation.getFrom() == null) {
            transformationNode.get(FROM);
        } else {
            transformationNode.get(FROM).set(transformation.getFrom().toString());
        }

        if (transformation.getTo() == null) {
            transformationNode.get(TO);
        } else {
            transformationNode.get(TO).set(transformation.getTo().toString());
        }

        if (transformation.getType() == null) {
            transformationNode.get(TYPE);
        } else {
            transformationNode.get(TYPE).set(transformation.getType());
        }

        return transformationNode;
    }

    /**
     * Creates a new {@link ModelNode} tree from the {@link ArtifactModel}. The
     * tree has the form: <br>
     * <code><pre>
     *      "name" =&gt; "nameType",
     *      "url" =&gt; "urlType",
     * </pre></code>
     * 
     * @param artifact the {@link ArtifactModel} used to populate the node.
     * @return a new {@link ModelNode}
     */
    public static ModelNode createArtifactNode(ArtifactModel artifact) {
        ModelNode artifactNode = new ModelNode();
        artifactNode.get(NAME).set(artifact.getName());
        artifactNode.get(URL).set(artifact.getURL());
        return artifactNode;
    }

    /**
     * Creates a new {@link ModelNode} tree from the validtors. The tree has the
     * form: <br>
     * <code><pre>
     *      "name" =&gt; "typeName",
     *      "type" =&gt; "XML",
     * </pre></code>
     * 
     * @param validator the {@link Validator} used to populate the node.
     * @return a new {@link ModelNode}
     */
    public static ModelNode createValidatorNode(Validator validator) {
        ModelNode validatorNode = new ModelNode();
        if (validator.getName() == null) {
            validatorNode.get(NAME);
        } else {
            validatorNode.get(NAME).set(validator.getName().toString());
        }
        if (validator.getType() == null) {
            validatorNode.get(TYPE);
        } else {
            validatorNode.get(TYPE).set(validator.getType());
        }
        return validatorNode;
    }

    /**
     * Adds metrics to an existing node from the {@link MessageMetrics}. The
     * tree has the form: <br>
     * <code><pre>
     *      "successCount" =&gt; "successCount",
     *      "faultCount" =&gt; "faultCount",
     *      "totalCount" =&gt; "totalCount",
     *      "averageTime" =&gt; "averageTime",
     *      "minTime" =&gt; "minTime",
     *      "maxTime" =&gt; "maxTime",
     *      "totalTime" =&gt; "totalTime"
     * </pre></code>
     * 
     * @param service the {@link Service} used to populate the node.
     * @param node the node to add metrics to
     * @return a new {@link ModelNode}
     */
    public static ModelNode addMetricsToNode(ModelNode node, MessageMetrics metrics) {

        node.get(SUCCESS_COUNT).set(metrics.getSuccessCount());
        node.get(FAULT_COUNT).set(metrics.getFaultCount());
        node.get(TOTAL_COUNT).set(metrics.getTotalCount());
        node.get(AVERAGE_TIME).set(metrics.getAverageProcessingTime());
        node.get(MIN_TIME).set(metrics.getMinProcessingTime());
        node.get(MAX_TIME).set(metrics.getMaxProcessingTime());
        node.get(TOTAL_TIME).set(metrics.getTotalProcessingTime());

        return node;
    }

    /**
     * Creates a new {@link ModelNode} tree from the {@link Service} for
     * metrics. The tree has the form: <br>
     * <code><pre>
     *      "name" =&gt; "serviceName",
     *      "successCount" =&gt; "successCount",
     *      "faultCount" =&gt; "faultCount",
     *      "totalCount" =&gt; "totalCount",
     *      "averageTime" =&gt; "averageTime",
     *      "minTime" =&gt; "minTime",
     *      "maxTime" =&gt; "maxTime",
     *      "totalTime" =&gt; "totalTime",
     *      "references" =&gt; [
     *          {
     *              "name" =&gt; "referenceName",
     *               "successCount" =&gt; "successCount",
     *               "faultCount" =&gt; "faultCount",
     *               "totalCount" =&gt; "totalCount",
     *               "averageTime" =&gt; "averageTime",
     *               "minTime" =&gt; "minTime",
     *               "maxTime" =&gt; "maxTime",
     *               "totalTime" =&gt; "totalTime",
     *          },
     *          ...
     *      ]
     * </pre></code>
     * 
     * @param service the {@link Service} used to populate the node.
     * @return a new {@link ModelNode}
     */
    public static ModelNode createServiceMetricsNode(Service service) {
        ModelNode serviceNode = new ModelNode();

        serviceNode.get(NAME).set(service.getName().toString());
        addMetricsToNode(serviceNode, service.getPromotedService().getMessageMetrics());

        ModelNode referencesNode = new ModelNode();
        for (ComponentReference reference : service.getPromotedService().getReferences()) {
            ModelNode referenceNode = new ModelNode();
            referenceNode.get(NAME).set(reference.getName().toString());
            addMetricsToNode(referenceNode, reference.getMessageMetrics());
            referencesNode.add(referenceNode);
        }
        serviceNode.get(REFERENCES).set(referencesNode);

        return serviceNode;
    }

    private ModelNodeCreationUtil() {
    }

}
