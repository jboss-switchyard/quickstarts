/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.component.common.knowledge.config.builder;

import static org.switchyard.component.common.knowledge.service.StandardSwitchYardServiceTaskHandler.SERVICE_TASK;
import static org.switchyard.component.common.knowledge.service.SwitchYardServiceTaskHandler.SWITCHYARD_SERVICE_TASK;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.namespace.QName;

import org.jbpm.services.task.wih.AbstractHTWorkItemHandler;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessRuntime;
import org.kie.api.runtime.process.WorkItemHandler;
import org.switchyard.ServiceDomain;
import org.switchyard.common.type.reflect.Access;
import org.switchyard.common.type.reflect.Construction;
import org.switchyard.common.type.reflect.MethodAccess;
import org.switchyard.component.common.knowledge.CommonKnowledgeMessages;
import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;
import org.switchyard.component.common.knowledge.config.model.WorkItemHandlerModel;
import org.switchyard.component.common.knowledge.config.model.WorkItemHandlersModel;
import org.switchyard.component.common.knowledge.service.StandardSwitchYardServiceTaskHandler;
import org.switchyard.component.common.knowledge.service.SwitchYardServiceInvoker;
import org.switchyard.component.common.knowledge.service.SwitchYardServiceTaskHandler;
import org.switchyard.config.model.composite.ComponentModel;

