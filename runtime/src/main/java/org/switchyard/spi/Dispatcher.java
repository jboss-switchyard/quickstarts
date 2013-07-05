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

package org.switchyard.spi;

import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ExchangePattern;
import org.switchyard.ServiceReference;

/**
 * Dispatches messages to a given service.
 */
public interface Dispatcher {

    /**
     * The name of the service.
     * @return service reference
     */
    ServiceReference getServiceReference();

    /**
     * Creates exchange which is ment to deliver to given operation.
     * 
     * @param handler Handler to handle reply from operation (if any returned). May be null.
     * @param pattern Exchange pattern used.
     * @return Exchange used to send messages.
     */
    Exchange createExchange(ExchangeHandler handler, ExchangePattern pattern);

    /**
     * Dispatch given exchange.
     * 
     * @param exchange Exchange to dispatch.
     */
    void dispatch(Exchange exchange);

}
