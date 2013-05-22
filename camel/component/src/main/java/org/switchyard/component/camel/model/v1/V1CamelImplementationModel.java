/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.camel.model.v1;

import static org.switchyard.component.camel.model.Constants.COMPONENT_NAMESPACE_V1;

import org.switchyard.component.camel.common.model.v1.NameValueModel;
import org.switchyard.component.camel.model.CamelComponentImplementationModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.v1.V1ComponentImplementationModel;

/**
 * Version 1 implementation.
 * 
 * @author Daniel Bevenius
 */
public class V1CamelImplementationModel extends V1ComponentImplementationModel
    implements CamelComponentImplementationModel {

    // The class attribute for Java DSL routes
    private static final String CLASS = "class";

    // The path attribute for XML DSL routes
    private static final String PATH = "path";

    /**
     * Create a new CamelImplementationModel.
     */
    public V1CamelImplementationModel() {
        super(CAMEL, COMPONENT_NAMESPACE_V1);
    }

    /**
     * Create a CamelImplementationModel from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V1CamelImplementationModel(Configuration config, Descriptor desc) {
        super(config, desc);
        setModelChildrenOrder();
    }

    @Override
    public String getJavaClass() {
        Configuration classConfig = getModelConfiguration().getFirstChild(JAVA);
        return classConfig != null ? classConfig.getAttribute(CLASS) : null;
    }

    @Override
    public V1CamelImplementationModel setJavaClass(String className) {
        Configuration classConfig = getModelConfiguration().getFirstChild(JAVA);
        if (classConfig == null) {
            NameValueModel model = new NameValueModel(getNamespaceURI(), JAVA);
            model.getModelConfiguration().setAttribute(CLASS, className);
            setChildModel(model);
        } else {
            classConfig.setAttribute(CLASS, className);
        }
        return this;
    }

    @Override
    public String getXMLPath() {
        Configuration classConfig = getModelConfiguration().getFirstChild(XML);
        return classConfig != null ? classConfig.getAttribute(PATH) : null;
    }

    @Override
    public CamelComponentImplementationModel setXMLPath(String path) {
        Configuration pathConfig = getModelConfiguration().getFirstChild(XML);
        if (pathConfig == null) {
            NameValueModel model = new NameValueModel(getNamespaceURI(), XML);
            model.getModelConfiguration().setAttribute(PATH, path);
            setChildModel(model);
        } else {
            pathConfig.setAttribute(PATH, path);
        }
        return this;
    }

}
