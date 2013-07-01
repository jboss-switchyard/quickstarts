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
package org.switchyard.component.common.knowledge.expression;

import org.switchyard.common.property.PropertyResolver;
import org.switchyard.common.property.SystemAndTestPropertyResolver;

/**
 * ExpressionFactory.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class ExpressionFactory {

    /** The singleton instance. */
    public static final ExpressionFactory INSTANCE = new ExpressionFactory();

    private ExpressionFactory() {}

    /**
     * Creates a new Expression.
     * @param expression the expression string
     * @return the Expression
     */
    public Expression create(String expression) {
        return create(expression, null);
    }

    /**
     * Creates a new Expression.
     * @param expression the expression string
     * @param expressionType the expression type
     * @return the Expression
     */
    public Expression create(String expression, ExpressionType expressionType) {
        return create(expression, expressionType, null);
    }

    /**
     * Creates a new Expression.
     * @param expression the expression string
     * @param expressionType the expression type
     * @param propertyResolver the property resolver
     * @return the Expression
     */
    public Expression create(String expression, ExpressionType expressionType, PropertyResolver propertyResolver) {
        if (expressionType == null) {
            expressionType = ExpressionType.MVEL;
        }
        if (propertyResolver == null) {
            propertyResolver = SystemAndTestPropertyResolver.INSTANCE;
        }
        switch (expressionType) {
            case MVEL:
                return new MVELExpression(expression, propertyResolver);
            default:
                throw new IllegalArgumentException("Unknown expression type: " + expressionType);
        }
    }

}
