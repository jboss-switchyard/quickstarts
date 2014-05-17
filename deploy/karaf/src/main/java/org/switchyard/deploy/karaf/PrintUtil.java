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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.switchyard.admin.Application;
import org.switchyard.admin.Binding;
import org.switchyard.admin.ComponentReference;
import org.switchyard.admin.ComponentService;
import org.switchyard.admin.MessageMetrics;
import org.switchyard.admin.Reference;
import org.switchyard.admin.Service;
import org.switchyard.admin.ServiceOperation;
import org.switchyard.admin.Throttling;
import org.switchyard.admin.Transformer;
import org.switchyard.admin.Validator;
import org.switchyard.config.model.switchyard.ArtifactModel;

/**
 * PrintUtil
 * 
 * Utility class for creating text output for
 * {@link org.switchyard.admin.SwitchYard} admin objects.
 */
final public class PrintUtil {

    private static final String INTERFACE = "interface";
    private static final String TYPE = "type";
    private static final String APPLICATION = "application";
    private static final String COMPONENT_SERVICES = "componentServices";
    private static final String CONFIGURATION = "configuration";
    private static final String FROM = "from";
    private static final String IMPLEMENTATION = "implementation";
    private static final String IMPLEMENTATION_CONFIGURATION = "implementationConfiguration";
    private static final String GATEWAYS = "gateways";
    private static final String OPERATIONS = "operations";
    private static final String PROPERTIES = "properties";
    private static final String PROMOTED_REFERENCE = "promotedReference";
    private static final String PROMOTED_SERVICE = "promotedService";
    private static final String REFERENCES = "references";
    private static final String SERVICES = "services";
    private static final String STATE = "state";
    private static final String THROTTLING = "throttling";
    private static final String TO = "to";
    private static final String TRANSFORMERS = "transformers";
    private static final String ARTIFACTS = "artifacts";
    private static final String URL = "url";
    private static final String VALIDATORS = "validators";
    private static final String SUCCESS_COUNT = "successCount";
    private static final String FAULT_COUNT = "faultCount";
    private static final String TOTAL_COUNT = "totalCount";
    private static final String AVERAGE_TIME = "averageTime";
    private static final String MIN_TIME = "minTime";
    private static final String MAX_TIME = "maxTime";
    private static final String TOTAL_TIME = "totalTime";
    private static final String ENABLED = "enabled";
    private static final String MAX_REQUESTS = "maxRequests";
    private static final String TIME_PERIOD = "timePeriod";

