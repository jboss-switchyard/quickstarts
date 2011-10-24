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

import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.Message;
import org.switchyard.Scope;
import org.switchyard.composer.BaseMessageComposer;

/**
 * The Camel implementation of MessageComposer.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class CamelMessageComposer extends BaseMessageComposer<org.apache.camel.Message> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Message compose(org.apache.camel.Message source, Exchange exchange, boolean create) throws Exception {
        getContextMapper().mapFrom(source, exchange.getContext());
        Message message = create ? exchange.createMessage() : exchange.getMessage();
        Class<?> contentType = getContentType(exchange);
        if (contentType != null) {
            message.setContent(source.getBody(contentType));
        } else {
            message.setContent(source.getBody());
        }
        return message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public org.apache.camel.Message decompose(Exchange exchange, org.apache.camel.Message target) throws Exception {
        getContextMapper().mapTo(exchange.getContext(), target);
        Class<?> contentType = getContentType(exchange);
        if (contentType != null) {
            target.setBody(exchange.getMessage().getContent(contentType));
        } else {
            target.setBody(exchange.getMessage().getContent());
        }
        return target;
    }

    /**
     * Gets the content type given the specified exchange.
     * @param exchange the specified exchange
     * @return the content type
     */
    public static final Class<?> getContentType(Exchange exchange) {
        ContentTypeProvider contentTypeProvider = getContentTypeProvider(exchange);
        return contentTypeProvider != null ? contentTypeProvider.getContentType() : null;
    }

    /**
     * Gets the content type provider given the specified exchange.
     * @param exchange the specified exchange
     * @return the context type provider
     */
    public static final ContentTypeProvider getContentTypeProvider(Exchange exchange) {
        return (ContentTypeProvider)exchange.getContext().getPropertyValue(ContentTypeProvider.KEY);
    }

    /**
     * Sets the content type provider on the specified exchange.
     * @param exchange the specified exchange
     * @param contentTypeProvider the specified content type provider
     */
    public static final void setContentTypeProvider(Exchange exchange, ContentTypeProvider contentTypeProvider) {
        Context context = exchange.getContext();
        if (contentTypeProvider != null) {
            context.setProperty(ContentTypeProvider.KEY, contentTypeProvider, Scope.EXCHANGE);
        } else {
            context.setProperty(ContentTypeProvider.KEY, null, Scope.EXCHANGE);
        }
    }

    /**
     * A content type provider is necessary for certain camel code to provide visibility to what the body
     * of a message should be converted to, when there is not any access to the required variables in that
     * part of the code.  It is set in a try block before execution, and unset in a finally block.
     */
    public static interface ContentTypeProvider {

        /**
         * The key in which the content type provider will be stored in the exchange.
         */
        public static final String KEY = ContentTypeProvider.class.getName();

        /**
         * Provides the content type.
         * @return the content type
         */
        public Class<?> getContentType();

    }

}
