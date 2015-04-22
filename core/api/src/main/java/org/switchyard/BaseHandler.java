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

package org.switchyard;

/**
 * Empty implementation of the ExchangeHandler interface that can be used to
 * selectively override fault or message handling logic.
 */
public class BaseHandler implements ExchangeHandler {

    /**
     * @param exchange the exchange
     * @see ExchangeHandler#handleFault(Exchange)
     */
    public void handleFault(final Exchange exchange) {
        // Default implementation does nothing for now
    }

    /**
     * @param exchange the exchange
     * @throws HandlerException handler exception
     * @see ExchangeHandler#handleMessage(Exchange)
     */
    public void handleMessage(final Exchange exchange) throws HandlerException {
        // Default implementation does nothing for now
    }
}