    /**
     * Creates output tree for the {@link Application}. The tree has the form: <br>
     * <code><pre>
     *  "name" = [
     *      "services" = [
     *          "name" = [
     *              "application" = "name"
     *              "interface" = "interfaceName"
     *              "promotedService" = "promotedServiceName"
     *              "gateways" = [
     *                  "name" = [
     *                      "type" = "typeName"
     *                      "configuration" = "&lt;?binding.foo ..."
     *                  ]
     *                  ...
     *              ]
     *          ]
     *          ...
     *      ]
     *      "references" = [
     *          "name" = [
     *              "application" = "name"
     *              "interface" = "interfaceName"
     *              "promotedReference" = "promotedReferenceName"
     *              "gateways" = [
     *                  "name" = [
     *                      "type" = "typeName"
     *                      "configuration" = "&lt;?binding.foo ..."
     *                  ]
     *                  ...
     *              ]
     *          ]
     *          ...
     *      ]
     *      "componentServices" = [
     *          "name" = [
     *              "application" = "name"
     *              "interface" = "interfaceName"
     *              "implementation" = "implementationTypeName"
     *              "references" = [
     *                  "name" = "interfaceName"
     *                  ...
     *              ]
     *          ]
     *          ...
     *      ]
     *      "transformers" = [
     *          [
     *              "from" = "fromType"
     *              "to" = "toType"
     *              "type" = "transformerType"
     *          ]
     *          ...
     *      ]
     *      "artifacts" = [
     *          "name" = "urlType"
     *          ...
     *      ]
     *      "validators" = [
     *          "name" = "XML"
     *      ]
     *      "properties" = [
     *          "key" = "value",
     *      ]
     *  ]
     * </pre></code>
     * 
     * @param application the {@link Application} used to populate the node.
     * @return text
     */
    public static String printApplication(Application application) {
        final StringBuffer applicationNode = new StringBuffer();
        int indentLevel = 0;
        applicationNode.append(indent(indentLevel)).append(application.getName()).append(" = [\n");

        ++indentLevel;

        applicationNode.append(indent(indentLevel)).append(SERVICES).append(" = [\n");
        ++indentLevel;
        for (Service service : application.getServices()) {
            applicationNode.append(printService(service, indentLevel));
        }
        --indentLevel;
        applicationNode.append(indent(indentLevel)).append("]\n");

        applicationNode.append(indent(indentLevel)).append(REFERENCES).append(" = [\n");
        ++indentLevel;
        for (Reference reference : application.getReferences()) {
            applicationNode.append(printReference(reference, indentLevel));
        }
        --indentLevel;
        applicationNode.append(indent(indentLevel)).append("]\n");

        applicationNode.append(indent(indentLevel)).append(COMPONENT_SERVICES).append(" = [\n");
        ++indentLevel;
        for (ComponentService componentService : application.getComponentServices()) {
            applicationNode.append(printComopnentService(componentService, indentLevel));
        }
        --indentLevel;
        applicationNode.append(indent(indentLevel)).append("]\n");

        applicationNode.append(indent(indentLevel)).append(TRANSFORMERS).append(" = [\n");
        ++indentLevel;
        for (Transformer transformer : application.getTransformers()) {
            applicationNode.append(indent(indentLevel)).append("[\n");
            ++indentLevel;
            applicationNode.append(printTransformer(transformer, indentLevel));
            --indentLevel;
            applicationNode.append(indent(indentLevel)).append("]\n");
        }

        if (application.getConfig().getArtifacts() != null) {
            applicationNode.append(indent(indentLevel)).append(ARTIFACTS).append(" = [\n");
            ++indentLevel;
            for (ArtifactModel artifact : application.getConfig().getArtifacts().getArtifacts()) {
                applicationNode.append(printArtifact(artifact, indentLevel));
            }
            --indentLevel;
            applicationNode.append(indent(indentLevel)).append("]\n");
        }

        if (application.getValidators() != null) {
            applicationNode.append(indent(indentLevel)).append(VALIDATORS).append(" = [\n");
            ++indentLevel;
            for (Validator validator : application.getValidators()) {
                applicationNode.append(printValidator(validator, indentLevel));
            }
            --indentLevel;
            applicationNode.append(indent(indentLevel)).append("]\n");
        }

        if (application.getProperties() != null) {
            applicationNode.append(indent(indentLevel)).append(PROPERTIES).append(" = [\n");
            ++indentLevel;
            for (Map.Entry<String, String> property : application.getProperties().entrySet()) {
                applicationNode.append(indent(indentLevel)).append(property.getKey()).append(" = ")
                        .append(property.getValue());
                applicationNode.append("\n");
            }
            --indentLevel;
            applicationNode.append(indent(indentLevel)).append("]\n");
        }

        --indentLevel;
        applicationNode.append(indent(indentLevel)).append("]\n");

        return applicationNode.toString();
    }

