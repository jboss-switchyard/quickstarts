/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;

import org.switchyard.common.io.resource.Resource;
import org.switchyard.common.io.resource.ResourceType;
import org.switchyard.common.io.resource.SimpleResource;
import org.switchyard.common.type.classpath.ClasspathScanner;
import org.switchyard.common.type.classpath.IsAnnotationPresentFilter;
import org.switchyard.common.type.reflect.Construction;
import org.switchyard.component.bpm.AbortProcessInstance;
import org.switchyard.component.bpm.Process;
import org.switchyard.component.bpm.SignalEvent;
import org.switchyard.component.bpm.StartProcess;
import org.switchyard.component.bpm.common.ProcessActionType;
import org.switchyard.component.bpm.config.model.v1.V1BpmComponentImplementationModel;
import org.switchyard.component.bpm.config.model.v1.V1ProcessActionModel;
import org.switchyard.component.bpm.config.model.v1.V1TaskHandlerModel;
import org.switchyard.component.bpm.task.SwitchYardServiceTaskHandler;
import org.switchyard.component.bpm.task.TaskHandler;
import org.switchyard.config.model.Scanner;
import org.switchyard.config.model.ScannerInput;
import org.switchyard.config.model.ScannerOutput;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.JavaComponentServiceInterfaceModel;
import org.switchyard.config.model.composite.v1.V1ComponentModel;
import org.switchyard.config.model.composite.v1.V1ComponentServiceModel;
import org.switchyard.config.model.composite.v1.V1CompositeModel;
import org.switchyard.config.model.composite.v1.V1JavaComponentServiceInterfaceModel;
import org.switchyard.config.model.resource.v1.V1ResourceModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.switchyard.v1.V1SwitchYardModel;
import org.switchyard.metadata.ServiceOperation;
import org.switchyard.metadata.java.JavaService;

