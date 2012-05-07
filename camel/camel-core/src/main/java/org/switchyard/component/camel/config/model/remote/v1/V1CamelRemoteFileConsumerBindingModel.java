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
package org.switchyard.component.camel.config.model.remote.v1;

import java.text.DateFormat;
import java.util.Date;

import javax.xml.namespace.QName;

import org.switchyard.component.camel.config.model.remote.CamelRemoteFileConsumerBindingModel;
import org.switchyard.component.camel.config.model.v1.NameValueModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * Consumer binding model.
 * 
 * @author Lukasz Dywicki
 */
public class V1CamelRemoteFileConsumerBindingModel extends BaseModel
    implements CamelRemoteFileConsumerBindingModel {

    private static final String RECURSIVE = "recursive";
    private static final String DELAY = "delay";
    private static final String INITIAL_DELAY = "initialDelay";
    private static final String USE_FIXED_DELAY = "useFixedDelay";

    /**
     * Creates consumer binding model.
     * 
     * @param config Configuration.
     * @param desc Descriptor.
     */
    public V1CamelRemoteFileConsumerBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * Creates new remote file consumer binding.
     */
    public V1CamelRemoteFileConsumerBindingModel() {
        super(new QName(V1CamelRemoteFileBindingModel.CONSUME));
    }

    @Override
    public Boolean getRecursive() {
        return getBooleanConfig(RECURSIVE);
    }

    @Override
    public CamelRemoteFileConsumerBindingModel setRecursive(Boolean recursive) {
        return setConfig(RECURSIVE, String.valueOf(recursive));
    }

    @Override
    public Integer getDelay() {
        return getIntegerConfig(DELAY);
    }

    @Override
    public CamelRemoteFileConsumerBindingModel setDelay(Integer delay) {
        return setConfig(DELAY, String.valueOf(delay));
    }

    @Override
    public Integer getInitialDelay() {
        return getIntegerConfig(INITIAL_DELAY);
    }

    @Override
    public CamelRemoteFileConsumerBindingModel setInitialDelay(Integer initialDelay) {
        return setConfig(INITIAL_DELAY, String.valueOf(initialDelay));
    }

    @Override
    public Boolean getUseFixedDelay() {
        return getBooleanConfig(USE_FIXED_DELAY);
    }

    @Override
    public CamelRemoteFileConsumerBindingModel setUseFixedDelay(Boolean userFixedDelay) {
        return setConfig(USE_FIXED_DELAY, String.valueOf(userFixedDelay));
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

    protected final <X> X setConfig(String name, String value) {
        Configuration config = getModelConfiguration().getFirstChild(name);
        if (config != null) {
            // set an existing config value
            config.setValue(value);
        } else {
            // create the config model and set the value
            NameValueModel model = new NameValueModel(name);
            model.setValue(value);
            setChildModel(model);
        }
        return (X) this;
    }

}