    /**
     * Creates text tree from the {@link Reference}. The tree has the form: <br>
     * <code><pre>
     *  "name" = [
     *      "application" = "name"
     *      "interface" = "interfaceName"
     *      "promotedReference" = "promotedReferenceName"
     *      "gateways" = [
     *          name = [
     *              "type" = "typeName"
     *              "configuration" = "&lt;?binding.foo ..."
     *          ]
     *          ...
     *      ]
     *  ]
     * </pre></code>
     * 
     * @param reference the {@link Reference} used to populate the node.
     * @param indentLevel the beginning indent level
     * @return text
     */
    public static String printReference(Reference reference, int indentLevel) {
        final StringBuffer referenceNode = new StringBuffer();

        referenceNode.append(indent(indentLevel)).append(reference.getName().toString()).append(" = [\n");

        ++indentLevel;

        referenceNode.append(indent(indentLevel)).append(APPLICATION).append(" = ")
                .append(reference.getApplication().getName().toString());
        referenceNode.append("\n");

        String interfaceName = reference.getInterface();
        if (interfaceName != null) {
            referenceNode.append(indent(indentLevel)).append(INTERFACE).append(" = ").append(interfaceName);
            referenceNode.append("\n");
        }

        String promotedReference = reference.getPromotedReference();
        if (promotedReference != null) {
            referenceNode.append(indent(indentLevel)).append(PROMOTED_REFERENCE).append(" = ")
                    .append(promotedReference);
            referenceNode.append("\n");
        }

        referenceNode.append(indent(indentLevel)).append(GATEWAYS).append(" = [\n");
        ++indentLevel;
        for (Binding gateway : reference.getGateways()) {
            referenceNode.append(createGateway(gateway, indentLevel));
        }
        --indentLevel;
        referenceNode.append(indent(indentLevel)).append("]\n");

        --indentLevel;
        referenceNode.append(indent(indentLevel)).append("]\n");

        return referenceNode.toString();
    }

    /**
     * Creates text tree from the {@link Service}. The tree has the form: <br>
     * <code><pre>
     *  "name" = [
     *      "application" = "name"
     *      "interface" = "interfaceName"
     *      "promotedService" = "promotedServiceName"
     *      "gateways" = [
     *          name = [
     *              "type" = "typeName"
     *              "configuration" = "&lt;?binding.foo ..."
     *          ]
     *          ...
     *      ]
     *      "throttling" = [
     *          "enabled" = "true"
     *          "maxRequests" = "maxRequests"
     *          "timePeriod" = "timePeriod"
     *      ]
     *  ]
     * </pre></code>
     * 
     * @param service the {@link Service} used to populate the node.
     * @param indentLevel the beginning indent level
     * @return text
     */
    public static String printService(Service service, int indentLevel) {
        final StringBuffer serviceNode = new StringBuffer();

        serviceNode.append(indent(indentLevel)).append(service.getName()).append(" = [\n");

        ++indentLevel;

        serviceNode.append(indent(indentLevel)).append(APPLICATION).append(" = ")
                .append(service.getApplication().getName().toString());
        serviceNode.append("\n");

        String interfaceName = service.getInterface();
        if (interfaceName != null) {
            serviceNode.append(indent(indentLevel)).append(INTERFACE).append(" = ").append(interfaceName);
            serviceNode.append("\n");
        }

        ComponentService promotedService = service.getPromotedService();
        if (promotedService != null) {
            serviceNode.append(indent(indentLevel)).append(PROMOTED_SERVICE).append(" = ")
                    .append(promotedService.getName().toString());
            serviceNode.append("\n");
        }

        serviceNode.append(indent(indentLevel)).append(GATEWAYS).append(" = [\n");
        ++indentLevel;
        for (Binding gateway : service.getGateways()) {
            serviceNode.append(createGateway(gateway, indentLevel));
        }
        --indentLevel;
        serviceNode.append(indent(indentLevel)).append("]\n");

        serviceNode.append(printThrottlingTo(service.getThrottling(), indentLevel));

        --indentLevel;
        serviceNode.append(indent(indentLevel)).append("]\n");

        return serviceNode.toString();
    }

