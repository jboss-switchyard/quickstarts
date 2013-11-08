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
import org.switchyard.component.rules.RulesMessages;
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
import org.switchyard.config.model.switchyard.SwitchYardNamespace;
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
        SwitchYardNamespace switchyardNamespace = input.getSwitchyardNamespace();
        SwitchYardModel switchyardModel = new V1SwitchYardModel(switchyardNamespace.uri());
        CompositeModel compositeModel = new V1CompositeModel();
        compositeModel.setName(input.getCompositeName());
        ClasspathScanner rulesScanner = new ClasspathScanner(_rulesFilter);
        for (URL url : input.getURLs()) {
            rulesScanner.scan(url);
        }
        List<Class<?>> rulesClasses = _rulesFilter.getMatchedTypes();
        for (Class<?> rulesClass : rulesClasses) {
            compositeModel.addComponent(scan(rulesClass, switchyardNamespace));
        }
        if (!compositeModel.getModelChildren().isEmpty()) {
            switchyardModel.setComposite(compositeModel);
        }
        return new ScannerOutput<SwitchYardModel>().setModel(switchyardModel);
    }

    /**
     * Scans a class using the default switchyard namespace.
     * @param rulesClass the class
     * @return the component model
     * @throws IOException oops
     */
    public ComponentModel scan(Class<?> rulesClass) throws IOException {
        return scan(rulesClass, null);
    }

    /**
     * Scans a class using the specified switchyard namespace.
     * @param rulesClass the class
     * @param switchyardNamespace the switchyard namespace
     * @return the component model
     * @throws IOException oops
     */
    public ComponentModel scan(Class<?> rulesClass, SwitchYardNamespace switchyardNamespace) throws IOException {
        if (switchyardNamespace == null) {
            switchyardNamespace = SwitchYardNamespace.DEFAULT;
        }
        Rules rules = rulesClass.getAnnotation(Rules.class);
        Class<?> rulesInterface = rules.value();
        if (Rules.UndefinedRulesInterface.class.equals(rulesInterface)) {
            rulesInterface = rulesClass;
        }
        if (!rulesInterface.isInterface()) {
            throw RulesMessages.MESSAGES.rulesInterfaceGetNameIsAClassRulesOnlyAllowedOnInterfaces(rulesInterface.getName());
        }
        String rulesName = Strings.trimToNull(rules.name());
        if (rulesName == null) {
            rulesName = rulesInterface.getSimpleName();
        }
        ComponentModel componentModel = new V1ComponentModel();
        componentModel.setName(rulesName);
        RulesNamespace rulesNamespace = RulesNamespace.fromUri(rules.namespace());
        if (rulesNamespace == null) {
            rulesNamespace = RulesNamespace.DEFAULT;
            for (RulesNamespace value : RulesNamespace.values()) {
                if (value.versionMatches(switchyardNamespace)) {
                    rulesNamespace = value;
                    break;
                }
            }
        }
        RulesComponentImplementationModel componentImplementationModel = new V1RulesComponentImplementationModel(rulesNamespace.uri());
        OperationsModel operationsModel = new V1OperationsModel(rulesNamespace.uri());
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
                    OperationModel operationModel = new V1RulesOperationModel(rulesNamespace.uri());
                    operationModel.setEventId(eventId);
                    operationModel.setName(serviceOperation.getName());
                    operationModel.setType(operationType);
                    operationModel.setGlobals(toGlobalsModel(globalMappingAnnotations, rulesNamespace));
                    operationModel.setInputs(toInputsModel(inputMappingAnnotations, rulesNamespace));
                    operationModel.setOutputs(toOutputsModel(outputMappingAnnotations, rulesNamespace));
                    operationModel.setFaults(toFaultsModel(faultMappingAnnotations, rulesNamespace));
                    operationsModel.addOperation(operationModel);
                }
            }
        }
        if (!operationsModel.getOperations().isEmpty()) {
            componentImplementationModel.setOperations(operationsModel);
        }
        componentImplementationModel.setChannels(toChannelsModel(rules.channels(), rulesNamespace, componentModel, switchyardNamespace));
        componentImplementationModel.setListeners(toListenersModel(rules.listeners(), rulesNamespace));
        componentImplementationModel.setLoggers(toLoggersModel(rules.loggers(), rulesNamespace));
        componentImplementationModel.setManifest(toManifestModel(rules.manifest(), rulesNamespace));
        componentImplementationModel.setProperties(toPropertiesModel(rules.properties(), rulesNamespace));
        componentModel.setImplementation(componentImplementationModel);
        ComponentServiceModel componentServiceModel = new V1ComponentServiceModel(switchyardNamespace.uri());
        InterfaceModel interfaceModel = new V1InterfaceModel(InterfaceModel.JAVA);
        interfaceModel.setInterface(rulesInterface.getName());
        componentServiceModel.setInterface(interfaceModel);
        componentServiceModel.setName(rulesName);
        componentModel.addService(componentServiceModel);
        return componentModel;
    }

}
