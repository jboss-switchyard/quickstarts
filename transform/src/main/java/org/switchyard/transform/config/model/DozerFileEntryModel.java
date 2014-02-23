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

import org.switchyard.config.model.Model;

/**
 * A "entry" configuration model.
 */
public interface DozerFileEntryModel extends Model {

    /** entry. */
    public static final String ENTRY = "entry";
    
    /** file. */
    public static final String FILE = "file";
    
    /**
     * Get file.
     * @return file
     */
    String getFile();

    /**
     * Set file.
     * @param file file
     * @return model representation
     */
    DozerFileEntryModel setFile(String file);
    
}
