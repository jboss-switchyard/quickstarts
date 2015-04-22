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
package org.switchyard.component.soap.config.model.v1;

import static org.switchyard.component.soap.composer.SOAPHeadersType.VALUE;

import org.switchyard.component.soap.composer.SOAPHeadersType;
import org.switchyard.component.soap.config.model.SOAPContextMapperModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composer.v1.V1ContextMapperModel;

/**
 * V1SOAPContextMapperModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2012 Red Hat Inc.
 */
public class V1SOAPContextMapperModel extends V1ContextMapperModel implements SOAPContextMapperModel {

    /**
     * Constructs a new V1SOAPContextMapperModel in the specified namespace.
     * @param namespace the specified namespace
     */
    public V1SOAPContextMapperModel(String namespace) {
        super(namespace);
    }

    /**
     * Constructs a new V1SOAPContextMapperModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1SOAPContextMapperModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SOAPHeadersType getSOAPHeadersType() {
        String sht = getModelAttribute("soapHeadersType");
        return sht != null ? SOAPHeadersType.valueOf(sht) : VALUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SOAPContextMapperModel setSOAPHeadersType(SOAPHeadersType soapHeadersType) {
        String sht = soapHeadersType != null ? soapHeadersType.name() : null;
        setModelAttribute("soapHeadersType", sht);
        return this;
    }

}
