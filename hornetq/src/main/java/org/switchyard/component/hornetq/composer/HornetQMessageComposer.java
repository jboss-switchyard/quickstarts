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
package org.switchyard.component.hornetq.composer;

import org.switchyard.Exchange;
import org.switchyard.Message;
import org.switchyard.component.common.composer.BaseMessageComposer;
import org.switchyard.component.hornetq.internal.HornetQUtil;

/**
 * The HornetQ implementation of MessageComposer.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class HornetQMessageComposer extends BaseMessageComposer<HornetQBindingData> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Message compose(HornetQBindingData source, Exchange exchange, boolean create) throws Exception {
        final Message message = create ? exchange.createMessage() : exchange.getMessage();
        getContextMapper().mapFrom(source, exchange.getContext());
        message.setContent(HornetQUtil.readBytes(source.getClientMessage()));
        return message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HornetQBindingData decompose(Exchange exchange, HornetQBindingData target) throws Exception {
        getContextMapper().mapTo(exchange.getContext(), target);
        final byte[] content = exchange.getMessage().getContent(byte[].class);
        target.getClientMessage().getBodyBuffer().writeBytes(content);
        return target;
    }

}
