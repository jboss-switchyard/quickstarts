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
package org.switchyard.component.camel.sap.model.v2;

import org.switchyard.component.camel.common.QueryString;
import org.switchyard.component.camel.sap.model.RfcServerModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * V2 RFC server model.
 */
public class V2RfcServerModel extends V2ServerModel implements RfcServerModel {

    /**
     * Constructor.
     * @param config configuration
     * @param desc descriptor
     */
    public V2RfcServerModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * Constructor.
     * @param namespace namespace
     * @param name name
     */
    public V2RfcServerModel(String namespace, String name) {
        super(namespace, name);
        setModelChildrenOrder(Constants.SERVER_NAME, Constants.RFC_NAME);
    }

    @Override
    public String getRfcName() {
        return getConfig(Constants.RFC_NAME);
    }

    @Override
    public V2RfcServerModel setRfcName(String rfc) {
        setConfig(Constants.RFC_NAME, rfc);
        return this;
    }

    @Override
    public StringBuilder createBaseURIString(QueryString queryString) {
        return new StringBuilder(getSchema())
                    .append(':').append(getServerName())
                    .append(':').append(getRfcName());
    }

}
