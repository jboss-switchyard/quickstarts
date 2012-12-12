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
package org.switchyard.as7.extension;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.ADD;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.ATTRIBUTES;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.CHILDREN;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.DESCRIPTION;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.HEAD_COMMENT_ALLOWED;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.INTERFACE;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.MAX_OCCURS;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.MIN_OCCURS;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.MODEL_DESCRIPTION;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.NAME;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.NAMESPACE;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.NILLABLE;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OPERATION_NAME;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.REMOVE;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.REPLY_PROPERTIES;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.REQUEST_PROPERTIES;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.REQUIRED;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.TAIL_COMMENT_ALLOWED;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.TYPE;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.VALUE_TYPE;
import static org.switchyard.as7.extension.SwitchYardModelConstants.APPLICATION;
import static org.switchyard.as7.extension.SwitchYardModelConstants.APPLICATION_NAME;
import static org.switchyard.as7.extension.SwitchYardModelConstants.ARTIFACTS;
import static org.switchyard.as7.extension.SwitchYardModelConstants.AVERAGE_TIME;
import static org.switchyard.as7.extension.SwitchYardModelConstants.COMPONENT_SERVICES;
import static org.switchyard.as7.extension.SwitchYardModelConstants.CONFIGURATION;
import static org.switchyard.as7.extension.SwitchYardModelConstants.FAULT_COUNT;
import static org.switchyard.as7.extension.SwitchYardModelConstants.FROM;
import static org.switchyard.as7.extension.SwitchYardModelConstants.GATEWAYS;
import static org.switchyard.as7.extension.SwitchYardModelConstants.GET_VERSION;
import static org.switchyard.as7.extension.SwitchYardModelConstants.IMPLEMENTATION;
import static org.switchyard.as7.extension.SwitchYardModelConstants.IMPLEMENTATION_CONFIGURATION;
import static org.switchyard.as7.extension.SwitchYardModelConstants.LIST_APPLICATIONS;
import static org.switchyard.as7.extension.SwitchYardModelConstants.LIST_SERVICES;
import static org.switchyard.as7.extension.SwitchYardModelConstants.MAX_TIME;
import static org.switchyard.as7.extension.SwitchYardModelConstants.MIN_TIME;
import static org.switchyard.as7.extension.SwitchYardModelConstants.PROMOTED_SERVICE;
import static org.switchyard.as7.extension.SwitchYardModelConstants.READ_APPLICATION;
import static org.switchyard.as7.extension.SwitchYardModelConstants.READ_SERVICE;
import static org.switchyard.as7.extension.SwitchYardModelConstants.REFERENCES;
import static org.switchyard.as7.extension.SwitchYardModelConstants.SERVICES;
import static org.switchyard.as7.extension.SwitchYardModelConstants.SERVICE_NAME;
import static org.switchyard.as7.extension.SwitchYardModelConstants.SHOW_METRICS;
import static org.switchyard.as7.extension.SwitchYardModelConstants.SUCCESS_COUNT;
import static org.switchyard.as7.extension.SwitchYardModelConstants.TO;
import static org.switchyard.as7.extension.SwitchYardModelConstants.TOTAL_COUNT;
import static org.switchyard.as7.extension.SwitchYardModelConstants.TOTAL_TIME;
import static org.switchyard.as7.extension.SwitchYardModelConstants.TRANSFORMERS;
import static org.switchyard.as7.extension.SwitchYardModelConstants.URL;
import static org.switchyard.as7.extension.SwitchYardModelConstants.USES_ARTIFACT;
import static org.switchyard.as7.extension.SwitchYardModelConstants.VALIDATORS;

import java.util.Locale;
import java.util.ResourceBundle;

import org.jboss.as.controller.descriptions.DescriptionProvider;
import org.jboss.as.controller.descriptions.common.CommonDescriptions;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;


