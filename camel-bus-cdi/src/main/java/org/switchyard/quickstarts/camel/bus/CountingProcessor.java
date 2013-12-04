/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.switchyard.quickstarts.camel.bus;

import org.apache.camel.AsyncCallback;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.processor.DelegateAsyncProcessor;
import org.apache.log4j.Logger;

/**
 * Example implementation of processor which counts execution time of wrapped
 * processors.
 */
public class CountingProcessor extends DelegateAsyncProcessor {

    private Logger _logger = Logger.getLogger(CountingProcessor.class);
    private final ProcessorDefinition<?> _node;

    /**
     * Creates new processor wrapper which counts execution time of wrapped processor.
     * 
     * @param definition Definition of node.
     * @param target Wrapped processor.
     */
    public CountingProcessor(ProcessorDefinition<?> definition, Processor target) {
        super(target);
        this._node = definition;
    }

    @Override
    public boolean process(Exchange exchange, final AsyncCallback callback) {
        final long time = System.currentTimeMillis();
        return super.process(exchange, new AsyncCallback() {
            @Override
            public void done(boolean doneSync) {
                if (doneSync) {
                    long processingTime = System.currentTimeMillis() - time;
                    _logger.info("Route node " + _node.getLabel() + " took " + processingTime + " ms");
                }
                callback.done(doneSync);
            }
        });
    }

}
