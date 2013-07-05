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
