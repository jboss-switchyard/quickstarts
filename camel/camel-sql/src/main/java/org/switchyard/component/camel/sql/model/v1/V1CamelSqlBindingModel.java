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
package org.switchyard.component.camel.sql.model.v1;

import static org.switchyard.component.camel.sql.model.Constants.SQL_NAMESPACE_V1;

import java.net.URI;
import java.util.List;

import org.apache.camel.util.UnsafeUriCharactersEncoder;
import org.switchyard.component.camel.common.QueryString;
import org.switchyard.component.camel.common.model.v1.V1BaseCamelBindingModel;
import org.switchyard.component.camel.sql.model.CamelSqlBindingModel;
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

    /**
     * Create a new CamelSqlBindingModel.
     */
    public V1CamelSqlBindingModel() {
        super(SQL, SQL_NAMESPACE_V1);

        setModelChildrenOrder(QUERY, DATA_SOURCE_REF, BATCH, PLACEHOLDER);
    }

    /**
     * Create a V1CamelSqlBindingModel from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
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
    public URI getComponentURI() {
        Configuration modelConfiguration = getModelConfiguration();
        List<Configuration> children = modelConfiguration.getChildren();

        String baseUri = SQL + "://" + getQuery();

        QueryString queryStr = new QueryString();
        traverseConfiguration(children, queryStr, QUERY);

        return URI.create(UnsafeUriCharactersEncoder.encode(baseUri + queryStr.toString()));
    }

}
