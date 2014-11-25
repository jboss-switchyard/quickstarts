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
package org.switchyard.component.camel.sql.model.v2;

import java.net.URI;
import java.util.List;

import org.apache.camel.util.UnsafeUriCharactersEncoder;
import org.switchyard.component.camel.common.QueryString;
import org.switchyard.component.camel.sql.model.CamelSqlBindingModel;
import org.switchyard.component.camel.sql.model.CamelSqlConsumerBindingModel;
import org.switchyard.component.camel.sql.model.v1.V1CamelSqlBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
/**
 * Implementation of sql configuration binding.
 *
 * @author Lukasz Dywicki
 */
public class V2CamelSqlBindingModel extends V1CamelSqlBindingModel {

    /**
     * Camel component prefix.
     */
    public static final String SQL = "sql";

    private static final String QUERY = "query";
    private static final String DATA_SOURCE_REF = "dataSourceRef";
    private static final String BATCH = "batch";
    private static final String PLACEHOLDER = "placeholder";


    private CamelSqlConsumerBindingModel _consume;

    /**
     * Create a new CamelSqlBindingModel.
     * @param namespace namespace
     */
    public V2CamelSqlBindingModel(String namespace) {
        super(namespace);

        setModelChildrenOrder(QUERY, DATA_SOURCE_REF, BATCH, PLACEHOLDER, CONSUME);
    }

    /**
     * Create a V1CamelSqlBindingModel from the specified configuration and descriptor.
     *
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V2CamelSqlBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }


    @Override
    public URI getComponentURI() {
        // In case the old values are setted (period and get Initial Delay, the
        // new consumer object is created and setted
        if (getPeriod() != null && !getPeriod().equals("")) {
            if (getConsumer() == null) {
                _consume = new V2CamelSqlConsumerBindingModel(getModelConfiguration(), getModelDescriptor());
            }
            _consume.setDelay(convertPeriodToLongMilliseconds(this.getPeriod()));
        }
        if (getInitialDelay() != null) {
            if (getConsumer() == null) {
                _consume = new V2CamelSqlConsumerBindingModel(getModelConfiguration(), getModelDescriptor());
            }
            _consume.setInitialDelay(getInitialDelay());
        }
        Configuration modelConfiguration = getModelConfiguration();
        List<Configuration> children = modelConfiguration.getChildren();

        String baseUri = SQL + "://" + getQuery();

        QueryString queryStr = new QueryString();

        traverseConfiguration(children, queryStr, QUERY);

        return URI.create(UnsafeUriCharactersEncoder.encode(baseUri + queryStr.toString()));
    }

    @Override
    public CamelSqlConsumerBindingModel getConsumer() {
        if (_consume == null) {
            Configuration config = getModelConfiguration().getFirstChild(CONSUME);
            if (config != null) {
                _consume = new V2CamelSqlConsumerBindingModel(config, getModelDescriptor());
            }
        }
        return _consume;
    }

    @Override
    public CamelSqlBindingModel setConsumer(CamelSqlConsumerBindingModel consumer) {
        Configuration config = getModelConfiguration().getFirstChild(CONSUME);
        if (config != null) {
            // set an existing config value
            getModelConfiguration().removeChildren(CONSUME);
            getModelConfiguration().addChild(((V2CamelSqlConsumerBindingModel) consumer).getModelConfiguration());
        } else {
            setChildModel((V2CamelSqlConsumerBindingModel) consumer);
        }
        _consume = consumer;
        return this;
    }


}
