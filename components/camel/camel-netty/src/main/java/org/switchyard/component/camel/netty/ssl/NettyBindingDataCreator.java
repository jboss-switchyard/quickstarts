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
package org.switchyard.component.camel.netty.ssl;

import org.apache.camel.Message;
import org.switchyard.component.camel.common.composer.BindingDataCreator;

/**
 * Netty-specific binding data creator.
 */
public class NettyBindingDataCreator implements BindingDataCreator<NettyBindingData> {

    @Override
    public NettyBindingData createBindingData(Message message) {
        return new NettyBindingData(message);
    }

}
