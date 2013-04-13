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
