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

import org.switchyard.common.io.resource.ResourceType;
import org.switchyard.common.io.resource.SimpleResource;
import org.switchyard.common.lang.Strings;
import org.switchyard.common.type.classpath.ClasspathScanner;
import org.switchyard.common.type.classpath.IsAnnotationPresentFilter;
import org.switchyard.common.type.reflect.Construction;
import org.switchyard.component.bpm.AbortProcessInstance;
import org.switchyard.component.bpm.Process;
import org.switchyard.component.bpm.ProcessActionType;
import org.switchyard.component.bpm.SignalEvent;
import org.switchyard.component.bpm.StartProcess;
import org.switchyard.component.bpm.config.model.v1.V1BPMComponentImplementationModel;
import org.switchyard.component.bpm.config.model.v1.V1ProcessActionModel;
import org.switchyard.component.bpm.config.model.v1.V1TaskHandlerModel;
import org.switchyard.component.bpm.task.work.SwitchYardServiceTaskHandler;
import org.switchyard.component.bpm.task.work.TaskHandler;
import org.switchyard.component.common.rules.Audit;
import org.switchyard.component.common.rules.config.model.AuditModel;
import org.switchyard.component.common.rules.config.model.v1.V1AuditModel;
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
public class BPMSwitchYardScanner implements Scanner<SwitchYardModel> {

    private static final IsAnnotationPresentFilter START_PROCESS_FILTER = new IsAnnotationPresentFilter(StartProcess.class);
    private static final IsAnnotationPresentFilter SIGNAL_EVENT_FILTER = new IsAnnotationPresentFilter(SignalEvent.class);
    private static final IsAnnotationPresentFilter ABORT_PROCESS_INSTANCE_FILTER = new IsAnnotationPresentFilter(AbortProcessInstance.class);

    private static final String UNDEFINED = "";
    private static final String INTERFACE_ERR_MSG = " is a class. @Process only allowed on interfaces.";
    private static final String FILTER_ERR_MSG = " is in error. @StartProcess, @SignalEvent and @AbortProcessInstance cannot co-exist on the same method.";

    private final IsAnnotationPresentFilter _processFilter = new IsAnnotationPresentFilter(Process.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public ScannerOutput<SwitchYardModel> scan(ScannerInput<SwitchYardModel> input) throws IOException {
        SwitchYardModel switchyardModel = new V1SwitchYardModel();
        CompositeModel compositeModel = new V1CompositeModel();
        compositeModel.setName(input.getName());
        ClasspathScanner processScanner = new ClasspathScanner(_processFilter);
        for (URL url : input.getURLs()) {
            processScanner.scan(url);
        }
        List<Class<?>> processClasses = _processFilter.getMatchedTypes();
        for (Class<?> processClass : processClasses) {
            Process process = processClass.getAnnotation(Process.class);
            Class<?> processInterface = process.value();
            if (Process.UndefinedProcessInterface.class.equals(processInterface)) {
                processInterface = processClass;
            }
            if (!processInterface.isInterface()) {
                throw new IOException(processInterface.getName() + INTERFACE_ERR_MSG);
            }
            String processName = Strings.trimToNull(process.name());
            if (processName == null) {
                processName = processInterface.getSimpleName();
            }
            JavaComponentServiceInterfaceModel csiModel = new V1JavaComponentServiceInterfaceModel();
            csiModel.setInterface(processInterface.getName());
            ComponentServiceModel serviceModel = new V1ComponentServiceModel();
            serviceModel.setInterface(csiModel);
            serviceModel.setName(processName);
            ComponentModel componentModel = new V1ComponentModel();
            componentModel.setName(processName);
            componentModel.addService(serviceModel);
            compositeModel.addComponent(componentModel);
            BPMComponentImplementationModel bciModel = new V1BPMComponentImplementationModel();
            String processDefinition = process.definition();
            if (UNDEFINED.equals(processDefinition)) {
                processDefinition = "META-INF/" + processName + ".bpmn";
            }
            SimpleResource procDefRes = new SimpleResource(processDefinition);
            String procDefResType = process.definitionType();
            if (!"".equals(procDefResType)) {
                procDefRes.setType(ResourceType.valueOf(procDefResType));
            }
            bciModel.setProcessDefinition(procDefRes);
            String processId = process.id();
            if (UNDEFINED.equals(processId)) {
                processId = processName;
            }
            bciModel.setProcessId(processId);
            if (process.agent()) {
                bciModel.setAgent(true);
            }
            String messageContentInName = process.messageContentInName();
            if (!UNDEFINED.equals(messageContentInName)) {
                bciModel.setMessageContentInName(messageContentInName);
            }
            String messageContentOutName = process.messageContentOutName();
            if (!UNDEFINED.equals(messageContentOutName)) {
                bciModel.setMessageContentOutName(messageContentOutName);
            }
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
            Audit audit = processClass.getAnnotation(Audit.class);
            if (audit != null) {
                AuditModel aModel = new V1AuditModel(bciModel.getModelConfiguration().getQName().getNamespaceURI());
                aModel.setType(audit.type());
                int interval = audit.interval();
                if (interval != -1) {
                    aModel.setInterval(Integer.valueOf(interval));
                }
                if (!UNDEFINED.equals(audit.log())) {
                    aModel.setLog(audit.log());
                }
                bciModel.setAudit(aModel);
            }
            bciModel.addTaskHandler(new V1TaskHandlerModel().setClazz(SwitchYardServiceTaskHandler.class).setName(SwitchYardServiceTaskHandler.SWITCHYARD_SERVICE));
            for (Class<? extends TaskHandler> taskHandlerClass : process.taskHandlers()) {
                if (Process.UndefinedTaskHandler.class.equals(taskHandlerClass) || SwitchYardServiceTaskHandler.class.equals(taskHandlerClass)) {
                    continue;
                }
                TaskHandler taskHandler = Construction.construct(taskHandlerClass);
                bciModel.addTaskHandler(new V1TaskHandlerModel().setClazz(taskHandlerClass).setName(taskHandler.getName()));
            }
            for (String location : process.resources()) {
                if (UNDEFINED.equals(location)) {
                    continue;
                }
                // setting the location will trigger deducing and setting the type
                bciModel.addResource(new V1ResourceModel(BPMComponentImplementationModel.DEFAULT_NAMESPACE).setLocation(location));
            }
            componentModel.setImplementation(bciModel);
        }

        if (!compositeModel.getModelChildren().isEmpty()) {
            switchyardModel.setComposite(compositeModel);
        }

        return new ScannerOutput<SwitchYardModel>().setModel(switchyardModel);
    }

}
