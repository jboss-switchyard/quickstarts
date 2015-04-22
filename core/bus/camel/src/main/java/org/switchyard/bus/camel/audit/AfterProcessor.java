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
import org.apache.camel.Processor;
import org.switchyard.bus.camel.processors.Processors;

/**
 * Camel processor calling {@link Auditor#afterCall(Processors, Exchange)} of auditor.
 */
public class AfterProcessor implements Processor {

    private final Processors _processor;
    private final Auditor _auditor;

    /**
     * Creates new processor.
     * 
     * @param processor Processing step.
     * @param auditor Auditor instance.
     */
    public AfterProcessor(Processors processor, Auditor auditor) {
        this._processor = processor;
        this._auditor = auditor;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        _auditor.afterCall(_processor, exchange);
    }

}
