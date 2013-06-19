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
package org.switchyard.component.camel.switchyard.util;

import org.switchyard.Exchange;
import org.switchyard.Message;
import org.switchyard.component.camel.common.composer.CamelBindingData;
import org.switchyard.component.camel.common.composer.CamelMessageComposer;

/**
 * Dummy message composer.
 */
public class Composer extends CamelMessageComposer {

    public static final String COMPOSE_PREFIX = "Composer compose ";
    public static final String DECOMPOSE_PREFIX = "Composer decompose ";

    @Override
    public Message compose(CamelBindingData source, Exchange exchange) throws Exception {
        Message message = super.compose(source, exchange);
        message.setContent(COMPOSE_PREFIX + message.getContent(String.class));
        return message;
    }

    @Override
    public CamelBindingData decompose(Exchange exchange, CamelBindingData target) throws Exception {
        CamelBindingData decompose = super.decompose(exchange, target);
        decompose.getMessage().setBody(DECOMPOSE_PREFIX + decompose.getMessage().getBody(String.class));
        return decompose;
    }
}
