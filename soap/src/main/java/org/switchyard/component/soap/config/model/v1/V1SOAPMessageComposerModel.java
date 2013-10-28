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

import org.switchyard.component.soap.config.model.SOAPMessageComposerModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composer.v1.V1MessageComposerModel;

/**
 * V1SOAPMessageComposerModel.
 */
public class V1SOAPMessageComposerModel extends V1MessageComposerModel implements SOAPMessageComposerModel {

    /**
     * Constructs a new V1SOAPMessageComposerModel in the specified namespace.
     * @param namespace the specified namespace
     */
    public V1SOAPMessageComposerModel(String namespace) {
        super(namespace);
    }

    /**
     * Constructs a new V1SOAPMessageComposerModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1SOAPMessageComposerModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public Boolean isUnwrapped() {
        String unwrap = getModelAttribute("unwrapped");
        return unwrap != null && Boolean.valueOf(unwrap);
    }

    @Override
    public SOAPMessageComposerModel setUnwrapped(boolean unwrapped) {
        setModelAttribute("unwrapped", String.valueOf(unwrapped));
        return this;
    }

}
