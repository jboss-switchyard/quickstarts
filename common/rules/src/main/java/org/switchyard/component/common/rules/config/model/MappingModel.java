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
package org.switchyard.component.common.rules.config.model;

import org.switchyard.Scope;
import org.switchyard.component.common.rules.expression.ExpressionType;
import org.switchyard.config.model.Model;

/**
 * MappingModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2012 Red Hat Inc.
 */
public interface MappingModel extends Model {

    /**
     * The mapping XML element.
     */
    public static final String MAPPING = "mapping";

    /**
     * Gets the context scope of the mapping.
     * @return the context scope of the mapping
     */
    public Scope getContextScope();

    /**
     * Sets the context scope of the mapping.
     * @param contextScope the context scope of the mapping
     * @return this MappingModel (useful for chaining)
     */
    public MappingModel setContextScope(Scope contextScope);

    /**
     * Gets the expression of the mapping.
     * @return the expression of the mapping
     */
    public String getExpression();

    /**
     * Sets the expression of the mapping.
     * @param expression the expression of the mapping
     * @return this MappingModel (useful for chaining)
     */
    public MappingModel setExpression(String expression);

    /**
     * Gets the expression type of the mapping.
     * @return the expression type of the mapping
     */
    public ExpressionType getExpressionType();

    /**
     * Sets the expression type of the mapping.
     * @param expressionType the expression type of the mapping
     * @return this MappingModel (useful for chaining)
     */
    public MappingModel setExpressionType(ExpressionType expressionType);


    /**
     * Gets the variable of the mapping.
     * @return the variable of the mapping
     */
    public String getVariable();

    /**
     * Sets the variable of the mapping.
     * @param variable the variable of the mapping
     * @return this MappingModel (useful for chaining)
     */
    public MappingModel setVariable(String variable);

}
