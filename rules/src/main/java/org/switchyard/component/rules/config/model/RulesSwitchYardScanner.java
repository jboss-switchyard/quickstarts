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
package org.switchyard.component.rules.config.model;

import static org.switchyard.component.rules.config.model.RulesComponentImplementationModel.DEFAULT_NAMESPACE;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;

import org.switchyard.common.lang.Strings;
import org.switchyard.common.type.classpath.ClasspathScanner;
import org.switchyard.common.type.classpath.IsAnnotationPresentFilter;
import org.switchyard.component.common.knowledge.annotation.Fault;
import org.switchyard.component.common.knowledge.annotation.Global;
import org.switchyard.component.common.knowledge.annotation.Input;
import org.switchyard.component.common.knowledge.annotation.Output;
import org.switchyard.component.common.knowledge.config.model.KnowledgeSwitchYardScanner;
import org.switchyard.component.common.knowledge.config.model.OperationModel;
import org.switchyard.component.common.knowledge.config.model.OperationsModel;
import org.switchyard.component.common.knowledge.config.model.v1.V1OperationsModel;
import org.switchyard.component.rules.RulesOperationType;
import org.switchyard.component.rules.annotation.Execute;
import org.switchyard.component.rules.annotation.FireAllRules;
import org.switchyard.component.rules.annotation.FireUntilHalt;
import org.switchyard.component.rules.annotation.Insert;
import org.switchyard.component.rules.annotation.Rules;
import org.switchyard.component.rules.config.model.v1.V1RulesComponentImplementationModel;
import org.switchyard.component.rules.config.model.v1.V1RulesOperationModel;
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
import org.switchyard.extensions.java.JavaService;
import org.switchyard.metadata.ServiceOperation;

