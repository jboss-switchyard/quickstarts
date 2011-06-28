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

package org.switchyard.component.camel.config.model.mock.v1;

import java.net.URI;

import org.switchyard.component.camel.config.model.QueryString;
import org.switchyard.component.camel.config.model.mock.CamelMockBindingModel;
import org.switchyard.component.camel.config.model.v1.NameValueModel;
import org.switchyard.component.camel.config.model.v1.V1BaseCamelBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;


/**
 * Represents the configuration settings for Camel Mock binding.
 * 
 * @author Mario Antollini
 */
public class V1CamelMockBindingModel  extends V1BaseCamelBindingModel 
                 implements CamelMockBindingModel {

    /**
     * Camel endpoint type.
     */
    public static final String MOCK = "mock";
    
    /**
     * Camel endpoint configuration values.
     */
    private static final String NAME                = "name";
    private static final String REPORT_GROUP        = "reportGroup";

    /**
     * Create a new CamelMockBindingModel.
     */
    public V1CamelMockBindingModel() {
        super(MOCK);
        setModelChildrenOrder(
                NAME,
                REPORT_GROUP);
    }

    /**
     * Create a CamelMockBindingModel from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V1CamelMockBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public String getName() {
        return getConfig(NAME);
    }

    @Override
    public CamelMockBindingModel setName(String name) {
        setConfig(NAME, name);
        return this;
    }

    @Override
    public Integer getReportGroup() {
        return getIntegerConfig(REPORT_GROUP);
    }

    @Override
    public CamelMockBindingModel setReportGroup(Integer reportGroup) {
        setConfig(REPORT_GROUP, String.valueOf(reportGroup));
        return this;
    }

    @Override
    public URI getComponentURI() {
        // base URI without params
        String uriStr = MOCK + "://" + getConfig(NAME);
        // create query string from config values
        QueryString queryStr = new QueryString()
        .add(REPORT_GROUP, getConfig(REPORT_GROUP));

        return URI.create(uriStr.toString() + queryStr);
    }

    private Integer getIntegerConfig(String configName) {
        String value = getConfig(configName);
        return value != null ? Integer.parseInt(value) : null;
    }

    private String getConfig(String configName) {
        Configuration config = getModelConfiguration().getFirstChild(configName);
        if (config != null) {
            return config.getValue();
        } else {
            return null;
        }
    }

    private void setConfig(String name, String value) {
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
    }


}
