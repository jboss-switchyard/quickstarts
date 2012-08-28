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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.switchyard.component.common.selector.config.model.v1.V1BindingModel;
import org.switchyard.component.camel.config.model.CamelBindingModel;
import org.switchyard.component.camel.config.model.QueryString;
import org.switchyard.config.Configuration;
import org.switchyard.config.Configurations;
import org.switchyard.config.model.Descriptor;

/**
 * Version 1.0 implementation of a {@link CamelBindingModel}.
 * 
 * @author Daniel Bevenius
 */
public abstract class V1BaseCamelBindingModel extends V1BindingModel implements
    CamelBindingModel {

    /**
     * Camel endpoint type.
     */
    public static final String CAMEL = "camel";

    private Configuration _environment = Configurations.emptyConfig();

    /**
     * Constructor.
     */
    public V1BaseCamelBindingModel() {
        this(CAMEL);
        setModelChildrenOrder();
    }

    /**
     * 
     * Create a new CamelBindingModel.
     * 
     * @param type
     *            binding type
     */
    public V1BaseCamelBindingModel(String type) {
        super(type, CamelBindingModel.DEFAULT_NAMESPACE);
    }

    /**
     * Create a CamelBindingModel from the specified configuration and
     * descriptor.
     * 
     * @param config
     *            The switchyard configuration instance.
     * @param desc
     *            The switchyard descriptor instance.
     */
    public V1BaseCamelBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
        setModelChildrenOrder();
    }

    /**
     * Returns the global configuration.
     * 
     * @return the environment/global config
     */
    public Configuration getEnvironment() {
        return _environment;
    }

    /**
     * Sets the global configuration.
     * 
     * @param config the environment/global config
     */
    public void setEnvironment(Configuration config) {
        _environment = config;
    }

    protected final void traverseConfiguration(List<Configuration> parent, QueryString queryString,
        String ... excludes) {

        if (parent.size() != 0) {
            List<String> excludeParameters = new ArrayList<String>(Arrays.asList(excludes));

            Iterator<Configuration> parentIterator = parent.iterator();
            while (parentIterator.hasNext()) {
                Configuration child = parentIterator.next();

                if (child != null && child.getName() != null && excludeParameters.contains(child.getName())) {
                    continue;
                }

                if (child != null && child.getChildren().size() == 0) {
                    queryString.add(child.getName(), child.getValue());
                } else {
                    traverseConfiguration(child.getChildren(), queryString, excludes);
                }
            }
        }
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

    protected <X extends V1BaseCamelBindingModel> X setConfig(String name, Object value) {
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
