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
package org.switchyard.component.rules.config.model;

import static org.switchyard.component.rules.config.model.RulesComponentImplementationModel.DEFAULT_NAMESPACE;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.EventListener;
import java.util.List;

import org.switchyard.common.lang.Strings;
import org.switchyard.common.type.classpath.ClasspathScanner;
import org.switchyard.common.type.classpath.IsAnnotationPresentFilter;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.common.rules.Audit;
import org.switchyard.component.common.rules.Mapping;
import org.switchyard.component.common.rules.config.model.AuditModel;
import org.switchyard.component.common.rules.config.model.MappingModel;
import org.switchyard.component.common.rules.config.model.v1.V1AuditModel;
import org.switchyard.component.common.rules.config.model.v1.V1EventListenerModel;
import org.switchyard.component.common.rules.config.model.v1.V1MappingModel;
import org.switchyard.component.rules.Channel;
import org.switchyard.component.rules.Execute;
import org.switchyard.component.rules.FireAllRules;
import org.switchyard.component.rules.FireUntilHalt;
import org.switchyard.component.rules.Rules;
import org.switchyard.component.rules.RulesActionType;
import org.switchyard.component.rules.config.model.v1.V1ChannelModel;
import org.switchyard.component.rules.config.model.v1.V1FactsModel;
import org.switchyard.component.rules.config.model.v1.V1GlobalsModel;
import org.switchyard.component.rules.config.model.v1.V1RulesActionModel;
import org.switchyard.component.rules.config.model.v1.V1RulesComponentImplementationModel;
import org.switchyard.config.model.Scanner;
import org.switchyard.config.model.ScannerInput;
import org.switchyard.config.model.ScannerOutput;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.InterfaceModel;
import org.switchyard.config.model.composite.v1.V1ComponentModel;
import org.switchyard.config.model.composite.v1.V1ComponentReferenceModel;
import org.switchyard.config.model.composite.v1.V1ComponentServiceModel;
import org.switchyard.config.model.composite.v1.V1CompositeModel;
import org.switchyard.config.model.composite.v1.V1InterfaceModel;
import org.switchyard.config.model.resource.v1.V1ResourceModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.switchyard.v1.V1SwitchYardModel;
import org.switchyard.metadata.ServiceOperation;
import org.switchyard.metadata.java.JavaService;

