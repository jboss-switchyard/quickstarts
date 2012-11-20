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
package org.switchyard.component.common.knowledge.expression;

import org.switchyard.component.common.knowledge.config.model.MappingModel;

/**
 * ExpressionFactory.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class ExpressionFactory {

    private static final ExpressionFactory INSTANCE = new ExpressionFactory();

    private ExpressionFactory() {}

    /**
     * Creates a new Expression.
     * @param expression the expression value
     * @param expressionType the expression type
     * @return the Expression
     */
    public Expression create(String expression, ExpressionType expressionType) {
        switch (expressionType) {
            case MVEL:
                return new MVELExpression(expression);
            default:
                throw new IllegalArgumentException("Unknown expressionType: " + expressionType);
        }
    }

    /**
     * Creates a new Expression.
     * @param mappingModel the mapping model which contains the expression value and expression type
     * @return the Expression
     */
    public Expression create(MappingModel mappingModel) {
        ExpressionType expressionType = mappingModel.getExpressionType();
        if (expressionType == null) {
            expressionType = ExpressionType.MVEL;
        }
        return create(mappingModel.getExpression(), expressionType);
    }

    /**
     * Returns the singleton instance.
     * @return the singleton instance
     */
    public static ExpressionFactory instance() {
        return INSTANCE;
    }

}
