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

import java.util.HashMap;
import java.util.Map;

import org.switchyard.Exchange;
import org.switchyard.Message;
import org.switchyard.component.common.composer.BaseMessageComposer;

/**
 * MessageComposer implementation for CCI MappedRecord that is used by JCA component.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public class MappedRecordMessageComposer extends BaseMessageComposer<MappedRecordBindingData> {

    /**
     * {@inheritDoc}
     */
    @Override
    public org.switchyard.Message compose(MappedRecordBindingData source, Exchange exchange, boolean create) throws Exception {
        
        final org.switchyard.Message message = create ? exchange.createMessage() : exchange.getMessage();
        getContextMapper().mapFrom(source, exchange.getContext(message));
        Map<Object,Object> m = new HashMap<Object,Object>();
        m.putAll(source.getRecord());
        message.setContent(m);
        return message;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public MappedRecordBindingData decompose(Exchange exchange, MappedRecordBindingData target) throws Exception {
        Message sourceMessage = exchange.getMessage();

        getContextMapper().mapTo(exchange.getContext(), target);
        final Map<?,?> content = sourceMessage.getContent(Map.class);
        target.getRecord().putAll(content);
        return target;
    }

}
