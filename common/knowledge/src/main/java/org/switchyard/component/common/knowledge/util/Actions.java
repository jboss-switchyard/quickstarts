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

import static org.switchyard.component.common.knowledge.KnowledgeConstants.CONTEXT;
import static org.switchyard.component.common.knowledge.KnowledgeConstants.DEFAULT;
import static org.switchyard.component.common.knowledge.KnowledgeConstants.MESSAGE;
import static org.switchyard.component.common.knowledge.KnowledgeConstants.PARAMETER;
import static org.switchyard.component.common.knowledge.KnowledgeConstants.RESULT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.kie.runtime.Globals;
import org.switchyard.Message;
import org.switchyard.Scope;
import org.switchyard.common.lang.Strings;
import org.switchyard.component.common.knowledge.ActionType;
import org.switchyard.component.common.knowledge.config.model.ActionModel;
import org.switchyard.component.common.knowledge.config.model.ActionsModel;
import org.switchyard.component.common.knowledge.config.model.GlobalModel;
import org.switchyard.component.common.knowledge.config.model.GlobalsModel;
import org.switchyard.component.common.knowledge.config.model.InputModel;
import org.switchyard.component.common.knowledge.config.model.InputsModel;
import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;
import org.switchyard.component.common.knowledge.config.model.OutputModel;
import org.switchyard.component.common.knowledge.config.model.OutputsModel;
import org.switchyard.component.common.knowledge.exchange.KnowledgeAction;
import org.switchyard.component.common.knowledge.expression.ContextMap;
import org.switchyard.component.common.knowledge.expression.Expression;
import org.switchyard.component.common.knowledge.expression.ExpressionFactory;
import org.switchyard.component.common.knowledge.expression.ExpressionMapping;
import org.switchyard.component.common.knowledge.session.KnowledgeSession;
import org.switchyard.exception.SwitchYardException;

