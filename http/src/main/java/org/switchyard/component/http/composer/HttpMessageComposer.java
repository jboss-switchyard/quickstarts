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
 
package org.switchyard.component.http.composer;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import org.switchyard.Exchange;
import org.switchyard.ExchangeState;
import org.switchyard.Message;
import org.switchyard.Property;
import org.switchyard.common.xml.QNameUtil;
import org.switchyard.component.common.composer.BaseMessageComposer;
import org.switchyard.component.common.label.EndpointLabel;
import org.switchyard.config.model.composer.MessageComposerModel;

/**
 * The HTTP implementation of MessageComposer simply copies the HTTP body into
 * the Message and HTTP headers into the Message's context, and vice-versa.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class HttpMessageComposer extends BaseMessageComposer<HttpBindingData> {

    private MessageComposerModel _config;

    /**
     * {@inheritDoc}
     */
    @Override
    public Message compose(HttpBindingData source, Exchange exchange) throws Exception {
        final Message message = exchange.createMessage();

        getContextMapper().mapFrom(source, exchange.getContext(message));

        QName msgType = getMessageType(exchange);
        message.setContent(source.getBody());
        Object content;
        if ((msgType != null) && (QNameUtil.isJavaMessageType(msgType))) {
            // Hack - the getContent() call triggers a conversion from native content type and the result is set as the new body
            message.setContent(message.getContent(QNameUtil.toJavaMessageType(msgType)));
        }

        return message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpBindingData decompose(Exchange exchange, HttpBindingData target) throws Exception {
        final Message message = exchange.getMessage();
        if (message != null) {
            Object content = message.getContent();
            if (target instanceof HttpResponseBindingData) {
                Property responseCode = exchange.getContext().getProperty(HttpContextMapper.HTTP_RESPONSE_STATUS);
                if (!((responseCode != null) && responseCode.hasLabel(EndpointLabel.HTTP.label()))) {
                    int status = HttpServletResponse.SC_ACCEPTED;
                    if (content == null) {
                        status = HttpServletResponse.SC_NO_CONTENT;
                    } else if (content instanceof HttpResponseBindingData) {
                        status = ((HttpResponseBindingData) content).getStatus();
                    } else if ((content instanceof String) || (content instanceof byte[]) 
                                || (content instanceof InputStream) || (content instanceof Reader)) {
                        status = HttpServletResponse.SC_OK;
                    } else if (exchange.getState() == ExchangeState.FAULT) {
                        status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                    } else {
                        status = HttpServletResponse.SC_BAD_GATEWAY;
                    }
                    HttpResponseBindingData response = (HttpResponseBindingData) target;
                    response.setStatus(status);
                }
            }
            setContent(content, target);
        }

        getContextMapper().mapTo(exchange.getContext(), target);

        return target;
    }

    private void setContent(final Object content, HttpBindingData message) throws IOException {
        if (content == null) {
            message.setBodyBytes(null);
        } else if (content instanceof String) {
            message.setBody((String) content);
        } else if (content instanceof byte[]) {
            message.setBodyBytes((byte[]) content);
        } else if (content instanceof InputStream) {
            message.setBodyFromStream((InputStream) content);
        } else if (content instanceof Reader) {
            message.setBodyFromReader((Reader) content);
        } else {
            message.setBody("" + content);
        }
    }

    /**
     * Gets the MessageComposerModel config.
     * @return the MessageComposerModel
     */
    public MessageComposerModel getComposerConfig() {
        return _config;
    }

    /**
     * Sets the MessageComposerModel config.
     * @param composerConfig configuration
     */
    public void setComposerConfig(MessageComposerModel composerConfig) {
        _config = composerConfig;
    }

}
