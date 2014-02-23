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

package org.switchyard.transform.config.model.v1;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.transform.config.model.DozerFileEntryModel;
import org.switchyard.transform.config.model.DozerMappingFilesModel;

/**
 * A version 1 DozerMappingFilesModel.
 */
public class V1DozerMappingFilesModel extends BaseModel implements DozerMappingFilesModel {

    private List<DozerFileEntryModel> _entries = new ArrayList<DozerFileEntryModel>();
    
    /**
     * Constructs a new V1DozerMappingFilesModel.
     * @param namespace namespace
     */
    public V1DozerMappingFilesModel(String namespace) {
        super(new QName(namespace, DozerMappingFilesModel.MAPPING_FILES));
    }

    /**
     * Constructs a new V1DozerMappingFilesModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1DozerMappingFilesModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration entryConfig : config.getChildrenStartsWith(DozerFileEntryModel.ENTRY)) {
            DozerFileEntryModel entry = (DozerFileEntryModel)readModel(entryConfig);
            if (entry != null) {
                _entries.add(entry);
            }
        }
        setModelChildrenOrder(DozerFileEntryModel.ENTRY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DozerFileEntryModel> getEntries() {
        return _entries;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DozerMappingFilesModel addEntry(DozerFileEntryModel entry) {
        addChildModel(entry);
        _entries.add(entry);
        return this;
    }
}
