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
package org.switchyard.component.bpm.util;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jbpm.services.task.wih.AbstractHTWorkItemHandler;
import org.jbpm.services.task.wih.ExternalTaskEventListener;
import org.jbpm.services.task.wih.LocalHTWorkItemHandler;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessRuntime;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.internal.runtime.KnowledgeRuntime;
import org.kie.internal.runtime.manager.Disposable;
import org.kie.internal.runtime.manager.DisposeListener;
import org.kie.internal.task.api.EventService;
import org.switchyard.ServiceDomain;
import org.switchyard.SwitchYardException;
import org.switchyard.common.type.reflect.Access;
import org.switchyard.common.type.reflect.Construction;
import org.switchyard.common.type.reflect.MethodAccess;
import org.switchyard.component.bpm.config.model.BPMComponentImplementationModel;
import org.switchyard.component.bpm.config.model.WorkItemHandlerModel;
import org.switchyard.component.bpm.config.model.WorkItemHandlersModel;
import org.switchyard.component.bpm.runtime.BPMRuntimeManager;
import org.switchyard.component.bpm.service.StandardSwitchYardServiceTaskHandler;
import org.switchyard.component.bpm.service.SwitchYardServiceTaskHandler;
import org.switchyard.component.common.knowledge.service.SwitchYardServiceInvoker;

