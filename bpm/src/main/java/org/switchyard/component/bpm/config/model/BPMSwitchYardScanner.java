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
package org.switchyard.component.bpm.config.model;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;

import org.jbpm.services.task.wih.AbstractHTWorkItemHandler;
import org.switchyard.common.lang.Strings;
import org.switchyard.common.type.classpath.ClasspathScanner;
import org.switchyard.common.type.classpath.IsAnnotationPresentFilter;
import org.switchyard.component.bpm.BPMMessages;
import org.switchyard.component.bpm.BPMOperationType;
import org.switchyard.component.bpm.annotation.AbortProcessInstance;
import org.switchyard.component.bpm.annotation.BPM;
import org.switchyard.component.bpm.annotation.SignalEvent;
import org.switchyard.component.bpm.annotation.SignalEventAll;
import org.switchyard.component.bpm.annotation.StartProcess;
import org.switchyard.component.bpm.annotation.UserGroupCallback;
import org.switchyard.component.bpm.annotation.WorkItemHandler;
import org.switchyard.component.bpm.config.model.v1.V1BPMComponentImplementationModel;
import org.switchyard.component.bpm.config.model.v1.V1BPMOperationModel;
import org.switchyard.component.bpm.config.model.v1.V1UserGroupCallbackModel;
import org.switchyard.component.bpm.config.model.v1.V1WorkItemHandlerModel;
import org.switchyard.component.bpm.config.model.v1.V1WorkItemHandlersModel;
import org.switchyard.component.bpm.service.SwitchYardServiceTaskHandler;
import org.switchyard.component.bpm.util.WorkItemHandlers;
import org.switchyard.component.common.knowledge.annotation.Fault;
import org.switchyard.component.common.knowledge.annotation.Global;
import org.switchyard.component.common.knowledge.annotation.Input;
import org.switchyard.component.common.knowledge.annotation.Output;
import org.switchyard.component.common.knowledge.config.model.KnowledgeSwitchYardScanner;
import org.switchyard.component.common.knowledge.config.model.OperationModel;
import org.switchyard.component.common.knowledge.config.model.OperationsModel;
import org.switchyard.component.common.knowledge.config.model.v1.V1OperationsModel;
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
import org.switchyard.config.model.switchyard.SwitchYardNamespace;
import org.switchyard.config.model.switchyard.v1.V1SwitchYardModel;
import org.switchyard.extensions.java.JavaService;
import org.switchyard.metadata.ServiceOperation;