/**
 * WorkItemHandlerBuilder.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class WorkItemHandlerBuilder extends KnowledgeBuilder {

    private static final String HUMAN_TASK = "Human Task";

    private static final Class<?>[][] PARAMETER_TYPES = new Class<?>[][]{
        new Class<?>[]{ProcessRuntime.class},
        new Class<?>[]{KieRuntime.class},
        new Class<?>[0]
    };

    private static final Map<String, Class<? extends WorkItemHandler>> DEFAULT_HANDLERS = new HashMap<String, Class<? extends WorkItemHandler>>();
    static {
        DEFAULT_HANDLERS.put(SWITCHYARD_SERVICE_TASK, SwitchYardServiceTaskHandler.class);
        DEFAULT_HANDLERS.put(SERVICE_TASK, StandardSwitchYardServiceTaskHandler.class);
    }

    private Class<? extends WorkItemHandler> _workItemHandlerClass;
    private String _workItemHandlerName;
    private QName _componentName;
    private String _targetNamespace;

    /**
     * Creates a WorkItemHandlerBuilder.
     * @param classLoader classLoader
     * @param serviceDomain serviceDomain
     * @param workItemHandlerModel workItemHandlerModel
     */
    @SuppressWarnings("unchecked")
    public WorkItemHandlerBuilder(ClassLoader classLoader, ServiceDomain serviceDomain, WorkItemHandlerModel workItemHandlerModel) {
        super(classLoader, serviceDomain);
        if (workItemHandlerModel != null) {
            WorkItemHandlersModel workItemHandlersModel = (WorkItemHandlersModel)workItemHandlerModel.getModelParent();
            KnowledgeComponentImplementationModel implementationModel = (KnowledgeComponentImplementationModel)workItemHandlersModel.getModelParent();
            ComponentModel componentModel = implementationModel.getComponent();
            _componentName =  componentModel.getQName();
            _targetNamespace =  componentModel.getTargetNamespace();
            _workItemHandlerClass = (Class<? extends WorkItemHandler>)workItemHandlerModel.getClazz(getClassLoader());
            if (_workItemHandlerClass == null) {
                throw CommonKnowledgeMessages.MESSAGES.couldNotLoadWorkItemHandlerClass(workItemHandlerModel.getModelConfiguration().getAttribute("class"));
            }
            _workItemHandlerName = workItemHandlerModel.getName();
            if (_workItemHandlerName == null) {
                if (StandardSwitchYardServiceTaskHandler.class.isAssignableFrom(_workItemHandlerClass)) {
                    _workItemHandlerName = SERVICE_TASK;
                } else if (SwitchYardServiceTaskHandler.class.isAssignableFrom(_workItemHandlerClass)) {
                    _workItemHandlerName = SWITCHYARD_SERVICE_TASK;
                } else if (AbstractHTWorkItemHandler.class.isAssignableFrom(_workItemHandlerClass)) {
                    _workItemHandlerName = HUMAN_TASK;
                } else {
                    throw CommonKnowledgeMessages.MESSAGES.couldNotUseNullNameToRegisterWorkItemHandler(_workItemHandlerClass.getName());
                }
            }
        }
    }

    private WorkItemHandlerBuilder(
            ClassLoader classLoader,
            ServiceDomain serviceDomain,
            KnowledgeComponentImplementationModel implementationModel,
            Class<? extends WorkItemHandler> workItemHandlerClass,
            String workItemHandlerName) {
        super(classLoader, serviceDomain);
        _workItemHandlerClass = workItemHandlerClass;
        _workItemHandlerName = workItemHandlerName;
        ComponentModel componentModel = implementationModel.getComponent();
        _componentName =  componentModel.getQName();
        _targetNamespace =  componentModel.getTargetNamespace();
    }

    /**
     * Gets the workItemHandlerName.
     * @return the workItemHandlerName
     */
    public String getWorkItemHandlerName() {
        return _workItemHandlerName;
    }

    /**
     * Builds a WorkItemHandler.
     * @param processRuntime processRuntime
     * @param runtimeManager runtimeManager
     * @return a WorkItemHandler
     */
    public WorkItemHandler build(ProcessRuntime processRuntime, RuntimeManager runtimeManager) {
        WorkItemHandler workItemHandler = construct(processRuntime, runtimeManager);
        if (workItemHandler instanceof SwitchYardServiceTaskHandler) {
            SwitchYardServiceTaskHandler systh = (SwitchYardServiceTaskHandler)workItemHandler;
            systh.setComponentName(_componentName);
            systh.setInvoker(new SwitchYardServiceInvoker(getServiceDomain(), _targetNamespace));
            systh.setProcessRuntime(processRuntime);
        }
        return workItemHandler;
    }

    private WorkItemHandler construct(ProcessRuntime processRuntime, RuntimeManager runtimeManager) {
        WorkItemHandler handler = null;
        if (_workItemHandlerClass != null) {
            Constructor<? extends WorkItemHandler> constructor = null;
            for (Class<?>[] parameterTypes : PARAMETER_TYPES) {
                try {
                    constructor = _workItemHandlerClass.getConstructor(parameterTypes);
                    if (constructor != null) {
                        break;
                    }
                } catch (Throwable t) {
                    // keep checkstyle happy ("at least one statement")
                    t.getMessage();
                }
            }
            Class<?>[] parameterTypes = constructor != null ? constructor.getParameterTypes() : new Class<?>[0];
            try {
                if (parameterTypes.length == 0) {
                    handler = Construction.construct(_workItemHandlerClass);
                } else if (parameterTypes.length == 1) {
                    handler = Construction.construct(_workItemHandlerClass, parameterTypes, new Object[]{processRuntime});
                }
            } catch (Throwable t) {
                throw CommonKnowledgeMessages.MESSAGES.couldNotInstantiateWorkItemHandlerClass(_workItemHandlerClass.getName());
            }
            if (handler != null && runtimeManager != null) {
                Access<RuntimeManager> access = new MethodAccess<RuntimeManager>(handler.getClass(), "getRuntimeManager", "setRuntimeManager");
                if (access.isWriteable()) {
                    access.write(handler, runtimeManager);
                }
            }
        }
        return handler;
    }

    /**
     * Creates WorkItemHandlerBuilders.
     * @param classLoader classLoader
     * @param serviceDomain serviceDomain
     * @param implementationModel implementationModel
     * @return WorkItemHandlerBuilders
     */
    public static List<WorkItemHandlerBuilder> builders(ClassLoader classLoader, ServiceDomain serviceDomain, KnowledgeComponentImplementationModel implementationModel) {
        List<WorkItemHandlerBuilder> builders = new ArrayList<WorkItemHandlerBuilder>();
        Set<String> registeredNames = new HashSet<String>();
        if (implementationModel != null) {
            WorkItemHandlersModel workItemHandlersModel = implementationModel.getWorkItemHandlers();
            if (workItemHandlersModel != null) {
                for (WorkItemHandlerModel workItemHandlerModel : workItemHandlersModel.getWorkItemHandlers()) {
                    if (workItemHandlerModel != null) {
                        WorkItemHandlerBuilder builder = new WorkItemHandlerBuilder(classLoader, serviceDomain, workItemHandlerModel);
                        builders.add(builder);
                        String name = builder.getWorkItemHandlerName();
                        if (name != null) {
                            registeredNames.add(name);
                        }
                    }
                }
            }
            // add our default WorkItemHandlers if they haven't already been added
            for (Entry<String, Class<? extends WorkItemHandler>> entry : DEFAULT_HANDLERS.entrySet()) {
                String name = entry.getKey();
                if (!registeredNames.contains(name)) {
                    WorkItemHandlerBuilder builder = new WorkItemHandlerBuilder(
                            classLoader, serviceDomain, implementationModel, entry.getValue(), name);
                    builders.add(builder);
                    registeredNames.add(name);
                }
            }
        }
        return builders;
    }

}
