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

import java.io.Serializable;

import org.mvel2.MVEL;
import org.mvel2.ParserContext;

/**
 * MVELExpression.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class MVELExpression implements Expression {

    private final String _expression;
    private Serializable _compiled;

    /**
     * Creates a new MVELExpression with the specified expression.
     * @param expression the specified expression
     */
    public MVELExpression(String expression) {
        _expression = expression;
        compile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExpression() {
        return _expression;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExpressionType getType() {
        return ExpressionType.MVEL;
    }

    private void compile() {
        ParserContext pc = new ParserContext();
        pc.addPackageImport("org.switchyard");
        _compiled = MVEL.compileExpression(_expression, pc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCompiled() {
        return _compiled != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object evaluate() {
        return isCompiled() ? MVEL.executeExpression(_compiled) : MVEL.eval(_expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object evaluate(Object context) {
        return isCompiled() ? MVEL.executeExpression(_compiled, context) : MVEL.eval(_expression, context);
    }

}
