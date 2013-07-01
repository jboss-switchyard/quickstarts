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
package org.switchyard.component.jca.endpoint;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.switchyard.Exchange;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.jca.composer.JMSBindingData;
import org.switchyard.SwitchYardException;
import org.switchyard.selector.OperationSelector;
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
            exchange.send(_composer.compose(bindingData, exchange));
        } catch (Exception e) {
            throw new SwitchYardException(e);
        }
    }
    
}
