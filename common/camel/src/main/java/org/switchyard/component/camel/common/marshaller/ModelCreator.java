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
package org.switchyard.component.camel.common.marshaller;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;

/**
 * Callback interface used to produce model instances for given configuration and
 * descriptor.
 * 
 * @param <T> Type of model.
 */
public interface ModelCreator<T extends Model> {

    /**
     * Creates new model instance from given configuration and descriptor.
     * 
     * @param config Configuration object.
     * @param descriptor Descriptor instance.
     * @return Model wrapping given configuration.
     */
    T create(Configuration config, Descriptor descriptor);

}