/**
 * RulesSwitchYardScanner.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class RulesSwitchYardScanner extends KnowledgeSwitchYardScanner {

    private static final IsAnnotationPresentFilter EXECUTE_FILTER = new IsAnnotationPresentFilter(Execute.class);
    private static final IsAnnotationPresentFilter INSERT_FILTER = new IsAnnotationPresentFilter(Insert.class);
    private static final IsAnnotationPresentFilter FIRE_ALL_RULES_FILTER = new IsAnnotationPresentFilter(FireAllRules.class);
    private static final IsAnnotationPresentFilter FIRE_UNTIL_HALT_FILTER = new IsAnnotationPresentFilter(FireUntilHalt.class);

    private final IsAnnotationPresentFilter _rulesFilter = new IsAnnotationPresentFilter(Rules.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public ScannerOutput<SwitchYardModel> scan(ScannerInput<SwitchYardModel> input) throws IOException {
        SwitchYardModel switchyardModel = new V1SwitchYardModel();
        CompositeModel compositeModel = new V1CompositeModel();
        compositeModel.setName(input.getName());
        ClasspathScanner rulesScanner = new ClasspathScanner(_rulesFilter);
        for (URL url : input.getURLs()) {
            rulesScanner.scan(url);
        }
        List<Class<?>> rulesClasses = _rulesFilter.getMatchedTypes();
        for (Class<?> rulesClass : rulesClasses) {
            compositeModel.addComponent(scan(rulesClass));
        }
        if (!compositeModel.getModelChildren().isEmpty()) {
            switchyardModel.setComposite(compositeModel);
        }
        return new ScannerOutput<SwitchYardModel>().setModel(switchyardModel);
    }

    /**
     * Scans a class.
     * @param rulesClass the class
     * @return the component model
     * @throws IOException oops
     */
    public ComponentModel scan(Class<?> rulesClass) throws IOException {
        Rules rules = rulesClass.getAnnotation(Rules.class);
        Class<?> rulesInterface = rules.value();
        if (Rules.UndefinedRulesInterface.class.equals(rulesInterface)) {
            rulesInterface = rulesClass;
        }
        if (!rulesInterface.isInterface()) {
            throw new IOException(rulesInterface.getName() +  " is a class. @Rules only allowed on interfaces.");
        }
        String rulesName = Strings.trimToNull(rules.name());
        if (rulesName == null) {
            rulesName = rulesInterface.getSimpleName();
        }
        ComponentModel componentModel = new V1ComponentModel();
        componentModel.setName(rulesName);
        RulesComponentImplementationModel componentImplementationModel = new V1RulesComponentImplementationModel();
        OperationsModel operationsModel = new V1OperationsModel(DEFAULT_NAMESPACE);
        JavaService javaService = JavaService.fromClass(rulesInterface);
        for (Method method : rulesClass.getDeclaredMethods()) {
            RulesOperationType operationType = null;
            String eventId = null;
            Global[] globalMappingAnnotations = null;
            Input[] inputMappingAnnotations = null;
            Output[] outputMappingAnnotations = null;
            Fault[] faultMappingAnnotations = null;
            if (EXECUTE_FILTER.matches(method)) {
                operationType = RulesOperationType.EXECUTE;
                Execute executeAnnotation = method.getAnnotation(Execute.class);
                globalMappingAnnotations = executeAnnotation.globals();
                inputMappingAnnotations = executeAnnotation.inputs();
                outputMappingAnnotations = executeAnnotation.outputs();
                faultMappingAnnotations = executeAnnotation.faults();
            } else if (INSERT_FILTER.matches(method)) {
                operationType = RulesOperationType.INSERT;
                Insert insertAnnotation = method.getAnnotation(Insert.class);
                globalMappingAnnotations = insertAnnotation.globals();
                inputMappingAnnotations = insertAnnotation.inputs();
                outputMappingAnnotations = insertAnnotation.outputs();
                faultMappingAnnotations = insertAnnotation.faults();
            } else if (FIRE_ALL_RULES_FILTER.matches(method)) {
                operationType = RulesOperationType.FIRE_ALL_RULES;
                FireAllRules fireAllRulesAnnotation = method.getAnnotation(FireAllRules.class);
                globalMappingAnnotations = fireAllRulesAnnotation.globals();
                inputMappingAnnotations = fireAllRulesAnnotation.inputs();
                outputMappingAnnotations = fireAllRulesAnnotation.outputs();
                faultMappingAnnotations = fireAllRulesAnnotation.faults();
            } else if (FIRE_UNTIL_HALT_FILTER.matches(method)) {
                operationType = RulesOperationType.FIRE_UNTIL_HALT;
                FireUntilHalt fireUntilHaltAnnotation = method.getAnnotation(FireUntilHalt.class);
                eventId = Strings.trimToNull(fireUntilHaltAnnotation.eventId());
                globalMappingAnnotations = fireUntilHaltAnnotation.globals();
                inputMappingAnnotations = fireUntilHaltAnnotation.inputs();
                outputMappingAnnotations = fireUntilHaltAnnotation.outputs();
                faultMappingAnnotations = fireUntilHaltAnnotation.faults();
            }
            if (operationType != null) {
                ServiceOperation serviceOperation = javaService.getOperation(method.getName());
                if (serviceOperation != null) {
                    OperationModel operationModel = new V1RulesOperationModel();
                    operationModel.setEventId(eventId);
                    operationModel.setName(serviceOperation.getName());
                    operationModel.setType(operationType);
                    operationModel.setGlobals(toGlobalsModel(globalMappingAnnotations, DEFAULT_NAMESPACE));
                    operationModel.setInputs(toInputsModel(inputMappingAnnotations, DEFAULT_NAMESPACE));
                    operationModel.setOutputs(toOutputsModel(outputMappingAnnotations, DEFAULT_NAMESPACE));
                    operationModel.setFaults(toFaultsModel(faultMappingAnnotations, DEFAULT_NAMESPACE));
                    operationsModel.addOperation(operationModel);
                }
            }
        }
        if (!operationsModel.getOperations().isEmpty()) {
            componentImplementationModel.setOperations(operationsModel);
        }
        componentImplementationModel.setChannels(toChannelsModel(rules.channels(), DEFAULT_NAMESPACE, componentModel));
        componentImplementationModel.setListeners(toListenersModel(rules.listeners(), DEFAULT_NAMESPACE));
        componentImplementationModel.setLoggers(toLoggersModel(rules.loggers(), DEFAULT_NAMESPACE));
        componentImplementationModel.setManifest(toManifestModel(rules.manifest(), DEFAULT_NAMESPACE));
        componentImplementationModel.setProperties(toPropertiesModel(rules.properties(), DEFAULT_NAMESPACE));
        componentModel.setImplementation(componentImplementationModel);
        ComponentServiceModel componentServiceModel = new V1ComponentServiceModel();
        InterfaceModel interfaceModel = new V1InterfaceModel(InterfaceModel.JAVA);
        interfaceModel.setInterface(rulesInterface.getName());
        componentServiceModel.setInterface(interfaceModel);
        componentServiceModel.setName(rulesName);
        componentModel.addService(componentServiceModel);
        return componentModel;
    }

}
