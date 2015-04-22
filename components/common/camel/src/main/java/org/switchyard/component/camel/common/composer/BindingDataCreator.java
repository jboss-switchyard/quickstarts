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
package org.switchyard.component.camel.common.composer;

import org.apache.camel.Message;

/**
 * BindingDataCreator interface is an extension point which allows 3rd party endpoints
 * decide what kind of {@link CamelBindingData} instance should be created. This SPI
 * point allows implement SecurityBindingData for these endpoints who support secure
 * exchanges.
 *
 * @param <T> Type of binding data.
 */
public interface BindingDataCreator<T extends CamelBindingData> {

    /**
     * Creates new camel binding data for given input message.
     * 
     * @param message Message instance.
     * @return Camel binding data.
     */
    T createBindingData(Message message);

}
