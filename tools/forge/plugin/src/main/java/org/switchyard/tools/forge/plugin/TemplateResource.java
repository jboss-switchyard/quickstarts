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
package org.switchyard.tools.forge.plugin;

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
     * Write the state of this resource to the specified file.
     * @param resource file to write to
     * @throws java.io.IOException error writing to file
     */
    public void writeTo(FileResource<?> resource) throws java.io.IOException {
        resource.setContents(_content);
    }
    
    private String regexFormat(String token) {
        return token.replaceAll("\\$", "\\\\\\$")
                .replaceAll("\\{", "\\\\\\{")
                .replaceAll("\\}", "\\\\\\}");
    }
}
