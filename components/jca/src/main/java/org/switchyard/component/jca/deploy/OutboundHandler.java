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
package org.switchyard.component.jca.deploy;

import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.Scope;
import org.switchyard.ServiceDomain;
import org.switchyard.component.jca.JCAMessages;
import org.switchyard.component.jca.processor.AbstractOutboundProcessor;
import org.switchyard.deploy.BaseServiceHandler;
import org.switchyard.label.BehaviorLabel;
import org.switchyard.runtime.event.ExchangeCompletionEvent;

/**
 * An ExchangeHandler for JCA outbound binding.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class OutboundHandler extends BaseServiceHandler {
    
    private AbstractOutboundProcessor _processor;
    private final String _bindingName;
    private final String _referenceName;

    /**
     * Constructor.
     * 
     * @param processor {@link AbstractOutboundProcessor}
     * @param domain the service domain
     */
    public OutboundHandler(AbstractOutboundProcessor processor, ServiceDomain domain) {
        super(domain);
        _processor = processor;
        _bindingName = processor.getJCABindingModel().getName();
        _referenceName = processor.getJCABindingModel().getReference().getName();
    }

    @Override
    protected void doStart() {
        _processor.initialize();
    }
    
    @Override
    protected void doStop() {
        _processor.uninitialize();
    }
    
    @Override
    public void handleMessage(final Exchange exchange) throws HandlerException {
        // identify ourselves
        exchange.getContext()
                .setProperty(ExchangeCompletionEvent.GATEWAY_NAME, _bindingName,
                        Scope.EXCHANGE).addLabels(BehaviorLabel.TRANSIENT.label());
        if (getState() != State.STARTED) {
            throw JCAMessages.MESSAGES.referenceBindingNotStarted(_referenceName, _bindingName);
        }

        Message out = _processor.process(exchange);
        if (exchange.getContract().getProviderOperation().getExchangePattern() == ExchangePattern.IN_OUT) {
            exchange.send(out);
        }
    }
}
