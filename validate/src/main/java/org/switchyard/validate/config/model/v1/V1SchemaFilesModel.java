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

import java.util.ArrayList;
import java.util.List;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.validate.ValidateModel;
import org.switchyard.validate.config.model.FileEntryModel;
import org.switchyard.validate.config.model.SchemaFilesModel;

import javax.xml.namespace.QName;

/**
 * A version 1 SchemaFilesModel.
 */
public class V1SchemaFilesModel extends BaseModel implements SchemaFilesModel {

    private List<FileEntryModel> _entries = new ArrayList<FileEntryModel>();
    
    /**
     * Constructs a new V1SchemaCatalogsModel.
     */
    public V1SchemaFilesModel() {
        super(new QName(ValidateModel.DEFAULT_NAMESPACE, SchemaFilesModel.SCHEMA_FILES));
    }

    /**
     * Constructs a new V1SchemaFilesModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1SchemaFilesModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration entryConfig : config.getChildrenStartsWith(FileEntryModel.ENTRY)) {
            FileEntryModel entry = (FileEntryModel)readModel(entryConfig);
            if (entry != null) {
                _entries.add(entry);
            }
        }
        setModelChildrenOrder(FileEntryModel.ENTRY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FileEntryModel> getEntries() {
        return _entries;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchemaFilesModel addEntry(FileEntryModel entry) {
        addChildModel(entry);
        _entries.add(entry);
        return this;
    }
}
