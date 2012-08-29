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
package org.switchyard.component.camel.composer;

import static org.switchyard.Exchange.FAULT_TYPE;
import static org.switchyard.Exchange.OPERATION_NAME;
import static org.switchyard.Exchange.SERVICE_NAME;

import java.io.InputStream;

import javax.xml.namespace.QName;

import org.switchyard.Exchange;
import org.switchyard.Message;
import org.switchyard.common.xml.QNameUtil;
import org.switchyard.component.common.composer.BaseMessageComposer;
import org.switchyard.metadata.ServiceOperation;

/**
 * The Camel implementation of MessageComposer.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class CamelMessageComposer extends BaseMessageComposer<CamelBindingData> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Message compose(CamelBindingData source, Exchange exchange, boolean create) throws Exception {
        // map context properties
        getContextMapper().mapFrom(source, exchange.getContext());

        org.apache.camel.Message sourceMessage = source.getMessage();

        // map content
        Message message = create ? exchange.createMessage() : exchange.getMessage();
        QName msgType = getMessageType(exchange);
        Object content;
        if (msgType == null) {
            content = sourceMessage.getBody();
        } else if (QNameUtil.isJavaMessageType(msgType)) {
            content = sourceMessage.getBody(QNameUtil.toJavaMessageType(msgType));
        } else {
            content = sourceMessage.getBody(InputStream.class);
        }
        message.setContent(content);
        return message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CamelBindingData decompose(Exchange exchange, CamelBindingData target) throws Exception {
        getContextMapper().mapTo(exchange.getContext(), target);

        org.apache.camel.Message targetMessage = target.getMessage();

        ServiceOperation operation = exchange.getContract().getServiceOperation();
        targetMessage.setHeader(OPERATION_NAME, operation.getName());
        targetMessage.setHeader(FAULT_TYPE, operation.getFaultType());
        targetMessage.setHeader(SERVICE_NAME, exchange.getServiceName());

        targetMessage.setBody(exchange.getMessage().getContent());
        return target;
    }
    
    /**
     * Returns the current message type based on the state of the exchange.
     * @param exchange exchange to query
     * @return the current message type based on the exchange contract
     */
    private QName getMessageType(Exchange exchange) {
        QName msgType;
        if (exchange.getPhase() == null) {
            msgType = exchange.getContract().getInvokerInvocationMetaData().getInputType();
        } else {
            msgType = exchange.getContract().getInvokerInvocationMetaData().getOutputType();
        }
        
        return msgType;
    }
}
