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

import java.util.List;

/**
 * ExchangeInterceptors allow code to be injected around specific interactions
 * in the core exchange bus via a target definition.  Valid targets are documented
 * as constant fields in this interface.
 */
public interface ExchangeInterceptor {
    
    /**
     * Intercepts an exchange before a message is passed into the processing pipeline
     * and after all processing steps have completed.
     */
    String CONSUMER = "Consumer";
    
    /**
     * Intercepts an exchange immediately before the provider is invoked and again
     * immediately after the provider returns.
     */
    String PROVIDER = "Provider";
    
    /**
     * Invoked immediately before the specified target scope in a message exchange.
     * @param target interception point
     * @param exchange message exchange
     * @throws HandlerException in order to fail a message exchange
     */
    void before(String target, Exchange exchange) throws HandlerException;
    
    /**
     * Invoked immediately after the specified target scope in a message exchange.
     * @param target interception point
     * @param exchange message exchange
     * @throws HandlerException in order to fail a message exchange
     */
    void after(String target, Exchange exchange) throws HandlerException;
    
    /**
     * The list of targets, or interception points, where this interceptor should be invoked.
     * @return list of targets where this interceptor applies.
     */
    List<String> getTargets();
}
