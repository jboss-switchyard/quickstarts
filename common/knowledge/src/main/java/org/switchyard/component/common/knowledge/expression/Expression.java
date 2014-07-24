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

import java.util.Map;

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
     * @param variables the variables to run with
     * @return the result of running the expression
     */
    public Object run(Map<String, Object> variables);

}
