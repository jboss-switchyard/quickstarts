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
package org.switchyard.component.common.knowledge.util;

import static org.switchyard.component.common.knowledge.KnowledgeConstants.CONTEXT;
import static org.switchyard.component.common.knowledge.KnowledgeConstants.DEFAULT;
import static org.switchyard.component.common.knowledge.KnowledgeConstants.FAULT;
import static org.switchyard.component.common.knowledge.KnowledgeConstants.GLOBALS;
import static org.switchyard.component.common.knowledge.KnowledgeConstants.MESSAGE;
import static org.switchyard.component.common.knowledge.KnowledgeConstants.PARAMETER;
import static org.switchyard.component.common.knowledge.KnowledgeConstants.RESULT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.api.runtime.Globals;
import org.switchyard.Message;
import org.switchyard.Scope;
import org.switchyard.common.lang.Strings;
import org.switchyard.component.common.knowledge.CommonKnowledgeMessages;
import org.switchyard.component.common.knowledge.OperationType;
import org.switchyard.component.common.knowledge.config.model.FaultModel;
import org.switchyard.component.common.knowledge.config.model.FaultsModel;
import org.switchyard.component.common.knowledge.config.model.GlobalModel;
import org.switchyard.component.common.knowledge.config.model.GlobalsModel;
import org.switchyard.component.common.knowledge.config.model.InputModel;
import org.switchyard.component.common.knowledge.config.model.InputsModel;
import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;
import org.switchyard.component.common.knowledge.config.model.OperationModel;
import org.switchyard.component.common.knowledge.config.model.OperationsModel;
import org.switchyard.component.common.knowledge.config.model.OutputModel;
import org.switchyard.component.common.knowledge.config.model.OutputsModel;
import org.switchyard.component.common.knowledge.exchange.KnowledgeOperation;
import org.switchyard.component.common.knowledge.expression.ContextMap;
import org.switchyard.component.common.knowledge.expression.Expression;
import org.switchyard.component.common.knowledge.expression.ExpressionFactory;
import org.switchyard.component.common.knowledge.expression.ExpressionMapping;
import org.switchyard.component.common.knowledge.session.KnowledgeSession;

