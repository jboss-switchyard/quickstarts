/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
 
package org.switchyard.component.http.composer;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.servlet.http.HttpServletResponse;

import org.switchyard.Exchange;
import org.switchyard.Message;
import org.switchyard.component.common.composer.BaseMessageComposer;
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

        message.setContent(source.getBody());

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
                int status = HttpServletResponse.SC_OK;
                if (content == null) {
                    status = HttpServletResponse.SC_NO_CONTENT;
                } else if (content instanceof HttpResponseBindingData) {
                    status = ((HttpResponseBindingData) content).getStatus();
                } else if ((content instanceof String) || (content instanceof byte[]) 
                            || (content instanceof InputStream) || (content instanceof Reader)) {
                    status = HttpServletResponse.SC_OK;
                } else {
                    status = HttpServletResponse.SC_BAD_GATEWAY;
                }
                HttpResponseBindingData response = (HttpResponseBindingData) target;
                response.setStatus(status);
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
