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
package org.switchyard.bus.camel;

import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.HandlerException;

/**
 * An exchange handler which throws exception during message handling and
 * error handling.
 */
public class ErrorExchangeHandler implements ExchangeHandler {

    @Override
    public void handleMessage(Exchange exchange) throws HandlerException {
        throw new HandlerException("Service is not implemented");
    }

    @Override
    public void handleFault(Exchange exchange) {
        // it's service handler this method will not be called
    }

}
