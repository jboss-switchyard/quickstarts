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
import org.mvel2.integration.VariableResolver;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.impl.ImmutableDefaultFactory;
import org.mvel2.integration.impl.SimpleValueResolver;
import org.switchyard.common.property.PropertyResolver;

/**
 * MVELExpression.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class MVELExpression implements Expression {

    private final String _expression;
    private final VariableResolverFactory _resolverFactory;
    private Serializable _compiled;

    /**
     * Creates a new MVELExpression with the specified expression and {@link PropertyResolver}.
     * @param expression the specified expression
     * @param propertyResolver the specified {@link PropertyResolver}
     */
    public MVELExpression(String expression, PropertyResolver propertyResolver) {
        _expression = expression;
        _resolverFactory = new PropertyResolverFactory(propertyResolver);
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
        return isCompiled() ? MVEL.executeExpression(_compiled, _resolverFactory) : MVEL.eval(_expression, _resolverFactory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object evaluate(Object context) {
        return isCompiled() ? MVEL.executeExpression(_compiled, context, _resolverFactory) : MVEL.eval(_expression, context, _resolverFactory);
    }

    @SuppressWarnings("serial")
    private static final class PropertyResolverFactory extends ImmutableDefaultFactory {

        private final PropertyResolver _propertyResolver;

        private PropertyResolverFactory(PropertyResolver propertyResolver) {
            _propertyResolver = propertyResolver;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isResolveable(String name) {
            return name != null && _propertyResolver.resolveProperty(name) != null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public VariableResolver getVariableResolver(String name) {
            Object value = name != null ? _propertyResolver.resolveProperty(name) : null;
            return new SimpleValueResolver(value);
        }

    }

}
