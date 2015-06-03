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
package org.switchyard.component.camel.common.model.file;

import org.switchyard.component.camel.common.model.CamelBindingModel;

/**
 * Binding model for file endpoints.
 * 
 * @author Lukasz Dywicki
 */
public interface GenericFileBindingModel extends CamelBindingModel {

    /**
     * The endpoint directory.
     * @return the directory
     */
    String getDirectory();

    /**
     * Specify the endpoint consume/produce directory.
     * @param directory the target directory
     * @return a reference to this Camel binding model
     */
    GenericFileBindingModel setDirectory(String directory);

    /**
     * Whether missing directories in the file's pathname will be created or not. 
     * For the file consumer, that means creating the starting directory. 
     * For the file producer, it means the directory to where the files should be written.
     * @return the auto-create setting or null if it has not been specified
     */
    Boolean isAutoCreate();

    /**
     * Specify whether missing directories in the file's pathname will be created or not.
     * @param autoCreate true if directories will be created, false otherwise
     * @return a reference to this Camel binding model
     */
    GenericFileBindingModel setAutoCreate(Boolean autoCreate);

    /**
     * Buffer sized in bytes.
     * @return the buffer size setting or null if it has not been specified
     */
    Integer getBufferSize();

    /**
     * Specify buffer size in bytes.
     * @param bufferSize buffer size in bytes
     * @return a reference to this Camel binding model
     */
    GenericFileBindingModel setBufferSize(Integer bufferSize);

    /**
     * Expression such as File Language to dynamically set the filename. 
     * For consumers, it's used as a filename filter. 
     * For producers, it's used to evaluate the filename to write
     * @return the file name setting or null if it has not been specified
     */
    String getFileName();

    /**
     * Specify expression such as File Language to dynamically set the filename.
     * @param fileName expression to dynamically set the filename
     * @return a reference to this Camel binding model
     */
    GenericFileBindingModel setFileName(String fileName);

    /**
     * Whether the file name path will get leading paths stripped; so it's just the file name. 
     * @return the flatten setting or null if it has not been specified
     */
    Boolean isFlatten();

    /**
     * Specify whether the file name will be stripped.
     * @param flatten true to get it flattened, false otherwise
     * @return a reference to this Camel binding model
     */
    GenericFileBindingModel setFlatten(Boolean flatten);

    /**
     * This option is used to specify the encoding of the file, and camel will 
     * set the Exchange property with Exchange.CHARSET_NAME with the value of this option. 
     * @return the charset setting or null if it has not been specified
     */
    String getCharset();

    /**
     * Specify the encoding of the file.
     * 
     * @param charset the encoding of the file
     * @return a reference to this Camel binding model
     */
    GenericFileBindingModel setCharset(String charset);

}
