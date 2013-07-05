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
package org.switchyard.tools.forge.plugin;

import java.io.File;

import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.resources.FileResource;
import org.switchyard.common.io.pull.StringPuller;
import org.switchyard.common.type.Classes;

/**
 * Represents a template file with <code>${name}</code> tokens in it.  Calls to 
 * replaceToken will change the state of the content store in this object, so instances
 * of TemplateResource should not be re-used if different values for a given token name
 * are required
 */
public class TemplateResource {
    
    /** Substitution token for service name. */
    public static final String VAR_SERVICE_NAME = "${service.name}";
    /** Substitution token for package name. */
    public static final String VAR_PACKAGE_NAME = "${package.name}";
    
    private String _content;
    
    /**
     * Create a new template resource from the specified template path.
     * @param templatePath path to a template file
     * @throws java.io.IOException error reading the template file
     */
    public TemplateResource(String templatePath) throws java.io.IOException {
        _content = new StringPuller().pull(Classes.getResourceAsStream(templatePath));
        if (_content == null) {
            throw new java.io.IOException("Unable to load template from path: " + templatePath);
        }
    }
    
    /**
     * Replace all instances of <code>${name}</code> with the specified value.
     * @param name name of the token
     * @param value value to replace the token with
     * @return a reference to this object for chaining calls
     */
    public TemplateResource replaceToken(String name, String value) {
        _content = _content.replaceAll(regexFormat(name), value);
        return this;
    }
    
    /**
     * Replace all instances of VAR_SERVICE_NAME with the specified value.
     * @param serviceName service name to use
     * @return a reference to this object for chaining calls
     */
    public TemplateResource serviceName(String serviceName) {
        return replaceToken(VAR_SERVICE_NAME, serviceName);
    }

    /**
     * Replace all instances of VAR_PACKAGE_NAME with the specified value.
     * @param packageName package name to use
     * @return a reference to this object for chaining calls
     */
    public TemplateResource packageName(String packageName) {
        return replaceToken(VAR_PACKAGE_NAME, packageName);
    }
    
    /**
     * Write the state of this resource to the specified file.
     * @param resource file to write to
     * @throws java.io.IOException error writing to file
     */
    public void writeResource(FileResource<?> resource) throws java.io.IOException {
        resource.setContents(_content);
    }
    
    /**
     * Write the content of this template as a Java source file.  Any 
     * VAR_PACKAGE_NAME and VAR_SERVICE_NAME tokens are replaced by input
     * parameters to this method.
     * @param resources the resource facet to use when creating the file
     * @param packageName the package name used in the class
     * @param className the local name of the class
     * @param isTest true if this file is a test source file, false otherwise
     * @return name of the file created
     * @throws java.io.IOException error writing java source
     */
    public String writeJavaSource(ResourceFacet resources,
            String packageName, 
            String className,
            boolean isTest) throws java.io.IOException {
        
        packageName(packageName);

        String destDir = ".." + File.separator + ".." + File.separator
            + File.separator + (isTest ? "test" : "main")
            + File.separator + "java";
        if (packageName != null && packageName.length() > 0) {
            for (String pkgDir : packageName.split("\\.")) {
                destDir += File.separator + pkgDir;
            }
        }
        String destFile = className + ".java";
        writeResource(resources.getResource(destDir + File.separator + destFile));
        
        return destFile;
    }
    
    private String regexFormat(String token) {
        return token.replaceAll("\\$", "\\\\\\$")
                .replaceAll("\\{", "\\\\\\{")
                .replaceAll("\\}", "\\\\\\}");
    }
}
