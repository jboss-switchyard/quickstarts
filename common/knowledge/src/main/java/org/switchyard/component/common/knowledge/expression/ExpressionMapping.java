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

import org.switchyard.Scope;

/**
 * An expression mapping.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class ExpressionMapping {

    private final Expression _expression;
    private final Scope _scope;
    private final String _variable;

    /**
     * Constructs an expression mapping.
     * @param expression the expression
     * @param scope the scope
     * @param variable the variable
     */
    public ExpressionMapping(Expression expression, Scope scope, String variable) {
        _expression = expression;
        _scope = scope;
        _variable = variable;
    }

    /**
     * Gets the expression.
     * @return the expression
     */
    public Expression getExpression() {
        return _expression;
    }

    /**
     * Gets the scope.
     * @return the scope
     */
    public Scope getScope() {
        return _scope;
    }

    /**
     * Gets the variable.
     * @return the variable
     */
    public String getVariable() {
        return _variable;
    }

}
