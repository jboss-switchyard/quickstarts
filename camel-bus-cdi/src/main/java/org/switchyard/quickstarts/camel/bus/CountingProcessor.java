/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
