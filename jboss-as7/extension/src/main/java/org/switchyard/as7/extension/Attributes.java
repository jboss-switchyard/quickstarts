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

package org.switchyard.as7.extension;

import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.as.controller.operations.validation.StringLengthValidator;
import org.jboss.dmr.ModelType;

/**
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
interface Attributes {
    SimpleAttributeDefinition IDENTIFIER = new SimpleAttributeDefinitionBuilder(CommonAttributes.IDENTIFIER, ModelType.STRING)
            .setAllowNull(false)
            .setMinSize(1)
            .setAllowExpression(false)
            .setValidator(new StringLengthValidator(1, Integer.MAX_VALUE, true, true))
            .build();

    SimpleAttributeDefinition IMPLCLASS = new SimpleAttributeDefinitionBuilder(CommonAttributes.IMPLCLASS, ModelType.STRING)
            .setAllowNull(false)
            .setMinSize(1)
            .setAllowExpression(false)
            .setValidator(new StringLengthValidator(1, Integer.MAX_VALUE, true, true))
            .build();

    SimpleAttributeDefinition PROPERTIES = new SimpleAttributeDefinitionBuilder(CommonAttributes.PROPERTIES, ModelType.STRING)
            .setAllowNull(true)
            .setMinSize(1)
            .setAllowExpression(true)
            .setValidator(new StringLengthValidator(1, Integer.MAX_VALUE, true, true))
            .build();
    
    SimpleAttributeDefinition SOCKET_BINDING = new SimpleAttributeDefinitionBuilder(CommonAttributes.SOCKET_BINDING, ModelType.STRING)
            .setAllowNull(true)
            .setMinSize(1)
            .setAllowExpression(true)
            .setValidator(new StringLengthValidator(1, Integer.MAX_VALUE, true, true))
            .build();
}
