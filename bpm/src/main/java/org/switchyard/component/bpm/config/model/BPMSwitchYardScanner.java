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
import static org.switchyard.component.common.knowledge.config.model.MappingsModel.GLOBALS;
import static org.switchyard.component.common.knowledge.config.model.MappingsModel.INPUTS;
import static org.switchyard.component.common.knowledge.config.model.MappingsModel.OUTPUTS;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;

import org.jbpm.process.workitem.wsht.AbstractHTWorkItemHandler;
import org.switchyard.common.lang.Strings;
import org.switchyard.common.type.classpath.ClasspathScanner;
import org.switchyard.common.type.classpath.IsAnnotationPresentFilter;
import org.switchyard.component.bpm.BPMActionType;
import org.switchyard.component.bpm.annotation.AbortProcessInstance;
import org.switchyard.component.bpm.annotation.BPM;
import org.switchyard.component.bpm.annotation.SignalEvent;
import org.switchyard.component.bpm.annotation.StartProcess;
import org.switchyard.component.bpm.annotation.WorkItemHandler;
import org.switchyard.component.bpm.config.model.v1.V1BPMActionModel;
import org.switchyard.component.bpm.config.model.v1.V1BPMComponentImplementationModel;
import org.switchyard.component.bpm.config.model.v1.V1WorkItemHandlerModel;
import org.switchyard.component.bpm.config.model.v1.V1WorkItemHandlersModel;
import org.switchyard.component.bpm.work.SwitchYardWorkItemHandler;
import org.switchyard.component.bpm.work.WorkItemHandlers;
import org.switchyard.component.common.knowledge.annotation.Mapping;
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
        int sessionId = bpm.sessionId();
        if (sessionId > -1) {
            componentImplementationModel.setSessionId(sessionId);
        }
        ActionsModel actionsModel = new V1ActionsModel(DEFAULT_NAMESPACE);
        JavaService javaService = JavaService.fromClass(bpmInterface);
        for (Method method : bpmClass.getDeclaredMethods()) {
            BPMActionType actionType = null;
            String id = null;
            Mapping[] globalMappingAnnotations = null;
            Mapping[] inputMappingAnnotations = null;
            Mapping[] outputMappingAnnotations = null;
            if (START_PROCESS_FILTER.matches(method)) {
                actionType = BPMActionType.START_PROCESS;
                StartProcess startProcessAnnotation = method.getAnnotation(StartProcess.class);
                globalMappingAnnotations = startProcessAnnotation.globals();
                inputMappingAnnotations = startProcessAnnotation.inputs();
                outputMappingAnnotations = startProcessAnnotation.outputs();
            } else if (SIGNAL_EVENT_FILTER.matches(method)) {
                actionType = BPMActionType.SIGNAL_EVENT;
                SignalEvent signalEventAnnotation = method.getAnnotation(SignalEvent.class);
                globalMappingAnnotations = signalEventAnnotation.globals();
                inputMappingAnnotations = signalEventAnnotation.inputs();
                outputMappingAnnotations = signalEventAnnotation.outputs();
                id = Strings.trimToNull(signalEventAnnotation.id());
            } else if (ABORT_PROCESS_INSTANCE_FILTER.matches(method)) {
                actionType = BPMActionType.ABORT_PROCESS_INSTANCE;
                AbortProcessInstance abortProcessInstanceAnnotation = method.getAnnotation(AbortProcessInstance.class);
                globalMappingAnnotations = abortProcessInstanceAnnotation.globals();
                inputMappingAnnotations = abortProcessInstanceAnnotation.inputs();
                outputMappingAnnotations = abortProcessInstanceAnnotation.outputs();
            }
            if (actionType != null) {
                ServiceOperation serviceOperation = javaService.getOperation(method.getName());
                if (serviceOperation != null) {
                    ActionModel actionModel = new V1BPMActionModel();
                    actionModel.setId(id);
                    actionModel.setOperation(serviceOperation.getName());
                    actionModel.setType(actionType);
                    actionModel.setGlobals(toMappingsModel(globalMappingAnnotations, DEFAULT_NAMESPACE, GLOBALS));
                    actionModel.setInputs(toMappingsModel(inputMappingAnnotations, DEFAULT_NAMESPACE, INPUTS));
                    actionModel.setOutputs(toMappingsModel(outputMappingAnnotations, DEFAULT_NAMESPACE, OUTPUTS));
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

    private WorkItemHandlersModel toWorkItemHandlersModel(WorkItemHandler[] workItemHandlerAnnotations) {
        if (workItemHandlerAnnotations == null || workItemHandlerAnnotations.length == 0) {
            return null;
        }
        WorkItemHandlersModel workItemHandlersModel = new V1WorkItemHandlersModel();
        for (WorkItemHandler workItemHandlerAnnotation : workItemHandlerAnnotations) {
            WorkItemHandlerModel workItemHandlerModel = new V1WorkItemHandlerModel();
            Class<? extends org.kie.runtime.process.WorkItemHandler> clazz = workItemHandlerAnnotation.value();
            workItemHandlerModel.setClazz(clazz);
            String name = workItemHandlerAnnotation.name();
            if (UNDEFINED.equals(name)) {
                org.kie.runtime.process.WorkItemHandler wih = WorkItemHandlers.newWorkItemHandler(clazz, null);
                if (wih instanceof SwitchYardWorkItemHandler) {
                    SwitchYardWorkItemHandler sywih = (SwitchYardWorkItemHandler)wih;
                    if (sywih.getName() != null) {
                        name = sywih.getName();
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
