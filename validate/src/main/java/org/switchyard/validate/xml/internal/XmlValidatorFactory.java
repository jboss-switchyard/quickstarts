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
package org.switchyard.validate.xml.internal;

import org.jboss.logging.Logger;
import org.switchyard.validate.Validator;
import org.switchyard.validate.config.model.XmlValidateModel;
import org.switchyard.validate.internal.ValidatorFactory;

/**
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public final class XmlValidatorFactory implements ValidatorFactory<XmlValidateModel>{

    private static final Logger LOGGER = Logger.getLogger(XmlValidatorFactory.class);
    
    /**
     * Create a {@link Validator} instance from the supplied {@link XmlValidateModel}.
     * @param model the XML Validator model. 
     * @return the Transformer instance.
     */
    public Validator newValidator(XmlValidateModel model) {
        return new XmlValidator(model.getName(), model);
    }
    
}
