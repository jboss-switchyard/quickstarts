/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package org.switchyard.component.camel.config.model.sql;

import org.switchyard.component.camel.config.model.CamelBindingModel;

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

}
