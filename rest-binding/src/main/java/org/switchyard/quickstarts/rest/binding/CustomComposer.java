/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.quickstarts.rest.binding;

import org.switchyard.Exchange;
import org.switchyard.Message;

import org.switchyard.component.resteasy.composer.RESTEasyBindingData;
import org.switchyard.component.resteasy.composer.RESTEasyMessageComposer;

/**
 * Composes/decomposes multiple parameter RESTEasy messages.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public class CustomComposer extends RESTEasyMessageComposer {

    /**
     * {@inheritDoc}
     */
    @Override
    public Message compose(RESTEasyBindingData source, Exchange exchange, boolean create) throws Exception {
        final Message message = super.compose(source, exchange, create);
        if (source.getOperationName().equals("addItem") && (source.getParameters().length == 2)) {
            // Wrap the parameters
            Item item = new Item((Integer)source.getParameters()[0], (String)source.getParameters()[1]);
            message.setContent(item);
        }
        return message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RESTEasyBindingData decompose(Exchange exchange, RESTEasyBindingData target) throws Exception {
        target = super.decompose(exchange, target);
        Object content = exchange.getMessage().getContent();
        if (target.getOperationName().equals("addItem") && (content instanceof Item)) {
            // Unwrap the parameters
            target.setParameters(new Object[]{((Item)content).getItemId(), ((Item)content).getName()});
        }
        return target;
    }

}
