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
package org.switchyard.component.camel.switchyard;

import javax.activation.DataHandler;

import org.apache.camel.impl.DefaultMessage;
import org.switchyard.ExchangePhase;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.common.camel.ContextPropertyUtil;
import org.switchyard.common.camel.HandlerDataSource;
import org.switchyard.common.camel.SwitchYardMessage;
import org.switchyard.label.BehaviorLabel;

/**
 * ExchangeMapper handles the mapping between SwitchYard and Camel exchanges when used from a Camel
 * implementation.  This mapping is distinct from what message composers do with gateways for two
 * reasons: 
 * 1) Gateway composers are configurable by users. Internal mapping between exchanges
 * within a camel implementation should not be configurable.
 * 2) The mapping rules for gateways and implementations can differ in subtle ways.  For example,
 * transient context properties propagate from gateway routes with SwitchYardProducer, but this should
 * not happen in implementation routes as it allows exchange properties to leak between multiple 
 * switchyard:// endpoint invocations.
 */
public final class ExchangeMapper {
    
    private ExchangeMapper() {
        
    }

    /**
     * Map from a camel exchange to a SwitchYard exchange.
     * @param camelExchange the camel exchange
     * @param syExchange the switchyard exchange
     * @param phase ExchangePhase.IN to target camelExchange.getIn(), ExchangePhase.OUT to 
     * target camelExchange.getIn().
     * @return the 
     */
    public static org.switchyard.Message mapCamelToSwitchYard(
            org.apache.camel.Exchange camelExchange,
            org.switchyard.Exchange syExchange,
            ExchangePhase phase) {
        
        // Associate the correct Camel message with the SY exchange
        org.switchyard.Message message = syExchange.createMessage();
        org.apache.camel.Message camelMessage;
        if (phase.equals(ExchangePhase.OUT) && camelExchange.hasOut()) {
            camelMessage = camelExchange.getOut();
        } else {
            camelMessage = camelExchange.getIn();
        }
        message.setContent(camelMessage.getBody());
        
        for (String property : camelExchange.getProperties().keySet()) {
            if (ContextPropertyUtil.isReservedProperty(property, Scope.EXCHANGE)) {
                continue;
            }
            // skip the implementation route property as we don't want that propagated
            if (SwitchYardConsumer.IMPLEMENTATION_ROUTE.equals(property)) {
                continue;
            }
            
            message.getContext().setProperty(property, camelExchange.getProperty(property), Scope.EXCHANGE);
        }
        for (String header : camelMessage.getHeaders().keySet()) {
            if (ContextPropertyUtil.isReservedProperty(header, Scope.MESSAGE)) {
                continue;
            }
            message.getContext().setProperty(header, camelMessage.getHeader(header), Scope.MESSAGE);
        }

        for (String attachmentName : camelMessage.getAttachmentNames()) {
            message.addAttachment(attachmentName, new HandlerDataSource(camelMessage.getAttachment(attachmentName)));
        }
        
        return message;
    }
    
    /**
     * Maps a SwitchYard exchange to a Camel exchange.  Keep in mind that the camel message created
     * during mapping is *not* associate with the exchange.  You need to call setIn() or setOut() 
     * with the returned reference depending on your use case.
     * @param syExchange switchyard exchange
     * @param camelExchange camel exchange
     * @return mapped camel message
     */
    public static DefaultMessage mapSwitchYardToCamel(
            org.switchyard.Exchange syExchange,
            org.apache.camel.Exchange camelExchange) {
        
        DefaultMessage camelMessage = new SwitchYardMessage();
        camelMessage.setBody(syExchange.getMessage().getContent());

        for (Property property : syExchange.getContext().getProperties()) {
            if (property.hasLabel(BehaviorLabel.TRANSIENT.label()) 
                    || ContextPropertyUtil.isReservedProperty(property.getName(), property.getScope())) {
                continue;
            }

            if (Scope.EXCHANGE.equals(property.getScope())) {
                camelExchange.setProperty(property.getName(), property.getValue());
            } else {
                camelMessage.setHeader(property.getName(), property.getValue());
            }
        }
        
        for (String attachmentName : syExchange.getMessage().getAttachmentMap().keySet()) {
            camelMessage.addAttachment(attachmentName, 
                    new DataHandler(syExchange.getMessage().getAttachment(attachmentName)));
        }
        
        return camelMessage;
    }
}
