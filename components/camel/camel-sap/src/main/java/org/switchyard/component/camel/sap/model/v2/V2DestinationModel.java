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

import org.switchyard.component.camel.sap.model.DestinationModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * V2 camel-sap destination model.
 */
public abstract class V2DestinationModel extends V2EndpointModel implements DestinationModel {

    protected V2DestinationModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    protected V2DestinationModel(String namespace, String name) {
        super(namespace, name);
    }

    @Override
    public String getDestinationName() {
        return getConfig(Constants.DESTINATION_NAME);
    }

    @Override
    public V2DestinationModel setDestinationName(String destination) {
        setConfig(Constants.DESTINATION_NAME, destination);
        return this;
    }

}
