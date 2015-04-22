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
package org.switchyard.console.components.rebind;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.switchyard.console.components.client.extension.ComponentExtension;
import org.switchyard.console.components.client.extension.ComponentProvider;
import org.switchyard.console.components.client.internal.ComponentExtensionManager;
import org.switchyard.console.components.client.internal.ComponentProviderProxyImpl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.google.inject.Inject;

/**
 * ComponentExtensionManagerGenerator
 * 
 * GWT code generator for ComponentExtensionManager. A code generator is used
 * because reflection cannot be used on the client side. This generator scans
 * for classes annotated with ComponentExtension and adds them to the manager.
 * 
 * @author Rob Cernich
 */
public class ComponentExtensionManagerGenerator extends Generator {

    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName)
            throws UnableToCompleteException {
        TypeOracle typeOracle = context.getTypeOracle();
        JClassType componentExtensionManager = typeOracle.findType(typeName);
        if (componentExtensionManager == null) {
            logger.log(TreeLogger.ERROR, "Unable to find metadata for type '" + typeName + "'", null);
            throw new UnableToCompleteException();
        }
        if (componentExtensionManager.isInterface() == null) {
            logger.log(TreeLogger.ERROR, componentExtensionManager.getQualifiedSourceName() + " is not an interface",
                    null);
            throw new UnableToCompleteException();
        }

        JClassType componentProvider = typeOracle.findType(ComponentProvider.class.getCanonicalName());
        if (componentProvider == null) {
            logger.log(TreeLogger.ERROR, "Unable to find metadata for type 'ComponentProvider'", null);
            throw new UnableToCompleteException();
        }

        List<JClassType> componentExtensionClasses = new ArrayList<JClassType>();
        for (JClassType type : typeOracle.getTypes()) {
            if (type.isAnnotationPresent(ComponentExtension.class)) {
                if (type.isClass() == null || type.isAbstract()) {
                    // type must be a class that can be instantiated
                    logger.log(TreeLogger.ERROR, "ComponentExtension type '" + type.getQualifiedSourceName()
                            + "' cannot be instantiated.", null);
                    throw new UnableToCompleteException();
                } else if (!type.isDefaultInstantiable()) {
                    // type must have default constructor
                    logger.log(TreeLogger.ERROR, "ComponentExtension type '" + type.getQualifiedSourceName()
                            + "' does not provide a default constructor.", null);
                    throw new UnableToCompleteException();
                } else if (!type.isAssignableTo(componentProvider)) {
                    // type must implement ComponentProvider
                    logger.log(TreeLogger.ERROR, "ComponentExtension type '" + type.getQualifiedSourceName()
                            + "' does not implement ComponentProvider.", null);
                    throw new UnableToCompleteException();
                }
                componentExtensionClasses.add(type);
            }
        }

        String packageName = componentExtensionManager.getPackage().getName();
        String className = componentExtensionManager.getSimpleSourceName() + "Impl";

        generateClass(logger, context, packageName, className, componentExtensionClasses);

        return packageName + "." + className;
    }

    private void generateClass(TreeLogger logger, GeneratorContext context, String packageName, String className,
            List<JClassType> componentExtensionClasses) {
        PrintWriter pw = context.tryCreate(logger, packageName, className);
        if (pw == null) {
            return;
        }

        ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, className);

        // imports
        composerFactory.addImport(HashMap.class.getCanonicalName());
        composerFactory.addImport(Map.class.getCanonicalName());
        composerFactory.addImport(ComponentProvider.class.getCanonicalName());
        composerFactory.addImport(ComponentExtensionManager.class.getCanonicalName());
        composerFactory.addImport(ComponentProviderProxyImpl.class.getCanonicalName());
        composerFactory.addImport(GWT.class.getCanonicalName());
        composerFactory.addImport(Inject.class.getCanonicalName());

        // interface
        composerFactory.addImplementedInterface(ComponentExtensionManager.class.getCanonicalName());

        SourceWriter sw = composerFactory.createSourceWriter(context, pw);

        // fields
        sw.println("private Map<String, ComponentProviderProxy> _providers = new HashMap<String, ComponentProviderProxy>();");
        sw.println("private Map<String, String> _typeToName = new HashMap<String, String>();");

        // constructor
        sw.println("public " + className + "() {");
        sw.indent();
        for (JClassType extensionClass : componentExtensionClasses) {
            ComponentExtension extensionAnnotation = extensionClass.getAnnotation(ComponentExtension.class);
            sw.println("_providers.put(\"" + extensionAnnotation.componentName()
                    + "\", new ComponentProviderProxyImpl(\"" + extensionAnnotation.displayName() + "\") {");
            sw.indent();
            sw.println("public ComponentProvider instantiate() {");
            sw.indentln("return GWT.create(" + extensionClass.getQualifiedSourceName() + ".class);");
            sw.println("}");
            sw.outdent();
            sw.println("});");
            for (String type : extensionAnnotation.activationTypes()) {
                sw.println("_typeToName.put(\"" + type + "\", \"" + extensionAnnotation.componentName() + "\");");
            }
        }
        sw.outdent();
        sw.println("}");

        // methods
        // getExtensionProviders
        sw.println("public Map<String, ComponentProviderProxy> getExtensionProviders() {");
        sw.indentln("return _providers;");
        sw.println("}");

        // getExtensionProviderByComponentName
        sw.println("public ComponentProviderProxy getExtensionProviderByComponentName(String componentName) {");
        sw.indent();
        sw.println("if (_providers.containsKey(componentName)) {");
        sw.indentln("return _providers.get(componentName);");
        sw.println("}");
        sw.println("return null;");
        sw.outdent();
        sw.println("}");

        // getExtensionProviderByTypeName
        sw.println("public ComponentProviderProxy getExtensionProviderByTypeName(String typeName) {");
        sw.indent();
        sw.println("if (_typeToName.containsKey(typeName)) {");
        sw.indentln("return getExtensionProviderByComponentName(_typeToName.get(typeName));");
        sw.println("}");
        sw.println("return null;");
        sw.outdent();
        sw.println("}");

        // close it out
        sw.outdent();
        sw.println("}");

        context.commit(logger, pw);
    }
}
