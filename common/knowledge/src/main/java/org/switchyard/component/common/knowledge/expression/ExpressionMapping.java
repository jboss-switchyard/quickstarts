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

import org.switchyard.common.lang.Strings;
import org.switchyard.common.property.PropertyResolver;
import org.switchyard.common.property.SystemAndTestPropertyResolver;
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

}
