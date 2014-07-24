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

import org.switchyard.common.lang.Strings;
import org.switchyard.common.property.PropertyResolver;
import org.switchyard.common.property.SystemAndTestPropertyResolver;
import org.switchyard.component.common.knowledge.config.model.InputModel;
import org.switchyard.component.common.knowledge.config.model.MappingModel;

/**
 * An expression mapping.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class ExpressionMapping {

    private final PropertyResolver _propertyResolver;
    private final String _from;
    private final String _to;
    private final String _output;
    private Expression _fromExpression = null;
    private Expression _toExpression = null;

    /**
     * Constructs an expression mapping from a mapping model.
     * @param mappingModel the mapping model
     */
    public ExpressionMapping(MappingModel mappingModel) {
        PropertyResolver propertyResolver = mappingModel.getModelConfiguration().getPropertyResolver();
        _propertyResolver = propertyResolver != null ? propertyResolver : SystemAndTestPropertyResolver.INSTANCE;
        _from = Strings.trimToNull(mappingModel.getFrom());
        _to = Strings.trimToNull(mappingModel.getTo());
        _output = mappingModel instanceof InputModel ? Strings.trimToNull(((InputModel)mappingModel).getOutput()) : null;
    }

    /**
     * Gets the property resolver.
     * @return the property resolver
     */
    public PropertyResolver getPropertyResolver() {
        return _propertyResolver;
    }

    /**
     * Gets the from.
     * @return the from
     */
    public String getFrom() {
        return _from;
    }

    /**
     * Gets the from expression.
     * @return the from expression
     */
    public Expression getFromExpression() {
        if (_fromExpression == null && _from != null) {
            _fromExpression = ExpressionFactory.INSTANCE.create(_from, null, _propertyResolver);
        }
        return _fromExpression;
    }

    /**
     * Gets the to.
     * @return the to
     */
    public String getTo() {
        return _to;
    }

    /**
     * Gets the to expression.
     * @return the to expression
     */
    public Expression getToExpression() {
        if (_toExpression == null && _to != null) {
            _toExpression = ExpressionFactory.INSTANCE.create(_to, null, _propertyResolver);
        }
        return _toExpression;
    }

    /**
     * Gets the output.
     * @return the output
     */
    public String getOutput() {
        return _output;
    }

}
