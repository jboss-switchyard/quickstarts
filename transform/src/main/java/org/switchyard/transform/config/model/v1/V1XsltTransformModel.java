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

package org.switchyard.transform.config.model.v1;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.config.model.transform.v1.V1BaseTransformModel;
import org.switchyard.transform.config.model.XsltTransformModel;
import org.switchyard.transform.internal.TransformerFactoryClass;
import org.switchyard.transform.xslt.internal.XsltTransformFactory;

/**
 * @author Alejandro Montenegro <a
 *         href="mailto:aamonten@gmail.com">aamonten@gmail.com</a>
 */
@TransformerFactoryClass(XsltTransformFactory.class)
public class V1XsltTransformModel extends V1BaseTransformModel implements XsltTransformModel {

    /**
     * Constructs a new V1XsltTransformModel.
     * @param namespace namespace
     */
    public V1XsltTransformModel(String namespace) {
        super(new QName(namespace, TransformModel.TRANSFORM + '.' + XSLT));
    }

    /**
     * Constructs a new V1XsltTransformModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    protected V1XsltTransformModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getXsltFile() {
        return getModelAttribute(XSLT_FILE_URI);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V1XsltTransformModel setXsltFile(String xsltFile) {
        setModelAttribute(XSLT_FILE_URI, xsltFile);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean failOnWarning() {
        String failOnWarn = getModelAttribute(FAIL_ON_WARNING);
        return Boolean.parseBoolean(failOnWarn);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public XsltTransformModel setFailOnWarning(boolean failOnWarning) {
        setModelAttribute(FAIL_ON_WARNING, Boolean.toString(failOnWarning));
        return this;
    }

}