/**
 * WorkItemHandler functions.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class WorkItemHandlers {

    private static final String HUMAN_TASK = "Human Task";

    private static final Class<?>[][] PARAMETER_TYPES = new Class<?>[][]{
        new Class<?>[]{ProcessRuntime.class},
        new Class<?>[]{KieRuntime.class},
        new Class<?>[]{KnowledgeRuntime.class},
        new Class<?>[0]
    };

    private static final Map<String, Class<? extends WorkItemHandler>> DEFAULT_HANDLERS = new HashMap<String, Class<? extends WorkItemHandler>>();
    static {
        DEFAULT_HANDLERS.put(SwitchYardServiceTaskHandler.SWITCHYARD_SERVICE_TASK, SwitchYardServiceTaskHandler.class);
        DEFAULT_HANDLERS.put(StandardSwitchYardServiceTaskHandler.SERVICE_TASK, StandardSwitchYardServiceTaskHandler.class);
    }

    /**
     * Registers work item handlers.
     * @param model the model
     * @param loader the class loader
     * @param processRuntime the process runtime
     * @param runtimeManager the runtime manager
     * @param serviceDomain the service domain
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void registerWorkItemHandlers(BPMComponentImplementationModel model, ClassLoader loader, ProcessRuntime processRuntime, BPMRuntimeManager runtimeManager, ServiceDomain serviceDomain) {
        String tns = model.getComponent().getTargetNamespace();
        Set<String> registeredNames = new HashSet<String>();
        WorkItemHandlersModel workItemHandlersModel = model.getWorkItemHandlers();
        if (workItemHandlersModel != null) {
            for (WorkItemHandlerModel workItemHandlerModel : workItemHandlersModel.getWorkItemHandlers()) {
                Class<? extends WorkItemHandler> workItemHandlerClass = (Class<? extends WorkItemHandler>)workItemHandlerModel.getClazz(loader);
                if (workItemHandlerClass == null) {
                    throw new SwitchYardException("Could not load workItemHandler class: " + workItemHandlerModel.getModelConfiguration().getAttribute("class"));
                }
                WorkItemHandler workItemHandler = newWorkItemHandler(workItemHandlerClass, processRuntime, runtimeManager);
                String name = workItemHandlerModel.getName();
                if (workItemHandler instanceof SwitchYardServiceTaskHandler) {
                    SwitchYardServiceTaskHandler ssth = (SwitchYardServiceTaskHandler)workItemHandler;
                    if (name != null) {
                        ssth.setName(name);
                    } else {
                        name = ssth.getName();
                    }
                    ssth.setComponentName(model.getComponent().getQName());
                    ssth.setInvoker(new SwitchYardServiceInvoker(serviceDomain, tns));
                    ssth.setProcessRuntime(processRuntime);
                }
                if (name == null && workItemHandler instanceof AbstractHTWorkItemHandler) {
                    name = HUMAN_TASK;
                }
                if (name == null) {
                    throw new SwitchYardException("Could not use null name to register workItemHandler: " + workItemHandler.getClass().getName());
                }
                processRuntime.getWorkItemManager().registerWorkItemHandler(name, workItemHandler);
                registeredNames.add(name);
            }
        }
        for (Entry<String, Class<? extends WorkItemHandler>> entry : DEFAULT_HANDLERS.entrySet()) {
            String name = entry.getKey();
            if (!registeredNames.contains(name)) {
                WorkItemHandler defaultHandler = newWorkItemHandler(entry.getValue(), processRuntime, runtimeManager);
                if (defaultHandler instanceof SwitchYardServiceTaskHandler) {
                    SwitchYardServiceTaskHandler ssth = (SwitchYardServiceTaskHandler)defaultHandler;
                    ssth.setName(name);
                    ssth.setComponentName(model.getComponent().getQName());
                    ssth.setInvoker(new SwitchYardServiceInvoker(serviceDomain, tns));
                    ssth.setProcessRuntime(processRuntime);
                }
                processRuntime.getWorkItemManager().registerWorkItemHandler(name, defaultHandler);
                registeredNames.add(name);
            }
        }
        // TODO: this whole block can be removed after SWITCHYARD-1584
        if (!registeredNames.contains(HUMAN_TASK) && runtimeManager != null) {
            RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine();
            ExternalTaskEventListener listener = new ExternalTaskEventListener();
            listener.setRuntimeManager(runtimeManager);
            LocalHTWorkItemHandler htwih = new LocalHTWorkItemHandler();
            htwih.setRuntimeManager(runtimeManager);
            if (runtimeEngine.getTaskService() instanceof EventService) {
                ((EventService)runtimeEngine.getTaskService()).registerTaskLifecycleEventListener(listener);
            }
            if (processRuntime instanceof Disposable) {
                ((Disposable)processRuntime).addDisposeListener(new DisposeListener() {
                    @Override
                    public void onDispose(RuntimeEngine re) {
                        if (re.getTaskService() instanceof EventService) {
                            ((EventService)re.getTaskService()).clearTaskLifecycleEventListeners();
                            ((EventService)re.getTaskService()).clearTasknotificationEventListeners();
                        }
                    }
                });
            }
            processRuntime.getWorkItemManager().registerWorkItemHandler(HUMAN_TASK, htwih);
            registeredNames.add(HUMAN_TASK);
        }
    }

    /**
     * Creates a new work item handler.
     * @param workItemHandlerClass the class
     * @param processRuntime the process runtime
     * @param runtimeManager the runtime manager
     * @return the work item handler
     */
    public static WorkItemHandler newWorkItemHandler(Class<? extends WorkItemHandler> workItemHandlerClass, ProcessRuntime processRuntime, BPMRuntimeManager runtimeManager) {
        WorkItemHandler workItemHandler = null;
        Constructor<? extends WorkItemHandler> constructor = getConstructor(workItemHandlerClass);
        Class<?>[] parameterTypes = constructor != null ? constructor.getParameterTypes() : new Class<?>[0];
        try {
            if (parameterTypes.length == 0) {
                workItemHandler = Construction.construct(workItemHandlerClass);
            } else if (parameterTypes.length == 1) {
                workItemHandler = Construction.construct(workItemHandlerClass, parameterTypes, new Object[]{processRuntime});
            }
        } catch (Throwable t) {
            throw new SwitchYardException("Could not instantiate workItemHandler class: " + workItemHandlerClass.getName());
        }
        if (workItemHandler != null && runtimeManager != null) {
            Access<RuntimeManager> access = new MethodAccess<RuntimeManager>(workItemHandler.getClass(), "getRuntimeManager", "setRuntimeManager");
            if (access.isWriteable()) {
                access.write(workItemHandler, runtimeManager);
            }
        }
        return workItemHandler;
    }

    private static Constructor<? extends WorkItemHandler> getConstructor(Class<? extends WorkItemHandler> workItemHandlerClass) {
        Constructor<? extends WorkItemHandler> constructor = null;
        for (Class<?>[] parameterTypes : PARAMETER_TYPES) {
            try {
                constructor = workItemHandlerClass.getConstructor(parameterTypes);
                if (constructor != null) {
                    break;
                }
            } catch (Throwable t) {
                // keep checkstyle happy ("at least one statement")
                t.getMessage();
            }
        }
        return constructor;
    }

    private WorkItemHandlers() {}

}
