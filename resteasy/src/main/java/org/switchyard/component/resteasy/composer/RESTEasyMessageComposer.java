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
package org.switchyard.component.resteasy.composer;

import javax.ws.rs.WebApplicationException;

import org.switchyard.Exchange;
import org.switchyard.ExchangeState;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.component.common.composer.BaseMessageComposer;
import org.switchyard.component.resteasy.RestEasyLogger;

/**
 * Composes/decomposes RESTEasy messages.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public class RESTEasyMessageComposer extends BaseMessageComposer<RESTEasyBindingData> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Message compose(RESTEasyBindingData source, Exchange exchange) throws Exception {
        final Message message = exchange.createMessage();
        getContextMapper().mapFrom(source, exchange.getContext(message));
        Object content = null;
        if (source.getParameters().length > 0) {
            content = source.getParameters()[0];
        }
        message.setContent(content);

        if (source.getParameters().length > 1) {
            RestEasyLogger.ROOT_LOGGER.defaultRESTEasyMessageComposerDoesnTHandleMultipleInputParameters();
        }
        return message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RESTEasyBindingData decompose(Exchange exchange, RESTEasyBindingData target) throws Exception {
        Message sourceMessage = exchange.getMessage();
        Object content = sourceMessage.getContent();
        if (exchange.getContract().getProviderOperation() != null) {
            target.setOperationName(exchange.getContract().getProviderOperation().getName());
        } else if (exchange.getContract().getConsumerOperation() != null) {
            target.setOperationName(exchange.getContract().getConsumerOperation().getName());
        }
        
        if (exchange.getState().equals(ExchangeState.FAULT)) {
            if (content instanceof WebApplicationException) {
                throw (WebApplicationException)content;
            } else if (content instanceof HandlerException) {
                Throwable throwable = ((HandlerException) content).getCause();
                if (throwable != null) {
                    if (throwable instanceof WebApplicationException) {
                        throw (WebApplicationException) throwable;
                    } else {
                        throw new WebApplicationException(throwable);
                    }
                } else {
                    throw new WebApplicationException((HandlerException) content);
                }
            } else if (content instanceof Exception) {
                throw (Exception) content;
            } else if (content instanceof Throwable) {
                throw new WebApplicationException((Throwable)content);
            } else {
                throw new WebApplicationException(new Exception(sourceMessage.getContent(String.class)));
            }
        }
        if (content != null) {
            target.setParameters(new Object[]{content});
        }
        getContextMapper().mapTo(exchange.getContext(), target);
        return target;
    }

}
