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
 * The role of a Handler is to handle messages and fault events during the
 * course of a service invocation.  When consuming a service, a handler can be
 * registered during Exchange creation to handle consumer events (e.g. fault
 * or reply message).  When providing a service, a handler is specified while
 * registering the service to process invocations of that service.
 */
public interface ExchangeHandler {
    /**
     * Called when a message is sent through an exchange.
     * @param exchange an {@code Exchange} instance containing a message to be
     * processed
     * @throws HandlerException when handling of the message event fails (e.g.
     * invalid request message).
     */
    void handleMessage(Exchange exchange) throws HandlerException;
    /**
     * Called when a fault is generated while processing an exchange.
     * @param exchange an {@code Exchange} instance containing a message to be
     * processed
     */
    void handleFault(Exchange exchange);
}
