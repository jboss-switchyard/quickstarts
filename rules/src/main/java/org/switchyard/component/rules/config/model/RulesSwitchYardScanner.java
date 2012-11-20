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
package org.switchyard.component.rules.config.model;

import static org.switchyard.component.common.knowledge.config.model.MappingsModel.GLOBALS;
import static org.switchyard.component.common.knowledge.config.model.MappingsModel.INPUTS;
import static org.switchyard.component.common.knowledge.config.model.MappingsModel.OUTPUTS;
import static org.switchyard.component.rules.config.model.RulesComponentImplementationModel.DEFAULT_NAMESPACE;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;

import org.switchyard.common.lang.Strings;
import org.switchyard.common.type.classpath.ClasspathScanner;
import org.switchyard.common.type.classpath.IsAnnotationPresentFilter;
import org.switchyard.component.common.knowledge.annotation.Mapping;
import org.switchyard.component.common.knowledge.config.model.ActionModel;
import org.switchyard.component.common.knowledge.config.model.ActionsModel;
import org.switchyard.component.common.knowledge.config.model.KnowledgeSwitchYardScanner;
import org.switchyard.component.common.knowledge.config.model.v1.V1ActionsModel;
import org.switchyard.component.rules.RulesActionType;
import org.switchyard.component.rules.annotation.Execute;
import org.switchyard.component.rules.annotation.FireAllRules;
import org.switchyard.component.rules.annotation.FireUntilHalt;
import org.switchyard.component.rules.annotation.Rules;
import org.switchyard.component.rules.config.model.v1.V1RulesActionModel;
import org.switchyard.component.rules.config.model.v1.V1RulesComponentImplementationModel;
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
 * RulesSwitchYardScanner.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class RulesSwitchYardScanner extends KnowledgeSwitchYardScanner {

    private static final IsAnnotationPresentFilter EXECUTE_FILTER = new IsAnnotationPresentFilter(Execute.class);
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
        ActionsModel actionsModel = new V1ActionsModel(DEFAULT_NAMESPACE);
        JavaService javaService = JavaService.fromClass(rulesInterface);
        for (Method method : rulesClass.getDeclaredMethods()) {
            RulesActionType actionType = null;
            String id = null;
            Mapping[] globalMappingAnnotations = null;
            Mapping[] inputMappingAnnotations = null;
            Mapping[] outputMappingAnnotations = null;
            if (EXECUTE_FILTER.matches(method)) {
                actionType = RulesActionType.EXECUTE;
                Execute executeAnnotation = method.getAnnotation(Execute.class);
                globalMappingAnnotations = executeAnnotation.globals();
                inputMappingAnnotations = executeAnnotation.inputs();
                outputMappingAnnotations = executeAnnotation.outputs();
            } else if (FIRE_ALL_RULES_FILTER.matches(method)) {
                actionType = RulesActionType.FIRE_ALL_RULES;
                FireAllRules fireAllRulesAnnotation = method.getAnnotation(FireAllRules.class);
                globalMappingAnnotations = fireAllRulesAnnotation.globals();
                inputMappingAnnotations = fireAllRulesAnnotation.inputs();
                outputMappingAnnotations = fireAllRulesAnnotation.outputs();
            } else if (FIRE_UNTIL_HALT_FILTER.matches(method)) {
                actionType = RulesActionType.FIRE_UNTIL_HALT;
                FireUntilHalt fireUntilHaltAnnotation = method.getAnnotation(FireUntilHalt.class);
                globalMappingAnnotations = fireUntilHaltAnnotation.globals();
                inputMappingAnnotations = fireUntilHaltAnnotation.inputs();
                outputMappingAnnotations = fireUntilHaltAnnotation.outputs();
                id = Strings.trimToNull(fireUntilHaltAnnotation.id());
            }
            if (actionType != null) {
                ServiceOperation serviceOperation = javaService.getOperation(method.getName());
                if (serviceOperation != null) {
                    ActionModel actionModel = new V1RulesActionModel();
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
