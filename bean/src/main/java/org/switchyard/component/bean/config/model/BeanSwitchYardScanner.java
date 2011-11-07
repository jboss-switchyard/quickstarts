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

package org.switchyard.component.bean.config.model;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.switchyard.common.type.classpath.ClasspathScanner;
import org.switchyard.common.type.classpath.IsAnnotationPresentFilter;
import org.switchyard.component.bean.Reference;
import org.switchyard.component.bean.Service;
import org.switchyard.component.bean.config.model.v1.V1BeanComponentImplementationModel;
import org.switchyard.config.model.Scanner;
import org.switchyard.config.model.ScannerInput;
import org.switchyard.config.model.ScannerOutput;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentReferenceInterfaceModel;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.JavaComponentServiceInterfaceModel;
import org.switchyard.config.model.composite.v1.V1ComponentModel;
import org.switchyard.config.model.composite.v1.V1ComponentReferenceModel;
import org.switchyard.config.model.composite.v1.V1ComponentServiceModel;
import org.switchyard.config.model.composite.v1.V1CompositeModel;
import org.switchyard.config.model.composite.v1.V1JavaComponentReferenceInterfaceModel;
import org.switchyard.config.model.composite.v1.V1JavaComponentServiceInterfaceModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.switchyard.v1.V1SwitchYardModel;

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
        SwitchYardModel switchyardModel = new V1SwitchYardModel();
        CompositeModel compositeModel = new V1CompositeModel();
        compositeModel.setName(input.getName());

        List<Class<?>> serviceClasses = scanForServiceBeans(input.getURLs());

        for (Class<?> serviceClass : serviceClasses) {
            if (serviceClass.isInterface()) {
                continue;
            }
            if (Modifier.isAbstract(serviceClass.getModifiers())) {
                continue;
            }

            ComponentModel componentModel = new V1ComponentModel();
            String name;
            
            // @Service annotation is required for generation
            Service service = serviceClass.getAnnotation(Service.class);
            if (service == null) {
                continue;
            }
            
            BeanComponentImplementationModel beanModel = new V1BeanComponentImplementationModel();
            beanModel.setClazz(serviceClass.getName());
            componentModel.setImplementation(beanModel);
            
            Class<?> iface = service.value();
            ComponentServiceModel serviceModel = new V1ComponentServiceModel();
            JavaComponentServiceInterfaceModel csiModel = new V1JavaComponentServiceInterfaceModel();

            if (service.name().equals(Service.EMPTY)) {
                name = iface.getSimpleName();
            } else {
                name = service.name();
            }

            serviceModel.setName(name);
            serviceModel.setInterface(csiModel);
            csiModel.setInterface(iface.getName());
            componentModel.addService(serviceModel);

            // Add any references
            for (Class<?> reference : getReferences(serviceClass)) {
                ComponentReferenceModel referenceModel = new V1ComponentReferenceModel();
                ComponentReferenceInterfaceModel interfaceModel = new V1JavaComponentReferenceInterfaceModel();

                referenceModel.setName(reference.getSimpleName());
                referenceModel.setInterface(interfaceModel);
                interfaceModel.setInterface(reference.getCanonicalName());
                componentModel.addReference(referenceModel);
            }

            compositeModel.addComponent(componentModel);
            componentModel.setName(name);
            compositeModel.addComponent(componentModel);
        }

        if (!compositeModel.getModelChildren().isEmpty()) {
            switchyardModel.setComposite(compositeModel);
        }

        return new ScannerOutput<SwitchYardModel>().setModel(switchyardModel);
    }

    private List<Class<?>> scanForServiceBeans(List<URL> urls) throws IOException {
        IsAnnotationPresentFilter filter = new IsAnnotationPresentFilter(Service.class);
        ClasspathScanner serviceScanner = new ClasspathScanner(filter);

        for (URL url : urls) {
            serviceScanner.scan(url);
        }

        return filter.getMatchedTypes();
    }
    
    /**
     * Pick up @Reference fields in the specified class
     */
    private Set<Class<?>> getReferences(Class<?> serviceClass) {
        HashSet<Class<?>> references = new HashSet<Class<?>>();
        for (Field field : serviceClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Reference.class)) {
                references.add(field.getType());
            }
        }
        return references;
    }
}
