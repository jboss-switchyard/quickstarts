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
package org.switchyard.component.jca.composer;

import java.util.ArrayList;
import java.util.List;

import org.switchyard.Exchange;
import org.switchyard.Message;
import org.switchyard.component.common.composer.BaseMessageComposer;

/**
 * MessageComposer implementation for CCI IndexedRecord that is used by JCA component.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public class IndexedRecordMessageComposer extends BaseMessageComposer<IndexedRecordBindingData> {

    /**
     * {@inheritDoc}
     */
    @Override
    public org.switchyard.Message compose(IndexedRecordBindingData source, Exchange exchange) throws Exception {
        
        final org.switchyard.Message message = exchange.createMessage();
        getContextMapper().mapFrom(source, exchange.getContext(message));
        List<Object> l = new ArrayList<Object>();
        l.addAll(source.getRecord());
        message.setContent(l);
        return message;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public IndexedRecordBindingData decompose(Exchange exchange, IndexedRecordBindingData target) throws Exception {
        Message sourceMessage = exchange.getMessage();

        getContextMapper().mapTo(exchange.getContext(), target);
        final List<?> content = sourceMessage.getContent(List.class);
        target.getRecord().addAll(content);
        return target;
    }

}
