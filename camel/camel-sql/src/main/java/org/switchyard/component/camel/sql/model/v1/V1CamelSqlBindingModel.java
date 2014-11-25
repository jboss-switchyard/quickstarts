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
package org.switchyard.component.camel.sql.model.v1;
import java.net.URI;
import java.util.List;

import org.apache.camel.util.UnsafeUriCharactersEncoder;
import org.switchyard.component.camel.common.QueryString;
import org.switchyard.component.camel.common.model.v1.V1BaseCamelBindingModel;
import org.switchyard.component.camel.sql.model.CamelSqlBindingModel;
import org.switchyard.component.camel.sql.model.CamelSqlConsumerBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * Implementation of sql configuration binding.
 *
 * @author Lukasz Dywicki
 */
public class V1CamelSqlBindingModel extends V1BaseCamelBindingModel
 implements CamelSqlBindingModel {
    /**
     * Camel component prefix.
     */
    public static final String SQL = "sql";
    private static final String QUERY = "query";
    private static final String DATA_SOURCE_REF = "dataSourceRef";
    private static final String BATCH = "batch";
    private static final String PLACEHOLDER = "placeholder";
    // timer related attributes
    private static final String PERIOD = "period";
    private static final String INITIAL_DELAY = "initialDelay";

    /**
     * Create a new CamelSqlBindingModel.
     *
     * @param namespace
     *            namespace
     */
    public V1CamelSqlBindingModel(String namespace) {
        super(SQL, namespace);
        setModelChildrenOrder(QUERY, DATA_SOURCE_REF, BATCH, PLACEHOLDER);
    }

    /**
     * Create a V1CamelSqlBindingModel from the specified configuration and
     * descriptor.
     *
     * @param config
     *            The switchyard configuration instance.
     * @param desc
     *            The switchyard descriptor instance.
     */
    public V1CamelSqlBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public String getQuery() {
        return getConfig(QUERY);
    }

    @Override
    public V1CamelSqlBindingModel setQuery(String query) {
        return setConfig(QUERY, query);
    }

    @Override
    public String getDataSourceRef() {
        return getConfig(DATA_SOURCE_REF);
    }

    @Override
    public V1CamelSqlBindingModel setDataSourceRef(String dataSourceRef) {
        return setConfig(DATA_SOURCE_REF, dataSourceRef);
    }

    @Override
    public Boolean isBatch() {
        return getBooleanConfig(BATCH);
    }

    @Override
    public V1CamelSqlBindingModel setBatch(Boolean batch) {
        return setConfig(BATCH, batch);
    }

    @Override
    public String getPlaceholder() {
        return getConfig(PLACEHOLDER);
    }

    @Override
    public V1CamelSqlBindingModel setPlaceholder(String placeholder) {
        return setConfig(PLACEHOLDER, placeholder);
    }

    @Override
    public String getPeriod() {
        return getModelAttribute(PERIOD);
    }

    @Override
    public V1CamelSqlBindingModel setPeriod(String schedule) {
        setModelAttribute(PERIOD, schedule);
        return this;
    }

    @Override
    public V1CamelSqlBindingModel setInitialDelay(Long initialDelay) {
        setModelAttribute(INITIAL_DELAY, Long.toString(initialDelay));
        return this;
    }

    @Override
    public Long getInitialDelay() {
        String value = getModelAttribute(INITIAL_DELAY);
        return value == null ? null : Long.valueOf(value);
    }


    @Override
    public URI getComponentURI() {
        Configuration modelConfiguration = getModelConfiguration();
        List<Configuration> children = modelConfiguration.getChildren();
        String baseUri = SQL + "://" + getQuery();
        QueryString queryStr = new QueryString();
        queryStr.add("consumer.delay", getPeriod());
        queryStr.add("consumer.initialDelay", getInitialDelay());
        traverseConfiguration(children, queryStr, QUERY);
        return URI.create(UnsafeUriCharactersEncoder.encode(baseUri + queryStr.toString()));
    }

    @Override
    public CamelSqlConsumerBindingModel getConsumer() {

        return null;
    }

    @Override
    public CamelSqlBindingModel setConsumer(CamelSqlConsumerBindingModel consumer) {
        return this;
    }

    protected Long convertPeriodToLongMilliseconds(String period) {
        if (isLong(period)) {
            return new Long(period);
        } else if (period.endsWith("s")) {
            if (isLong(period.substring(0, period.length() - 1))) {
                return new Long(period.substring(0, period.length() - 1) + "000");
            }
        }
        return null;
    }

    protected boolean isLong(String s) {
        try {
            Long.parseLong(s);
        } catch (NumberFormatException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }
}

