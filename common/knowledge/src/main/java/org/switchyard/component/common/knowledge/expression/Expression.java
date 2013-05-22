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

/**
 * Expression.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public interface Expression {

    /**
     * Gets the expression value.
     * @return the expression value
     */
    public String getExpression();

    /**
     * Gets the expression type.
     * @return the expression type
     */
    public ExpressionType getType();

    /**
     * If this expression is compiled.
     * @return if this expression is compiled
     */
    public boolean isCompiled();

    /**
     * Runs the expression.
     * @return the result of running the expression
     */
    public Object run();

    /**
     * Runs the expression.
     * @param context the context to run with
     * @return the result of running the expression
     */
    public Object run(Object context);

}
