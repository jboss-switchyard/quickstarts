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

/**
 * Binding model for file based producers.
 * 
 * @author Lukasz Dywicki
 */
public interface GenericFileProducerBindingModel {

    /**
     * What to do if a file already exists with the same name. The following
     * values can be obtained: Override, Append, Fail and Ignore.
     * 
     * @return the fileExist value
     */
    String getFileExist();

    /**
     * Specify what to do if a file already exists with the same name.
     * 
     * @param fileExist one of these: Override, Append, Fail or Ignore
     * @return a reference to this Camel File binding model
     */
    GenericFileProducerBindingModel setFileExist(String fileExist);

    /**
     * This option is used to write the file using a temporary name and then,
     * after the write is complete, rename it to the real name.
     * 
     * @return the tempPrefix value
     */
    String getTempPrefix();

    /**
     * Specify the name of the temporary name when writting it. After the write
     * is complete, it will be renamed to the real name.
     * 
     * @param tempPrefix the temporary file name
     * @return a reference to this Camel File binding model
     */
    GenericFileProducerBindingModel setTempPrefix(String tempPrefix);

    /**
     * Camel 2.1: The same as tempPrefix option but offering a more fine grained
     * control on the naming of the temporary filename.
     * 
     * @return the temporary file name value
     */
    String getTempFileName();

    /**
     * Camel 2.1: The same as tempPrefix option but offering a more fine grained
     * control on the naming of the temporary filename.
     * 
     * @param tempFileName
     *            the temporary file name
     * @return a reference to this Camel File binding model
     */
    GenericFileProducerBindingModel setTempFileName(String tempFileName);

    /**
     * Camel 2.2: Will keep the last modified timestamp from the source file (if any).
     * 
     * @return true to keep the last modified timestamp; false otherwise
     */
    Boolean isKeepLastModified();

    /**
     * Camel 2.2: Will keep the last modified timestamp from the source file (if any).
     * 
     * @param keepLastModified whether to keep the last modified timestamp (true), or not (false)
     * @return a reference to this Camel File binding model
     */
    GenericFileProducerBindingModel setKeepLastModified(Boolean keepLastModified);

    /**
     * Camel 2.3: Whether or not to eagerly delete any existing target file.
     * 
     * @return true if eagerly delete existing target file; false otherwise
     */
    Boolean isEagerDeleteTargetFile();

    /**
     * Camel 2.3: Whether or not to eagerly delete any existing target file.
     * 
     * @param eagerDeleteTargetFile true if eagerly delete existing target file; false otherwise
     * @return a reference to this Camel File binding model
     */
    GenericFileProducerBindingModel setEagerDeleteTargetFile(Boolean eagerDeleteTargetFile);

    /**
     * Camel 2.6: If provided, then Camel will write a 2nd done file when the
     * original file has been written.
     * 
     * @return the file name to use
     */
    String getDoneFileName();

    /**
     * If provided, then Camel will write a 2nd done file when the original file
     * has been written.
     * 
     * @param doneFileName the file name to use
     * @return a reference to this Camel File binding model
     */
    GenericFileProducerBindingModel setDoneFileName(String doneFileName);

}
