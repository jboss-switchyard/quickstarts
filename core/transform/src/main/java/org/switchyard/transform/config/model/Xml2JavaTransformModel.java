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

import org.switchyard.config.model.transform.TransformModel;

/**
 * A "transform.xml2java" configuration model.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public interface Xml2JavaTransformModel extends TransformModel {

    /** The "xml2java" name. */
    public static final String XML2JAVA = "xml2java";

    /** The "config" name. */
    public static final String CONFIG = "config";

    /**
     * Gets the config attribute.
     * @return the config attribute
     */
    public String getConfig();

    /**
     * Sets the config attribute.
     * @param config the config attribute
     * @return this Xml2JavaTransformModel (useful for chaining)
     */
    public Xml2JavaTransformModel setConfig(String config);

}
