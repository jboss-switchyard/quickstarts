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
package org.switchyard.component.camel.sql.model;

import org.switchyard.component.camel.common.model.CamelBindingModel;

/**
 * Represents the configuration settings for a SQL endpoint in Camel.
 */
public interface CamelSqlBindingModel extends CamelBindingModel {

    /**
     * Get SQL query to be executed.
     *
     * @return SQL query.
     */
    String getQuery();

    /**
     * Specify sql query to execute.
     *
     * @param query SQL query.
     * @return a reference to this Camel binding model
     */
    CamelSqlBindingModel setQuery(String query);

    /**
     * Reference to a DataSource to look up in the registry.
     *
     * @return Data source bean name.
     */
    String getDataSourceRef();

    /**
     * Specify data source bean name.
     *
     * @param dataSourceRef Bean name.
     * @return a reference to this Camel binding model
     */
    CamelSqlBindingModel setDataSourceRef(String dataSourceRef);

    /**
     * Execute SQL batch update statements.
     *
     * @return True if jdbc batch update should be performed.
     */
    Boolean isBatch();

    /**
     * Turn on/off JDBC batching support.
     *
     * @param batch Batch flag.
     * @return a reference to this Camel binding model
     */
    CamelSqlBindingModel setBatch(Boolean batch);

    /**
     * Get placeholder character.
     *
     * @return Paceholder character.
     */
    String getPlaceholder();

    /**
     * Specifies a character that will be replaced to ? in SQL query.
     * Notice, that it is simple String.replaceAll() operation and no SQL parsing is
     * involved (quoted strings will also change).
     *
     * @param placeholder Placeholder in query.
     * @return a reference to this Camel binding model
     */
    CamelSqlBindingModel setPlaceholder(String placeholder);

    /**
     * Period between polls.
     *
     * @return Period between polls.
     */
    String getPeriod();

    /**
     * Specifies delays between pools. Possible values are long (millis) and string, eg: 1s, 1m.
     *
     * @param period Period between polls.
     * @return a reference to this Camel binding model
     */
    CamelSqlBindingModel setPeriod(String period);

    /**
     * Specifies delay in millis before first poll.
     *
     * @param initialDelay First poll delay.
     * @return a reference to this Camel binding model
     */
    CamelSqlBindingModel setInitialDelay(Long initialDelay);

    /**
     * Get first poll delay.
     *
     * @return First poll delay.
     */
    Long getInitialDelay();


}
