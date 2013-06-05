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
package org.switchyard.component.bpm.config.model;

import static org.switchyard.component.bpm.config.model.BPMComponentImplementationModel.DEFAULT_NAMESPACE;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;

import org.jbpm.services.task.wih.AbstractHTWorkItemHandler;
import org.switchyard.common.lang.Strings;
import org.switchyard.common.type.classpath.ClasspathScanner;
import org.switchyard.common.type.classpath.IsAnnotationPresentFilter;
import org.switchyard.component.bpm.BPMActionType;
import org.switchyard.component.bpm.annotation.AbortProcessInstance;
import org.switchyard.component.bpm.annotation.BPM;
import org.switchyard.component.bpm.annotation.SignalEvent;
import org.switchyard.component.bpm.annotation.StartProcess;
import org.switchyard.component.bpm.annotation.UserGroupCallback;
import org.switchyard.component.bpm.annotation.WorkItemHandler;
import org.switchyard.component.bpm.config.model.v1.V1BPMActionModel;
import org.switchyard.component.bpm.config.model.v1.V1BPMComponentImplementationModel;
import org.switchyard.component.bpm.config.model.v1.V1UserGroupCallbackModel;
import org.switchyard.component.bpm.config.model.v1.V1WorkItemHandlerModel;
import org.switchyard.component.bpm.config.model.v1.V1WorkItemHandlersModel;
import org.switchyard.component.bpm.service.SwitchYardServiceTaskHandler;
import org.switchyard.component.bpm.util.WorkItemHandlers;
import org.switchyard.component.common.knowledge.annotation.Global;
import org.switchyard.component.common.knowledge.annotation.Input;
import org.switchyard.component.common.knowledge.annotation.Output;
import org.switchyard.component.common.knowledge.config.model.ActionModel;
import org.switchyard.component.common.knowledge.config.model.ActionsModel;
import org.switchyard.component.common.knowledge.config.model.KnowledgeSwitchYardScanner;
import org.switchyard.component.common.knowledge.config.model.v1.V1ActionsModel;
import org.switchyard.config.model.ScannerInput;
import org.switchyard.config.model.ScannerOutput;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.InterfaceModel;
import org.switchyard.config.model.composite.v1.V1ComponentModel;
import org.switchyard.config.model.composite.v1.V1ComponentServiceModel;
import org.switchyard.config.model.composite.v1.V1CompositeModel;
import org.switchyard.config.model.composite.v1.V1InterfaceModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.switchyard.v1.V1SwitchYardModel;
import org.switchyard.metadata.ServiceOperation;
import org.switchyard.metadata.java.JavaService;

