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
package org.switchyard.component.camel.sap.model;

import org.switchyard.component.camel.common.model.CamelBindingModel;

/**
 * Camel SAP binding model.
 */
public interface CamelSapBindingModel extends CamelBindingModel {
    /**
     * Gets server.
     * @return server
     */
    String getServer();

    /**
     * Sets server.
     * @param server server
     * @return this BindingModel (useful for chaining)
     */
    CamelSapBindingModel setServer(String server);

    /**
     * Gets destination.
     * @return destination
     */
    String getDestination();

    /**
     * Sets destination.
     * @param destination destination
     * @return this BindingModel (useful for chaining)
     */
    CamelSapBindingModel setDestination(String destination);

    /**
     * Gets RFC name.
     * @return RFC name
     */
    String getRfcName();

    /**
     * Sets RFC name.
     * @param rfcName RFC name
     * @return this BindingModel (useful for chaining)
     */
    CamelSapBindingModel setRfcName(String rfcName);

    /**
     * Gets if it's transacted.
     * @return true if it's transacted
     */
    Boolean isTransacted();

    /**
     * Sets if it's transacted.
     * @param transacted true if it's transacted
     * @return this BindingModel (useful for chaining)
     */
    CamelSapBindingModel setTransacted(Boolean transacted);
}