/**
 * Operation functions.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class Operations {

    /**
     * Registers operations.
     * @param model the model
     * @param operations the operations
     * @param defaultOperation the default operation
     */
    public static void registerOperations(KnowledgeComponentImplementationModel model, Map<String, KnowledgeOperation> operations, KnowledgeOperation defaultOperation) {
        OperationsModel operationsModel = model.getOperations();
        if (operationsModel != null) {
            for (OperationModel operationModel : operationsModel.getOperations()) {
                String name = Strings.trimToNull(operationModel.getName());
                if (name == null) {
                    name = DEFAULT;
                }
                OperationType type = operationModel.getType();
                if (type == null) {
                    type = defaultOperation.getType();
                }
                String eventId = operationModel.getEventId();
                if (eventId == null) {
                    eventId = defaultOperation.getEventId();
                }
                KnowledgeOperation operation = new KnowledgeOperation(type, eventId);
                mapExpressions(operationModel, operation);
                if (operations.containsKey(name)) {
                    throw CommonKnowledgeMessages.MESSAGES.cannotRegisterOperation(type.toString(), name);
                }
                operations.put(name, operation);
            }
        }
        if (!operations.containsKey(DEFAULT)) {
            operations.put(DEFAULT, defaultOperation);
        }
    }

    private static void mapExpressions(OperationModel operationModel, KnowledgeOperation operation) {
        GlobalsModel globalsModel = operationModel.getGlobals();
        if (globalsModel != null) {
            for (GlobalModel globalModel : globalsModel.getGlobals()) {
                operation.getGlobalExpressionMappings().add(new ExpressionMapping(globalModel));
            }
        }
        InputsModel inputsModel = operationModel.getInputs();
        if (inputsModel != null) {
            for (InputModel inputModel : inputsModel.getInputs()) {
                operation.getInputExpressionMappings().add(new ExpressionMapping(inputModel));
            }
        }
        OutputsModel outputsModel = operationModel.getOutputs();
        if (outputsModel != null) {
            for (OutputModel outputModel : outputsModel.getOutputs()) {
                operation.getOutputExpressionMappings().add(new ExpressionMapping(outputModel));
            }
        }
        FaultsModel faultsModel = operationModel.getFaults();
        if (faultsModel != null) {
            for (FaultModel faultModel : faultsModel.getFaults()) {
                operation.getFaultExpressionMappings().add(new ExpressionMapping(faultModel));
            }
        }
    }

    /**
     * Sets the globals.
     * @param message the message
     * @param operation the operation
     * @param session the session
     */
    public static void setGlobals(Message message, KnowledgeOperation operation, KnowledgeSession session) {
        Globals globals = session.getGlobals();
        Map<String, Object> globalsMap = new HashMap<String, Object>();
        globalsMap.put(GLOBALS, new ConcurrentHashMap<String, Object>());
        Map<String, Object> expressionMap = getMap(message, operation.getGlobalExpressionMappings(), null);
        if (expressionMap != null) {
            globalsMap.putAll(expressionMap);
        }
        for (Entry<String, Object> globalsEntry : globalsMap.entrySet()) {
            globals.set(globalsEntry.getKey(), globalsEntry.getValue());
        }
    }

    /**
     * Gets the input.
     * @param message the message
     * @param operation the operation
     * @return the input
     */
    public static Object getInput(Message message, KnowledgeOperation operation) {
        List<Object> list = getList(message, operation.getInputExpressionMappings());
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
     * @param operation the operation
     * @return the input list
     */
    public static List<Object> getInputList(Message message, KnowledgeOperation operation) {
        List<Object> list = new ArrayList<Object>();
        List<ExpressionMapping> inputs = operation.getInputExpressionMappings();
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
     * @param operation the operation
     * @return the input map
     */
    public static Map<String, Object> getInputMap(Message message, KnowledgeOperation operation) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<ExpressionMapping> inputs = operation.getInputExpressionMappings();
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
     * Sets the outputs.
     * @param message the message
     * @param operation the operation
     * @param contextOverrides the context overrides
     */
    public static void setOutputs(Message message, KnowledgeOperation operation, Map<String, Object> contextOverrides) {
        setOutputsOrFaults(message, operation.getOutputExpressionMappings(), contextOverrides, RESULT);
    }

    /**
     * Sets the faults.
     * @param message the message
     * @param operation the operation
     * @param contextOverrides the context overrides
     */
    public static void setFaults(Message message, KnowledgeOperation operation, Map<String, Object> contextOverrides) {
        setOutputsOrFaults(message, operation.getFaultExpressionMappings(), contextOverrides, FAULT);
    }

    private static void setOutputsOrFaults(Message message, List<ExpressionMapping> expressionMappings, Map<String, Object> expressionContext, String defaultReturnVariable) {
        Map<String, List<ExpressionMapping>> toListMap = new HashMap<String, List<ExpressionMapping>>();
        for (ExpressionMapping expressionMapping : expressionMappings) {
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
            Object output = getValue(expressionContext, defaultReturnVariable);
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
                    Object from_value = run(message, from_em.getFromExpression(), expressionContext);
                    if (from_value != null) {
                        from_list.add(from_value);
                    } else {
                        from_value = getValue(expressionContext, from_em.getFrom());
                        if (from_value != null) {
                            from_list.add(from_value);
                        }
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
                expressionContext.put(output_var, output);
                String output_to = to_em.getTo() + " = " + output_var;
                Expression output_to_expr = ExpressionFactory.INSTANCE.create(output_to, null, to_em.getPropertyResolver());
                run(message, output_to_expr, expressionContext);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static Object getValue(Map<String, Object> expressionContext, String name) {
        Object output = null;
        if (expressionContext != null) {
            output = expressionContext.get(name);
            if (output == null) {
                Object globals = expressionContext.get(GLOBALS);
                if (globals instanceof Map) {
                    output = ((Map<String, Object>)globals).get(name);
                }
            }
        }
        return output;
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

    private static Map<String, List<Object>> getListMap(Message message, List<ExpressionMapping> expressionMappings, boolean expand, String undefinedVariable, Map<String, Object> expressionContext) {
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
                    Object value = run(message, em.getFromExpression(), expressionContext);
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

    private static Object run(Message message, Expression expression, Map<String, Object> expressionContext) {
        Map<String, Object> context = new HashMap<String, Object>();
        if (expressionContext != null) {
            context.putAll(expressionContext);
        }
        // these always take precedence!
        context.put(CONTEXT, new ContextMap(message.getContext(), Scope.MESSAGE));
        context.put(MESSAGE, message);
        return expression.run(context);
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

    private Operations() {}

}
