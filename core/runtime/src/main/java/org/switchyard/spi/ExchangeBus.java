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

import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;

/**
 * An implementation-neutral representation of a message bus.  The bus handles
 * dispatch for message exchange between service consumer and provider.
 */
public interface ExchangeBus {
    
    /**
     * Create an exchange dispatcher for the specified service reference.
     * @param reference service reference
     * @return Dispatcher service dispatch reference
     */
    Dispatcher createDispatcher(ServiceReference reference);
    
    /**
     * Retrieves a dispatcher for the specified service reference.
     * @param reference target reference
     * @return dispatcher for the specified service, or null if no dispatcher
     * has been created
     */
    Dispatcher getDispatcher(ServiceReference reference);

    /**
     * Initialize the exchange bus with it's parent service domain.
     * @param domain service domain in which this bus will be used
     */
    void init(ServiceDomain domain);
    
    /**
     * Start the exchange bus.
     */
    void start();
    
    /**
     * Stop the exchange bus.
     */
    void stop();

}