/**
 * BPMSwitchYardScanner.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class BPMSwitchYardScanner extends KnowledgeSwitchYardScanner {

    private static final IsAnnotationPresentFilter START_PROCESS_FILTER = new IsAnnotationPresentFilter(StartProcess.class);
    private static final IsAnnotationPresentFilter SIGNAL_EVENT_FILTER = new IsAnnotationPresentFilter(SignalEvent.class);
    private static final IsAnnotationPresentFilter SIGNAL_EVENT_ALL_FILTER = new IsAnnotationPresentFilter(SignalEventAll.class);
    private static final IsAnnotationPresentFilter ABORT_PROCESS_INSTANCE_FILTER = new IsAnnotationPresentFilter(AbortProcessInstance.class);

    private final IsAnnotationPresentFilter _bpmFilter = new IsAnnotationPresentFilter(BPM.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public ScannerOutput<SwitchYardModel> scan(ScannerInput<SwitchYardModel> input) throws IOException {
        SwitchYardNamespace switchyardNamespace = input.getSwitchyardNamespace();
        SwitchYardModel switchyardModel = new V1SwitchYardModel(switchyardNamespace.uri());
        CompositeModel compositeModel = new V1CompositeModel();
        compositeModel.setName(input.getCompositeName());
        ClasspathScanner bpmScanner = new ClasspathScanner(_bpmFilter);
        for (URL url : input.getURLs()) {
            bpmScanner.scan(url);
        }
        List<Class<?>> bpmClasses = _bpmFilter.getMatchedTypes();
        for (Class<?> bpmClass : bpmClasses) {
            compositeModel.addComponent(scan(bpmClass, switchyardNamespace));
        }
        if (!compositeModel.getModelChildren().isEmpty()) {
            switchyardModel.setComposite(compositeModel);
        }
        return new ScannerOutput<SwitchYardModel>().setModel(switchyardModel);
    }

    /**
     * Scans a class using the default switchyard namespace.
     * @param bpmClass the class
     * @return the component model
     * @throws IOException oops
     */
    public ComponentModel scan(Class<?> bpmClass) throws IOException {
        return scan(bpmClass, null);
    }

    /**
     * Scans a class using the specified switchyard namespace.
     * @param bpmClass the class
     * @param switchyardNamespace the switchyard namespace
     * @return the component model
     * @throws IOException oops
     */
    public ComponentModel scan(Class<?> bpmClass, SwitchYardNamespace switchyardNamespace) throws IOException {
        if (switchyardNamespace == null) {
            switchyardNamespace = SwitchYardNamespace.DEFAULT;
        }
        BPM bpm = bpmClass.getAnnotation(BPM.class);
        if (bpm == null) {
            throw BPMMessages.MESSAGES.bpmClassGetNameIsMissingTheBPMAnnotation(bpmClass.getName());
        }
        Class<?> bpmInterface = bpm.value();
        if (BPM.UndefinedBPMInterface.class.equals(bpmInterface)) {
            bpmInterface = bpmClass;
        }
        if (!bpmInterface.isInterface()) {
            throw BPMMessages.MESSAGES.bpmInterfaceGetNameIsAClassBPMOnlyAllowedOnInterfaces(bpmInterface.getName());
        }
        String bpmName = Strings.trimToNull(bpm.name());
        if (bpmName == null) {
            bpmName = bpmInterface.getSimpleName();
        }
        ComponentModel componentModel = new V1ComponentModel();
        componentModel.setName(bpmName);
        BPMNamespace bpmNamespace = BPMNamespace.fromUri(bpm.namespace());
        if (bpmNamespace == null) {
            bpmNamespace = BPMNamespace.DEFAULT;
            for (BPMNamespace value : BPMNamespace.values()) {
                if (value.versionMatches(switchyardNamespace)) {
                    bpmNamespace = value;
                    break;
                }
            }
        }
        BPMComponentImplementationModel componentImplementationModel = new V1BPMComponentImplementationModel(bpmNamespace.uri());
        boolean persistent = bpm.persistent();
        if (persistent) {
            componentImplementationModel.setPersistent(persistent);
        }
        String processId = bpm.processId();
        if (UNDEFINED.equals(processId)) {
            processId = bpmName;
        }
        componentImplementationModel.setProcessId(processId);
        OperationsModel operationsModel = new V1OperationsModel(bpmNamespace.uri());
        JavaService javaService = JavaService.fromClass(bpmInterface);
        for (Method method : bpmClass.getDeclaredMethods()) {
            BPMOperationType operationType = null;
            String eventId = null;
            Global[] globalMappingAnnotations = null;
            Input[] inputMappingAnnotations = null;
            Output[] outputMappingAnnotations = null;
            Fault[] faultMappingAnnotations = null;
            if (START_PROCESS_FILTER.matches(method)) {
                operationType = BPMOperationType.START_PROCESS;
                StartProcess startProcessAnnotation = method.getAnnotation(StartProcess.class);
                globalMappingAnnotations = startProcessAnnotation.globals();
                inputMappingAnnotations = startProcessAnnotation.inputs();
                outputMappingAnnotations = startProcessAnnotation.outputs();
                faultMappingAnnotations = startProcessAnnotation.faults();
            } else if (SIGNAL_EVENT_FILTER.matches(method)) {
                operationType = BPMOperationType.SIGNAL_EVENT;
                SignalEvent signalEventAnnotation = method.getAnnotation(SignalEvent.class);
                eventId = Strings.trimToNull(signalEventAnnotation.eventId());
                globalMappingAnnotations = signalEventAnnotation.globals();
                inputMappingAnnotations = signalEventAnnotation.inputs();
                outputMappingAnnotations = signalEventAnnotation.outputs();
                faultMappingAnnotations = signalEventAnnotation.faults();
            } else if (SIGNAL_EVENT_ALL_FILTER.matches(method)) {
                operationType = BPMOperationType.SIGNAL_EVENT_ALL;
                SignalEventAll signalEventAllAnnotation = method.getAnnotation(SignalEventAll.class);
                eventId = Strings.trimToNull(signalEventAllAnnotation.eventId());
                globalMappingAnnotations = signalEventAllAnnotation.globals();
                inputMappingAnnotations = signalEventAllAnnotation.inputs();
                outputMappingAnnotations = signalEventAllAnnotation.outputs();
                faultMappingAnnotations = signalEventAllAnnotation.faults();
            } else if (ABORT_PROCESS_INSTANCE_FILTER.matches(method)) {
                operationType = BPMOperationType.ABORT_PROCESS_INSTANCE;
                AbortProcessInstance abortProcessInstanceAnnotation = method.getAnnotation(AbortProcessInstance.class);
                globalMappingAnnotations = new Global[]{};
                inputMappingAnnotations = new Input[]{};
                outputMappingAnnotations = abortProcessInstanceAnnotation.outputs();
                faultMappingAnnotations = abortProcessInstanceAnnotation.faults();
            }
            if (operationType != null) {
                ServiceOperation serviceOperation = javaService.getOperation(method.getName());
                if (serviceOperation != null) {
                    OperationModel operationModel = new V1BPMOperationModel(bpmNamespace.uri());
                    operationModel.setEventId(eventId);
                    operationModel.setName(serviceOperation.getName());
                    operationModel.setType(operationType);
                    operationModel.setGlobals(toGlobalsModel(globalMappingAnnotations, bpmNamespace));
                    operationModel.setInputs(toInputsModel(inputMappingAnnotations, bpmNamespace));
                    operationModel.setOutputs(toOutputsModel(outputMappingAnnotations, bpmNamespace));
                    operationModel.setFaults(toFaultsModel(faultMappingAnnotations, bpmNamespace));
                    operationsModel.addOperation(operationModel);
                }
            }
        }
        if (!operationsModel.getOperations().isEmpty()) {
            componentImplementationModel.setOperations(operationsModel);
        }
        componentImplementationModel.setChannels(toChannelsModel(bpm.channels(), bpmNamespace, componentModel, switchyardNamespace));
        componentImplementationModel.setListeners(toListenersModel(bpm.listeners(), bpmNamespace));
        componentImplementationModel.setLoggers(toLoggersModel(bpm.loggers(), bpmNamespace));
        componentImplementationModel.setManifest(toManifestModel(bpm.manifest(), bpmNamespace));
        componentImplementationModel.setProperties(toPropertiesModel(bpm.properties(), bpmNamespace));
        componentImplementationModel.setUserGroupCallback(toUserGroupCallbackModel(bpm.userGroupCallback(), bpmNamespace));
        componentImplementationModel.setWorkItemHandlers(toWorkItemHandlersModel(bpm.workItemHandlers(), bpmNamespace));
        componentModel.setImplementation(componentImplementationModel);
        ComponentServiceModel componentServiceModel = new V1ComponentServiceModel(switchyardNamespace.uri());
        InterfaceModel interfaceModel = new V1InterfaceModel(InterfaceModel.JAVA);
        interfaceModel.setInterface(bpmInterface.getName());
        componentServiceModel.setInterface(interfaceModel);
        componentServiceModel.setName(bpmName);
        componentModel.addService(componentServiceModel);
        return componentModel;
    }

    private UserGroupCallbackModel toUserGroupCallbackModel(UserGroupCallback[] userGroupCallbackAnnotations, BPMNamespace bpmNamespace) {
        if (userGroupCallbackAnnotations == null || userGroupCallbackAnnotations.length == 0) {
            return null;
        }
        UserGroupCallbackModel userGroupCallbackModel = null;
        for (UserGroupCallback userGroupCallbackAnnotation : userGroupCallbackAnnotations) {
            userGroupCallbackModel = new V1UserGroupCallbackModel(bpmNamespace.uri());
            // SWITCHYARD-1755: internal api usage still required (public APIs insufficient)
            Class<? extends org.kie.internal.task.api.UserGroupCallback> clazz = userGroupCallbackAnnotation.value();
            userGroupCallbackModel.setClazz(clazz);
            userGroupCallbackModel.setProperties(toPropertiesModel(userGroupCallbackAnnotation.properties(), bpmNamespace));
            break;
        }
        return userGroupCallbackModel;
    }

    private WorkItemHandlersModel toWorkItemHandlersModel(WorkItemHandler[] workItemHandlerAnnotations, BPMNamespace bpmNamespace) {
        if (workItemHandlerAnnotations == null || workItemHandlerAnnotations.length == 0) {
            return null;
        }
        WorkItemHandlersModel workItemHandlersModel = new V1WorkItemHandlersModel(bpmNamespace.uri());
        for (WorkItemHandler workItemHandlerAnnotation : workItemHandlerAnnotations) {
            WorkItemHandlerModel workItemHandlerModel = new V1WorkItemHandlerModel(bpmNamespace.uri());
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
