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
package org.switchyard.component.common.knowledge.exchange;

import java.util.ArrayList;
import java.util.List;

import org.switchyard.component.common.knowledge.ActionType;
import org.switchyard.component.common.knowledge.expression.ExpressionMapping;

/**
 * A Knowledge action.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class KnowledgeAction {

    private final ActionType _type;
    private final String _eventId;

    private final List<ExpressionMapping> _globalExpressionMappings = new ArrayList<ExpressionMapping>();
    private final List<ExpressionMapping> _inputExpressionMappings = new ArrayList<ExpressionMapping>();
    private final List<ExpressionMapping> _outputExpressionMappings = new ArrayList<ExpressionMapping>();

    /**
     * Constructs a new knowledge action.
     * @param type the type
     */
    public KnowledgeAction(ActionType type) {
        this(type, null);
    }

    /**
     * Constructs a new knowledge action.
     * @param type the type
     * @param eventId the event id
     */
    public KnowledgeAction(ActionType type, String eventId) {
        _type = type;
        _eventId = eventId;
    }

    /**
     * Gets the type.
     * @return the type
     */
    public ActionType getType() {
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

}
