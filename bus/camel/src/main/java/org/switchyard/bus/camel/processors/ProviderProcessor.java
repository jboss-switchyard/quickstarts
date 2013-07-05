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
package org.switchyard.bus.camel.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.switchyard.bus.camel.CamelExchange;

/**
 * Processor calling service provider.
 */
public class ProviderProcessor implements Processor {

    @Override
    public void process(Exchange ex) throws Exception {
        CamelExchange exchange = new CamelExchange(ex);
        exchange.getProvider().getProviderHandler().handleMessage(exchange);
    }

    @Override
    public String toString() {
        return "ProviderProcessor@" + System.identityHashCode(this);
    }

}
