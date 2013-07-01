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
package org.switchyard.component.http.config.model.v1;

import org.switchyard.component.http.config.model.NtlmAuthModel;
import org.switchyard.component.http.config.model.HttpNameValueModel;
import org.switchyard.component.http.config.model.HttpNameValueModel.HttpName;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * A NtlmAuthModel V1 implementation.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public class V1NtlmAuthModel extends V1BasicAuthModel implements NtlmAuthModel {

    private HttpNameValueModel _domain;

    /**
     * Creates a new NtlmAuthModel.
     */
    public V1NtlmAuthModel() {
        super(HttpName.ntlm.name());
    }

    /**
     * Creates a new NtlmAuthModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1NtlmAuthModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    public String getDomain() {
        if (_domain == null) {
            _domain = getNameValue(HttpName.domain);
        }
        return _domain != null ? _domain.getValue() : null;
    }

    /**
     * {@inheritDoc}
     */
    public NtlmAuthModel setDomain(String domain) {
        _domain = setNameValue(_domain, HttpName.domain, domain);
        return this;
    }
}
