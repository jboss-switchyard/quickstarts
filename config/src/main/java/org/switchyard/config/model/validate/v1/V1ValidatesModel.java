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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.validate.ValidateModel;
import org.switchyard.config.model.validate.ValidatesModel;

/**
 * A version 1 ValidatesModel.
 *
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public class V1ValidatesModel extends BaseModel implements ValidatesModel {

    private List<ValidateModel> _validates = new ArrayList<ValidateModel>();

    /**
     * Constructs a new V1ValidatesModel.
     */
    public V1ValidatesModel() {
        super(new QName(SwitchYardModel.DEFAULT_NAMESPACE, ValidatesModel.VALIDATES));
        setModelChildrenOrder(ValidateModel.VALIDATE);
    }

    /**
     * Constructs a new V1ValidatesModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1ValidatesModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration validate_config : config.getChildrenStartsWith(ValidateModel.VALIDATE)) {
            ValidateModel validate = (ValidateModel)readModel(validate_config);
            if (validate != null) {
                _validates.add(validate);
            }
        }
        setModelChildrenOrder(ValidateModel.VALIDATE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SwitchYardModel getSwitchYard() {
        return (SwitchYardModel)getModelParent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<ValidateModel> getValidates() {
        return Collections.unmodifiableList(_validates);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized ValidatesModel addValidate(ValidateModel validate) {
        addChildModel(validate);
        _validates.add(validate);
        return this;
    }

}