    /**
     * Creates text tree from the {@link ComponentService}. The tree has the
     * form: <br>
     * <code><pre>
     *  "name" = [
     *      "application" = "name"
     *      "interface" = "interfaceName"
     *      "implementation" = "implementationTypeName"
     *      "references" = [
     *          "name" = [
     *              "interface" = "interfaceName"
     *          ]
     *          ...
     *      ]
     *  ]
     * </pre></code>
     * 
     * @param service the {@link ComponentService} used to populate the node.
     * @param indentLevel the beginning indent level
     * @return text
     */
    public static String printComopnentService(ComponentService service, int indentLevel) {
        final StringBuffer serviceNode = new StringBuffer();

        serviceNode.append(indent(indentLevel)).append(service.getName()).append(" = [\n");

        ++indentLevel;

        serviceNode.append(indent(indentLevel)).append(APPLICATION).append(" = ")
                .append(service.getApplication().getName().toString());
        serviceNode.append("\n");

        String interfaceName = service.getInterface();
        if (interfaceName != null) {
            serviceNode.append(indent(indentLevel)).append(INTERFACE).append(" = ").append(interfaceName);
            serviceNode.append("\n");
        }

        String implementation = service.getImplementation();
        if (implementation != null) {
            serviceNode.append(indent(indentLevel)).append(IMPLEMENTATION).append(" = ").append(implementation);
            serviceNode.append("\n");
        }

        String implementationConfiguration = service.getImplementationConfiguration();
        if (implementationConfiguration != null) {
            serviceNode.append(indent(indentLevel)).append(IMPLEMENTATION_CONFIGURATION).append(" = ")
                    .append(implementationConfiguration);
            serviceNode.append("\n");
        }

        serviceNode.append(indent(indentLevel)).append(REFERENCES).append(" = [\n");
        ++indentLevel;
        for (ComponentReference reference : service.getReferences()) {
            serviceNode.append(printComponentReference(reference, indentLevel));
        }
        --indentLevel;
        serviceNode.append(indent(indentLevel)).append("]\n");

        --indentLevel;
        serviceNode.append(indent(indentLevel)).append("]\n");

        return serviceNode.toString();
    }

    /**
     * Creates text tree from the {@link Service}. The tree has the form: <br>
     * <code><pre>
     *  "name" = [
     *      "type" = "typeName"
     *      "configuration" = "&lt;?binding.foo ..."
     *      "state" = "STARTED"
     *  ]
     * </pre></code>
     * 
     * @param binding the {@link Binding} used to populate the node.
     * @param indentLevel the beginning indentLevel
     * @return text
     */
    public static String createGateway(Binding binding, int indentLevel) {
        StringBuffer gatewayNode = new StringBuffer();

        gatewayNode.append(indent(indentLevel)).append(binding.getName()).append(" = [\n");

        ++indentLevel;

        if (binding.getType() != null) {
            gatewayNode.append(indent(indentLevel)).append(TYPE).append(" = ").append(binding.getType());
            gatewayNode.append("\n");
        }

        if (binding.getConfiguration() != null) {
            gatewayNode.append(indent(indentLevel)).append(CONFIGURATION).append(" = ")
                    .append(binding.getConfiguration());
            gatewayNode.append("\n");
        }

        gatewayNode.append(indent(indentLevel)).append(STATE).append(" = ").append(binding.getState().toString());
        gatewayNode.append("\n");

        --indentLevel;
        gatewayNode.append(indent(indentLevel)).append("]\n");

        return gatewayNode.toString();
    }

    /**
     * Creates text tree from the {@link ComponentReference}. The tree has the
     * form: <br>
     * <code><pre>
     *  "name" = [
     *      "interface" = "interfaceName"
     *  ]
     * </pre></code>
     * 
     * @param reference the {@link ComponentReference} used to populate the
     *            node.
     * @param indentLevel the beginning indent level
     * @return text
     */
    public static String printComponentReference(ComponentReference reference, int indentLevel) {
        final StringBuffer referenceNode = new StringBuffer();

        referenceNode.append(indent(indentLevel)).append(reference.getName().toString()).append(" = [\n");

        ++indentLevel;
        referenceNode.append(indent(indentLevel)).append(INTERFACE).append(" = ").append(reference.getInterface())
                .append("\n");

        --indentLevel;
        referenceNode.append(indent(indentLevel)).append("]\n");

        return referenceNode.toString();
    }

