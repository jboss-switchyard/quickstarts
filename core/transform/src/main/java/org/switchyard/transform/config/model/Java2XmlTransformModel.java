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
 * A "transform.java2xml" configuration model.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
  */
public interface Java2XmlTransformModel extends TransformModel {

    /** The "java2xml" name. */
    public static final String JAVA2XML = "java2xml";

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
     * @return this Java2XmlTransformModel (useful for chaining)
     */
    public Java2XmlTransformModel setConfig(String config);

}
