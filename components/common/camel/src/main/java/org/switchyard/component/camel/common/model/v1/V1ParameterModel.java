/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.camel.common.model.v1;

import javax.xml.namespace.QName;

import org.switchyard.component.camel.common.model.AdditionalUriParametersModel;
import org.switchyard.component.camel.common.model.ParameterModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * A version 1 AdditionalUriParametersModel.
 */
public class V1ParameterModel extends BaseModel implements ParameterModel {

    /**
     * Creates a new ParameterModel in the specified namespace.
     * 
     * @param namespace the specified namespace
     */
    public V1ParameterModel(String namespace) {
        super(new QName(namespace, PARAMETER));
    }

    /**
     * Creates a new ParameterModel with the specified configuration and
     * descriptor.
     * 
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1ParameterModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AdditionalUriParametersModel getAdditionalUriParameters() {
        return (AdditionalUriParametersModel) getModelParent();
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
    public ParameterModel setName(String name) {
        setModelAttribute("name", name);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue() {
        return getModelAttribute("value");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ParameterModel setValue(String value) {
        setModelAttribute("value", value);
        return this;
    }

}
