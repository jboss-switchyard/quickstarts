/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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

import org.jboss.as.controller.OperationDefinition;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.as.controller.SimpleOperationDefinitionBuilder;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.as.controller.descriptions.NonResolvingResourceDescriptionResolver;
import org.jboss.dmr.ModelType;

/**
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
interface Operations {

    SimpleAttributeDefinition NAME = SimpleAttributeDefinitionBuilder.create(SwitchYardModelConstants.NAME, ModelType.STRING)
            .setAllowExpression(true)
            .setAllowNull(true)
            .build();

    SimpleAttributeDefinition APPLICATION = SimpleAttributeDefinitionBuilder.create(SwitchYardModelConstants.APPLICATION, ModelType.STRING)
            .setAllowExpression(true)
            .setAllowNull(true)
            .build();

    SimpleAttributeDefinition APPLICATION_NAME = SimpleAttributeDefinitionBuilder.create(SwitchYardModelConstants.APPLICATION_NAME, ModelType.STRING)
            .setAllowExpression(true)
            .setAllowNull(true)
            .build();

    SimpleAttributeDefinition INTERFACE = SimpleAttributeDefinitionBuilder.create(SwitchYardModelConstants.INTERFACE, ModelType.STRING)
            .setAllowExpression(true)
            .setAllowNull(true)
            .build();

    SimpleAttributeDefinition IMPLEMENTATION = SimpleAttributeDefinitionBuilder.create(SwitchYardModelConstants.IMPLEMENTATION, ModelType.STRING)
            .setAllowExpression(true)
            .setAllowNull(true)
            .build();

    SimpleAttributeDefinition IMPLEMENTATION_CONFIGURATION = SimpleAttributeDefinitionBuilder.create(SwitchYardModelConstants.IMPLEMENTATION_CONFIGURATION, ModelType.STRING)
            .setAllowExpression(true)
            .setAllowNull(true)
            .build();

    SimpleAttributeDefinition PROMOTED_REFERENCE = SimpleAttributeDefinitionBuilder.create(SwitchYardModelConstants.PROMOTED_REFERENCE, ModelType.STRING)
            .setAllowExpression(true)
            .setAllowNull(true)
            .build();

    SimpleAttributeDefinition PROMOTED_SERVICE = SimpleAttributeDefinitionBuilder.create(SwitchYardModelConstants.PROMOTED_SERVICE, ModelType.STRING)
            .setAllowExpression(true)
            .setAllowNull(true)
            .build();

    SimpleAttributeDefinition TYPE = SimpleAttributeDefinitionBuilder.create(SwitchYardModelConstants.TYPE, ModelType.STRING)
            .setAllowExpression(true)
            .setAllowNull(true)
            .build();

    SimpleAttributeDefinition CONFIGURATION = SimpleAttributeDefinitionBuilder.create(SwitchYardModelConstants.CONFIGURATION, ModelType.STRING)
            .setAllowExpression(true)
            .setAllowNull(true)
            .build();

    SimpleAttributeDefinition FROM = SimpleAttributeDefinitionBuilder.create(SwitchYardModelConstants.FROM, ModelType.STRING)
            .setAllowExpression(true)
            .setAllowNull(true)
            .build();

    SimpleAttributeDefinition TO = SimpleAttributeDefinitionBuilder.create(SwitchYardModelConstants.TO, ModelType.STRING)
            .setAllowExpression(true)
            .setAllowNull(true)
            .build();

    SimpleAttributeDefinition URL = SimpleAttributeDefinitionBuilder.create(SwitchYardModelConstants.URL, ModelType.STRING)
            .setAllowExpression(true)
            .setAllowNull(true)
            .build();

    SimpleAttributeDefinition REFERENCE_NAME = SimpleAttributeDefinitionBuilder.create(SwitchYardModelConstants.REFERENCE_NAME, ModelType.STRING)
            .setAllowExpression(true)
            .setAllowNull(true)
            .build();

    SimpleAttributeDefinition SERVICE_NAME = SimpleAttributeDefinitionBuilder.create(SwitchYardModelConstants.SERVICE_NAME, ModelType.STRING)
            .setAllowExpression(true)
            .setAllowNull(true)
            .build();

    SimpleAttributeDefinition SUCCESS_COUNT = SimpleAttributeDefinitionBuilder.create(SwitchYardModelConstants.SUCCESS_COUNT, ModelType.INT)
            .setAllowExpression(true)
            .setAllowNull(true)
            .build();

    SimpleAttributeDefinition FAULT_COUNT = SimpleAttributeDefinitionBuilder.create(SwitchYardModelConstants.FAULT_COUNT, ModelType.INT)
            .setAllowExpression(true)
            .setAllowNull(true)
            .build();

    SimpleAttributeDefinition TOTAL_COUNT = SimpleAttributeDefinitionBuilder.create(SwitchYardModelConstants.TOTAL_COUNT, ModelType.INT)
            .setAllowExpression(true)
            .setAllowNull(true)
            .build();

    SimpleAttributeDefinition AVERAGE_TIME = SimpleAttributeDefinitionBuilder.create(SwitchYardModelConstants.AVERAGE_TIME, ModelType.BIG_DECIMAL)
            .setAllowExpression(true)
            .setAllowNull(true)
            .build();

    SimpleAttributeDefinition MIN_TIME = SimpleAttributeDefinitionBuilder.create(SwitchYardModelConstants.MIN_TIME, ModelType.INT)
            .setAllowExpression(true)
            .setAllowNull(true)
            .build();

    SimpleAttributeDefinition MAX_TIME = SimpleAttributeDefinitionBuilder.create(SwitchYardModelConstants.MAX_TIME, ModelType.INT)
            .setAllowExpression(true)
            .setAllowNull(true)
            .build();

    SimpleAttributeDefinition TOTAL_TIME = SimpleAttributeDefinitionBuilder.create(SwitchYardModelConstants.TOTAL_TIME, ModelType.LONG)
            .setAllowExpression(true)
            .setAllowNull(true)
            .build();

    SimpleAttributeDefinition GATEWAYS = SimpleAttributeDefinitionBuilder.create(SwitchYardModelConstants.GATEWAYS, ModelType.LIST)
            .setAllowExpression(true)
            .setAllowNull(true)
            .setMinSize(2)
            .setMaxSize(2)
            .build();

    SimpleAttributeDefinition REFERENCES = SimpleAttributeDefinitionBuilder.create(SwitchYardModelConstants.GATEWAYS, ModelType.LIST)
            .setAllowExpression(true)
            .setAllowNull(true)
            .setMinSize(2)
            .setMaxSize(2)
            .build();

    SimpleAttributeDefinition TRANSFORMERS = SimpleAttributeDefinitionBuilder.create(SwitchYardModelConstants.TRANSFORMERS, ModelType.LIST)
            .setAllowExpression(true)
            .setAllowNull(true)
            .setMinSize(3)
            .setMaxSize(3)
            .build();

    SimpleAttributeDefinition ARTIFACTS = SimpleAttributeDefinitionBuilder.create(SwitchYardModelConstants.ARTIFACTS, ModelType.LIST)
            .setAllowExpression(true)
            .setAllowNull(true)
            .setMinSize(2)
            .setMaxSize(2)
            .build();

    SimpleAttributeDefinition VALIDATORS = SimpleAttributeDefinitionBuilder.create(SwitchYardModelConstants.VALIDATORS, ModelType.LIST)
            .setAllowExpression(true)
            .setAllowNull(true)
            .setMinSize(2)
            .setMaxSize(2)
            .build();

    SimpleAttributeDefinition PROPERTIES = SimpleAttributeDefinitionBuilder.create(ModelDescriptionConstants.PROPERTIES, ModelType.LIST)
            .setAllowExpression(true)
            .setAllowNull(true)
            .setMinSize(2)
            .setMaxSize(2)
            .build();

    SimpleAttributeDefinition SERVICES = SimpleAttributeDefinitionBuilder.create(SwitchYardModelConstants.SERVICES, ModelType.LIST)
            .setAllowExpression(true)
            .setAllowNull(true)
            .setMinSize(5)
            .setMaxSize(5)
            .build();

    SimpleAttributeDefinition COMPONENT_SERVICES = SimpleAttributeDefinitionBuilder.create(SwitchYardModelConstants.COMPONENT_SERVICES, ModelType.LIST)
            .setAllowExpression(true)
            .setAllowNull(true)
            .setMinSize(5)
            .setMaxSize(5)
            .build();

    SimpleAttributeDefinition THROTTLING = SimpleAttributeDefinitionBuilder.create(SwitchYardModelConstants.THROTTLING, ModelType.OBJECT)
            .setAllowExpression(true)
            .build();

    OperationDefinition GET_VERSION = new SimpleOperationDefinitionBuilder(SwitchYardModelConstants.GET_VERSION, new NonResolvingResourceDescriptionResolver())
            .setReadOnly()
            .build();

    OperationDefinition LIST_APPLICATIONS = new SimpleOperationDefinitionBuilder(SwitchYardModelConstants.LIST_APPLICATIONS, new NonResolvingResourceDescriptionResolver())
            .setReadOnly()
            .setRuntimeOnly()
            .setReplyParameters(NAME)
            .setReplyType(ModelType.LIST)
            .build();

    OperationDefinition LIST_REFERENCES = new SimpleOperationDefinitionBuilder(SwitchYardModelConstants.LIST_REFERENCES, new NonResolvingResourceDescriptionResolver())
            .setReadOnly()
            .setRuntimeOnly()
            .setParameters(APPLICATION_NAME)
            .setReplyParameters(NAME, APPLICATION)
            .setReplyType(ModelType.LIST)
            .build();

    OperationDefinition LIST_SERVICES = new SimpleOperationDefinitionBuilder(SwitchYardModelConstants.LIST_SERVICES, new NonResolvingResourceDescriptionResolver())
            .setReadOnly()
            .setRuntimeOnly()
            .setParameters(APPLICATION_NAME)
            .setReplyParameters(NAME, APPLICATION)
            .setReplyType(ModelType.LIST)
            .build();

    OperationDefinition READ_APPLICATION = new SimpleOperationDefinitionBuilder(SwitchYardModelConstants.READ_APPLICATION, new NonResolvingResourceDescriptionResolver())
            .setReadOnly()
            .setRuntimeOnly()
            .setParameters(NAME)
            .setReplyParameters(NAME, SERVICES, COMPONENT_SERVICES, TRANSFORMERS, ARTIFACTS, VALIDATORS, PROPERTIES)
            .setReplyType(ModelType.LIST)
            .build();

    OperationDefinition READ_REFERENCE = new SimpleOperationDefinitionBuilder(SwitchYardModelConstants.READ_REFERENCE, new NonResolvingResourceDescriptionResolver())
            .setReadOnly()
            .setRuntimeOnly()
            .setParameters(REFERENCE_NAME, APPLICATION_NAME)
            .setReplyParameters(NAME, APPLICATION, INTERFACE, PROMOTED_REFERENCE, GATEWAYS)
            .setReplyType(ModelType.LIST)
            .build();

    OperationDefinition READ_SERVICE = new SimpleOperationDefinitionBuilder(SwitchYardModelConstants.READ_SERVICE, new NonResolvingResourceDescriptionResolver())
            .setReadOnly()
            .setRuntimeOnly()
            .setParameters(SERVICE_NAME, APPLICATION_NAME)
            .setReplyParameters(NAME, APPLICATION, INTERFACE, PROMOTED_SERVICE, GATEWAYS)
            .setReplyType(ModelType.LIST)
            .build();

    OperationDefinition USES_ARTIFACT = new SimpleOperationDefinitionBuilder(SwitchYardModelConstants.USES_ARTIFACT, new NonResolvingResourceDescriptionResolver())
            .setReadOnly()
            .setRuntimeOnly()
            .setParameters(NAME, URL)
            .setReplyParameters(NAME)
            .setReplyType(ModelType.LIST)
            .build();

    OperationDefinition SHOW_METRICS = new SimpleOperationDefinitionBuilder(SwitchYardModelConstants.SHOW_METRICS, new NonResolvingResourceDescriptionResolver())
            .setReadOnly()
            .setRuntimeOnly()
            .setParameters(SERVICE_NAME, TYPE)
            .setReplyParameters(SUCCESS_COUNT, FAULT_COUNT, TOTAL_COUNT, AVERAGE_TIME, MIN_TIME, MAX_TIME, TOTAL_TIME)
            .setReplyType(ModelType.LIST)
            .build();

    OperationDefinition RESET_METRICS = new SimpleOperationDefinitionBuilder(SwitchYardModelConstants.RESET_METRICS, new NonResolvingResourceDescriptionResolver())
            .setReadOnly()
            .setRuntimeOnly()
            .setParameters(NAME, APPLICATION_NAME)
            .build();

    OperationDefinition STOP_GATEWAY = new SimpleOperationDefinitionBuilder(SwitchYardModelConstants.STOP_GATEWAY, new NonResolvingResourceDescriptionResolver())
            .setReadOnly()
            .setRuntimeOnly()
            .setParameters(NAME, SERVICE_NAME, APPLICATION_NAME)
            .build();

    OperationDefinition START_GATEWAY = new SimpleOperationDefinitionBuilder(SwitchYardModelConstants.START_GATEWAY, new NonResolvingResourceDescriptionResolver())
            .setReadOnly()
            .setRuntimeOnly()
            .setParameters(NAME, SERVICE_NAME, APPLICATION_NAME)
            .build();

    OperationDefinition UPDATE_THROTTLING = new SimpleOperationDefinitionBuilder(SwitchYardModelConstants.UPDATE_THROTTLING, new NonResolvingResourceDescriptionResolver())
            .setReadOnly()
            .setRuntimeOnly()
            .setParameters(SERVICE_NAME, APPLICATION_NAME, THROTTLING)
            .build();
}
