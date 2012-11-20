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
package org.switchyard.component.common.knowledge.config.model.v1;

import org.switchyard.Scope;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.common.knowledge.config.model.MappingModel;
import org.switchyard.component.common.knowledge.expression.ExpressionType;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * The 1st version MappingModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class V1MappingModel extends BaseModel implements MappingModel {

    /**
     * Creates a new MappingModel in the specified namespace.
     * @param namespace the namespace
     */
    public V1MappingModel(String namespace) {
        super(XMLHelper.createQName(namespace, MAPPING));
    }

    /**
     * Creates a new MappingModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1MappingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExpression() {
        return getModelAttribute("expression");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MappingModel setExpression(String expression) {
        setModelAttribute("expression", expression);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExpressionType getExpressionType() {
        String et = getModelAttribute("expressionType");
        return et != null ? ExpressionType.valueOf(et) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MappingModel setExpressionType(ExpressionType expressionType) {
        String et = expressionType != null ? expressionType.name() : null;
        setModelAttribute("expressionType", et);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Scope getScope() {
        String s = getModelAttribute("scope");
        return s != null ? Scope.valueOf(s) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MappingModel setScope(Scope scope) {
        String s = scope != null ? scope.name() : null;
        setModelAttribute("scope", s);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getVariable() {
        return getModelAttribute("variable");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MappingModel setVariable(String variable) {
        setModelAttribute("variable", variable);
        return this;
    }
 
}
