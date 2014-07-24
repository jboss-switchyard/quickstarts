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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.integration.VariableResolver;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.impl.ImmutableDefaultFactory;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.mvel2.integration.impl.SimpleValueResolver;
import org.switchyard.common.property.PropertyResolver;

/**
 * MVELExpression.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class MVELExpression implements Expression {

    private final String _expression;
    private final PropertyResolverFactory _propertyResolverFactory;
    private Serializable _compiled;

    /**
     * Creates a new MVELExpression with the specified expression and {@link PropertyResolver}.
     * @param expression the specified expression
     * @param propertyResolver the specified {@link PropertyResolver}
     */
    public MVELExpression(String expression, PropertyResolver propertyResolver) {
        _expression = expression;
        _propertyResolverFactory = new PropertyResolverFactory(propertyResolver);
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
        pc.addPackageImport("java.util");
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
    public Object run() {
        return run(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object run(Map<String, Object> variables) {
        if (variables == null) {
            variables = new HashMap<String, Object>();
        }
        VariableResolverFactory factory = new MapVariableResolverFactory(variables, _propertyResolverFactory);
        return isCompiled() ? MVEL.executeExpression(_compiled, factory) : MVEL.eval(_expression, factory);
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
