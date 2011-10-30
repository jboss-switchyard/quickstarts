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

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;

import org.switchyard.common.type.classpath.ClasspathScanner;
import org.switchyard.common.type.classpath.IsAnnotationPresentFilter;
import org.switchyard.component.common.rules.audit.Audit;
import org.switchyard.component.common.rules.config.model.AuditModel;
import org.switchyard.component.common.rules.config.model.v1.V1AuditModel;
import org.switchyard.component.rules.ExecuteRules;
import org.switchyard.component.rules.Rules;
import org.switchyard.component.rules.common.RulesActionType;
import org.switchyard.component.rules.config.model.v1.V1RulesActionModel;
import org.switchyard.component.rules.config.model.v1.V1RulesComponentImplementationModel;
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
 * A SwitchYardScanner which scans for @Rule annotations.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class RulesSwitchYardScanner implements Scanner<SwitchYardModel> {

    private static final IsAnnotationPresentFilter RESOURCE_FILTER = new IsAnnotationPresentFilter(Rules.class);
    private static final IsAnnotationPresentFilter EXECUTE_RULES_FILTER = new IsAnnotationPresentFilter(ExecuteRules.class);

    private static final String INTERFACE_ERR_MSG = " is a class. @Rules only allowed on interfaces.";

    /**
     * {@inheritDoc}
     */
    @Override
    public ScannerOutput<SwitchYardModel> scan(ScannerInput<SwitchYardModel> input) throws IOException {
        SwitchYardModel switchyardModel = new V1SwitchYardModel();
        CompositeModel compositeModel = new V1CompositeModel();
        compositeModel.setName(input.getName());
        switchyardModel.setComposite(compositeModel);
        ClasspathScanner rulesScanner = new ClasspathScanner(RESOURCE_FILTER);
        for (URL url : input.getURLs()) {
            rulesScanner.scan(url);
        }
        List<Class<?>> rulesClasses = RESOURCE_FILTER.getMatchedTypes();
        for (Class<?> rulesClass : rulesClasses) {
            Rules rules = rulesClass.getAnnotation(Rules.class);
            Class<?> rulesInterface = rules.value();
            if (Rules.UndefinedRulesInterface.class.equals(rulesInterface)) {
                rulesInterface = rulesClass;
            }
            if (!rulesInterface.isInterface()) {
                throw new IOException(rulesInterface.getName() + INTERFACE_ERR_MSG);
            }
            String rulesName = rulesInterface.getSimpleName();
            ComponentModel componentModel = new V1ComponentModel();
            componentModel.setName(rulesName);
            RulesComponentImplementationModel rciModel = new V1RulesComponentImplementationModel();
            rciModel.setStateful(rules.stateful());
            rciModel.setAgent(rules.agent());
            JavaService javaService = JavaService.fromClass(rulesInterface);
            for (Method method : rulesClass.getDeclaredMethods()) {
                RulesActionType rat = null;
                if (EXECUTE_RULES_FILTER.matches(method)) {
                    rat = RulesActionType.EXECUTE_RULES;
                }
                if (rat != null) {
                    ServiceOperation srvOper = javaService.getOperation(method.getName());
                    if (srvOper != null) {
                        RulesActionModel ram = new V1RulesActionModel().setName(srvOper.getName()).setType(rat);
                        rciModel.addRulesAction(ram);
                    }
                }
            }
            Audit audit = rulesClass.getAnnotation(Audit.class);
            if (audit != null) {
                AuditModel aModel = new V1AuditModel(rciModel.getModelConfiguration().getQName().getNamespaceURI());
                int interval = audit.interval();
                if (interval != -1) {
                    aModel.setInterval(Integer.valueOf(interval));
                }
                aModel.setLog(audit.log());
                aModel.setType(audit.type());
                rciModel.setAudit(aModel);
            }
            for (String location : rules.resources()) {
                if (Rules.UNDEFINED_RESOURCE.equals(location)) {
                    continue;
                }
                // setting the location will trigger deducing and setting the type
                rciModel.addResource(new V1ResourceModel().setLocation(location));
            }
            componentModel.setImplementation(rciModel);
            ComponentServiceModel serviceModel = new V1ComponentServiceModel();
            JavaComponentServiceInterfaceModel csiModel = new V1JavaComponentServiceInterfaceModel();
            csiModel.setInterface(rulesInterface.getName());
            serviceModel.setInterface(csiModel);
            serviceModel.setName(rulesName);
            componentModel.addService(serviceModel);
            compositeModel.addComponent(componentModel);
        }
        return new ScannerOutput<SwitchYardModel>().setModel(switchyardModel);
    }

}
