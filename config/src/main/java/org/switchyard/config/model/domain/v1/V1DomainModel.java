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
package org.switchyard.config.model.domain.v1;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseNamedModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.domain.DomainModel;
import org.switchyard.config.model.domain.SecuritiesModel;
import org.switchyard.config.model.property.PropertiesModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.transform.TransformsModel;
import org.switchyard.config.model.validate.ValidatesModel;

/**
 * Implementation of DomainModel : v1.
 */
public class V1DomainModel extends BaseNamedModel implements DomainModel {
    
    private PropertiesModel _properties;
    private SecuritiesModel _securities;
    
    /**
     * Constructs a new V1DomainModel.
     */
    public V1DomainModel() {
        super(new QName(SwitchYardModel.DEFAULT_NAMESPACE, DomainModel.DOMAIN));
        setModelChildrenOrder(TransformsModel.TRANSFORMS, ValidatesModel.VALIDATES, PropertiesModel.PROPERTIES, SecuritiesModel.SECURITIES);
    }

    /**
     * Constructs a new V1DomainModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1DomainModel(Configuration config, Descriptor desc) {
        super(config, desc);
        setModelChildrenOrder(TransformsModel.TRANSFORMS, ValidatesModel.VALIDATES, PropertiesModel.PROPERTIES, SecuritiesModel.SECURITIES);
    }
    
    @Override
    public SwitchYardModel getSwitchYard() {
        return (SwitchYardModel)getModelParent();
    }

    @Override
    public synchronized PropertiesModel getProperties() {
        if (_properties == null) {
            _properties = (PropertiesModel)getFirstChildModel(PropertiesModel.PROPERTIES);
        }
        return _properties;
    }

    @Override
    public DomainModel setProperties(PropertiesModel properties) {
        setChildModel(properties);
        _properties = properties;
        SwitchYardModel switchyard = getSwitchYard();
        if (switchyard != null) {
            switchyard.setDomainPropertyResolver();
        }
        return this;
    }

    @Override
    public SecuritiesModel getSecurities() {
        if (_securities == null) {
            _securities = (SecuritiesModel)getFirstChildModel(SecuritiesModel.SECURITIES);
        }
        return _securities;
    }

    @Override
    public DomainModel setSecurities(SecuritiesModel securities) {
        setChildModel(securities);
        _securities = securities;
        return this;
    }

}
