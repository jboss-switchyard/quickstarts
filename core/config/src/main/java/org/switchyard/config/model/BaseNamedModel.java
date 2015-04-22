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
package org.switchyard.config.model;

import java.net.URI;

import javax.xml.namespace.QName;

import org.switchyard.common.lang.Strings;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.config.Configuration;

/**
 * An abstract representation of a NamedModel, useful for subclassing.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public abstract class BaseNamedModel extends BaseModel implements NamedModel {
    
    protected static final String TARGET_NAMESPACE = "targetNamespace";

    protected BaseNamedModel(QName qname) {
        super(qname);
    }

    protected BaseNamedModel(String namespace, String name) {
        super(namespace, name);
    }

    protected BaseNamedModel(Configuration config) {
        super(config);
    }

    protected BaseNamedModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return getModelAttribute("name");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NamedModel setName(String name) {
        setModelAttribute("name", name);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTargetNamespace() {
        String tns = null;
        Model model = this;
        while (model instanceof BaseModel) {
            tns = Strings.trimToNull(((BaseModel)model).getModelAttribute(TARGET_NAMESPACE));
            if (tns != null) {
                break;
            }
            model = model.getModelParent();
        }
        return tns;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getTargetNamespaceURI() {
        String tns = getTargetNamespace();
        return tns != null ? URI.create(tns) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QName getQName() {
        return XMLHelper.createQName(getTargetNamespace(), getName());
    }

}
