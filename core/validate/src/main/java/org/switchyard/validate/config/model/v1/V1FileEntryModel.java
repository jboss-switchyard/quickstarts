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

package org.switchyard.validate.config.model.v1;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.validate.config.model.FileEntryModel;

/**
 * A version 1 FileEntryModel.
 */
public class V1FileEntryModel extends BaseModel implements FileEntryModel {

    /**
     * Constructs a new V1SchemaCatalogsModel.
     * @param namespace namespace
     */
    public V1FileEntryModel(String namespace) {
        super(new QName(namespace, FileEntryModel.ENTRY));
    }

    /**
     * Constructs a new V1FileEntryModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1FileEntryModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public String getFile() {
        return getModelAttribute(FileEntryModel.FILE);
    }

    @Override
    public FileEntryModel setFile(String file) {
        setModelAttribute(FileEntryModel.FILE, file);
        return this;
    }
}
