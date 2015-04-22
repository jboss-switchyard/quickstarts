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
package org.switchyard.config.model.domain;

import org.switchyard.config.model.NamedModel;
import org.switchyard.config.model.property.PropertiesModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * The "domain" configuration model.
 */
public interface DomainModel extends NamedModel {
    
    /** The "domain" name. */
    public static final String DOMAIN = "domain";
    
    /**
     * Gets the parent switchyard model.
     * @return the parent switchyard model
     */
    public SwitchYardModel getSwitchYard();
    
    /**
     * Gets the properties defined for the domain.
     * @return properties for the domain
     */
    public PropertiesModel getProperties();
    
    /**
     * Sets the child properties model.
     * @param properties the child properties model.
     * @return this DomainModel (useful for chaining)
     */
    public DomainModel setProperties(PropertiesModel properties);
    
    /**
     * Gets the child securities model.
     * @return the child securities model
     */
    public SecuritiesModel getSecurities();
    
    /**
     * Sets the child securities model.
     * @param securities the child securities model
     * @return this DomainModel (useful for chaining)
     */
    public DomainModel setSecurities(SecuritiesModel securities);
    
}
