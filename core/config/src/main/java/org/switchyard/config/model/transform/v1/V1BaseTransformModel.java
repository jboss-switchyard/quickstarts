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
package org.switchyard.config.model.transform.v1;

import javax.xml.namespace.QName;

import org.switchyard.common.xml.XMLHelper;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseTypedModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.config.model.transform.TransformsModel;

/**
 * An abstract representation of a TransformModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public abstract class V1BaseTransformModel extends BaseTypedModel implements TransformModel {

    protected V1BaseTransformModel(String namespace, String type) {
        this(new QName(namespace, TransformModel.TRANSFORM + '.' + type));
    }

    protected V1BaseTransformModel(QName qname) {
        super(qname);
    }

    protected V1BaseTransformModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransformsModel getTransforms() {
        return (TransformsModel)getModelParent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QName getFrom() {
        return XMLHelper.createQName(getModelAttribute(TransformModel.FROM));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransformModel setFrom(QName from) {
        setModelAttribute(TransformModel.FROM, from != null ? from.toString() : null);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QName getTo() {
        return XMLHelper.createQName(getModelAttribute(TransformModel.TO));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransformModel setTo(QName to) {
        setModelAttribute(TransformModel.TO, to != null ? to.toString() : null);
        return this;
    }

}
