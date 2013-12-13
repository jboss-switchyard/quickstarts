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
package org.switchyard.component.camel.common.model.v1;

import java.text.DateFormat;
import java.util.Date;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

import org.switchyard.component.camel.CommonCamelMessages;

/**
 * Base class for all camel models which are not related to binding.
 * 
 * @author Lukasz Dywicki
 */
public abstract class V1BaseCamelModel extends BaseModel {

    protected V1BaseCamelModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    protected V1BaseCamelModel(String namespace, String name) {
        super(namespace, name);
    }

    protected V1BaseCamelModel(Configuration config) {
        super(config);
    }

    protected final Integer getIntegerConfig(String configName) {
        String value = getConfig(configName);
        return value != null ? Integer.parseInt(value) : null;
    }

    protected final Boolean getBooleanConfig(String configName) {
        String value = getConfig(configName);
        return value != null ? Boolean.valueOf(value) : Boolean.FALSE;
    }

    protected final Long getLongConfig(String configName) {
        String value = getConfig(configName);
        return value != null ? Long.parseLong(value) : null;
    }

    protected final Date getDateConfig(String configName, DateFormat format) {
        String value = getConfig(configName);
        if (value == null) {
            return null;
        } else {
            try {
                return format.parse(value);
            } catch (java.text.ParseException parseEx) {
                throw CommonCamelMessages.MESSAGES.failedToParse(configName, parseEx);
            }
        }
    }

    protected final String getConfig(String configName) {
        Configuration config = getModelConfiguration().getFirstChild(configName);
        if (config != null) {
            return config.getValue();
        } else {
            return null;
        }
    }

    protected final <T extends Enum<T>> T getEnumerationConfig(String configName, Class<T> type) {
        String constantName = getConfig(configName);
        if (constantName != null) {
            return Enum.valueOf(type, constantName);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    protected <X extends V1BaseCamelModel> X setConfig(String name, Object value) {
        String modelValue = String.valueOf(value);
        Configuration config = getModelConfiguration().getFirstChild(name);
        if (config != null) {
            // set an existing config value
            config.setValue(modelValue);
        } else {
            // create the config model and set the value
            NameValueModel model = new NameValueModel(getNamespaceURI(), name);
            model.setValue(modelValue);
            setChildModel(model);
        }
        return (X) this;
    }

}