/**
 * The SwitchYard subsystem providers.
 * 
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
final class SwitchYardSubsystemProviders {
    static final String RESOURCE_NAME = SwitchYardSubsystemProviders.class.getPackage().getName()
            + ".LocalDescriptions";

    private SwitchYardSubsystemProviders() {
    }

    static final DescriptionProvider SUBSYSTEM = new DescriptionProvider() {

        public ModelNode getModelDescription(final Locale locale) {
            return Descriptions.getSubsystem(locale);
        }
    };

    static final DescriptionProvider SUBSYSTEM_ADD_DESCRIBE = new DescriptionProvider() {

        public ModelNode getModelDescription(final Locale locale) {
            return Descriptions.getSubsystemAdd(locale);
        }
    };

    static final DescriptionProvider SUBSYSTEM_REMOVE_DESCRIBE = new DescriptionProvider() {

        public ModelNode getModelDescription(final Locale locale) {
            return Descriptions.getSubsystemRemove(locale);
        }
    };

    static final DescriptionProvider SOCKET_BINDING_DESCRIBE = new DescriptionProvider() {

        public ModelNode getModelDescription(final Locale locale) {
            return Descriptions.getSocketBinding(locale);
        }
    };

    static final DescriptionProvider MODULE_DESCRIBE = new DescriptionProvider() {

        public ModelNode getModelDescription(final Locale locale) {
            return Descriptions.getModule(locale);
        }
    };

    static final DescriptionProvider MODULE_ADD_DESCRIBE = new DescriptionProvider() {

        public ModelNode getModelDescription(final Locale locale) {
            return Descriptions.getModuleAdd(locale);
        }
    };

    static final DescriptionProvider MODULE_REMOVE_DESCRIBE = new DescriptionProvider() {

        public ModelNode getModelDescription(final Locale locale) {
            return Descriptions.getModuleRemove(locale);
        }
    };

    static final DescriptionProvider SUBSYSTEM_DESCRIBE = new DescriptionProvider() {

        public ModelNode getModelDescription(final Locale locale) {
            return CommonDescriptions.getSubsystemDescribeOperation(locale);
        }
    };

    static final DescriptionProvider SUBSYSTEM_GET_VERSION = new DescriptionProvider() {

        public ModelNode getModelDescription(final Locale locale) {
            return Descriptions.getSubsystemGetVersion(locale);
        }
    };

    static final DescriptionProvider SUBSYSTEM_LIST_APPLICATIONS = new DescriptionProvider() {

        public ModelNode getModelDescription(final Locale locale) {
            return Descriptions.getSubsystemListApplications(locale);
        }
    };

    static final DescriptionProvider SUBSYSTEM_LIST_SERVICES = new DescriptionProvider() {

        public ModelNode getModelDescription(final Locale locale) {
            return Descriptions.getSubsystemListServices(locale);
        }
    };

    static final DescriptionProvider SUBSYSTEM_READ_APPLICATION = new DescriptionProvider() {

        public ModelNode getModelDescription(final Locale locale) {
            return Descriptions.getSubsystemReadApplication(locale);
        }
    };

    static final DescriptionProvider SUBSYSTEM_READ_SERVICE = new DescriptionProvider() {

        public ModelNode getModelDescription(final Locale locale) {
            return Descriptions.getSubsystemReadService(locale);
        }
    };

    static final DescriptionProvider SUBSYSTEM_USES_ARTIFACT = new DescriptionProvider() {

        public ModelNode getModelDescription(final Locale locale) {
            return Descriptions.getSubsystemUsesArtifact(locale);
        }
    };

    static final DescriptionProvider SUBSYSTEM_SHOW_METRICS = new DescriptionProvider() {

        public ModelNode getModelDescription(final Locale locale) {
            return Descriptions.getSubsystemShowMetrics(locale);
        }
    };

    private static ResourceBundle getResourceBundle(Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        return ResourceBundle.getBundle(RESOURCE_NAME, locale);
    }

    private static final class Descriptions {
        private Descriptions() {
        }

        static ModelNode getSubsystem(Locale locale) {
            final ResourceBundle bundle = getResourceBundle(locale);

            final ModelNode subsystem = new ModelNode();

            subsystem.get(DESCRIPTION).set(bundle.getString("switchyard"));
            subsystem.get(HEAD_COMMENT_ALLOWED).set(true);
            subsystem.get(TAIL_COMMENT_ALLOWED).set(true);
            subsystem.get(NAMESPACE).set(SwitchYardExtension.NAMESPACE);

            subsystem.get(ATTRIBUTES, CommonAttributes.SOCKET_BINDING, DESCRIPTION).set(bundle.getString("switchyard.socket-binding"));
            subsystem.get(ATTRIBUTES, CommonAttributes.SOCKET_BINDING, TYPE).set(ModelType.STRING);
            subsystem.get(ATTRIBUTES, CommonAttributes.SOCKET_BINDING, REQUIRED).set(false);
            subsystem.get(ATTRIBUTES, CommonAttributes.SOCKET_BINDING, NILLABLE).set(false);

            subsystem.get(ATTRIBUTES, CommonAttributes.PROPERTIES, DESCRIPTION).set(bundle.getString("switchyard.properties"));
            subsystem.get(ATTRIBUTES, CommonAttributes.PROPERTIES, TYPE).set(ModelType.STRING);
            subsystem.get(ATTRIBUTES, CommonAttributes.PROPERTIES, REQUIRED).set(false);
            subsystem.get(ATTRIBUTES, CommonAttributes.PROPERTIES, NILLABLE).set(false);

            subsystem.get(CHILDREN, CommonAttributes.MODULE, DESCRIPTION).set(bundle.getString("switchyard.modules"));
            subsystem.get(CHILDREN, CommonAttributes.MODULE, MIN_OCCURS).set(0);
            subsystem.get(CHILDREN, CommonAttributes.MODULE, MAX_OCCURS).set(Integer.MAX_VALUE);
            subsystem.get(CHILDREN, CommonAttributes.MODULE, MODEL_DESCRIPTION);


            return subsystem;
        }

        static ModelNode getSubsystemAdd(Locale locale) {
            final ResourceBundle bundle = getResourceBundle(locale);

            final ModelNode op = new ModelNode();

            op.get(OPERATION_NAME).set(ADD);
            op.get(DESCRIPTION).set(bundle.getString("switchyard.add"));

            op.get(REPLY_PROPERTIES).setEmptyObject();

            return op;
        }

        static ModelNode getSubsystemRemove(Locale locale) {
            final ResourceBundle bundle = getResourceBundle(locale);

            final ModelNode op = new ModelNode();

            op.get(OPERATION_NAME).set(REMOVE);
            op.get(DESCRIPTION).set(bundle.getString("switchyard.remove"));

            op.get(REPLY_PROPERTIES).setEmptyObject();

            return op;
        }

        static ModelNode getSocketBinding(Locale locale) {
            final ResourceBundle bundle = getResourceBundle(locale);

            final ModelNode node = new ModelNode();

            node.get(DESCRIPTION).set(bundle.getString("switchyard.socket-binding"));

            return node;
        }

        static ModelNode getModule(Locale locale) {
            final ResourceBundle bundle = getResourceBundle(locale);

            final ModelNode node = new ModelNode();

            node.get(DESCRIPTION).set(bundle.getString("switchyard.modules.module"));
            node.get(HEAD_COMMENT_ALLOWED).set(true);
            node.get(TAIL_COMMENT_ALLOWED).set(true);
            node.get(ATTRIBUTES, CommonAttributes.IDENTIFIER, TYPE).set(ModelType.STRING);
            node.get(ATTRIBUTES, CommonAttributes.IDENTIFIER, DESCRIPTION).set(bundle.getString("switchyard.modules.module.identifier"));
            node.get(ATTRIBUTES, CommonAttributes.IDENTIFIER, REQUIRED).set(true);
            node.get(ATTRIBUTES, CommonAttributes.IDENTIFIER, NILLABLE).set(false);
            
            node.get(ATTRIBUTES, CommonAttributes.IMPLCLASS, TYPE).set(ModelType.STRING);
            node.get(ATTRIBUTES, CommonAttributes.IMPLCLASS, DESCRIPTION).set(bundle.getString("switchyard.modules.module.implclass"));
            node.get(ATTRIBUTES, CommonAttributes.IMPLCLASS, REQUIRED).set(true);
            node.get(ATTRIBUTES, CommonAttributes.IMPLCLASS, NILLABLE).set(false);

            node.get(ATTRIBUTES, CommonAttributes.PROPERTIES, DESCRIPTION).set(bundle.getString("switchyard.modules.module.properties"));
            node.get(ATTRIBUTES, CommonAttributes.PROPERTIES, TYPE).set(ModelType.STRING);
            node.get(ATTRIBUTES, CommonAttributes.PROPERTIES, REQUIRED).set(false);
            node.get(ATTRIBUTES, CommonAttributes.PROPERTIES, NILLABLE).set(false);

            return node;
        }

        static ModelNode getModuleAdd(Locale locale) {
            final ResourceBundle bundle = getResourceBundle(locale);

            final ModelNode node = new ModelNode();

            node.get(OPERATION_NAME).set(ADD);
            node.get(DESCRIPTION).set(bundle.getString("switchyard.modules.module.add"));

            return node;
        }

        static ModelNode getModuleRemove(Locale locale) {
            final ResourceBundle bundle = getResourceBundle(locale);

            final ModelNode node = new ModelNode();

            node.get(OPERATION_NAME).set(REMOVE);
            node.get(DESCRIPTION).set(bundle.getString("switchyard.modules.module.remove"));

            return node;
        }

        static ModelNode getSubsystemGetVersion(Locale locale) {
            final ResourceBundle bundle = getResourceBundle(locale);

            final ModelNode op = new ModelNode();

            op.get(OPERATION_NAME).set(GET_VERSION);
            op.get(DESCRIPTION).set(bundle.getString("switchyard.get-version"));

            op.get(REPLY_PROPERTIES, TYPE).set(ModelType.STRING);
            op.get(REPLY_PROPERTIES, DESCRIPTION).set(bundle.getString("switchyard.get-version.reply"));

            return op;
        }

        static ModelNode getSubsystemListApplications(Locale locale) {
            final ResourceBundle bundle = getResourceBundle(locale);

            final ModelNode op = new ModelNode();

            op.get(OPERATION_NAME).set(LIST_APPLICATIONS);
            op.get(DESCRIPTION).set(bundle.getString("switchyard.list-applications"));

            op.get(REPLY_PROPERTIES, TYPE).set(ModelType.LIST);
            op.get(REPLY_PROPERTIES, DESCRIPTION).set(bundle.getString("switchyard.list-applications.reply"));
            op.get(REPLY_PROPERTIES, VALUE_TYPE, NAME, TYPE).set(ModelType.STRING);
            op.get(REPLY_PROPERTIES, VALUE_TYPE, NAME, DESCRIPTION).set(
                    bundle.getString("switchyard.list-services.reply.name"));

            return op;
        }

        static ModelNode getSubsystemListServices(Locale locale) {
            final ResourceBundle bundle = getResourceBundle(locale);

            final ModelNode op = new ModelNode();

            op.get(OPERATION_NAME).set(LIST_SERVICES);
            op.get(DESCRIPTION).set(bundle.getString("switchyard.list-services"));

            op.get(REQUEST_PROPERTIES, APPLICATION_NAME, TYPE).set(ModelType.STRING);
            op.get(REQUEST_PROPERTIES, APPLICATION_NAME, DESCRIPTION).set(
                    bundle.getString("switchyard.list-services.param.application-name"));
            op.get(REQUEST_PROPERTIES, APPLICATION_NAME, NILLABLE).set(true);

            op.get(REPLY_PROPERTIES, TYPE).set(ModelType.LIST);
            op.get(REPLY_PROPERTIES, DESCRIPTION).set(bundle.getString("switchyard.list-services.reply"));
            op.get(REPLY_PROPERTIES, VALUE_TYPE, NAME, TYPE).set(ModelType.STRING);
            op.get(REPLY_PROPERTIES, VALUE_TYPE, NAME, DESCRIPTION).set(
                    bundle.getString("switchyard.list-services.reply.name"));
            op.get(REPLY_PROPERTIES, VALUE_TYPE, APPLICATION, TYPE).set(ModelType.STRING);
            op.get(REPLY_PROPERTIES, VALUE_TYPE, APPLICATION, DESCRIPTION).set(
                    bundle.getString("switchyard.list-services.reply.application"));

            return op;
        }

        static ModelNode getSubsystemReadApplication(Locale locale) {
            final ResourceBundle bundle = getResourceBundle(locale);

            final ModelNode op = new ModelNode();

            op.get(OPERATION_NAME).set(READ_APPLICATION);
            op.get(DESCRIPTION).set(bundle.getString("switchyard.read-application"));

            op.get(REQUEST_PROPERTIES, NAME, TYPE).set(ModelType.STRING);
            op.get(REQUEST_PROPERTIES, NAME, DESCRIPTION).set(
                    bundle.getString("switchyard.read-application.param.name"));
            op.get(REQUEST_PROPERTIES, NAME, NILLABLE).set(true);

            op.get(REPLY_PROPERTIES, TYPE).set(ModelType.LIST);
            op.get(REPLY_PROPERTIES, DESCRIPTION).set(bundle.getString("switchyard.read-application.reply"));
            op.get(REPLY_PROPERTIES, VALUE_TYPE, NAME, TYPE).set(ModelType.STRING);
            op.get(REPLY_PROPERTIES, VALUE_TYPE, NAME, DESCRIPTION).set(
                    bundle.getString("switchyard.read-application.reply.name"));

            op.get(REPLY_PROPERTIES, VALUE_TYPE, SERVICES, TYPE).set(ModelType.LIST);
            op.get(REPLY_PROPERTIES, VALUE_TYPE, SERVICES, DESCRIPTION).set(
                    bundle.getString("switchyard.read-application.reply.services"));
            populateServiceValueTypeNode(op.get(REPLY_PROPERTIES, VALUE_TYPE, SERVICES), locale);

            op.get(REPLY_PROPERTIES, VALUE_TYPE, COMPONENT_SERVICES, TYPE).set(ModelType.LIST);
            op.get(REPLY_PROPERTIES, VALUE_TYPE, COMPONENT_SERVICES, DESCRIPTION).set(
                    bundle.getString("switchyard.read-application.reply.componentServices"));
            populateComponentServiceValueTypeNode(op.get(REPLY_PROPERTIES, VALUE_TYPE, COMPONENT_SERVICES), locale);

            op.get(REPLY_PROPERTIES, VALUE_TYPE, TRANSFORMERS, TYPE).set(ModelType.LIST);
            op.get(REPLY_PROPERTIES, VALUE_TYPE, TRANSFORMERS, DESCRIPTION).set(
                    bundle.getString("switchyard.read-application.reply.transformers"));
            populateTransformerValueTypeNode(op.get(REPLY_PROPERTIES, VALUE_TYPE, TRANSFORMERS), locale);

            op.get(REPLY_PROPERTIES, VALUE_TYPE, ARTIFACTS, TYPE).set(ModelType.LIST);
            op.get(REPLY_PROPERTIES, VALUE_TYPE, ARTIFACTS, DESCRIPTION).set(
                    bundle.getString("switchyard.read-application.reply.artifacts"));
            populateArtifactValueTypeNode(op.get(REPLY_PROPERTIES, VALUE_TYPE, ARTIFACTS), locale);

            op.get(REPLY_PROPERTIES, VALUE_TYPE, VALIDATORS, TYPE).set(ModelType.LIST);
            op.get(REPLY_PROPERTIES, VALUE_TYPE, VALIDATORS, DESCRIPTION).set(
                    bundle.getString("switchyard.read-application.reply.validators"));
            populateValidatorValueTypeNode(op.get(REPLY_PROPERTIES, VALUE_TYPE, VALIDATORS), locale);

            return op;
        }

        static ModelNode getSubsystemReadService(Locale locale) {
            final ResourceBundle bundle = getResourceBundle(locale);

            final ModelNode op = new ModelNode();

            op.get(OPERATION_NAME).set(READ_SERVICE);
            op.get(DESCRIPTION).set(bundle.getString("switchyard.read-service"));

            op.get(REQUEST_PROPERTIES, SERVICE_NAME, TYPE).set(ModelType.STRING);
            op.get(REQUEST_PROPERTIES, SERVICE_NAME, DESCRIPTION).set(
                    bundle.getString("switchyard.read-service.param.service-name"));
            op.get(REQUEST_PROPERTIES, SERVICE_NAME, NILLABLE).set(true);
            op.get(REQUEST_PROPERTIES, APPLICATION_NAME, TYPE).set(ModelType.STRING);
            op.get(REQUEST_PROPERTIES, APPLICATION_NAME, DESCRIPTION).set(
                    bundle.getString("switchyard.read-service.param.application-name"));
            op.get(REQUEST_PROPERTIES, APPLICATION_NAME, NILLABLE).set(true);

            op.get(REPLY_PROPERTIES, TYPE).set(ModelType.LIST);
            op.get(REPLY_PROPERTIES, DESCRIPTION).set(bundle.getString("switchyard.read-service.reply"));

            populateServiceValueTypeNode(op.get(REPLY_PROPERTIES), locale);

            return op;
        }

        static ModelNode getSubsystemUsesArtifact(Locale locale) {
            final ResourceBundle bundle = getResourceBundle(locale);

            final ModelNode op = new ModelNode();

            op.get(OPERATION_NAME).set(USES_ARTIFACT);
            op.get(DESCRIPTION).set(bundle.getString("switchyard.uses-artifacts"));

            op.get(REQUEST_PROPERTIES, NAME, TYPE).set(ModelType.STRING);
            op.get(REQUEST_PROPERTIES, NAME, DESCRIPTION).set(
                    bundle.getString("switchyard.uses-artifacts.param.name"));
            op.get(REQUEST_PROPERTIES, NAME, NILLABLE).set(true);
            op.get(REQUEST_PROPERTIES, URL, TYPE).set(ModelType.STRING);
            op.get(REQUEST_PROPERTIES, URL, DESCRIPTION).set(
                    bundle.getString("switchyard.uses-artifacts.param.url"));
            op.get(REQUEST_PROPERTIES, URL, NILLABLE).set(true);

            op.get(REPLY_PROPERTIES, TYPE).set(ModelType.LIST);
            op.get(REPLY_PROPERTIES, DESCRIPTION).set(bundle.getString("switchyard.uses-artifacts.reply"));
            op.get(REPLY_PROPERTIES, VALUE_TYPE, NAME, TYPE).set(ModelType.STRING);
            op.get(REPLY_PROPERTIES, VALUE_TYPE, NAME, DESCRIPTION).set(
                    bundle.getString("switchyard.uses-artifacts.reply.name"));

            return op;
        }

        static ModelNode getSubsystemShowMetrics(Locale locale) {
            final ResourceBundle bundle = getResourceBundle(locale);

            final ModelNode op = new ModelNode();

            op.get(OPERATION_NAME).set(SHOW_METRICS);
            op.get(DESCRIPTION).set(bundle.getString("switchyard.show-metrics"));

            op.get(REQUEST_PROPERTIES, SERVICE_NAME, TYPE).set(ModelType.STRING);
            op.get(REQUEST_PROPERTIES, SERVICE_NAME, DESCRIPTION).set(
                    bundle.getString("switchyard.show-metrics.param.service-name"));
            op.get(REQUEST_PROPERTIES, SERVICE_NAME, NILLABLE).set(true);

            op.get(REPLY_PROPERTIES, TYPE).set(ModelType.LIST);
            op.get(REPLY_PROPERTIES, DESCRIPTION).set(bundle.getString("switchyard.show-metrics.reply"));
            op.get(REPLY_PROPERTIES, VALUE_TYPE, SUCCESS_COUNT, TYPE).set(ModelType.INT);
            op.get(REPLY_PROPERTIES, VALUE_TYPE, SUCCESS_COUNT, DESCRIPTION).set(
                    bundle.getString("switchyard.show-metrics.reply.successCount"));
            op.get(REPLY_PROPERTIES, VALUE_TYPE, FAULT_COUNT, TYPE).set(ModelType.INT);
            op.get(REPLY_PROPERTIES, VALUE_TYPE, FAULT_COUNT, DESCRIPTION).set(
                    bundle.getString("switchyard.show-metrics.reply.faultCount"));
            op.get(REPLY_PROPERTIES, VALUE_TYPE, TOTAL_COUNT, TYPE).set(ModelType.INT);
            op.get(REPLY_PROPERTIES, VALUE_TYPE, TOTAL_COUNT, DESCRIPTION).set(
                    bundle.getString("switchyard.show-metrics.reply.totalCount"));
            op.get(REPLY_PROPERTIES, VALUE_TYPE, AVERAGE_TIME, TYPE).set(ModelType.DOUBLE);
            op.get(REPLY_PROPERTIES, VALUE_TYPE, AVERAGE_TIME, DESCRIPTION).set(
                    bundle.getString("switchyard.show-metrics.reply.averageTime"));
            op.get(REPLY_PROPERTIES, VALUE_TYPE, MIN_TIME, TYPE).set(ModelType.INT);
            op.get(REPLY_PROPERTIES, VALUE_TYPE, MIN_TIME, DESCRIPTION).set(
                    bundle.getString("switchyard.show-metrics.reply.minTime"));
            op.get(REPLY_PROPERTIES, VALUE_TYPE, MAX_TIME, TYPE).set(ModelType.INT);
            op.get(REPLY_PROPERTIES, VALUE_TYPE, MAX_TIME, DESCRIPTION).set(
                    bundle.getString("switchyard.show-metrics.reply.maxTime"));
            op.get(REPLY_PROPERTIES, VALUE_TYPE, TOTAL_TIME, TYPE).set(ModelType.LONG);
            op.get(REPLY_PROPERTIES, VALUE_TYPE, MAX_TIME, DESCRIPTION).set(
                    bundle.getString("switchyard.show-metrics.reply.totalTime"));

            return op;
        }

        static void populateServiceValueTypeNode(ModelNode op, Locale locale) {
            final ResourceBundle bundle = getResourceBundle(locale);

            op.get(VALUE_TYPE, NAME, TYPE).set(ModelType.STRING);
            op.get(VALUE_TYPE, NAME, DESCRIPTION).set(bundle.getString("switchyard.read-service.reply.name"));
            op.get(VALUE_TYPE, APPLICATION, TYPE).set(ModelType.STRING);
            op.get(VALUE_TYPE, APPLICATION, DESCRIPTION).set(
                    bundle.getString("switchyard.read-service.reply.application"));
            op.get(VALUE_TYPE, INTERFACE, TYPE).set(ModelType.STRING);
            op.get(VALUE_TYPE, INTERFACE, DESCRIPTION).set(bundle.getString("switchyard.read-service.reply.interface"));
            op.get(VALUE_TYPE, PROMOTED_SERVICE, TYPE).set(ModelType.STRING);
            op.get(VALUE_TYPE, PROMOTED_SERVICE, DESCRIPTION).set(
                    bundle.getString("switchyard.read-service.reply.promotedService"));
            op.get(VALUE_TYPE, GATEWAYS, TYPE).set(ModelType.LIST);
            op.get(VALUE_TYPE, GATEWAYS, DESCRIPTION).set(bundle.getString("switchyard.read-service.reply.gateways"));
            op.get(VALUE_TYPE, GATEWAYS, VALUE_TYPE, TYPE, TYPE).set(ModelType.STRING);
            op.get(VALUE_TYPE, GATEWAYS, VALUE_TYPE, TYPE, DESCRIPTION).set(
                    bundle.getString("switchyard.read-service.reply.gateways.gateway"));
            op.get(VALUE_TYPE, GATEWAYS, VALUE_TYPE, CONFIGURATION, TYPE).set(ModelType.STRING);
            op.get(VALUE_TYPE, GATEWAYS, VALUE_TYPE, CONFIGURATION, DESCRIPTION).set(
                    bundle.getString("switchyard.read-service.reply.gateways.configuration"));
        }

        static void populateComponentServiceValueTypeNode(ModelNode op, Locale locale) {
            final ResourceBundle bundle = getResourceBundle(locale);

            op.get(VALUE_TYPE, NAME, TYPE).set(ModelType.STRING);
            op.get(VALUE_TYPE, NAME, DESCRIPTION).set(bundle.getString("switchyard.read-service.reply.name"));
            op.get(VALUE_TYPE, APPLICATION, TYPE).set(ModelType.STRING);
            op.get(VALUE_TYPE, APPLICATION, DESCRIPTION).set(
                    bundle.getString("switchyard.read-service.reply.application"));
            op.get(VALUE_TYPE, INTERFACE, TYPE).set(ModelType.STRING);
            op.get(VALUE_TYPE, INTERFACE, DESCRIPTION).set(bundle.getString("switchyard.read-service.reply.interface"));
            op.get(VALUE_TYPE, IMPLEMENTATION, TYPE).set(ModelType.STRING);
            op.get(VALUE_TYPE, IMPLEMENTATION, DESCRIPTION).set(
                    bundle.getString("switchyard.read-service.reply.implementation"));
            op.get(VALUE_TYPE, IMPLEMENTATION_CONFIGURATION, TYPE).set(ModelType.STRING);
            op.get(VALUE_TYPE, IMPLEMENTATION_CONFIGURATION, DESCRIPTION).set(
                    bundle.getString("switchyard.read-service.reply.implementationConfiguration"));
            op.get(VALUE_TYPE, REFERENCES, TYPE).set(ModelType.LIST);
            op.get(VALUE_TYPE, REFERENCES, DESCRIPTION).set(bundle.getString("switchyard.read-service.reply.references"));
            op.get(VALUE_TYPE, REFERENCES, VALUE_TYPE, NAME, TYPE).set(ModelType.STRING);
            op.get(VALUE_TYPE, REFERENCES, VALUE_TYPE, NAME, DESCRIPTION).set(
                    bundle.getString("switchyard.read-service.reply.references.name"));
            op.get(VALUE_TYPE, REFERENCES, VALUE_TYPE, INTERFACE, TYPE).set(ModelType.STRING);
            op.get(VALUE_TYPE, REFERENCES, VALUE_TYPE, INTERFACE, DESCRIPTION).set(
                    bundle.getString("switchyard.read-service.reply.references.interface"));
        }

        static void populateTransformerValueTypeNode(ModelNode op, Locale locale) {
            final ResourceBundle bundle = getResourceBundle(locale);

            op.get(VALUE_TYPE, FROM, TYPE).set(ModelType.STRING);
            op.get(VALUE_TYPE, FROM, DESCRIPTION).set(
                    bundle.getString("switchyard.read-application.reply.transformer.from"));
            op.get(VALUE_TYPE, TO, TYPE).set(ModelType.STRING);
            op.get(VALUE_TYPE, TO, DESCRIPTION).set(bundle.getString("switchyard.read-application.reply.transformer.to"));
            op.get(VALUE_TYPE, TYPE, TYPE).set(ModelType.STRING);
            op.get(VALUE_TYPE, TYPE, DESCRIPTION).set(bundle.getString("switchyard.read-application.reply.transformer.type"));
        }

        static void populateArtifactValueTypeNode(ModelNode op, Locale locale) {
            final ResourceBundle bundle = getResourceBundle(locale);

            op.get(VALUE_TYPE, NAME, TYPE).set(ModelType.STRING);
            op.get(VALUE_TYPE, NAME, DESCRIPTION).set(
                    bundle.getString("switchyard.read-application.reply.artifact.name"));
            op.get(VALUE_TYPE, URL, TYPE).set(ModelType.STRING);
            op.get(VALUE_TYPE, URL, DESCRIPTION).set(bundle.getString("switchyard.read-application.reply.artifact.url"));
        }

        static void populateValidatorValueTypeNode(ModelNode op, Locale locale) {
            final ResourceBundle bundle = getResourceBundle(locale);

            op.get(VALUE_TYPE, NAME, TYPE).set(ModelType.STRING);
            op.get(VALUE_TYPE, NAME, DESCRIPTION).set(
                    bundle.getString("switchyard.read-application.reply.validator.name"));
            op.get(VALUE_TYPE, TYPE, TYPE).set(ModelType.STRING);
            op.get(VALUE_TYPE, TYPE, DESCRIPTION).set(
                    bundle.getString("switchyard.read-application.reply.validator.type"));
        }
    }
}
