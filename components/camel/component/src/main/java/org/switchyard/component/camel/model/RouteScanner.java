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
package org.switchyard.component.camel.model;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.List;

import org.switchyard.common.type.classpath.AbstractTypeFilter;
import org.switchyard.common.type.classpath.ClasspathScanner;
import org.switchyard.component.camel.Route;
import org.switchyard.component.camel.model.v1.V1CamelImplementationModel;
import org.switchyard.config.model.Scannable;
import org.switchyard.config.model.Scanner;
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

/**
 * Scans for classes with @Route methods and creates a Camel Route definition
 * for each.
 * @author <a href="mailto:eduardo.devera@gmail.com">eduardo.devera</a>
 */
public class RouteScanner implements Scanner<SwitchYardModel> {

    @Override
    public ScannerOutput<SwitchYardModel> scan(ScannerInput<SwitchYardModel> input) throws IOException {
        SwitchYardNamespace switchyardNamespace = input.getSwitchyardNamespace();
        SwitchYardModel switchyardModel = new V1SwitchYardModel(switchyardNamespace.uri());
        CompositeModel compositeModel = new V1CompositeModel();
        compositeModel.setName(input.getCompositeName());

        CamelNamespace camelNamespace = CamelNamespace.DEFAULT;
        for (CamelNamespace value : CamelNamespace.values()) {
            if (value.versionMatches(switchyardNamespace)) {
                camelNamespace = value;
                break;
            }
        }

        List<Class<?>> routeClasses = scanForRoutes(input.getURLs());

        // Create a Camel component model for each class
        for (Class<?> routeClass : routeClasses) {
            // Top-level component definition
            ComponentModel componentModel = new V1ComponentModel();
            componentModel.setName(routeClass.getSimpleName());

            // Component implementation definition
            CamelComponentImplementationModel camelModel = new V1CamelImplementationModel(camelNamespace.uri());
            camelModel.setJavaClass(routeClass.getName());
            componentModel.setImplementation(camelModel);
            compositeModel.addComponent(componentModel);

            // Component service definition
            ComponentServiceModel serviceModel = new V1ComponentServiceModel(switchyardNamespace.uri());
            InterfaceModel csiModel = new V1InterfaceModel(InterfaceModel.JAVA);
            Class<?> serviceInterface = routeClass.getAnnotation(Route.class).value();
            serviceModel.setName(getServiceName(routeClass));
            csiModel.setInterface(serviceInterface.getName());
            serviceModel.setInterface(csiModel);
            componentModel.addService(serviceModel);

            // Component reference definition(s)
            // Need to add these!
        }

        if (!compositeModel.getModelChildren().isEmpty()) {
            switchyardModel.setComposite(compositeModel);
        }

        return new ScannerOutput<SwitchYardModel>().setModel(switchyardModel);
    }

    private String getServiceName(Class<?> routeClass) {
        return routeClass.getAnnotation(Route.class).name().equals(Route.EMPTY)
                ? routeClass.getAnnotation(Route.class).value().getSimpleName()
                : routeClass.getAnnotation(Route.class).name();
    }

    private List<Class<?>> scanForRoutes(List<URL> urls) throws IOException {
        AbstractTypeFilter filter = new RouteFilter();
        ClasspathScanner scanner = new ClasspathScanner(filter);

        for (URL url : urls) {
            scanner.scan(url);
        }

        return filter.getMatchedTypes();
    }

    private class RouteFilter extends AbstractTypeFilter {
        @Override
        public boolean matches(Class<?> clazz) {
            // Check to see if it's been excluded from scans
            Scannable scannable = clazz.getAnnotation(Scannable.class);
            if (scannable != null && !scannable.value()) {
                return false;
            }

            // Can't be an interface or abstract class
            if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
                return false;
            }

            // Must have a no-arg constructor
            try {
                clazz.getConstructor();
            } catch (NoSuchMethodException e) {
                return false;
            }

            // Check for @Route definitions
            return clazz.isAnnotationPresent(Route.class);
        }
    }
}
