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
package org.switchyard.camel.component;

import org.apache.camel.Processor;
import org.switchyard.Exchange;
import org.switchyard.Message;
import org.switchyard.ServiceReference;
import org.switchyard.metadata.BaseExchangeContract;
import org.switchyard.metadata.ExchangeContract;
import org.switchyard.metadata.InOnlyOperation;
import org.switchyard.metadata.InOutOperation;

/**
 * Switchard processor is a Camel {@link Processor} that is capable
 * of sending the contents of a Camel Message to a SwitchYard Service.
 */
public class SwitchyardProcessor implements Processor {
    
    private ServiceReference _serviceRef;
    private final String _operationName;
    
    /**
     * Sole constructor.
     * 
     * @param operationName The operation name of the target service.
     */
    public SwitchyardProcessor(final String operationName) {
        _operationName = operationName;
    }
    
    /**
     * Sets the {@link ServiceReference} which is the target service for this processor.
     * 
     * @param serviceReference The {@link ServiceReference} for the destination service.
     */
    public void setServiceReference(final ServiceReference serviceReference) {
       _serviceRef = serviceReference; 
    }

    /**
     * Will extract the payload from the {@link org.apache.camel.Exchange}, by calling
     * getIn(), and send that payload to the SwitchYard service by using the {@link ServiceReference}.
     * 
     * @param camelExchange The Camel Exchange instance
     * @throws Exception If an error occurs while calling SwitchYard.
     */
    @Override
    public void process(final org.apache.camel.Exchange camelExchange) throws Exception {
        if (_serviceRef == null) {
            throw new NullPointerException("No ServiceReference was set for this SwitchyardProcessor.");
        }
        
        final Exchange switchyardExchange = createSwitchyardExchange(camelExchange);
        final Object camelPayload = camelExchange.getIn().getBody();
        final Message switchyardMsg = switchyardExchange.createMessage().setContent(camelPayload);
        
        switchyardExchange.send(switchyardMsg);
    }
    
    private Exchange createSwitchyardExchange(final org.apache.camel.Exchange ex) {
        return isInOnly(ex.getPattern()) ? createInOnlyExchange(_serviceRef) : createInOutExchange(_serviceRef, ex);
    }
    
    private boolean isInOnly(final org.apache.camel.ExchangePattern pattern) {
        return pattern == org.apache.camel.ExchangePattern.InOnly;
    }
    
    private Exchange createInOnlyExchange(final ServiceReference serviceReference) {
        return serviceReference.createExchange(new BaseExchangeContract(new InOnlyOperation(_operationName)));
    }
    
    private Exchange createInOutExchange(final ServiceReference serviceReference, final org.apache.camel.Exchange camelExchange) {
        final ExchangeContract contract = new BaseExchangeContract(new InOutOperation(_operationName));
        return serviceReference.createExchange(contract, new CamelResponseHandler(camelExchange));
    }
    
}
