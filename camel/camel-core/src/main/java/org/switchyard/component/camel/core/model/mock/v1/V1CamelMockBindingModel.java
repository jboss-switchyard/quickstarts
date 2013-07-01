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
package org.switchyard.component.camel.core.model.mock.v1;

import static org.switchyard.component.camel.core.model.Constants.CORE_NAMESPACE_V1;

import java.net.URI;

import org.switchyard.component.camel.common.QueryString;
import org.switchyard.component.camel.common.model.v1.V1BaseCamelBindingModel;
import org.switchyard.component.camel.core.model.mock.CamelMockBindingModel;
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
    private static final String NAME  = "name";
    private static final String REPORT_GROUP = "reportGroup";

    /**
     * Create a new CamelMockBindingModel.
     */
    public V1CamelMockBindingModel() {
        super(MOCK, CORE_NAMESPACE_V1);

        setModelChildrenOrder(NAME, REPORT_GROUP);
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
    public String getEndpointName() {
        return getConfig(NAME);
    }

    @Override
    public V1CamelMockBindingModel setEndpointName(String name) {
        return setConfig(NAME, name);
    }

    @Override
    public Integer getReportGroup() {
        return getIntegerConfig(REPORT_GROUP);
    }

    @Override
    public V1CamelMockBindingModel setReportGroup(Integer reportGroup) {
        return setConfig(REPORT_GROUP, String.valueOf(reportGroup));
    }

    @Override
    public URI getComponentURI() {
        // base URI without params
        String uriStr = MOCK + "://" + getConfig(NAME);
        // create query string from config values
        QueryString queryStr = new QueryString().add(REPORT_GROUP, getConfig(REPORT_GROUP));

        return URI.create(uriStr.toString() + queryStr);
    }


}
