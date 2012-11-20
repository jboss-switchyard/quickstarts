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
package org.switchyard.component.common.knowledge.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.kie.runtime.Globals;
import org.switchyard.Exchange;
import org.switchyard.Scope;
import org.switchyard.common.lang.Strings;
import org.switchyard.component.common.knowledge.config.model.ActionModel;
import org.switchyard.component.common.knowledge.config.model.ActionsModel;
import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;
import org.switchyard.component.common.knowledge.config.model.MappingModel;
import org.switchyard.component.common.knowledge.config.model.MappingsModel;
import org.switchyard.component.common.knowledge.exchange.KnowledgeAction;
import org.switchyard.component.common.knowledge.expression.ContextMap;
import org.switchyard.component.common.knowledge.expression.Expression;
import org.switchyard.component.common.knowledge.expression.ExpressionFactory;
import org.switchyard.component.common.knowledge.expression.ExpressionMapping;
import org.switchyard.component.common.knowledge.session.KnowledgeSession;

/**
 * Mapping functions.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class Mappings {

    private static final String EXCHANGE = "exchange";
    private static final String CONTEXT = "context";
    private static final String MESSAGE = "message";

    /** contentInput. */
    public static final String CONTENT_INPUT = "contentInput";
    /** contentOutput. */
    public static final String CONTENT_OUTPUT = "contentOutput";

    /**
     * Registers action mappings.
     * @param model the model
     * @param actions the actions
     */
    public static void registerActionMappings(KnowledgeComponentImplementationModel model, Map<String, KnowledgeAction> actions) {
        ActionsModel operationsModel = model.getActions();
        if (operationsModel != null) {
            for (ActionModel actionModel : operationsModel.getActions()) {
                String operation = actionModel.getOperation();
                KnowledgeAction action = new KnowledgeAction(actionModel.getId(), actionModel.getType());
                registerExpressionMappings(actionModel.getGlobals(), action.getGlobalExpressionMappings(), Scope.EXCHANGE);
                registerExpressionMappings(actionModel.getInputs(), action.getInputExpressionMappings(), Scope.IN);
                registerExpressionMappings(actionModel.getOutputs(), action.getOutputExpressionMappings(), Scope.OUT);
                actions.put(operation, action);
            }
        }
    }

    private static void registerExpressionMappings(MappingsModel mappingsModel, List<ExpressionMapping> expressionMappings, Scope defaultScope) {
        if (mappingsModel != null) {
            ExpressionFactory expressionFactory = ExpressionFactory.instance();
            for (MappingModel mappingModel : mappingsModel.getMappings()) {
                Expression expression = expressionFactory.create(mappingModel);
                Scope scope = mappingModel.getScope();
                if (scope == null) {
                    scope = defaultScope;
                }
                String variable = mappingModel.getVariable();
                expressionMappings.add(new ExpressionMapping(expression, scope, variable));
            }
        }
    }

    /**
     * Sets globals.
     * @param exchange the exchange
     * @param action the action
     * @param session the session
     * @param includeTrifecta include the Exchange, Context and Message
     */
    public static void setGlobals(Exchange exchange, KnowledgeAction action, KnowledgeSession session, boolean includeTrifecta) {
        Globals globals = session.getGlobals();
        if (includeTrifecta) {
            globals.set(EXCHANGE, exchange);
            globals.set(CONTEXT, exchange.getContext());
            globals.set(MESSAGE, exchange.getMessage());
        }
        Map<String, Object> map = getGlobalMap(exchange, action);
        for (Entry<String, Object> entry : map.entrySet()) {
            globals.set(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Gets a global map.
     * @param exchange the exchange
     * @param action the action
     * @return the global map
     */
    public static Map<String, Object> getGlobalMap(Exchange exchange, KnowledgeAction action) {
        return getMap(exchange, action.getGlobalExpressionMappings());
    }

    /**
     * Gets an input map.
     * @param exchange the exchange
     * @param action the action
     * @return the input map
     */
    public static Map<String, Object> getInputMap(Exchange exchange, KnowledgeAction action) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<ExpressionMapping> inputs = action.getInputExpressionMappings();
        if (inputs.size() > 0) {
            map.putAll(getMap(exchange, inputs));
        } else {
            Object content = exchange.getMessage().getContent();
            if (content != null) {
                map.put(CONTENT_INPUT, content);
            }
        }
        return map;
    }

    /**
     * Gets an output map.
     * @param exchange the exchange
     * @param action the action
     * @return the output map
     */
    public static Map<String, Object> getOutputMap(Exchange exchange, KnowledgeAction action) {
        return getOutputMap(exchange, action, null);
    }

    /**
     * Gets an output map.
     * @param exchange the exchange
     * @param action the action
     * @param contextOverrides any overrides
     * @return the output map
     */
    public static Map<String, Object> getOutputMap(Exchange exchange, KnowledgeAction action, Map<String, Object> contextOverrides) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<ExpressionMapping> outputs = action.getOutputExpressionMappings();
        if (outputs.size() > 0) {
            map.putAll(getMap(exchange, outputs, contextOverrides));
        } else {
            Object content = null;
            if (contextOverrides != null) {
                content = contextOverrides.get(CONTENT_OUTPUT);
            }
            if (content == null) {
                content = exchange.getMessage().getContent();
            }
            if (content != null) {
                map.put(CONTENT_OUTPUT, content);
            }
        }
        return map;
    }

    /**
     * Gets an input list.
     * @param exchange the exchange
     * @param action the action
     * @return the input list
     */
    public static List<Object> getInputList(Exchange exchange, KnowledgeAction action) {
        List<Object> list = new ArrayList<Object>();
        List<ExpressionMapping> inputs = action.getInputExpressionMappings();
        if (inputs.size() > 0) {
            list.addAll(getList(exchange, inputs));
        } else {
            Object content = exchange.getMessage().getContent();
            if (content != null) {
                list.add(content);
            }
        }
        return list;
    }

    /**
     * Gets an output list.
     * @param exchange the exchange
     * @param action the action
     * @return the output list
     */
    public static List<Object> getOutputList(Exchange exchange, KnowledgeAction action) {
        return getList(exchange, action.getOutputExpressionMappings());
    }

    /**
     * Gets an output.
     * @param exchange the exchange
     * @param action the action
     * @return the output
     */
    public static Object getOutput(Exchange exchange, KnowledgeAction action) {
        List<Object> list = getOutputList(exchange, action);
        switch (list.size()) {
            case 0:
                return exchange.getMessage().getContent();
            case 1:
                return list.get(0);
            default:
                return list;
        }
    }

    private static Map<String, Object> getMap(Exchange exchange, List<ExpressionMapping> mappings) {
        return getMap(exchange, mappings, null);
    }

    private static Map<String, Object> getMap(Exchange exchange, List<ExpressionMapping> mappings, Map<String, Object> contextOverrides) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, List<Object>> listMap = getListMap(exchange, mappings, false, toVariable(exchange), contextOverrides);
        for (Entry<String, List<Object>> entry : listMap.entrySet()) {
            List<Object> list = entry.getValue();
            map.put(entry.getKey(), list != null && list.size() > 0 ? list.get(0) : null);
        }
        return map;
    }

    private static List<Object> getList(Exchange exchange, List<ExpressionMapping> mappings) {
        List<Object> list = new ArrayList<Object>();
        if (mappings == null || mappings.size() == 0) {
            expand(exchange.getMessage().getContent(), list);
        } else {
            Map<String, List<Object>> listMap = getListMap(exchange, mappings, true, toVariable(exchange));
            for (List<Object> value : listMap.values()) {
                expand(value, list);
            }
        }
        return list;
    }

    /**
     * Gets a list map.
     * @param exchange the exchange
     * @param mappings the mappings
     * @param expand if to expand
     * @param undefinedVariable undefined variable name
     * @return the list map
     */
    public static Map<String, List<Object>> getListMap(Exchange exchange, List<ExpressionMapping> mappings, boolean expand, String undefinedVariable) {
        return getListMap(exchange, mappings, expand, undefinedVariable, null);
    }

    /**
     * Gets a list map.
     * @param exchange the exchange
     * @param mappings the mappings
     * @param expand if to expand
     * @param undefinedVariable undefined variable name
     * @param contextOverrides any overrides
     * @return the list map
     */
    public static Map<String, List<Object>> getListMap(Exchange exchange, List<ExpressionMapping> mappings, boolean expand, String undefinedVariable, Map<String, Object> contextOverrides) {
        Map<String, List<Object>> map = new HashMap<String, List<Object>>();
        if (mappings != null) {
            for (ExpressionMapping em : mappings) {
                String var = Strings.trimToNull(em.getVariable());
                if (var == null && undefinedVariable != null) {
                    var = undefinedVariable;
                }
                if (var != null) {
                    List<Object> list = map.get(var);
                    if (list == null) {
                        list = new ArrayList<Object>();
                        map.put(var, list);
                    }
                    Map<String, Object> ctx = new HashMap<String, Object>();
                    ctx.put(EXCHANGE, exchange);
                    ctx.put(CONTEXT, new ContextMap(exchange.getContext(), em.getScope()));
                    ctx.put(MESSAGE, exchange.getMessage());
                    if (contextOverrides != null) {
                        for (Entry<String, Object> contextOverride : contextOverrides.entrySet()) {
                            ctx.put(contextOverride.getKey(), contextOverride.getValue());
                        }
                    }
                    Object value = em.getExpression().evaluate(ctx);
                    if (expand) {
                        expand(value, list);
                    } else if (value != null) {
                        list.add(value);
                    }
                }
            }
        }
        return map;
    }

    private static void expand(Object value, List<Object> list) {
        if (value != null) {
            if (value instanceof Iterable) {
                for (Object o : (Iterable<?>)value) {
                    if (o != null) {
                        list.add(o);
                    }
                }
            } else {
                list.add(value);
            }
        }
    }

    /**
     * Converts an object a variable name.
     * @param object the object
     * @return the variable name
     */
    public static String toVariable(Object object) {
        return "$_" + System.identityHashCode(object);
    }

    private Mappings() {}

}
