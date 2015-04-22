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
package org.switchyard.quickstarts.rest.binding;

import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.core.BaseClientResponse;
import org.switchyard.Exchange;
import org.switchyard.ExchangeState;
import org.switchyard.HandlerException;
import org.switchyard.Message;

import org.switchyard.component.resteasy.composer.RESTEasyBindingData;
import org.switchyard.component.resteasy.composer.RESTEasyMessageComposer;

/**
 * Composes/decomposes multiple parameter RESTEasy messages.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public class CustomComposer extends RESTEasyMessageComposer {

    /**
     * {@inheritDoc}
     */
    @Override
    public Message compose(RESTEasyBindingData source, Exchange exchange) throws Exception {
        final Message message = super.compose(source, exchange);
        if (message.getContent() instanceof BaseClientResponse) {
            BaseClientResponse<?> clientResponse = (BaseClientResponse<?>) message.getContent();
            if (clientResponse.getResponseStatus() == Response.Status.NOT_FOUND) {
                throw new ItemNotFoundException("Item not found");
            }
        } else if (source.getOperationName().equals("addItem") && (source.getParameters().length == 2)) {
            // Wrap the parameters
            Item item = new Item((Integer) source.getParameters()[0], (String) source.getParameters()[1]);
            message.setContent(item);
        }
        return message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RESTEasyBindingData decompose(Exchange exchange, RESTEasyBindingData target) throws Exception {
        Object content = exchange.getMessage().getContent();
        if (exchange.getState().equals(ExchangeState.FAULT)) {
            if (content instanceof HandlerException) {
                HandlerException he = (HandlerException) content;
                if (he.getCause() instanceof ItemNotFoundException) {
                    throw (Exception) he.getCause();
                }
            }
        }

        target = super.decompose(exchange, target);

        if (target.getOperationName().equals("addItem") && (content != null) && (content instanceof Item)) {
            // Unwrap the parameters
            target.setParameters(new Object[] { ((Item) content).getItemId(), ((Item) content).getName() });
        }
        return target;
    }
}