/**
 * A SwitchYardScanner which scans for @Process, @StartProcess, @SignalEvent and @AbortProcessInstance annotations.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class BpmSwitchYardScanner implements Scanner<SwitchYardModel> {

    private static final IsAnnotationPresentFilter PROCESS_FILTER = new IsAnnotationPresentFilter(Process.class);
    private static final IsAnnotationPresentFilter START_PROCESS_FILTER = new IsAnnotationPresentFilter(StartProcess.class);
    private static final IsAnnotationPresentFilter SIGNAL_EVENT_FILTER = new IsAnnotationPresentFilter(SignalEvent.class);
    private static final IsAnnotationPresentFilter ABORT_PROCESS_INSTANCE_FILTER = new IsAnnotationPresentFilter(AbortProcessInstance.class);

    private static final String INTERFACE_ERR_MSG = " is a class. @Process only allowed on interfaces.";
    private static final String FILTER_ERR_MSG = " is in error. @StartProcess, @SignalEvent and @AbortProcessInstance cannot co-exist on the same method.";

    /**
     * {@inheritDoc}
     */
    @Override
    public ScannerOutput<SwitchYardModel> scan(ScannerInput<SwitchYardModel> input) throws IOException {
        SwitchYardModel switchyardModel = new V1SwitchYardModel();
        CompositeModel compositeModel = new V1CompositeModel();
        compositeModel.setName(input.getName());
        switchyardModel.setComposite(compositeModel);
        ClasspathScanner processScanner = new ClasspathScanner(PROCESS_FILTER);
        for (URL url : input.getURLs()) {
            processScanner.scan(url);
        }
        List<Class<?>> processClasses = PROCESS_FILTER.getMatchedTypes();
        for (Class<?> processClass : processClasses) {
            Process process = processClass.getAnnotation(Process.class);
            Class<?> processInterface = process.value();
            if (Process.UndefinedProcessInterface.class.equals(processInterface)) {
                processInterface = processClass;
            }
            if (!processInterface.isInterface()) {
                throw new IOException(processInterface.getName() + INTERFACE_ERR_MSG);
            }
            String processName = processInterface.getSimpleName();
            JavaComponentServiceInterfaceModel csiModel = new V1JavaComponentServiceInterfaceModel();
            csiModel.setInterface(processInterface.getName());
            ComponentServiceModel serviceModel = new V1ComponentServiceModel();
            serviceModel.setInterface(csiModel);
            serviceModel.setName(processName);
            ComponentModel componentModel = new V1ComponentModel();
            componentModel.setName(processName);
            componentModel.addService(serviceModel);
            compositeModel.addComponent(componentModel);
            BpmComponentImplementationModel bciModel = new V1BpmComponentImplementationModel();
            String processDefinition = process.definition();
            if (Process.UNDEFINED_PROCESS_DEFINITION.equals(processDefinition)) {
                processDefinition = "META-INF/" + processName + ".bpmn";
            }
            bciModel.setProcessDefinition(new SimpleResource(processDefinition, process.definitionType()));
            String processId = process.id();
            if (Process.UNDEFINED_PROCESS_ID.equals(processId)) {
                processId = processName;
            }
            bciModel.setProcessId(processId);
            JavaService javaService = JavaService.fromClass(processInterface);
            for (Method method : processClass.getDeclaredMethods()) {
                ProcessActionType pat = null;
                String eventType = null;
                if (START_PROCESS_FILTER.matches(method)) {
                    if (SIGNAL_EVENT_FILTER.matches(method) || ABORT_PROCESS_INSTANCE_FILTER.matches(method)) {
                        throw new IOException(method.getName() + " " + FILTER_ERR_MSG);
                    }
                    pat = ProcessActionType.START_PROCESS;
                } else if (SIGNAL_EVENT_FILTER.matches(method)) {
                    if (ABORT_PROCESS_INSTANCE_FILTER.matches(method)) {
                        throw new IOException(method.getName() + " " + FILTER_ERR_MSG);
                    }
                    pat = ProcessActionType.SIGNAL_EVENT;
                    eventType = method.getAnnotation(SignalEvent.class).value();
                    if (SignalEvent.UNDEFINED_EVENT_TYPE.equals(eventType)) {
                        eventType = method.getName();
                    }
                } else if (ABORT_PROCESS_INSTANCE_FILTER.matches(method)) {
                    pat = ProcessActionType.ABORT_PROCESS_INSTANCE;
                }
                if (pat != null) {
                    ServiceOperation srvOper = javaService.getOperation(method.getName());
                    if (srvOper != null) {
                        ProcessActionModel pam = new V1ProcessActionModel().setName(srvOper.getName()).setType(pat);
                        if (eventType != null) {
                            pam.setEventType(eventType);
                        }
                        bciModel.addProcessAction(pam);
                    }
                }
            }
            bciModel.addTaskHandler(new V1TaskHandlerModel().setClazz(SwitchYardServiceTaskHandler.class).setName(SwitchYardServiceTaskHandler.SWITCHYARD_SERVICE));
            for (Class<? extends TaskHandler> taskHandlerClass : process.taskHandlers()) {
                if (Process.UndefinedTaskHandler.class.equals(taskHandlerClass) || SwitchYardServiceTaskHandler.class.equals(taskHandlerClass)) {
                    continue;
                }
                TaskHandler taskHandler = Construction.construct(taskHandlerClass);
                bciModel.addTaskHandler(new V1TaskHandlerModel().setClazz(taskHandlerClass).setName(taskHandler.getName()));
            }
            for (Class<? extends Resource> resourceClass : process.resources()) {
                if (Process.UndefinedResource.class.equals(resourceClass)) {
                    continue;
                }
                Resource resource = Construction.construct(resourceClass);
                String location = resource.getLocation();
                ResourceType type = resource.getType();
                bciModel.addResource(new V1ResourceModel().setLocation(location).setType(type));
            }
            componentModel.setImplementation(bciModel);
        }
        return new ScannerOutput<SwitchYardModel>().setModel(switchyardModel);
    }

}
