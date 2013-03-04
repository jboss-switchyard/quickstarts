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
package org.switchyard.component.resteasy.composer;

import org.apache.log4j.Logger;
import org.switchyard.Exchange;
import org.switchyard.Message;
import org.switchyard.component.common.composer.BaseMessageComposer;

/**
 * Composes/decomposes RESTEasy messages.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public class RESTEasyMessageComposer extends BaseMessageComposer<RESTEasyBindingData> {

    private static final Logger LOGGER = Logger.getLogger(RESTEasyMessageComposer.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public Message compose(RESTEasyBindingData source, Exchange exchange, boolean create) throws Exception {
        final Message message = create ? exchange.createMessage() : exchange.getMessage();
        getContextMapper().mapFrom(source, exchange.getContext());
        Object content = null;
        if (source.getParameters().length > 0) {
            content = source.getParameters()[0];
        }
        message.setContent(content);

        if (source.getParameters().length > 1) {
            LOGGER.warn("Default RESTEasy Message Composer doesn't handle multiple input parameters.");
        }
        return message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RESTEasyBindingData decompose(Exchange exchange, RESTEasyBindingData target) throws Exception {
        Object content = exchange.getMessage().getContent();
        target.setOperationName(exchange.getContract().getProviderOperation().getName());
        if (content != null) {
            target.setParameters(new Object[]{content});
        }
        getContextMapper().mapTo(exchange.getContext(), target);
        return target;
    }

}
