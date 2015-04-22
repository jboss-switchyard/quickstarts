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

package org.switchyard.component.resteasy.config.model.v1;

import org.switchyard.component.resteasy.config.model.RESTEasyNameValueModel.RESTEasyName;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseMarshaller;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composer.ContextMapperModel;
import org.switchyard.config.model.composer.MessageComposerModel;
import org.switchyard.config.model.composer.v1.V1ContextMapperModel;
import org.switchyard.config.model.composer.v1.V1MessageComposerModel;
import org.switchyard.config.model.composite.BindingModel;

/**
 * Marshaller for RESTEasy Gateway configurations.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class V1RESTEasyMarshaller extends BaseMarshaller {

    /**
     * Construct a RESTEasy Model Marshaller with help of a Descriptor.
     * 
     * @param desc the Descriptor 
     */
    public V1RESTEasyMarshaller(Descriptor desc) {
        super(desc);
    }

    /**
     * Reads a RESTEasy Model configuration.
     * 
     * @param config the configuration
     * @return the RESTEasy Binding Model 
     */
    @Override
    public Model read(Configuration config) {
        Descriptor desc = getDescriptor();
        String name = config.getName();
        if (name.startsWith(BindingModel.BINDING)) {
            return new V1RESTEasyBindingModel(config, desc);
        } else if (name.equals(ContextMapperModel.CONTEXT_MAPPER)) {
            return new V1ContextMapperModel(config, desc);
        } else if (name.equals(MessageComposerModel.MESSAGE_COMPOSER)) {
            return new V1MessageComposerModel(config, desc);
        } else if (name.equals(RESTEasyName.basic.name())) {
            return new V1BasicAuthModel(config, desc);
        } else if (name.equals(RESTEasyName.ntlm.name())) {
            return new V1NtlmAuthModel(config, desc);
        } else if (name.equals(RESTEasyName.proxy.name())) {
            return new V1ProxyModel(config, desc);
        } else {
            for (RESTEasyName n : RESTEasyName.values()) {
                if (n.name().equals(name)) {
                    return new V1RESTEasyNameValueModel(config, desc);
                }
            }
        }
        return null;
    }
}
