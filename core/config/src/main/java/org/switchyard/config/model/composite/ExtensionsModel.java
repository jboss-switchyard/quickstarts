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
package org.switchyard.config.model.composite;

import org.switchyard.config.model.Model;
import org.switchyard.config.model.switchyard.ThrottlingModel;

/**
 * The "extensions" configuration model.
 */
public interface ExtensionsModel extends Model {

    /** The "extensions" name. */
    public static final String EXTENSIONS = "extensions";

    /**
     * Gets the child throttling model.
     * 
     * @return the child throttling model
     */
    public ThrottlingModel getThrottling();

    /**
     * Sets the child throttling model.
     * 
     * @param throttling child throttling model
     * @return this ExtensionsModel (useful for chaining)
     */
    public ExtensionsModel setThrottling(ThrottlingModel throttling);
}
