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

package org.switchyard.validate.config.model.v1;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.validate.ValidateModel;
import org.switchyard.config.model.validate.v1.V1BaseValidateModel;
import org.switchyard.validate.ValidatorFactoryClass;
import org.switchyard.validate.config.model.XmlSchemaType;
import org.switchyard.validate.config.model.XmlValidateModel;
import org.switchyard.validate.xml.XmlValidatorFactory;
import javax.xml.namespace.QName;

/**
 * A version 1 XmlValidateModel.
 *
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
@ValidatorFactoryClass(XmlValidatorFactory.class)
public class V1XmlValidateModel extends V1BaseValidateModel implements XmlValidateModel {

    /**
     * Constructs a new V1XmlValidateModel.
     */
    public V1XmlValidateModel() {
        super(new QName(ValidateModel.DEFAULT_NAMESPACE, ValidateModel.VALIDATE + '.' + XML));
    }

    /**
     * Constructs a new V1XmlValidateModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1XmlValidateModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean failOnWarning() {
        String fow = getModelAttribute(FAIL_ON_WARNING);
        return Boolean.parseBoolean(fow);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public XmlValidateModel setFailOnWarning(boolean failOnWarning) {
        setModelAttribute(FAIL_ON_WARNING, Boolean.toString(failOnWarning));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSchemaFile() {
        return getModelAttribute(SCHEMA_FILE_URI);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public XmlValidateModel setSchemaFile(String file) {
        setModelAttribute(SCHEMA_FILE_URI, file);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public XmlSchemaType getSchemaType() {
        String type = getModelAttribute(SCHEMA_TYPE);
        return type != null ? XmlSchemaType.valueOf(type) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public XmlValidateModel setSchemaType(XmlSchemaType type) {
        if (type != null) {
            setModelAttribute(SCHEMA_TYPE, type.toString());
        }
        return this;
    }
}
