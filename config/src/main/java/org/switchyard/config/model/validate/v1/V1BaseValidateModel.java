/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
package org.switchyard.config.model.validate.v1;

import javax.xml.namespace.QName;

import org.switchyard.common.xml.XMLHelper;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseTypedModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.validate.ValidateModel;
import org.switchyard.config.model.validate.ValidatesModel;

/**
 * An abstract representation of a ValidateModel.
 *
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public abstract class V1BaseValidateModel extends BaseTypedModel implements ValidateModel {

    protected V1BaseValidateModel(String type) {
        this(new QName(SwitchYardModel.DEFAULT_NAMESPACE, ValidateModel.VALIDATE + '.' + type));
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
