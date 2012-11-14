/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.camel.config.model.v1;

import java.text.DateFormat;
import java.util.Date;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * Base class for all camel models which are not related to binding.
 * 
 * @author Lukasz Dywicki
 */
public abstract class V1BaseCamelModel extends BaseModel {

    protected V1BaseCamelModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    protected V1BaseCamelModel(QName qname) {
        super(qname);
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
        return value != null ? Boolean.valueOf(value) : null;
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
                throw new IllegalArgumentException("Failed to parse " + configName + " as a date.", parseEx);
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
            NameValueModel model = new NameValueModel(name);
            model.setValue(modelValue);
            setChildModel(model);
        }
        return (X) this;
    }

}
