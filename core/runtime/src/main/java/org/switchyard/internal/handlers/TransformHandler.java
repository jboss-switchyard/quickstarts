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

package org.switchyard.internal.handlers;

import java.util.Arrays;
import java.util.HashSet;

import org.switchyard.BaseHandler;
import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.Scope;
import org.switchyard.internal.transform.BaseTransformerRegistry;
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

    private static final String MESSAGE_NAME =
        "org.switchyard.message.name";
    private static final String SERVICE_MESSAGE_NAME =
        "org.switchyard.service.message.name";

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
                    new HashSet<Transformer>(Arrays.asList(transforms)));
        }
    }

    /**
     * Transform the current message on the exchange.
     * @param exchange exchange
     * @throws HandlerException handler exception
     */
    @Override
    public void handleMessage(Exchange exchange) throws HandlerException {
        Transformer t = locateTransform(exchange);

        if (t == null) {
            // nothing to do
            return;
        }

        Message msg = exchange.getMessage();
        Object fromContent = msg.getContent();
        Object toContent = t.transform(fromContent);
        msg.setContent(toContent);
    }

    /**
     * Locate a transformer instance to perform transformation.  The following
     * sources are searched in order: <br>
     * 1) A transformer set in the message context <br>
     * 2) A transformer set in the exchange context <br>
     * 3) A transformer found through a registry search <br>
     * @param exchange exchange
     */
    private Transformer locateTransform(Exchange exchange) {

        Transformer transform = null;

        // look in message context
        if (exchange.getContext(Scope.MESSAGE).hasProperty(Transformer.class.getName())) {
            transform = (Transformer)
                exchange.getContext(Scope.MESSAGE).getProperty(Transformer.class.getName());
        // look in exchange context
        } else if (exchange.getContext().hasProperty(Transformer.class.getName())) {
            transform = (Transformer)
                exchange.getContext().getProperty(Transformer.class.getName());
        // look to see if we can find it in the transformer registry
        } else {
            Context msgCtx = exchange.getContext(Scope.MESSAGE);
            String fromName = (String) msgCtx.getProperty(MESSAGE_NAME);
            // temp hack - toName should actually come from the Service reference
            String toName = (String) msgCtx.getProperty(SERVICE_MESSAGE_NAME);
            transform = _registry.getTransformer(fromName, toName);
        }

        return transform;
    }
}

