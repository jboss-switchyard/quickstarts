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
package org.switchyard.config.model.validate.v1;

import javax.xml.namespace.QName;

import org.switchyard.common.xml.XMLHelper;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseTypedModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.validate.ValidateModel;
import org.switchyard.config.model.validate.ValidatesModel;

/**
 * An abstract representation of a ValidateModel.
 *
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public abstract class V1BaseValidateModel extends BaseTypedModel implements ValidateModel {

    protected V1BaseValidateModel(String namespace, String type) {
        this(new QName(namespace, ValidateModel.VALIDATE + '.' + type));
    }

    protected V1BaseValidateModel(QName qname) {
        super(qname);
    }

    protected V1BaseValidateModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidatesModel getValidates() {
        return (ValidatesModel)getModelParent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QName getName() {
        return XMLHelper.createQName(getModelAttribute(ValidateModel.NAME));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidateModel setName(QName name) {
        setModelAttribute(ValidateModel.NAME, name != null ? name.toString() : null);
        return this;
    }

}
