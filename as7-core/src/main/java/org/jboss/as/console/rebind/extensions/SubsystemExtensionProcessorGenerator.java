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
package org.jboss.as.console.rebind.extensions;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jboss.as.console.client.shared.SubsystemExtension;
import org.jboss.as.console.client.shared.SubsystemExtension.SubsystemGroupDefinition;
import org.jboss.as.console.client.shared.SubsystemExtension.SubsystemItemDefinition;
import org.jboss.as.console.client.shared.SubsystemExtensionProcessor;
import org.jboss.as.console.client.shared.SubsystemGroup;
import org.jboss.as.console.client.shared.SubsystemGroupItem;
import org.jboss.as.console.client.widgets.nav.Predicate;
import org.jboss.ballroom.client.layout.LHSNavTreeItem;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * SubsystemExtensionProcessorGenerator
 * 
 * GWT code generator for SubsystemExtensionProcessor. A code generator is used
 * because reflection cannot be used on the client side. This generator scans
 * for classes annotated with SubsystemExtension and adds them to the manager.
 * 
 * @author Rob Cernich
 */
public class SubsystemExtensionProcessorGenerator extends Generator {

    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName)
            throws UnableToCompleteException {
        TypeOracle typeOracle = context.getTypeOracle();
        JClassType subsystemExtensionManager = typeOracle.findType(typeName);
        if (subsystemExtensionManager == null) {
            logger.log(TreeLogger.ERROR, "Unable to find metadata for type '" + typeName + "'", null);
            throw new UnableToCompleteException();
        }
        if (subsystemExtensionManager.isInterface() == null) {
            logger.log(TreeLogger.ERROR, subsystemExtensionManager.getQualifiedSourceName() + " is not an interface",
                    null);
            throw new UnableToCompleteException();
        }

        List<SubsystemExtension> subsystemExtensions = new ArrayList<SubsystemExtension>();
        for (JClassType type : typeOracle.getTypes()) {
            if (type.isAnnotationPresent(SubsystemExtension.class)) {
                subsystemExtensions.add(type.getAnnotation(SubsystemExtension.class));
            }
        }

        String packageName = subsystemExtensionManager.getPackage().getName();
        String className = subsystemExtensionManager.getSimpleSourceName() + "Impl";

        generateClass(logger, context, packageName, className, subsystemExtensions);

        return packageName + "." + className;
    }

    private void generateClass(TreeLogger logger, GeneratorContext context, String packageName, String className,
            List<SubsystemExtension> subsystemExtensions) {
        PrintWriter pw = context.tryCreate(logger, packageName, className);
        if (pw == null) {
            return;
        }

        ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, className);

        // imports
        composerFactory.addImport(ArrayList.class.getCanonicalName());
        composerFactory.addImport(LinkedHashMap.class.getCanonicalName());
        composerFactory.addImport(List.class.getCanonicalName());
        composerFactory.addImport(Map.class.getCanonicalName());
        composerFactory.addImport(SubsystemExtensionProcessor.class.getCanonicalName());
        composerFactory.addImport(SubsystemGroup.class.getCanonicalName());
        composerFactory.addImport(SubsystemGroupItem.class.getCanonicalName());
        composerFactory.addImport(Predicate.class.getCanonicalName());
        composerFactory.addImport(LHSNavTreeItem.class.getCanonicalName());

        // interface
        composerFactory.addImplementedInterface(SubsystemExtensionProcessor.class.getCanonicalName());

        SourceWriter sw = composerFactory.createSourceWriter(context, pw);

        // begin class definition
        sw.indent();

        // fields
        sw.println("private final Map<String, SubsystemGroup> extensionGroups = new LinkedHashMap<String, SubsystemGroup>();");
        sw.println("private final List<Predicate> _runtimeMetricsExtensions = new ArrayList<Predicate>();");
        sw.println("private final List<Predicate> _runtimeOperationsExtensions = new ArrayList<Predicate>();");

        // constructor
        sw.println("public " + className + "() {");
        sw.indent();
        for (SubsystemExtension extension : subsystemExtensions) {
            for (SubsystemGroupDefinition groupDef : extension.groups()) {
                sw.println("SubsystemGroup group;");
                sw.println("if (extensionGroups.containsKey(\"%s\")) {", groupDef.name());
                sw.indentln("group = extensionGroups.get(\"%s\");", groupDef.name());
                sw.println("} else {");
                sw.indent();
                sw.println("group = new SubsystemGroup(\"%s\");", groupDef.name());
                sw.println("extensionGroups.put(group.getName(), group);");
                sw.outdent();
                sw.println("}");
                for (SubsystemItemDefinition itemDef : groupDef.items()) {
                    sw.println("group.getItems().add(new SubsystemGroupItem(\"%s\", \"%s\", \"%s\"));", itemDef.name(),
                            extension.subsystem(), itemDef.presenter());
                }
            }
            for (SubsystemItemDefinition runtimeItemDef : extension.metrics()) {
                sw.println("_runtimeMetricsExtensions.add(new Predicate(\"%s\", new LHSNavTreeItem(\"%s\", \"%s\")));",
                        extension.subsystem(), runtimeItemDef.name(), runtimeItemDef.presenter());
            }
            for (SubsystemItemDefinition runtimeItemDef : extension.runtime()) {
                sw.println("_runtimeOperationsExtensions.add(new Predicate(\"%s\", new LHSNavTreeItem(\"%s\", \"%s\")));",
                        extension.subsystem(), runtimeItemDef.name(), runtimeItemDef.presenter());
            }
        }
        sw.outdent();
        sw.println("}");

        // methods
        // processProfileExtensions
        sw.println("public void processProfileExtensions(Map<String, SubsystemGroup> groups) {");
        sw.indent();
        sw.println("for (Map.Entry<String, SubsystemGroup> entry : extensionGroups.entrySet()) {");
        sw.indent();
        sw.println("if (groups.containsKey(entry.getKey())) {");
        sw.indent();
        sw.println("SubsystemGroup group = groups.get(entry.getKey());");
        sw.println("group.getItems().addAll(entry.getValue().getItems());");
        sw.outdent();
        sw.println("} else {");
        sw.indent();
        sw.println("SubsystemGroup group = entry.getValue();");
        sw.println("groups.put(group.getName(), group);");
        sw.outdent();
        sw.println("}");
        sw.outdent();
        sw.println("}");
        sw.outdent();
        sw.println("}");

        // getRuntimeMetricsExtensions
        sw.println("public List<Predicate> getRuntimeMetricsExtensions() {");
        sw.indentln("return _runtimeMetricsExtensions;");
        sw.println("}");

        // getRuntimeOperationsExtensions
        sw.println("public List<Predicate> getRuntimeOperationsExtensions() {");
        sw.indentln("return _runtimeOperationsExtensions;");
        sw.println("}");

        // close it out
        sw.outdent();
        sw.println("}");

        context.commit(logger, pw);
    }

}
