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
package org.switchyard.component.camel.common.composer;

import static org.switchyard.Exchange.FAULT_TYPE;
import static org.switchyard.Exchange.OPERATION_NAME;
import static org.switchyard.Exchange.SERVICE_NAME;

import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.switchyard.Exchange;
import org.switchyard.Message;
import org.switchyard.common.xml.QNameUtil;
import org.switchyard.component.common.composer.BaseMessageComposer;
import org.switchyard.metadata.ServiceOperation;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

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
    public Message compose(CamelBindingData source, Exchange exchange) throws Exception {
        Message message = exchange.createMessage();

        // map context properties
        getContextMapper().mapFrom(source, exchange.getContext(message));

        org.apache.camel.Message sourceMessage = source.getMessage();

        // map content
        QName msgType = getMessageType(exchange);
        Object content;
        if (msgType == null) {
            content = sourceMessage.getBody();
        } else if (QNameUtil.isJavaMessageType(msgType)) {
            content = sourceMessage.getBody(QNameUtil.toJavaMessageType(msgType));
        } else {
            content = sourceMessage.getBody();
            if (!(content instanceof String) && !(content instanceof Node)
                    && !(content instanceof InputSource) && !(content instanceof Source)) {
                // named binary content - content is not String nor XML, but has type name other than "java:*"
                content = sourceMessage.getBody(InputStream.class);
            }
        }
        message.setContent(content);

        Set<String> attachements = sourceMessage.getAttachmentNames();
        if (!attachements.isEmpty()) {
            for (Entry<String, DataHandler> entry : sourceMessage.getAttachments().entrySet()) {
                message.addAttachment(entry.getKey(), entry.getValue().getDataSource());
            }
        }

        return message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CamelBindingData decompose(Exchange exchange, CamelBindingData target) throws Exception {
        Message sourceMessage = exchange.getMessage();
        getContextMapper().mapTo(exchange.getContext(), target);

        org.apache.camel.Message targetMessage = target.getMessage();

        if (!sourceMessage.getAttachmentMap().isEmpty()) {
            for (Entry<String, DataSource> entry : sourceMessage.getAttachmentMap().entrySet()) {
                targetMessage.addAttachment(entry.getKey(), new DataHandler(entry.getValue()));
            }
        }

        ServiceOperation operation = exchange.getContract().getProviderOperation();
        target.getMessage().getExchange().setProperty(OPERATION_NAME, operation.getName());
        target.getMessage().getExchange().setProperty(FAULT_TYPE, operation.getFaultType());
        target.getMessage().getExchange().setProperty(SERVICE_NAME, exchange.getProvider().getName());

        targetMessage.setBody(sourceMessage.getContent());
        return target;
    }
}
