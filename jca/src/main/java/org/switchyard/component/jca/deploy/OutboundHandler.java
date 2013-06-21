/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.jca.deploy;

import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.Scope;
import org.switchyard.ServiceDomain;
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
            throw new HandlerException(String.format("Reference binding \"%s/%s\" is not started.", _referenceName,
                    _bindingName));
        }

        Message out = _processor.process(exchange);
        if (exchange.getContract().getProviderOperation().getExchangePattern() == ExchangePattern.IN_OUT) {
            exchange.send(out);
        }
    }
}
