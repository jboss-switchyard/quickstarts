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
package org.switchyard.component.jca.endpoint;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.switchyard.Exchange;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.jca.composer.JMSBindingData;
import org.switchyard.component.common.selector.OperationSelector;
import org.switchyard.exception.SwitchYardException;
/**
 * Concrete message endpoint class for JCA message inflow using JMS MessageListener interface.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class JMSEndpoint extends AbstractInflowEndpoint implements MessageListener {
    
    private MessageComposer<JMSBindingData> _composer;
    private OperationSelector<JMSBindingData> _selector;
    
    @Override
    public void initialize() {
        super.initialize();
        _composer = getMessageComposer(JMSBindingData.class);
        _selector = getOperationSelector(JMSBindingData.class);
    }
    
    @Override
    public void onMessage(Message message) {
        
        try {
            JMSBindingData bindingData = new JMSBindingData(message);
            final String operation = _selector != null ? _selector.selectOperation(bindingData).getLocalPart() : null;
            final Exchange exchange = createExchange(operation);
            exchange.send(_composer.compose(bindingData, exchange, true));
        } catch (Exception e) {
            throw new SwitchYardException(e);
        }
    }
    
}