    /**
     * Creates text tree from the {@link Transformer}. The tree has the form: <br>
     * <code><pre>
     *      "from" = "fromType",
     *      "to" = "toType",
     *      "type" = "transformerType",
     * </pre></code>
     * 
     * @param transformation the {@link Transformer} used to populate the node.
     * @param indentLevel the beginning indent level
     * @return text
     */
    public static String printTransformer(Transformer transformation, int indentLevel) {
        final StringBuffer transformationNode = new StringBuffer();

        if (transformation.getFrom() != null) {
            transformationNode.append(indent(indentLevel)).append(FROM).append(" = ")
                    .append(transformation.getFrom().toString());
            transformationNode.append("\n");
        }

        if (transformation.getTo() != null) {
            transformationNode.append(indent(indentLevel)).append(TO).append(" = ")
                    .append(transformation.getTo().toString());
            transformationNode.append("\n");
        }

        if (transformation.getType() != null) {
            transformationNode.append(indent(indentLevel)).append(TYPE).append(" = ").append(transformation.getType());
            transformationNode.append("\n");
        }

        return transformationNode.toString();
    }

    /**
     * Creates text tree from the {@link ArtifactModel}. The tree has the form: <br>
     * <code><pre>
     *  "name" = [
     *      "url" = "urlType"
     *  ]
     * </pre></code>
     * 
     * @param artifact the {@link ArtifactModel} used to populate the node.
     * @param indentLevel the beginning indent level;
     * @return text
     */
    public static String printArtifact(ArtifactModel artifact, int indentLevel) {
        final StringBuffer artifactNode = new StringBuffer();
        artifactNode.append(indent(indentLevel)).append(artifact.getName()).append(" = [\n");

        ++indentLevel;
        artifactNode.append(indent(indentLevel)).append(URL).append(" = ").append(artifact.getURL()).append("\n");
        --indentLevel;

        artifactNode.append("]\n");

        return artifactNode.toString();
    }

    /**
     * Creates text tree from the validtors. The tree has the form: <br>
     * <code><pre>
     *  "name" = [
     *      "type" = "XML"
     *  ]
     * </pre></code>
     * 
     * @param validator the {@link Validator} used to populate the node.
     * @param indentLevel the beginning indent level
     * @return text
     */
    public static String printValidator(Validator validator, int indentLevel) {
        final StringBuffer validatorNode = new StringBuffer();
        validatorNode.append(indent(indentLevel)).append(validator.getName().toString()).append(" = [\n");

        ++indentLevel;
        validatorNode.append(indent(indentLevel)).append(TYPE).append(" = ").append(validator.getType()).append("\n");
        --indentLevel;

        validatorNode.append(indent(indentLevel)).append("]\n");

        return validatorNode.toString();
    }

    /**
     * Adds metrics to an existing node from the {@link MessageMetrics}. The
     * tree has the form: <br>
     * <code><pre>
     *      "successCount" = "successCount",
     *      "faultCount" = "faultCount",
     *      "totalCount" = "totalCount",
     *      "averageTime" = "averageTime",
     *      "minTime" = "minTime",
     *      "maxTime" = "maxTime",
     *      "totalTime" = "totalTime"
     * </pre></code>
     * 
     * @param metrics the metrics to add to the node
     * @param indentLevel the indent level
     * @return text
     */
    public static String addMetricsToNode(MessageMetrics metrics, int indentLevel) {
        final StringBuffer metricsNode = new StringBuffer();
        metricsNode.append(indent(indentLevel)).append(SUCCESS_COUNT).append(" = ").append(metrics.getSuccessCount())
                .append("\n");
        metricsNode.append(indent(indentLevel)).append(FAULT_COUNT).append(" = ").append(metrics.getFaultCount())
                .append("\n");
        metricsNode.append(indent(indentLevel)).append(TOTAL_COUNT).append(" = ").append(metrics.getTotalCount())
                .append("\n");
        metricsNode.append(indent(indentLevel)).append(AVERAGE_TIME).append(" = ")
                .append(BigDecimal.valueOf(metrics.getAverageProcessingTime())).append("\n");
        metricsNode.append(indent(indentLevel)).append(MIN_TIME).append(" = ").append(metrics.getMinProcessingTime())
                .append("\n");
        metricsNode.append(indent(indentLevel)).append(MAX_TIME).append(" = ").append(metrics.getMaxProcessingTime())
                .append("\n");
        metricsNode.append(indent(indentLevel)).append(TOTAL_TIME).append(" = ")
                .append(metrics.getTotalProcessingTime()).append("\n");

        return metricsNode.toString();
    }

