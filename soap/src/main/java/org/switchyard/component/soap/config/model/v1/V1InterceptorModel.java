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
package org.switchyard.component.soap.config.model.v1;

import static org.switchyard.component.soap.config.model.SOAPBindingModel.DEFAULT_NAMESPACE;
import static org.switchyard.config.model.property.PropertiesModel.PROPERTIES;

import org.switchyard.common.type.Classes;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.soap.config.model.InterceptorModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseNamedModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.property.PropertiesModel;

/**
 * The 1st version InterceptorModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public class V1InterceptorModel extends BaseNamedModel implements InterceptorModel {

    private PropertiesModel _properties;

    /**
     * Creates a new InterceptorModel.
     */
    public V1InterceptorModel() {
        super(XMLHelper.createQName(DEFAULT_NAMESPACE, INTERCEPTOR));
        setModelChildrenOrder(PROPERTIES);
    }

    /**
     * Creates a new InterceptorModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1InterceptorModel(Configuration config, Descriptor desc) {
        super(config, desc);
        setModelChildrenOrder(PROPERTIES);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getClazz(ClassLoader loader) {
        String c = getModelAttribute("class");
        return c != null ? Classes.forName(c, loader) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InterceptorModel setClazz(Class<?> clazz) {
        String c = clazz != null ? clazz.getName() : null;
        setModelAttribute("class", c);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertiesModel getProperties() {
        if (_properties == null) {
            _properties = (PropertiesModel)getFirstChildModel(PROPERTIES);
        }
        return _properties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InterceptorModel setProperties(PropertiesModel properties) {
        setChildModel(properties);
        _properties = properties;
        return this;
    }

}