/**
 * Action functions.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class Actions {

    /**
     * Registers actions.
     * @param model the model
     * @param actions the actions
     * @param defaultAction the default action
     */
    public static void registerActions(KnowledgeComponentImplementationModel model, Map<String, KnowledgeAction> actions, KnowledgeAction defaultAction) {
        ActionsModel actionsModel = model.getActions();
        if (actionsModel != null) {
            for (ActionModel actionModel : actionsModel.getActions()) {
                String operation = Strings.trimToNull(actionModel.getOperation());
                if (operation == null) {
                    operation = DEFAULT;
                }
                ActionType type = actionModel.getType();
                if (type == null) {
                    type = defaultAction.getType();
                }
                String eventId = actionModel.getEventId();
                if (eventId == null) {
                    eventId = defaultAction.getEventId();
                }
                KnowledgeAction action = new KnowledgeAction(type, eventId);
                mapExpressions(actionModel, action);
                if (actions.containsKey(operation)) {
                    throw new SwitchYardException(String.format("cannot register %s action due to duplicate operation: %s", type, operation));
                }
                actions.put(operation, action);
            }
        }
        if (!actions.containsKey(DEFAULT)) {
            actions.put(DEFAULT, defaultAction);
        }
    }

    private static void mapExpressions(ActionModel actionModel, KnowledgeAction action) {
        GlobalsModel globalsModel = actionModel.getGlobals();
        if (globalsModel != null) {
            for (GlobalModel globalModel : globalsModel.getGlobals()) {
                action.getGlobalExpressionMappings().add(new ExpressionMapping(globalModel));
            }
        }
        InputsModel inputsModel = actionModel.getInputs();
        if (inputsModel != null) {
            for (InputModel inputModel : inputsModel.getInputs()) {
                action.getInputExpressionMappings().add(new ExpressionMapping(inputModel));
            }
        }
        OutputsModel outputsModel = actionModel.getOutputs();
        if (outputsModel != null) {
            for (OutputModel outputModel : outputsModel.getOutputs()) {
                action.getOutputExpressionMappings().add(new ExpressionMapping(outputModel));
            }
        }
    }

    /**
     * Sets the globals.
     * @param message the message
     * @param action the action
     * @param session the session
     */
    public static void setGlobals(Message message, KnowledgeAction action, KnowledgeSession session) {
        Globals globals = session.getGlobals();
        Map<String, Object> map = getMap(message, action.getGlobalExpressionMappings(), null);
        for (Entry<String, Object> entry : map.entrySet()) {
            globals.set(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Gets the input.
     * @param message the message
     * @param action the action
     * @return the input
     */
    public static Object getInput(Message message, KnowledgeAction action) {
        List<Object> list = getList(message, action.getInputExpressionMappings());
        switch (list.size()) {
            case 0:
                return message.getContent();
            case 1:
                return list.get(0);
            default:
                return list;
        }
    }

    /**
     * Gets an input list.
     * @param message the message
     * @param action the action
     * @return the input list
     */
    public static List<Object> getInputList(Message message, KnowledgeAction action) {
        List<Object> list = new ArrayList<Object>();
        List<ExpressionMapping> inputs = action.getInputExpressionMappings();
        if (inputs.size() > 0) {
            list.addAll(getList(message, inputs));
        } else {
            expand(message.getContent(), list);
        }
        return list;
    }

    /**
     * Gets an input map.
     * @param message the message
     * @param action the action
     * @return the input map
     */
    public static Map<String, Object> getInputMap(Message message, KnowledgeAction action) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<ExpressionMapping> inputs = action.getInputExpressionMappings();
        if (inputs.size() > 0) {
            map.putAll(getMap(message, inputs, null));
        } else {
            Object content = message.getContent();
            if (content != null) {
                map.put(PARAMETER, content);
            }
        }
        return map;
    }

    /**
     * Gets an output.
     * @param message the message
     * @param action the action
     * @return the output
     */
    public static Object getOutput(Message message, KnowledgeAction action) {
        List<Object> list = getList(message, action.getOutputExpressionMappings());
        switch (list.size()) {
            case 0:
                return null;
            case 1:
                return list.get(0);
            default:
                return list;
        }
    }

    /**
     * Sets the outputs.
     * @param message the message
     * @param action the action
     * @param contextOverrides the context overrides
     */
    public static void setOutputs(Message message, KnowledgeAction action, Map<String, Object> contextOverrides) {
        Map<String, List<ExpressionMapping>> toListMap = new HashMap<String, List<ExpressionMapping>>();
        for (ExpressionMapping expressionMapping : action.getOutputExpressionMappings()) {
            String to = expressionMapping.getTo();
            if (to != null) {
                List<ExpressionMapping> toList = toListMap.get(to);
                if (toList == null) {
                    toList = new ArrayList<ExpressionMapping>();
                    toListMap.put(to, toList);
                }
                toList.add(expressionMapping);
            }
        }
        if (toListMap.size() == 0) {
            Object output = contextOverrides.get(RESULT);
            if (output != null) {
                message.setContent(output);
            }
        } else {
            for (Entry<String, List<ExpressionMapping>> toListEntry : toListMap.entrySet()) {
                List<Object> from_list = new ArrayList<Object>();
                ExpressionMapping to_em = null;
                for (ExpressionMapping from_em : toListEntry.getValue()) {
                    if (to_em == null) {
                        to_em = from_em;
                    }
                    Object from_value = run(message, from_em.getFromExpression(), contextOverrides);
                    if (from_value != null) {
                        from_list.add(from_value);
                    }
                }
                final Object output;
                switch (from_list.size()) {
                    case 0:
                        output = null;
                        break;
                    case 1:
                        output = from_list.get(0);
                        break;
                    default:
                        output = from_list;
                        break;
                }
                String output_var = toVariable(output);
                contextOverrides.put(output_var, output);
                String output_to = to_em.getTo() + " = " + output_var;
                Expression output_to_expr = ExpressionFactory.INSTANCE.create(output_to, null, to_em.getPropertyResolver());
                run(message, output_to_expr, contextOverrides);
            }
        }
    }

    private static List<Object> getList(Message message, List<ExpressionMapping> expressionMappings) {
        List<Object> list = new ArrayList<Object>();
        if (expressionMappings == null || expressionMappings.size() == 0) {
            expand(message.getContent(), list);
        } else {
            Map<String, List<Object>> listMap = getListMap(message, expressionMappings, true, toVariable(message));
            for (List<Object> value : listMap.values()) {
                expand(value, list);
            }
        }
        return list;
    }

    private static Map<String, Object> getMap(Message message, List<ExpressionMapping> expressionMappings, Map<String, Object> contextOverrides) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, List<Object>> listMap = getListMap(message, expressionMappings, false, toVariable(message), contextOverrides);
        for (Entry<String, List<Object>> entry : listMap.entrySet()) {
            List<Object> list = entry.getValue();
            map.put(entry.getKey(), list != null && list.size() > 0 ? list.get(0) : null);
        }
        return map;
    }

    /**
     * Gets a list map.
     * @param message the message
     * @param expressionMappings the expression mappings
     * @param expand whether to expand
     * @param undefinedVariable the undefined variable name
     * @return the list map
     */
    public static Map<String, List<Object>> getListMap(Message message, List<ExpressionMapping> expressionMappings, boolean expand, String undefinedVariable) {
        return getListMap(message, expressionMappings, expand, undefinedVariable, null);
    }

    private static Map<String, List<Object>> getListMap(Message message, List<ExpressionMapping> expressionMappings, boolean expand, String undefinedVariable, Map<String, Object> contextOverrides) {
        Map<String, List<Object>> map = new HashMap<String, List<Object>>();
        if (expressionMappings != null) {
            for (ExpressionMapping em : expressionMappings) {
                String variable = em.getTo();
                if (variable == null && undefinedVariable != null) {
                    variable = undefinedVariable;
                }
                if (variable != null) {
                    List<Object> list = map.get(variable);
                    if (list == null) {
                        list = new ArrayList<Object>();
                        map.put(variable, list);
                    }
                    Object value = run(message, em.getFromExpression(), contextOverrides);
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

    private static Object run(Message message, Expression expression, Map<String, Object> contextOverrides) {
        Map<String, Object> expressionContext = new HashMap<String, Object>();
        expressionContext.put(CONTEXT, new ContextMap(message.getContext(), Scope.MESSAGE));
        expressionContext.put(MESSAGE, message);
        if (contextOverrides != null) {
            for (Entry<String, Object> contextOverride : contextOverrides.entrySet()) {
                expressionContext.put(contextOverride.getKey(), contextOverride.getValue());
            }
        }
        return expression.run(expressionContext);
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
     * Converts an object to a variable name.
     * @param object the object
     * @return the variable name
     */
    public static String toVariable(Object object) {
        return ("_var" + System.identityHashCode(object)).replaceFirst("-", "_");
    }

    private Actions() {}

}
