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

package org.switchyard.transform.config.model;

import java.util.List;

import org.switchyard.config.model.Model;

/**
 * A "mappingFiles" configuration model for dozer transformer.
 */
public interface DozerMappingFilesModel extends Model {

    /** mapping files. */
    public static final String MAPPING_FILES = "mappingFiles";
    
    /**
     * Get file entries.
     * @return file entries
     */
    List<DozerFileEntryModel> getEntries();

    /**
     * Set a file entry.
     * @param entry file entry
     * @return model representation
     */
    DozerMappingFilesModel addEntry(DozerFileEntryModel entry);
    
}
