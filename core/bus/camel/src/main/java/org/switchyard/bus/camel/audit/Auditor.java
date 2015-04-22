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
package org.switchyard.bus.camel.audit;

import org.apache.camel.Exchange;
import org.switchyard.bus.camel.processors.Processors;

/**
 * Basic interface for auditing features.
 */
public interface Auditor {

    /**
     * Method called before given processor.
     * 
     * @param processor Pointer to processing step in SwitchYard flow.
     * @param exchange Related exchange.
     */
    void beforeCall(Processors processor, Exchange exchange);

    /**
     * Method called after given processor.
     * 
     * @param processor Pointer to processing step in SwitchYard flow.
     * @param exchange Related exchange.
     */
    void afterCall(Processors processor, Exchange exchange);

}
