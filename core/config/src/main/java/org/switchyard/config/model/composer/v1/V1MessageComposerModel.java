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

package org.switchyard.config.model.composer.v1;

import javax.xml.namespace.QName;

import org.switchyard.common.lang.Strings;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composer.MessageComposerModel;

/**
 * A version 1 MessageComposerModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1MessageComposerModel extends BaseModel implements MessageComposerModel {

    /**
     * Constructs a new V1MessageComposerModel in the specified namespace.
     * @param namespace the specified namespace
     */
    public V1MessageComposerModel(String namespace) {
        super(new QName(namespace, MESSAGE_COMPOSER));
    }

    /**
     * Constructs a new V1MessageComposerModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1MessageComposerModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClazz() {
        return Strings.trimToNull(getModelAttribute("class"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageComposerModel setClazz(String clazz) {
        setModelAttribute("class", clazz);
        return this;
    }

}