/**
 * BPMSwitchYardScanner.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class BPMSwitchYardScanner extends KnowledgeSwitchYardScanner {

    private static final IsAnnotationPresentFilter START_PROCESS_FILTER = new IsAnnotationPresentFilter(StartProcess.class);
    private static final IsAnnotationPresentFilter SIGNAL_EVENT_FILTER = new IsAnnotationPresentFilter(SignalEvent.class);
    private static final IsAnnotationPresentFilter ABORT_PROCESS_INSTANCE_FILTER = new IsAnnotationPresentFilter(AbortProcessInstance.class);

    private final IsAnnotationPresentFilter _bpmFilter = new IsAnnotationPresentFilter(BPM.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public ScannerOutput<SwitchYardModel> scan(ScannerInput<SwitchYardModel> input) throws IOException {
        SwitchYardModel switchyardModel = new V1SwitchYardModel();
        CompositeModel compositeModel = new V1CompositeModel();
        compositeModel.setName(input.getName());
        ClasspathScanner bpmScanner = new ClasspathScanner(_bpmFilter);
        for (URL url : input.getURLs()) {
            bpmScanner.scan(url);
        }
        List<Class<?>> bpmClasses = _bpmFilter.getMatchedTypes();
        for (Class<?> bpmClass : bpmClasses) {
            compositeModel.addComponent(scan(bpmClass));
        }
        if (!compositeModel.getModelChildren().isEmpty()) {
            switchyardModel.setComposite(compositeModel);
        }
        return new ScannerOutput<SwitchYardModel>().setModel(switchyardModel);
    }

    /**
     * Scans a class.
     * @param bpmClass the class
     * @return the component model
     * @throws IOException oops
     */
    public ComponentModel scan(Class<?> bpmClass) throws IOException {
        BPM bpm = bpmClass.getAnnotation(BPM.class);
        if (bpm == null) {
            throw new IOException(bpmClass.getName() + " is missing the @BPM annotation.");
        }
        Class<?> bpmInterface = bpm.value();
        if (BPM.UndefinedBPMInterface.class.equals(bpmInterface)) {
            bpmInterface = bpmClass;
        }
        if (!bpmInterface.isInterface()) {
            throw new IOException(bpmInterface.getName() +  " is a class. @BPM only allowed on interfaces.");
        }
        String bpmName = Strings.trimToNull(bpm.name());
        if (bpmName == null) {
            bpmName = bpmInterface.getSimpleName();
        }
        ComponentModel componentModel = new V1ComponentModel();
        componentModel.setName(bpmName);
        BPMComponentImplementationModel componentImplementationModel = new V1BPMComponentImplementationModel();
        boolean persistent = bpm.persistent();
        if (persistent) {
            componentImplementationModel.setPersistent(persistent);
        }
        String processId = bpm.processId();
        if (UNDEFINED.equals(processId)) {
            processId = bpmName;
        }
        componentImplementationModel.setProcessId(processId);
        ActionsModel actionsModel = new V1ActionsModel(DEFAULT_NAMESPACE);
        JavaService javaService = JavaService.fromClass(bpmInterface);
        for (Method method : bpmClass.getDeclaredMethods()) {
            BPMActionType actionType = null;
            String eventId = null;
            Global[] globalMappingAnnotations = null;
            Input[] inputMappingAnnotations = null;
            Output[] outputMappingAnnotations = null;
            if (START_PROCESS_FILTER.matches(method)) {
                actionType = BPMActionType.START_PROCESS;
                StartProcess startProcessAnnotation = method.getAnnotation(StartProcess.class);
                globalMappingAnnotations = startProcessAnnotation.globals();
                inputMappingAnnotations = startProcessAnnotation.inputs();
                outputMappingAnnotations = startProcessAnnotation.outputs();
            } else if (SIGNAL_EVENT_FILTER.matches(method)) {
                actionType = BPMActionType.SIGNAL_EVENT;
                SignalEvent signalEventAnnotation = method.getAnnotation(SignalEvent.class);
                eventId = Strings.trimToNull(signalEventAnnotation.eventId());
                globalMappingAnnotations = signalEventAnnotation.globals();
                inputMappingAnnotations = signalEventAnnotation.inputs();
                outputMappingAnnotations = signalEventAnnotation.outputs();
            } else if (ABORT_PROCESS_INSTANCE_FILTER.matches(method)) {
                actionType = BPMActionType.ABORT_PROCESS_INSTANCE;
                AbortProcessInstance abortProcessInstanceAnnotation = method.getAnnotation(AbortProcessInstance.class);
                globalMappingAnnotations = new Global[]{};
                inputMappingAnnotations = new Input[]{};
                outputMappingAnnotations = abortProcessInstanceAnnotation.outputs();
            }
            if (actionType != null) {
                ServiceOperation serviceOperation = javaService.getOperation(method.getName());
                if (serviceOperation != null) {
                    ActionModel actionModel = new V1BPMActionModel();
                    actionModel.setEventId(eventId);
                    actionModel.setOperation(serviceOperation.getName());
                    actionModel.setType(actionType);
                    actionModel.setGlobals(toGlobalsModel(globalMappingAnnotations, DEFAULT_NAMESPACE));
                    actionModel.setInputs(toInputsModel(inputMappingAnnotations, DEFAULT_NAMESPACE));
                    actionModel.setOutputs(toOutputsModel(outputMappingAnnotations, DEFAULT_NAMESPACE));
                    actionsModel.addAction(actionModel);
                }
            }
        }
        if (!actionsModel.getActions().isEmpty()) {
            componentImplementationModel.setActions(actionsModel);
        }
        componentImplementationModel.setChannels(toChannelsModel(bpm.channels(), DEFAULT_NAMESPACE, componentModel));
        componentImplementationModel.setListeners(toListenersModel(bpm.listeners(), DEFAULT_NAMESPACE));
        componentImplementationModel.setLoggers(toLoggersModel(bpm.loggers(), DEFAULT_NAMESPACE));
        componentImplementationModel.setManifest(toManifestModel(bpm.manifest(), DEFAULT_NAMESPACE));
        componentImplementationModel.setProperties(toPropertiesModel(bpm.properties(), DEFAULT_NAMESPACE));
        componentImplementationModel.setUserGroupCallback(toUserGroupCallbackModel(bpm.userGroupCallback()));
        componentImplementationModel.setWorkItemHandlers(toWorkItemHandlersModel(bpm.workItemHandlers()));
        componentModel.setImplementation(componentImplementationModel);
        ComponentServiceModel componentServiceModel = new V1ComponentServiceModel();
        InterfaceModel interfaceModel = new V1InterfaceModel(InterfaceModel.JAVA);
        interfaceModel.setInterface(bpmInterface.getName());
        componentServiceModel.setInterface(interfaceModel);
        componentServiceModel.setName(bpmName);
        componentModel.addService(componentServiceModel);
        return componentModel;
    }

    private UserGroupCallbackModel toUserGroupCallbackModel(UserGroupCallback[] userGroupCallbackAnnotations) {
        if (userGroupCallbackAnnotations == null || userGroupCallbackAnnotations.length == 0) {
            return null;
        }
        UserGroupCallbackModel userGroupCallbackModel = null;
        for (UserGroupCallback userGroupCallbackAnnotation : userGroupCallbackAnnotations) {
            userGroupCallbackModel = new V1UserGroupCallbackModel();
            Class<? extends org.kie.internal.task.api.UserGroupCallback> clazz = userGroupCallbackAnnotation.value();
            userGroupCallbackModel.setClazz(clazz);
            userGroupCallbackModel.setProperties(toPropertiesModel(userGroupCallbackAnnotation.properties(), DEFAULT_NAMESPACE));
            break;
        }
        return userGroupCallbackModel;
    }

    private WorkItemHandlersModel toWorkItemHandlersModel(WorkItemHandler[] workItemHandlerAnnotations) {
        if (workItemHandlerAnnotations == null || workItemHandlerAnnotations.length == 0) {
            return null;
        }
        WorkItemHandlersModel workItemHandlersModel = new V1WorkItemHandlersModel();
        for (WorkItemHandler workItemHandlerAnnotation : workItemHandlerAnnotations) {
            WorkItemHandlerModel workItemHandlerModel = new V1WorkItemHandlerModel();
            Class<? extends org.kie.api.runtime.process.WorkItemHandler> clazz = workItemHandlerAnnotation.value();
            workItemHandlerModel.setClazz(clazz);
            String name = workItemHandlerAnnotation.name();
            if (UNDEFINED.equals(name)) {
                org.kie.api.runtime.process.WorkItemHandler wih = WorkItemHandlers.newWorkItemHandler(clazz, null, null);
                if (wih instanceof SwitchYardServiceTaskHandler) {
                    SwitchYardServiceTaskHandler ssth = (SwitchYardServiceTaskHandler)wih;
                    if (ssth.getName() != null) {
                        name = ssth.getName();
                    }
                } else if (wih instanceof AbstractHTWorkItemHandler) {
                    name = "Human Task";
                }
            }
            if (UNDEFINED.equals(name)) {
                name = clazz.getSimpleName();
            }
            workItemHandlerModel.setName(name);
            workItemHandlersModel.addWorkItemHandler(workItemHandlerModel);
        }
        return workItemHandlersModel;
    }

}
