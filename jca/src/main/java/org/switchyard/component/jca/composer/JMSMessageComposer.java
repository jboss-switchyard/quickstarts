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
package org.switchyard.component.jca.composer;

import javax.jms.BytesMessage;
import javax.jms.Message;
import org.switchyard.Exchange;
import org.switchyard.component.common.composer.BaseMessageComposer;
import org.switchyard.exception.SwitchYardException;

/**
 * MessageComposer implementation for JMS Message that is used by JCA component.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public class JMSMessageComposer extends BaseMessageComposer<Message> {

    /**
     * {@inheritDoc}
     */
    @Override
    public org.switchyard.Message compose(Message source, Exchange exchange, boolean create) throws Exception {
        // assuming the message is BytesMessage in this composer
        BytesMessage sourceBytes = (BytesMessage)source;
        if (sourceBytes.getBodyLength() > Integer.MAX_VALUE) {
            throw new SwitchYardException("The size of message content exceeds "
                    + Integer.MAX_VALUE + " bytes, that is not supported by this MessageComposer");
        }
        byte[] bytearr = new byte[(int)sourceBytes.getBodyLength()];
        sourceBytes.readBytes(bytearr);
        
        final org.switchyard.Message message = create ? exchange.createMessage() : exchange.getMessage();
        getContextMapper().mapFrom(source, exchange.getContext());
        message.setContent(new String(bytearr));
        return message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message decompose(Exchange exchange, Message target) throws Exception {
        BytesMessage targetBytes = (BytesMessage)target;
        getContextMapper().mapTo(exchange.getContext(), target);
        final byte[] content = exchange.getMessage().getContent(String.class).getBytes();
        targetBytes.writeBytes(content);
        return target;
    }

}
