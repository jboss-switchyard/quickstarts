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
package org.switchyard.component.common.knowledge.exchange;

import java.util.ArrayList;
import java.util.List;

import org.switchyard.component.common.knowledge.OperationType;
import org.switchyard.component.common.knowledge.expression.ExpressionMapping;

/**
 * A Knowledge operation.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class KnowledgeOperation {

    private final OperationType _type;
    private final String _eventId;

    private final List<ExpressionMapping> _globalExpressionMappings = new ArrayList<ExpressionMapping>();
    private final List<ExpressionMapping> _inputExpressionMappings = new ArrayList<ExpressionMapping>();
    private final List<ExpressionMapping> _outputExpressionMappings = new ArrayList<ExpressionMapping>();
    private final List<ExpressionMapping> _faultExpressionMappings = new ArrayList<ExpressionMapping>();

    /**
     * Constructs a new knowledge operation.
     * @param type the operation type
     */
    public KnowledgeOperation(OperationType type) {
        this(type, null);
    }

    /**
     * Constructs a new knowledge operation.
     * @param type the operation type
     * @param eventId the event id
     */
    public KnowledgeOperation(OperationType type, String eventId) {
        _type = type;
        _eventId = eventId;
    }

    /**
     * Gets the operation type.
     * @return the operation type
     */
    public OperationType getType() {
        return _type;
    }

    /**
     * Gets the event id.
     * @return the event id
     */
    public String getEventId() {
        return _eventId;
    }

    /**
     * Gets the global expression mappings.
     * @return the global expression mappings
     */
    public List<ExpressionMapping> getGlobalExpressionMappings() {
        return _globalExpressionMappings;
    }

    /**
     * Gets the input expression mappings.
     * @return the input expression mappings
     */
    public List<ExpressionMapping> getInputExpressionMappings() {
        return _inputExpressionMappings;
    }

    /**
     * Gets the output expression mappings.
     * @return the output expression mappings.
     */
    public List<ExpressionMapping> getOutputExpressionMappings() {
        return _outputExpressionMappings;
    }

    /**
     * Gets the fault expression mappings.
     * @return the fault expression mappings.
     */
    public List<ExpressionMapping> getFaultExpressionMappings() {
        return _faultExpressionMappings;
    }

}
