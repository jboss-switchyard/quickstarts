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

import org.switchyard.component.camel.common.model.v1.V1BaseCamelMarshaller;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;

/**
 * SAP model marshaller.
 */
public class V2CamelSapModelMarshaller extends V1BaseCamelMarshaller {

    /**
     * Creates new marshaller.
     * 
     * @param desc Descriptor.
     */
    public V2CamelSapModelMarshaller(Descriptor desc) {
        super(desc);
    }

    /**
     * Reads in the Configuration, looking for various models.
     * If not found, it falls back to the super class (V2BaseCamelMarshaller).
     *
     * @param config the Configuration
     * @return the Model
     */
    @Override
    public Model read(Configuration config) {
        String name = config.getName();
        Descriptor desc = getDescriptor();
        if (Constants.BINDING_SAP.equals(name)) {
            return new V2CamelSapBindingModel(config, desc);
        }
        if (Constants.IDOCLIST_SERVER.equals(name)) {
            return new V2IDocServerModel(config, desc);
        }
        if (Constants.SRFC_SERVER.equals(name) || Constants.TRFC_SERVER.equals(name)) {
            return new V2RfcServerModel(config, desc);
        }
        if (Constants.IDOC_DESTINATION.equals(name) || Constants.IDOCLIST_DESTINATION.equals(name)) {
            return new V2IDocDestinationModel(config, desc);
        }
        if (Constants.QIDOC_DESTINATION.equals(name) || Constants.QIDOCLIST_DESTINATION.equals(name)) {
            return new V2QIDocDestinationModel(config, desc);
        }
        if (Constants.QRFC_DESTINATION.equals(name)) {
            return new V2QRfcDestinationModel(config, desc);
        }
        if (Constants.SRFC_DESTINATION.equals(name) || Constants.TRFC_DESTINATION.equals(name)) {
            return new V2RfcDestinationModel(config, desc);
        }
        return super.read(config);
    }

}
