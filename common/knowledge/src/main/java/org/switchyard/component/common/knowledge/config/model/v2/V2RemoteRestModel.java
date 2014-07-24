/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.component.common.knowledge.config.model.v2;

import org.switchyard.component.common.knowledge.config.model.RemoteRestModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * The 2nd version RemoteRestModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class V2RemoteRestModel extends V2RemoteModel implements RemoteRestModel {

    /**
     * Constructs a new V2RemoteRestModel of the specified namespace.
     * @param namespace the namespace
     */
    public V2RemoteRestModel(String namespace) {
        super(namespace, REMOTE_REST);
    }

    /**
     * Constructs a new V2RemoteRestModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V2RemoteRestModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUrl() {
        return getModelAttribute("url");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RemoteRestModel setUrl(String url) {
        setModelAttribute("url", url);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUseFormBasedAuth() {
        String useFormBasedAuth = getModelAttribute("useFormBasedAuth");
        return useFormBasedAuth != null ? Boolean.parseBoolean(useFormBasedAuth) : false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RemoteRestModel setUseFormBasedAuth(boolean useFormBasedAuth) {
        setModelAttribute("useFormBasedAuth", String.valueOf(useFormBasedAuth));
        return this;
    }

}