    /**
     * Creates text tree from the {@link Reference} for metrics. The tree has
     * the form: <br>
     * <code><pre>
     *  "name" = [
     *      "successCount" = "successCount"
     *      "faultCount" = "faultCount"
     *      "totalCount" = "totalCount"
     *      "averageTime" = "averageTime"
     *      "minTime" = "minTime"
     *      "maxTime" = "maxTime"
     *      "totalTime" = "totalTime"
     *      "gateways" = [
     *          "name" = [
     *              "type" = "bindingType",
     *               "successCount" = "successCount",
     *               "faultCount" = "faultCount",
     *               "totalCount" = "totalCount",
     *               "averageTime" = "averageTime",
     *               "minTime" = "minTime",
     *               "maxTime" = "maxTime",
     *               "totalTime" = "totalTime",
     *          ]
     *          ...
     *      ]
     *      "operations" = [
     *          "name" = [
     *               "successCount" = "successCount"
     *               "faultCount" = "faultCount"
     *               "totalCount" = "totalCount"
     *               "averageTime" = "averageTime"
     *               "minTime" = "minTime"
     *               "maxTime" = "maxTime"
     *               "totalTime" = "totalTime"
     *          ]
     *          ...
     *      ]
     *  ]
     * </pre></code>
     * 
     * @param reference the {@link Reference} used to populate the node.
     * @param indentLevel the beginning indent level
     * @return text
     */
    public static String printReferenceMetrics(Reference reference, int indentLevel) {
        final StringBuffer referenceNode = new StringBuffer();

        referenceNode.append(indent(indentLevel)).append(reference.getName().toString()).append(" = [\n");

        ++indentLevel;

        referenceNode.append(addMetricsToNode(reference.getMessageMetrics(), indentLevel));

        referenceNode.append(indent(indentLevel)).append(GATEWAYS).append(" = [\n");
        ++indentLevel;
        for (Binding gateway : reference.getGateways()) {
            referenceNode.append(indent(indentLevel)).append(gateway.getName()).append(" = [\n");
            ++indentLevel;
            referenceNode.append(indent(indentLevel)).append(TYPE).append(" = ").append(gateway.getType()).append("\n");
            referenceNode.append(addMetricsToNode(gateway.getMessageMetrics(), indentLevel));
            --indentLevel;
            referenceNode.append(indent(indentLevel)).append("]\n");
        }
        --indentLevel;
        referenceNode.append(indent(indentLevel)).append("]\n");

        referenceNode.append(indent(indentLevel)).append(OPERATIONS).append(" = [\n");
        ++indentLevel;
        for (ServiceOperation operation : reference.getServiceOperations()) {
            referenceNode.append(indent(indentLevel)).append(operation.getName()).append(" = [\n");
            ++indentLevel;
            referenceNode.append(addMetricsToNode(operation.getMessageMetrics(), indentLevel));
            --indentLevel;
            referenceNode.append(indent(indentLevel)).append("]\n");
        }
        --indentLevel;
        referenceNode.append(indent(indentLevel)).append("]\n");

        --indentLevel;
        referenceNode.append(indent(indentLevel)).append("]\n");

        return referenceNode.toString();
    }

