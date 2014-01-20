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

package org.switchyard.component.bean.config.model;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.switchyard.annotations.Requires;
import org.switchyard.common.type.classpath.ClasspathScanner;
import org.switchyard.common.type.classpath.CompositeFilter;
import org.switchyard.common.type.classpath.IsAnnotationPresentFilter;
import org.switchyard.common.type.classpath.PackageFilter;
import org.switchyard.component.bean.BeanMessages;
import org.switchyard.component.bean.Reference;
import org.switchyard.component.bean.Service;
import org.switchyard.component.bean.config.model.v1.V1BeanComponentImplementationModel;
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
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.switchyard.SwitchYardNamespace;
import org.switchyard.config.model.switchyard.v1.V1SwitchYardModel;
import org.switchyard.policy.Policy.PolicyType;
import org.switchyard.policy.SecurityPolicy;
import org.switchyard.policy.TransactionPolicy;

/**
 * Bean Scanner.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class BeanSwitchYardScanner implements Scanner<SwitchYardModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ScannerOutput<SwitchYardModel> scan(ScannerInput<SwitchYardModel> input) throws IOException {
        SwitchYardNamespace switchyardNamespace = input.getSwitchyardNamespace();
        SwitchYardModel switchyardModel = new V1SwitchYardModel(switchyardNamespace.uri());
        CompositeModel compositeModel = new V1CompositeModel();
        compositeModel.setName(input.getCompositeName());

        BeanNamespace beanNamespace = BeanNamespace.DEFAULT;
        for (BeanNamespace value : BeanNamespace.values()) {
            if (value.versionMatches(switchyardNamespace)) {
                beanNamespace = value;
                break;
            }
        }

        List<Class<?>> serviceClasses = scanForServiceBeans(input);

        for (Class<?> serviceClass : serviceClasses) {
            if (serviceClass.isInterface()) {
                continue;
            }
            if (Modifier.isAbstract(serviceClass.getModifiers())) {
                continue;
            }

            ComponentModel componentModel = new V1ComponentModel();
            ComponentServiceModel serviceModel = new V1ComponentServiceModel(switchyardNamespace.uri());
            String name = serviceClass.getSimpleName();
            
            BeanComponentImplementationModel beanModel = new V1BeanComponentImplementationModel(beanNamespace.uri());
            beanModel.setClazz(serviceClass.getName());
            componentModel.setImplementation(beanModel);

            Service service = serviceClass.getAnnotation(Service.class);
            if (service != null) {
                Class<?> iface = service.value();
                if (iface == Service.class) {
                    Class<?>[] interfaces = serviceClass.getInterfaces();
                    if (interfaces.length == 1) {
                        iface = interfaces[0];
                    } else {
                        throw BeanMessages.MESSAGES.unexpectedExceptionTheServiceAnnotationHasNoValueItCannotBeOmmittedUnlessTheBeanImplementsExactlyOneInterface();
                    }
                }
                InterfaceModel csiModel = new V1InterfaceModel(InterfaceModel.JAVA);

                if (service.name().equals(Service.EMPTY)) {
                    name = iface.getSimpleName();
                } else {
                    name = service.name();
                }
                
                serviceModel.setName(name);
                serviceModel.setInterface(csiModel);
                csiModel.setInterface(iface.getName());
                
                componentModel.addService(serviceModel);
            }
            
            // Check to see if a policy requirements have been defined
            Requires requires = serviceClass.getAnnotation(Requires.class);
            if (requires != null) {
                for (SecurityPolicy secPolicy : requires.security()) {
                    if (secPolicy == SecurityPolicy.AUTHORIZATION) {
                        // authorization supports both interaction and implementation,
                        // and we want to add it as implementation to be more correct.
                        beanModel.addPolicyRequirement(secPolicy.getName());
                    } else if (secPolicy.supports(PolicyType.INTERACTION)) {
                        serviceModel.addPolicyRequirement(secPolicy.getName());
                    } else if (secPolicy.supports(PolicyType.IMPLEMENTATION)) {
                        beanModel.addPolicyRequirement(secPolicy.getName());
                    } else {
                        throw BeanMessages.MESSAGES.unknownPolicy(secPolicy.toString());
                    }
                }
                for (TransactionPolicy txPolicy : requires.transaction()) {
                    if (txPolicy.supports(PolicyType.INTERACTION)) {
                        serviceModel.addPolicyRequirement(txPolicy.getName());
                    } else if (txPolicy.supports(PolicyType.IMPLEMENTATION)) {
                        beanModel.addPolicyRequirement(txPolicy.getName());
                    } else {
                        throw BeanMessages.MESSAGES.unknownPolicy(txPolicy.toString());
                    }
                }
                // Make sure we don't have conflicting policies
                String ptx = TransactionPolicy.PROPAGATES_TRANSACTION.getName();
                String stx = TransactionPolicy.SUSPENDS_TRANSACTION.getName();
                if (serviceModel.hasPolicyRequirement(ptx) && serviceModel.hasPolicyRequirement(stx)) {
                    throw BeanMessages.MESSAGES.transactionPoliciesCannotCoexistService(ptx, stx, name);
                }
                String gtx = TransactionPolicy.MANAGED_TRANSACTION_GLOBAL.getName();
                String ltx = TransactionPolicy.MANAGED_TRANSACTION_LOCAL.getName();
                String ntx = TransactionPolicy.NO_MANAGED_TRANSACTION.getName();
                if (beanModel.hasPolicyRequirement(gtx) && beanModel.hasPolicyRequirement(ltx)
                        || beanModel.hasPolicyRequirement(gtx) && beanModel.hasPolicyRequirement(ntx)
                        || beanModel.hasPolicyRequirement(ltx) && beanModel.hasPolicyRequirement(ntx)) {
                    throw BeanMessages.MESSAGES.transactionPoliciesCannotCoexistImplementation(gtx, ltx, ntx, name);
                }
            }

            // Add any references
            for (ComponentReferenceModel reference : getReferences(switchyardNamespace, serviceClass, name)) {
                componentModel.addReference(reference);
            }
            
            compositeModel.addComponent(componentModel);
            componentModel.setName(getComponentName(name, service));
            compositeModel.addComponent(componentModel);
        }

        if (!compositeModel.getModelChildren().isEmpty()) {
            switchyardModel.setComposite(compositeModel);
        }

        return new ScannerOutput<SwitchYardModel>().setModel(switchyardModel);
    }

    private String getComponentName(String serviceName, Service service) {
        if (service == null) {
            return serviceName;
        }
        String componentName = service.componentName();
        return Service.EMPTY.equals(componentName) ? serviceName : componentName;
    }
    private List<Class<?>> scanForServiceBeans(ScannerInput<SwitchYardModel> input) throws IOException {
        IsAnnotationPresentFilter annoFilter = new IsAnnotationPresentFilter(Service.class);
        annoFilter.addType(Reference.class);
        PackageFilter pkgFilter = new PackageFilter(input.getIncludePackages().toArray(new Package[0]));
        for (Package pkg : input.getExcludePackages()) {
            pkgFilter.addExclude(pkg);
        }
        CompositeFilter filter = new CompositeFilter(annoFilter, pkgFilter);
        ClasspathScanner serviceScanner = new ClasspathScanner(filter);

        for (URL url : input.getURLs()) {
            serviceScanner.scan(url);
        }

        return filter.getMatchedTypes();
    }
    
    /**
     * Pick up @Reference fields in the specified class and
     * create corresponding ComponentReferenceModel
     */
    private Set<ComponentReferenceModel> getReferences(
                                            SwitchYardNamespace switchyardNamespace,
                                            Class<?> serviceClass,
                                            String name) throws IOException {
        HashSet<ComponentReferenceModel> references = new HashSet<ComponentReferenceModel>();
        for (Field field : serviceClass.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Reference.class)) {
                continue;
            }
            
            Class<?> reference = field.getType(); 
            ComponentReferenceModel referenceModel = new V1ComponentReferenceModel(switchyardNamespace.uri());
            InterfaceModel interfaceModel = new V1InterfaceModel(InterfaceModel.JAVA);
                  
            if (field.getAnnotation(Reference.class) != null) {
                Reference ref = field.getAnnotation(Reference.class);
                if (ref.value() == null || "".equals(ref.value())) {
                    referenceModel.setName(reference.getSimpleName());
                } else {
                    QName qname = QName.valueOf(ref.value());
                    referenceModel.setName(qname.getLocalPart());
                }
            } else {
                referenceModel.setName(reference.getSimpleName());
            }
            
            referenceModel.setInterface(interfaceModel);
            interfaceModel.setInterface(reference.getCanonicalName());
            // Add policy requirements to reference if specified
            Requires refRequires = field.getAnnotation(Requires.class);
            if (refRequires != null) {
                for (SecurityPolicy secPolicy : refRequires.security()) {
                    if (!secPolicy.supports(PolicyType.INTERACTION)) {
                        throw BeanMessages.MESSAGES.referenceOnlyCouldBeMarkedWithInteractionPolicyButIsNotTheOne(secPolicy.toString());
                    }
                    referenceModel.addPolicyRequirement(secPolicy.getName());
                }
                for (TransactionPolicy txPolicy : refRequires.transaction()) {
                    if (!txPolicy.supports(PolicyType.INTERACTION)) {
                        throw BeanMessages.MESSAGES.referenceOnlyCouldBeMarkedWithInteractionPolicyButIsNotTheOne(txPolicy.toString());
                    }
                    referenceModel.addPolicyRequirement(txPolicy.getName());
                }
                // Make sure we don't have conflicting policies
                String ptx = TransactionPolicy.PROPAGATES_TRANSACTION.getName();
                String stx = TransactionPolicy.SUSPENDS_TRANSACTION.getName();
                if (referenceModel.hasPolicyRequirement(ptx) && referenceModel.hasPolicyRequirement(stx)) {
                    throw BeanMessages.MESSAGES.transactionPoliciesCannotCoexistService(ptx, stx, name);
                }
            }
            references.add(referenceModel);
        }
        return references;
    }
}
