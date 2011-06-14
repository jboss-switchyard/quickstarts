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
import java.net.URL;
import java.util.List;

import org.switchyard.common.type.classpath.ClasspathScanner;
import org.switchyard.common.type.classpath.IsAnnotationPresentFilter;
import org.switchyard.common.type.reflect.Construction;
import org.switchyard.component.bpm.BPM;
import org.switchyard.component.bpm.config.model.v1.V1BpmComponentImplementationModel;
import org.switchyard.component.bpm.config.model.v1.V1ProcessResourceModel;
import org.switchyard.component.bpm.config.model.v1.V1TaskHandlerModel;
import org.switchyard.component.bpm.process.ProcessResource;
import org.switchyard.component.bpm.process.ProcessResourceType;
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
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.switchyard.v1.V1SwitchYardModel;

/**
 * A SwitchYardScanner which process @BPM annotations.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class BpmSwitchYardScanner implements Scanner<SwitchYardModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ScannerOutput<SwitchYardModel> scan(ScannerInput<SwitchYardModel> input) throws IOException {
        SwitchYardModel switchyardModel = new V1SwitchYardModel();
        CompositeModel compositeModel = new V1CompositeModel();
        compositeModel.setName(input.getName());
        switchyardModel.setComposite(compositeModel);
        IsAnnotationPresentFilter bpmFilter = new IsAnnotationPresentFilter(BPM.class);
        ClasspathScanner bpmScanner = new ClasspathScanner(bpmFilter);
        for (URL url : input.getURLs()) {
            bpmScanner.scan(url);
        }
        List<Class<?>> bpmClasses = bpmFilter.getMatchedTypes();
        for (Class<?> bpmClass : bpmClasses) {
            BPM bpm = bpmClass.getAnnotation(BPM.class);
            final String bpmName;
            Class<?> processInterface = bpm.processInterface();
            if (BPM.UndefinedProcessInterface.class.equals(processInterface)) {
                bpmName = bpmClass.getSimpleName();
                if (bpmClass.isInterface()) {
                    processInterface = bpmClass;
                } else {
                    processInterface = BPM.DefaultProcessInterface.class;
                }
            } else {
                bpmName = processInterface.getSimpleName();
            }
            JavaComponentServiceInterfaceModel csiModel = new V1JavaComponentServiceInterfaceModel();
            csiModel.setInterface(processInterface.getName());
            ComponentServiceModel serviceModel = new V1ComponentServiceModel();
            serviceModel.setInterface(csiModel);
            serviceModel.setName(bpmName);
            ComponentModel componentModel = new V1ComponentModel();
            componentModel.setName(bpmName);
            componentModel.addService(serviceModel);
            compositeModel.addComponent(componentModel);
            BpmComponentImplementationModel bciModel = new V1BpmComponentImplementationModel();
            String processDefinition = bpm.processDefinition();
            if (BPM.UNDEFINED_PROCESS_DEFINITION.equals(processDefinition)) {
                processDefinition = "META-INF/" + bpmName + ".bpmn";
            }
            bciModel.setProcessDefinition(processDefinition);
            bciModel.setProcessDefinitionType(bpm.processDefinitionType());
            String processId = bpm.processId();
            if (BPM.UNDEFINED_PROCESS_ID.equals(processId)) {
                processId = bpmName;
            }
            bciModel.setProcessId(processId);
            for (Class<? extends ProcessResource> processResourceClass : bpm.processResources()) {
                if (BPM.UndefinedProcessResource.class.equals(processResourceClass)) {
                    continue;
                }
                ProcessResource processResource = Construction.construct(processResourceClass);
                String location = processResource.getLocation();
                ProcessResourceType type = processResource.getType();
                bciModel.addProcessResource(new V1ProcessResourceModel().setLocation(location).setType(type));
            }
            bciModel.addTaskHandler(new V1TaskHandlerModel().setClazz(SwitchYardServiceTaskHandler.class).setName(SwitchYardServiceTaskHandler.SWITCHYARD_SERVICE));
            for (Class<? extends TaskHandler> taskHandlerClass : bpm.taskHandlers()) {
                if (BPM.UndefinedTaskHandler.class.equals(taskHandlerClass) || SwitchYardServiceTaskHandler.class.equals(taskHandlerClass)) {
                    continue;
                }
                TaskHandler taskHandler = Construction.construct(taskHandlerClass);
                bciModel.addTaskHandler(new V1TaskHandlerModel().setClazz(taskHandlerClass).setName(taskHandler.getName()));
            }
            componentModel.setImplementation(bciModel);
        }
        return new ScannerOutput<SwitchYardModel>().setModel(switchyardModel);
    }

}
