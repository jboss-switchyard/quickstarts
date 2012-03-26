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

package org.switchyard.tools.forge.bean;

import javax.inject.Inject;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaInterface;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.PromptType;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresProject;
import org.jboss.forge.shell.plugins.Topic;
import org.switchyard.component.bean.Reference;

/**
 * Forge plugin for Bean references commands.
 * 
 * @author Antollini Mario.
 * 
 */
@Alias("bean-reference")
@RequiresProject
@RequiresFacet({BeanFacet.class})
@Topic("SOA")
@Help("Provides commands to create, edit, and remove references in CDI-based services in SwitchYard.")
public class BeanReferencePlugin implements Plugin {
    
    @Inject
    private Project _project;
    
    @Inject
    private Shell _shell;
    
    /**
     * Create a new Bean service interface and implementation.
     * @param beanName bean name
     * @param referenceName reference name
     * @param referenceBeanName the bean to be referenced
     * @param out shell output
     * @throws java.io.IOException trouble reading Switchyard config or reading/writing Bean file
     */
    @Command(value = "create", help = "Create a new reference in a CDI bean.")
    public void newReference(
            @Option(required = true,
                     name = "beanName",
                     description = "The bean where the refrence will be added") 
             final String beanName,
             @Option(required = true,
                     name = "referenceName",
                     description = "The name of the reference to be added") 
             final String referenceName,
             @Option(required = false,
                     name = "referenceBeanName",
                     description = "The bean to be referenced") 
             final String referenceBeanName,
             final PipeOut out)
    throws java.io.IOException {
      
        String pkgName = _project.getFacet(MetadataFacet.class).getTopLevelPackage();
        
        if (pkgName == null) {
            pkgName = _shell.promptCommon(
                "Java package for service interface and implementation:",
                PromptType.JAVA_PACKAGE);
        }
        
        final MetadataFacet meta = _project.getFacet(MetadataFacet.class);
        final JavaSourceFacet java = _project.getFacet(JavaSourceFacet.class);
        
        // Make sure the bean exists
        JavaResource beanFile = _project.getProjectRoot().getChildOfType(JavaResource.class, "src/main/java/" + meta.getTopLevelPackage().replace(".", "/") + "/" + beanName + "Bean.java");
        if (!beanFile.exists()) {
            out.println(out.renderColor(ShellColor.RED, "Bean not found: " + beanName));
            return;
        }


        JavaClass javaClass = JavaParser.parse(JavaClass.class, beanFile.getUnderlyingResourceObject());
        
        if (!javaClass.hasImport(Inject.class)) {
            javaClass.addImport(Inject.class);
        }
        if (!javaClass.hasImport(Reference.class)) {
            javaClass.addImport(Reference.class);
        }
        
        String referenceBeanJavaType = referenceBeanName;
        if (referenceBeanJavaType == null) {
            referenceBeanJavaType = referenceName;
        }
        
        String referenceFieldName = new StringBuilder(referenceBeanJavaType.length())
            .append(Character.toLowerCase(referenceBeanJavaType.charAt(0)))
            .append(referenceBeanJavaType.substring(1))
            .toString();
        
        if (javaClass.hasField(referenceFieldName)) {
            referenceFieldName = referenceFieldName + Math.random() * 100;
        }
        
        Field<JavaClass> referenceField = javaClass.addField("private " + referenceBeanJavaType + " " + referenceFieldName + ";");
        
        referenceField.addAnnotation(Inject.class);
        Annotation<?> referenceAnnotation = referenceField.addAnnotation(Reference.class);
        referenceAnnotation.setStringValue(referenceName);
        
        if (javaClass.hasSyntaxErrors()) {
            out.println(out.renderColor(ShellColor.YELLOW, 
                    "WARNING: " + javaClass.getName() + " seems to have syntax errors."));
        }
        
        java.saveJavaSource(javaClass);
        
        //If not present already, let's create a bare bean interface file for the specified 
        //referenceName in order to avoid compilation errors.
        JavaResource referenceFile = _project.getProjectRoot().getChildOfType(JavaResource.class, "src/main/java/" + meta.getTopLevelPackage().replace(".", "/") + "/" + referenceBeanJavaType + ".java");
        if (!referenceFile.exists()) {

            JavaInterface skeletonReferencedBean = JavaParser.create(JavaInterface.class);
            skeletonReferencedBean.setPackage(meta.getTopLevelPackage());
            skeletonReferencedBean.setName(referenceBeanJavaType);
            
            if (skeletonReferencedBean.hasSyntaxErrors()) {
                out.println(out.renderColor(ShellColor.YELLOW, 
                        "WARNING: " + skeletonReferencedBean.getName() + " seems to have syntax errors."));
            }
            
            java.saveJavaSource(skeletonReferencedBean);
        }
        
        // Notify success to the user
        out.println(out.renderColor(ShellColor.BLUE, 
                "NOTE: Run 'mvn package' to make " + referenceName + " visible to SwitchYard shell."));
    }
    
            
}
