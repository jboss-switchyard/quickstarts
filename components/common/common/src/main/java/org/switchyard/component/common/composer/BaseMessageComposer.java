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
package org.switchyard.component.common.composer;

import javax.xml.namespace.QName;

import org.switchyard.Exchange;

/**
 * Base class for MessageComposer.
 *
 * @param <D> the type of binding data
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public abstract class BaseMessageComposer<D extends BindingData> implements MessageComposer<D> {

    private ContextMapper<D> _contextMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public ContextMapper<D> getContextMapper() {
        return _contextMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageComposer<D> setContextMapper(ContextMapper<D> contextMapper) {
        _contextMapper = contextMapper;
        return this;
    }

    /**
     * Returns the current message type based on the state of the exchange.
     * @param exchange exchange to query
     * @return the current message type based on the exchange contract
     */
    public static QName getMessageType(Exchange exchange) {
        QName msgType;
        if (exchange.getPhase() == null) {
            msgType = exchange.getContract().getConsumerOperation().getInputType();
        } else {
            msgType = exchange.getContract().getProviderOperation().getOutputType();
        }
        
        return msgType;
    }

}