    /**
     * Creates text tree from the {@link Service} for metrics. The tree has the
     * form: <br>
     * <code><pre>
     *  "name" = [
     *      "successCount" = "successCount"
     *      "faultCount" = "faultCount"
     *      "totalCount" = "totalCount"
     *      "averageTime" = "averageTime"
     *      "minTime" = "minTime"
     *      "maxTime" = "maxTime"
     *      "totalTime" = "totalTime"
     *      "operations" = [
     *          "name" = [
     *               "successCount" = "successCount"
     *               "faultCount" = "faultCount"
     *               "totalCount" = "totalCount"
     *               "averageTime" = "averageTime"
     *               "minTime" = "minTime"
     *               "maxTime" = "maxTime"
     *               "totalTime" = "totalTime"
     *          ]
     *          ...
     *      ]
     *      "references" = [
     *          "name" = [
     *               "successCount" = "successCount"
     *               "faultCount" = "faultCount"
     *               "totalCount" = "totalCount"
     *               "averageTime" = "averageTime"
     *               "minTime" = "minTime"
     *               "maxTime" = "maxTime"
     *               "totalTime" = "totalTime"
     *          ]
     *          ...
     *      ]
     *      "gateways" = [
     *          "name" = [
     *              "type" = "bindingType"
     *               "successCount" = "successCount"
     *               "faultCount" = "faultCount"
     *               "totalCount" = "totalCount"
     *               "averageTime" = "averageTime"
     *               "minTime" = "minTime"
     *               "maxTime" = "maxTime"
     *               "totalTime" = "totalTime"
     *          ]
     *          ...
     *      ]
     * </pre></code>
     * 
     * @param service the {@link Service} used to populate the node.
     * @param indentLevel the beginning indent level
     * @return text
     */
    public static String printServiceMetrics(Service service, int indentLevel) {
        final StringBuffer serviceNode = new StringBuffer();

        serviceNode.append(indent(indentLevel)).append(service.getName().toString()).append(" = [\n");

        ++indentLevel;

        serviceNode.append(addMetricsToNode(service.getMessageMetrics(), indentLevel));

        serviceNode.append(indent(indentLevel)).append(OPERATIONS).append(" = [\n");
        ++indentLevel;
        for (ServiceOperation operation : service.getPromotedService().getServiceOperations()) {
            serviceNode.append(indent(indentLevel)).append(operation.getName()).append(" = [\n");
            ++indentLevel;
            serviceNode.append(addMetricsToNode(operation.getMessageMetrics(), indentLevel));
            --indentLevel;
            serviceNode.append(indent(indentLevel)).append("]\n");
        }
        --indentLevel;
        serviceNode.append(indent(indentLevel)).append("]\n");

        serviceNode.append(indent(indentLevel)).append(REFERENCES).append(" = [\n");
        ++indentLevel;
        for (ComponentReference reference : service.getPromotedService().getReferences()) {
            serviceNode.append(indent(indentLevel)).append(reference.getName()).append(" = [\n");
            ++indentLevel;
            serviceNode.append(addMetricsToNode(reference.getMessageMetrics(), indentLevel));
            --indentLevel;
            serviceNode.append(indent(indentLevel)).append("]\n");
        }
        --indentLevel;
        serviceNode.append(indent(indentLevel)).append("]\n");

        serviceNode.append(indent(indentLevel)).append(GATEWAYS).append(" = [\n");
        ++indentLevel;
        for (Binding gateway : service.getGateways()) {
            serviceNode.append(indent(indentLevel)).append(gateway.getName()).append(" = [\n");
            ++indentLevel;
            serviceNode.append(indent(indentLevel)).append(TYPE).append(" = ").append(gateway.getType()).append("\n");
            serviceNode.append(addMetricsToNode(gateway.getMessageMetrics(), indentLevel));
            --indentLevel;
            serviceNode.append(indent(indentLevel)).append("]\n");
        }
        --indentLevel;
        serviceNode.append(indent(indentLevel)).append("]\n");

        --indentLevel;
        serviceNode.append(indent(indentLevel)).append("]\n");

        return serviceNode.toString();
    }

