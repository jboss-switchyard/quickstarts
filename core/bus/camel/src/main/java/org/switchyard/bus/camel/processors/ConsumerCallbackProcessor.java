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
import org.switchyard.ExchangeHandler;
import org.switchyard.ExchangePattern;
import org.switchyard.ExchangeState;
import org.switchyard.bus.camel.CamelExchange;

/**
 * Processor used to call reply chain.
 */
public class ConsumerCallbackProcessor implements Processor {

    @Override
    public void process(Exchange ex) throws Exception {
        org.switchyard.Exchange syEx = new CamelExchange(ex);

        if (syEx.getState() == ExchangeState.FAULT) {
            ExchangeHandler handler = syEx.getReplyHandler();
            if (handler != null) {
                handler.handleFault(syEx);
            }
        } else {
            // Only call back with reply messages if the MEP is In-Out
            if (ExchangePattern.IN_OUT.equals(syEx.getPattern())) {
                syEx.getReplyHandler().handleMessage(syEx);
            }
        }
    }
}
