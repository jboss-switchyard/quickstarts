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
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.DESCRIPTION;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.HEAD_COMMENT_ALLOWED;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.INTERFACE;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.NAME;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.NAMESPACE;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.NILLABLE;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OPERATION_NAME;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.REPLY_PROPERTIES;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.REQUEST_PROPERTIES;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.TAIL_COMMENT_ALLOWED;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.TYPE;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.VALUE_TYPE;
import static org.switchyard.as7.extension.SwitchYardModelConstants.APPLICATION;
import static org.switchyard.as7.extension.SwitchYardModelConstants.APPLICATION_NAME;
import static org.switchyard.as7.extension.SwitchYardModelConstants.CONFIG_SCHEMA;
import static org.switchyard.as7.extension.SwitchYardModelConstants.GATEWAYS;
import static org.switchyard.as7.extension.SwitchYardModelConstants.GET_VERSION;
import static org.switchyard.as7.extension.SwitchYardModelConstants.IMPLEMENTATION;
import static org.switchyard.as7.extension.SwitchYardModelConstants.LIST_APPLICATIONS;
import static org.switchyard.as7.extension.SwitchYardModelConstants.LIST_COMPONENTS;
import static org.switchyard.as7.extension.SwitchYardModelConstants.LIST_SERVICES;
import static org.switchyard.as7.extension.SwitchYardModelConstants.READ_APPLICATION;
import static org.switchyard.as7.extension.SwitchYardModelConstants.READ_COMPONENT;
import static org.switchyard.as7.extension.SwitchYardModelConstants.READ_SERVICE;
import static org.switchyard.as7.extension.SwitchYardModelConstants.SERVICES;
import static org.switchyard.as7.extension.SwitchYardModelConstants.SERVICE_NAME;

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
    static final String RESOURCE_NAME = SwitchYardSubsystemProviders.class.getPackage().getName() + ".LocalDescriptions";

    private SwitchYardSubsystemProviders() {
    }

    static final DescriptionProvider SUBSYSTEM = new DescriptionProvider() {

        public ModelNode getModelDescription(final Locale locale) {
            return Descriptions.getSubsystem(locale);
        }
    };

    static final DescriptionProvider SUBSYSTEM_ADD = new DescriptionProvider() {

        public ModelNode getModelDescription(final Locale locale) {
            return Descriptions.getSubsystemAdd(locale);
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

    static final DescriptionProvider SUBSYSTEM_LIST_COMPONENTS = new DescriptionProvider() {

        public ModelNode getModelDescription(final Locale locale) {
            return Descriptions.getSubsystemListComponents(locale);
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

    static final DescriptionProvider SUBSYSTEM_READ_COMPONENT = new DescriptionProvider() {

        public ModelNode getModelDescription(final Locale locale) {
            return Descriptions.getSubsystemReadComponent(locale);
        }
    };

    static final DescriptionProvider SUBSYSTEM_READ_SERVICE = new DescriptionProvider() {

        public ModelNode getModelDescription(final Locale locale) {
            return Descriptions.getSubsystemReadService(locale);
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
            op.get(REPLY_PROPERTIES, VALUE_TYPE, NAME, DESCRIPTION).set(bundle.getString("switchyard.list-services.reply.name"));

            return op;
        }

        static ModelNode getSubsystemListComponents(Locale locale) {
            final ResourceBundle bundle = getResourceBundle(locale);

            final ModelNode op = new ModelNode();

            op.get(OPERATION_NAME).set(LIST_COMPONENTS);
            op.get(DESCRIPTION).set(bundle.getString("switchyard.list-components"));

            op.get(REQUEST_PROPERTIES, TYPE, TYPE).set(ModelType.STRING);
            op.get(REQUEST_PROPERTIES, TYPE, DESCRIPTION).set(bundle.getString("switchyard.list-components.param.type"));
            op.get(REQUEST_PROPERTIES, TYPE, NILLABLE).set(true);

            op.get(REPLY_PROPERTIES, TYPE).set(ModelType.LIST);
            op.get(REPLY_PROPERTIES, DESCRIPTION).set(bundle.getString("switchyard.list-components.reply"));
            op.get(REPLY_PROPERTIES, VALUE_TYPE).set(ModelType.STRING);

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
            op.get(REPLY_PROPERTIES, VALUE_TYPE, NAME, DESCRIPTION).set(bundle.getString("switchyard.list-services.reply.name"));
            op.get(REPLY_PROPERTIES, VALUE_TYPE, APPLICATION, TYPE).set(ModelType.STRING);
            op.get(REPLY_PROPERTIES, VALUE_TYPE, APPLICATION, DESCRIPTION).set(bundle.getString("switchyard.list-services.reply.application"));

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
            op.get(REPLY_PROPERTIES, VALUE_TYPE, SERVICES, VALUE_TYPE).set(ModelType.STRING);

            return op;
        }

        static ModelNode getSubsystemReadComponent(Locale locale) {
            final ResourceBundle bundle = getResourceBundle(locale);

            final ModelNode op = new ModelNode();

            op.get(OPERATION_NAME).set(READ_COMPONENT);
            op.get(DESCRIPTION).set(bundle.getString("switchyard.read-component"));

            op.get(REQUEST_PROPERTIES, NAME, TYPE).set(ModelType.STRING);
            op.get(REQUEST_PROPERTIES, NAME, DESCRIPTION).set(bundle.getString("switchyard.read-component.param.name"));
            op.get(REQUEST_PROPERTIES, NAME, NILLABLE).set(true);
            op.get(REQUEST_PROPERTIES, TYPE, TYPE).set(ModelType.STRING);
            op.get(REQUEST_PROPERTIES, TYPE, DESCRIPTION).set(bundle.getString("switchyard.read-component.param.type"));
            op.get(REQUEST_PROPERTIES, TYPE, NILLABLE).set(true);

            op.get(REPLY_PROPERTIES, TYPE).set(ModelType.LIST);
            op.get(REPLY_PROPERTIES, DESCRIPTION).set(bundle.getString("switchyard.read-component.reply"));
            op.get(REPLY_PROPERTIES, VALUE_TYPE, NAME, TYPE).set(ModelType.STRING);
            op.get(REPLY_PROPERTIES, VALUE_TYPE, NAME, DESCRIPTION).set(
                    bundle.getString("switchyard.read-component.reply.name"));
            op.get(REPLY_PROPERTIES, VALUE_TYPE, TYPE, TYPE).set(ModelType.STRING);
            op.get(REPLY_PROPERTIES, VALUE_TYPE, TYPE, DESCRIPTION).set(
                    bundle.getString("switchyard.read-component.reply.type"));
            op.get(REPLY_PROPERTIES, VALUE_TYPE, CONFIG_SCHEMA, TYPE).set(ModelType.STRING);
            op.get(REPLY_PROPERTIES, VALUE_TYPE, CONFIG_SCHEMA, DESCRIPTION).set(
                    bundle.getString("switchyard.read-component.reply.config-schema"));

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
            op.get(REPLY_PROPERTIES, VALUE_TYPE, NAME, TYPE).set(ModelType.STRING);
            op.get(REPLY_PROPERTIES, VALUE_TYPE, NAME, DESCRIPTION).set(
                    bundle.getString("switchyard.read-service.reply.name"));
            op.get(REPLY_PROPERTIES, VALUE_TYPE, APPLICATION, TYPE).set(ModelType.STRING);
            op.get(REPLY_PROPERTIES, VALUE_TYPE, APPLICATION, DESCRIPTION).set(
                    bundle.getString("switchyard.read-service.reply.application"));
            op.get(REPLY_PROPERTIES, VALUE_TYPE, INTERFACE, TYPE).set(ModelType.STRING);
            op.get(REPLY_PROPERTIES, VALUE_TYPE, INTERFACE, DESCRIPTION).set(
                    bundle.getString("switchyard.read-service.reply.interface"));
            op.get(REPLY_PROPERTIES, VALUE_TYPE, IMPLEMENTATION, TYPE).set(ModelType.STRING);
            op.get(REPLY_PROPERTIES, VALUE_TYPE, IMPLEMENTATION, DESCRIPTION).set(
                    bundle.getString("switchyard.read-service.reply.implementation"));
            op.get(REPLY_PROPERTIES, VALUE_TYPE, GATEWAYS, TYPE).set(ModelType.LIST);
            op.get(REPLY_PROPERTIES, VALUE_TYPE, GATEWAYS, DESCRIPTION).set(
                    bundle.getString("switchyard.read-service.reply.gateways"));
            op.get(REPLY_PROPERTIES, VALUE_TYPE, GATEWAYS, VALUE_TYPE, TYPE).set(ModelType.STRING);
            op.get(REPLY_PROPERTIES, VALUE_TYPE, GATEWAYS, VALUE_TYPE, DESCRIPTION).set(
                    bundle.getString("switchyard.read-service.reply.gateways.gateway"));

            return op;
        }

    }
}
