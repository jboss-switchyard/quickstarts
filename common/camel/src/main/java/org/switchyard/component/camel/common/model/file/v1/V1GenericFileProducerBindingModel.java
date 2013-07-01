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
package org.switchyard.component.camel.common.model.file.v1;

import org.switchyard.component.camel.common.model.file.GenericFileProducerBindingModel;
import org.switchyard.component.camel.common.model.v1.V1BaseCamelModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * Base implementation for file producers (both filesystem and remote).
 * 
 * @author Lukasz Dywicki
 */
public abstract class V1GenericFileProducerBindingModel extends V1BaseCamelModel 
    implements GenericFileProducerBindingModel {

    /**
     * The name of the 'fileExist' element.
     */
    public static final String FILE_EXIST = "fileExist";

    /**
     * The name of the 'tempPrefix' element.
     */
    public static final String TEMP_PREFIX = "tempPrefix";

    /**
     * The name of the 'tempFileName' element.
     */
    public static final String TEMP_FILENAME = "tempFileName";

    /**
     * The name of the 'keepLastModified' element.
     */
    public static final String KEEP_LAST_MODIFIED = "keepLastModified";

    /**
     * The name of the 'eagerDeleteTargetFile' element.
     */
    public static final String EAGER_DELETE_TARGET_FILE = "eagerDeleteTargetFile";

    /**
     * The name of the 'doneFileName' element.
     */
    public static final String DONE_FILE_NAME = "doneFileName";

    /**
     * Create a binding model bound to given namespace.
     * 
     * @param name Element name.
     * @param namespace Namespace to bound.
     */
    public V1GenericFileProducerBindingModel(String name, String namespace) {
        super(name, namespace);
        setModelChildrenOrder(FILE_EXIST, TEMP_PREFIX, TEMP_FILENAME,
            KEEP_LAST_MODIFIED, EAGER_DELETE_TARGET_FILE, DONE_FILE_NAME);
    }

    /**
     * Create a binding model from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V1GenericFileProducerBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public String getFileExist() {
        return getConfig(FILE_EXIST);
    }

    @Override
    public V1GenericFileProducerBindingModel setFileExist(String fileExist) {
        return setConfig(FILE_EXIST, fileExist);
    }

    @Override
    public String getTempPrefix() {
        return getConfig(TEMP_PREFIX);
    }

    @Override
    public V1GenericFileProducerBindingModel setTempPrefix(String tempPrefix) {
        return setConfig(TEMP_PREFIX, tempPrefix);
    }

    @Override
    public String getTempFileName() {
        return getConfig(TEMP_FILENAME);
    }

    @Override
    public V1GenericFileProducerBindingModel setTempFileName(String tempFileName) {
        return setConfig(TEMP_FILENAME, tempFileName);
    }

    @Override
    public Boolean isKeepLastModified() {
        return getBooleanConfig(KEEP_LAST_MODIFIED);
    }

    @Override
    public V1GenericFileProducerBindingModel setKeepLastModified(Boolean keepLastModified) {
        return setConfig(KEEP_LAST_MODIFIED, keepLastModified);
    }

    @Override
    public Boolean isEagerDeleteTargetFile() {
        return getBooleanConfig(EAGER_DELETE_TARGET_FILE);
    }

    @Override
    public V1GenericFileProducerBindingModel setEagerDeleteTargetFile(Boolean eagerDeleteTargetFile) {
        return setConfig(EAGER_DELETE_TARGET_FILE, eagerDeleteTargetFile);
    }

    @Override
    public String getDoneFileName() {
        return getConfig(DONE_FILE_NAME);
    }

    @Override
    public V1GenericFileProducerBindingModel setDoneFileName(String doneFileName) {
        return setConfig(DONE_FILE_NAME, doneFileName);
    }

}
