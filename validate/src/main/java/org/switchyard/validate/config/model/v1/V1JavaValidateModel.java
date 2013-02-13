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

package org.switchyard.validate.config.model.v1;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.validate.ValidateModel;
import org.switchyard.config.model.validate.v1.V1BaseValidateModel;
import org.switchyard.validate.config.model.JavaValidateModel;

/**
 * A version 1 JavaValidateModel.
 *
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public class V1JavaValidateModel extends V1BaseValidateModel implements JavaValidateModel {

    /**
     * Constructs a new V1JavaValidateModel.
     */
    public V1JavaValidateModel() {
        super(new QName(ValidateModel.DEFAULT_NAMESPACE, ValidateModel.VALIDATE + '.' + JAVA));
    }

    /**
     * Constructs a new V1JavaValidateModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1JavaValidateModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClazz() {
        return getModelAttribute(CLASS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JavaValidateModel setClazz(String clazz) {
        setModelAttribute(CLASS, clazz);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBean() {
        return getModelAttribute(BEAN);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JavaValidateModel setBean(String bean) {
        setModelAttribute(BEAN, bean);
        return this;
    }

}