    /**
     * Creates text tree from the {@link ComponentService} for metrics. The tree
     * has the form: <br>
     * <code><pre>
     *      "name" = "serviceName",
     *      "application" = "name",
     *      "successCount" = "successCount",
     *      "faultCount" = "faultCount",
     *      "totalCount" = "totalCount",
     *      "averageTime" = "averageTime",
     *      "minTime" = "minTime",
     *      "maxTime" = "maxTime",
     *      "totalTime" = "totalTime",
     *      "operations" = [
     *          {
     *              "name" = "operationName",
     *               "successCount" = "successCount",
     *               "faultCount" = "faultCount",
     *               "totalCount" = "totalCount",
     *               "averageTime" = "averageTime",
     *               "minTime" = "minTime",
     *               "maxTime" = "maxTime",
     *               "totalTime" = "totalTime",
     *          },
     *          ...
     *      ],
     *      "references" = [
     *          {
     *              "name" = "referenceName",
     *               "successCount" = "successCount",
     *               "faultCount" = "faultCount",
     *               "totalCount" = "totalCount",
     *               "averageTime" = "averageTime",
     *               "minTime" = "minTime",
     *               "maxTime" = "maxTime",
     *               "totalTime" = "totalTime",
     *          },
     *          ...
     *      ]
     * </pre></code>
     * 
     * @param componentService the {@link Service} used to populate the node.
     * @param indentLevel the beginning indent level
     * @return text
     */
    public static String printComponentServiceMetrics(ComponentService componentService, int indentLevel) {
        final StringBuffer serviceNode = new StringBuffer();

        serviceNode.append(indent(indentLevel)).append(componentService.getName().toString()).append(" = [\n");

        ++indentLevel;

        serviceNode.append(addMetricsToNode(componentService.getMessageMetrics(), indentLevel));

        serviceNode.append(indent(indentLevel)).append(OPERATIONS).append(" = [\n");
        ++indentLevel;
        for (ServiceOperation operation : componentService.getServiceOperations()) {
            serviceNode.append(indent(indentLevel)).append(operation.getName()).append(" = [\n");
            ++indentLevel;
            serviceNode.append(addMetricsToNode(operation.getMessageMetrics(), indentLevel));
            --indentLevel;
            serviceNode.append(indent(indentLevel)).append("]\n");
        }
        --indentLevel;
        serviceNode.append(indent(indentLevel)).append("]\n");

        serviceNode.append(indent(indentLevel)).append(REFERENCES).append(" = [\n");
        ++indentLevel;
        for (ComponentReference reference : componentService.getReferences()) {
            serviceNode.append(indent(indentLevel)).append(reference.getName()).append(" = [\n");
            ++indentLevel;
            serviceNode.append(addMetricsToNode(reference.getMessageMetrics(), indentLevel));
            --indentLevel;
            serviceNode.append(indent(indentLevel)).append("]\n");
        }
        --indentLevel;
        serviceNode.append(indent(indentLevel)).append("]\n");

        --indentLevel;
        serviceNode.append(indent(indentLevel)).append("]\n");

        return serviceNode.toString();
    }

    /**
     * Creates a new node from the {@link Throttling}. The tree has the form: <br>
     * <code><pre>
     *      "throttling" = [
     *          "enabled" = "true"
     *          "maxRequests" = "maxRequests"
     *          "timePeriod" = "timePeriod"
     *      ]
     * </pre></code>
     * 
     * @param throttling the throttling configuration to add to the node
     * @param indentLevel the beginning indent level
     * @return text
     */
    public static String printThrottlingTo(Throttling throttling, int indentLevel) {
        if (throttling == null) {
            return "";
        }
        final StringBuffer throttlingNode = new StringBuffer();

        throttlingNode.append(indent(indentLevel)).append(THROTTLING).append(" = [\n");

        ++indentLevel;

        throttlingNode.append(indent(indentLevel)).append(ENABLED).append(" = ").append(throttling.isEnabled())
                .append("\n");
        throttlingNode.append(indent(indentLevel)).append(MAX_REQUESTS).append(" = ")
                .append(throttling.getMaxRequests()).append("\n");
        throttlingNode.append(indent(indentLevel)).append(TIME_PERIOD).append(" = ").append(throttling.getTimePeriod())
                .append("\n");

        --indentLevel;

        throttlingNode.append(indent(indentLevel)).append("]\n");

        return throttlingNode.toString();
    }

    private static final List<String> INDENT = new ArrayList<String>();

    private static synchronized String indent(int indentLevel) {
        while (INDENT.size() <= indentLevel) {
            final StringBuffer indent = new StringBuffer();
            for (int i = 0, size = 4 * INDENT.size(); i <= size; ++i) {
                indent.append(' ');
            }
            INDENT.add(indent.toString());
        }
        return INDENT.get(indentLevel);
    }

    private PrintUtil() {
    }

}
