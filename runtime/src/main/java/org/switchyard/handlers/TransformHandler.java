/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */

package org.switchyard.handlers;

import java.util.Arrays;
import java.util.HashSet;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.switchyard.BaseHandler;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.internal.transform.BaseTransformerRegistry;
import org.switchyard.transform.TransformSequence;
import org.switchyard.transform.Transformer;
import org.switchyard.transform.TransformerRegistry;

/**
 * ExchangeHandler implementation used to introduce transformations to the
 * exchange handler chain.  The core runtime automatically creates a
 * TransformHandler and attaches it to the consumer handler chain for every
 * exchange.  TransformHandler can also be used in the service provider's
 * chain by using the <code>TransformHandler(Transformer<?,?>)</code>
 * constructor.
 *
 */
public class TransformHandler extends BaseHandler {

    private static final Logger _logger = Logger.getLogger(TransformHandler.class);

    private TransformerRegistry _registry;

    /**
     * Create a new TransformHandler.  The specified TransformerRegistry will
     * be used to locate transformers for each handled exchange.
     * @param registry transformation registry to use for lookups of transformer
     * instances
     */
    public TransformHandler(TransformerRegistry registry) {
        _registry = registry;
    }

    /**
     * Create a new TransformHandler.  The specified list of transforms will
     * be used in place of a TransformerRegistry to locate transforms for each
     * handled exchange.
     * @param transforms transform map
     */
    public TransformHandler(Transformer<?, ?> ... transforms) {
        if (transforms != null && transforms.length > 0) {
            _registry = new BaseTransformerRegistry(
                    new HashSet<Transformer<?,?>>(Arrays.asList(transforms)));
        }
    }

    /**
     * Transform the current message on the exchange.
     * @param exchange exchange
     * @throws HandlerException handler exception
     */
    @Override
    public void handleMessage(Exchange exchange) throws HandlerException {
        // Apply transforms to the message...
        TransformSequence.applySequence(exchange, _registry);
        if (!TransformSequence.assertTransformsApplied(exchange)) {
            QName actualPayloadType = TransformSequence.getCurrentMessageType(exchange);
            QName expectedPayloadType = TransformSequence.getTargetMessageType(exchange);

            throw new HandlerException("Transformations not applied.  Required payload type of '" + expectedPayloadType + "'.  Actual payload type is '" + actualPayloadType + "'.  You must define and register a Transformer to transform between these types.");
        }
    }

    @Override
    public void handleFault(Exchange exchange) {
        // Apply transforms to the fault...
        TransformSequence.applySequence(exchange, _registry);
        if (!TransformSequence.assertTransformsApplied(exchange)) {
            QName actualPayloadType = TransformSequence.getCurrentMessageType(exchange);
            QName expectedPayloadType = TransformSequence.getTargetMessageType(exchange);

            if (_logger.isDebugEnabled()) {
                _logger.debug("Transformations not applied.  Required payload type of '" + expectedPayloadType + "'.  Actual payload type is '" + actualPayloadType + "'.  You must define and register a Transformer to transform between these types.");
            }
        }
    }
}