/**
 * A SwitchYardScanner which scans for @Rule annotations.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class RulesSwitchYardScanner implements Scanner<SwitchYardModel> {

    private static final IsAnnotationPresentFilter EXECUTE_FILTER = new IsAnnotationPresentFilter(Execute.class);
    private static final IsAnnotationPresentFilter FIRE_ALL_RULES_FILTER = new IsAnnotationPresentFilter(FireAllRules.class);
    private static final IsAnnotationPresentFilter FIRE_UNTIL_HALT_FILTER = new IsAnnotationPresentFilter(FireUntilHalt.class);

    private static final String UNDEFINED = "";
    private static final String INTERFACE_ERR_MSG = " is a class. @Rules only allowed on interfaces.";

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
            Rules rules = rulesClass.getAnnotation(Rules.class);
            Class<?> rulesInterface = rules.value();
            if (Rules.UndefinedRulesInterface.class.equals(rulesInterface)) {
                rulesInterface = rulesClass;
            }
            if (!rulesInterface.isInterface()) {
                throw new IOException(rulesInterface.getName() + INTERFACE_ERR_MSG);
            }
            String rulesName = Strings.trimToNull(rules.name());
            if (rulesName == null) {
                rulesName = rulesInterface.getSimpleName();
            }
            ComponentModel componentModel = new V1ComponentModel();
            componentModel.setName(rulesName);
            RulesComponentImplementationModel rciModel = new V1RulesComponentImplementationModel();
            rciModel.setAgent(rules.agent());
            rciModel.setClock(rules.clock());
            rciModel.setEventProcessing(rules.eventProcessing());
            int maxThreads = rules.maxThreads();
            if (maxThreads != -1) {
                rciModel.setMaxThreads(Integer.valueOf(maxThreads));
            }
            rciModel.setMultithreadEvaluation(Boolean.valueOf(rules.multithreadEvaluation()));
            JavaService javaService = JavaService.fromClass(rulesInterface);
            for (Method method : rulesClass.getDeclaredMethods()) {
                RulesActionType rat = null;
                String ep = null;
                if (EXECUTE_FILTER.matches(method)) {
                    rat = RulesActionType.EXECUTE;
                } else if (FIRE_ALL_RULES_FILTER.matches(method)) {
                    rat = RulesActionType.FIRE_ALL_RULES;
                } else if (FIRE_UNTIL_HALT_FILTER.matches(method)) {
                    rat = RulesActionType.FIRE_UNTIL_HALT;
                    ep = Strings.trimToNull(method.getAnnotation(FireUntilHalt.class).entryPoint());
                }
                if (rat != null) {
                    ServiceOperation srvOper = javaService.getOperation(method.getName());
                    if (srvOper != null) {
                        RulesActionModel ram = new V1RulesActionModel().setName(srvOper.getName()).setType(rat).setEntryPoint(ep);
                        rciModel.addRulesAction(ram);
                    }
                }
            }
            Audit audit = rulesClass.getAnnotation(Audit.class);
            if (audit != null) {
                AuditModel aModel = new V1AuditModel(DEFAULT_NAMESPACE);
                aModel.setType(audit.type());
                int interval = audit.interval();
                if (interval != -1) {
                    aModel.setInterval(Integer.valueOf(interval));
                }
                if (!UNDEFINED.equals(audit.log())) {
                    aModel.setLog(audit.log());
                }
                rciModel.setAudit(aModel);
            }
            for (Class<? extends EventListener> elc : rules.eventListeners()) {
                rciModel.addEventListener(new V1EventListenerModel(DEFAULT_NAMESPACE).setClazz(elc));
            }
            for (Channel channel : rules.channels()) {
                Class<? extends org.drools.runtime.Channel> channelClass = channel.value();
                ChannelModel channelModel = new V1ChannelModel().setClazz(channelClass);
                String channelName = channel.name();
                if (UNDEFINED.equals(channelName)) {
                    channelName = channelClass.getSimpleName();
                }
                channelModel.setName(channelName);
                if (!UNDEFINED.equals(channel.operation())) {
                    channelModel.setOperation(channel.operation());
                }
                if (!UNDEFINED.equals(channel.input())) {
                    channelModel.setInput(XMLHelper.createQName(channel.input()));
                }
                if (!UNDEFINED.equals(channel.reference())) {
                    String reference = channel.reference();
                    channelModel.setReference(reference);
                    ComponentReferenceModel compRefModel = new V1ComponentReferenceModel();
                    compRefModel.setName(reference);
                    if (!Channel.UndefinedInterface.class.equals(channel.interfaze())) {
                        InterfaceModel compRefIfaceModel = new V1InterfaceModel(InterfaceModel.JAVA);
                        compRefIfaceModel.setInterface(channel.interfaze().getName());
                        compRefModel.setInterface(compRefIfaceModel);
                        componentModel.addReference(compRefModel);
                    }
                }
                rciModel.addChannel(channelModel);
            }
            for (String location : rules.resources()) {
                if (UNDEFINED.equals(location)) {
                    continue;
                }
                // setting the location will trigger deducing and setting the type
                rciModel.addResource(new V1ResourceModel(DEFAULT_NAMESPACE).setLocation(location));
            }
            addMappings(rules, rciModel);
            componentModel.setImplementation(rciModel);

            ComponentServiceModel serviceModel = new V1ComponentServiceModel();
            InterfaceModel csiModel = new V1InterfaceModel(InterfaceModel.JAVA);
            csiModel.setInterface(rulesInterface.getName());
            serviceModel.setInterface(csiModel);
            serviceModel.setName(rulesName);
            componentModel.addService(serviceModel);
            compositeModel.addComponent(componentModel);
        }

        if (!compositeModel.getModelChildren().isEmpty()) {
            switchyardModel.setComposite(compositeModel);
        }

        return new ScannerOutput<SwitchYardModel>().setModel(switchyardModel);
    }

    // Code here instead of in scan() because checkstyle complains when a method is longer than 150 lines.
    private void addMappings(Rules rules, RulesComponentImplementationModel rciModel) {
        GlobalsModel globalsModel = null;
        for (Mapping globalMapping : rules.globals()) {
            MappingModel mappingModel = new V1MappingModel(DEFAULT_NAMESPACE);
            mappingModel.setContextScope(globalMapping.contextScope());
            mappingModel.setExpression(globalMapping.expression());
            mappingModel.setExpressionType(globalMapping.expressionType());
            String variable = globalMapping.variable();
            if (!UNDEFINED.equals(variable)) {
                mappingModel.setVariable(variable);
            }
            if (globalsModel == null) {
                globalsModel = new V1GlobalsModel();
                rciModel.setGlobals(globalsModel);
            }
            globalsModel.addMapping(mappingModel);
        }
        FactsModel factsModel = null;
        for (Mapping factMapping : rules.facts()) {
            MappingModel mappingModel = new V1MappingModel(DEFAULT_NAMESPACE);
            mappingModel.setContextScope(factMapping.contextScope());
            mappingModel.setExpression(factMapping.expression());
            mappingModel.setExpressionType(factMapping.expressionType());
            String variable = factMapping.variable();
            if (!UNDEFINED.equals(variable)) {
                mappingModel.setVariable(variable);
            }
            if (factsModel == null) {
                factsModel = new V1FactsModel();
                rciModel.setFacts(factsModel);
            }
            factsModel.addMapping(mappingModel);
        }
    }

}
