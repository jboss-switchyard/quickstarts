/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.jca.config.model;

import org.switchyard.config.model.Model;

/**
 * binding.jca/inboundInteraction model.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public interface InboundInteractionModel extends Model {

    /**
     * get Listener model.
     * 
     * @return {@link ListenerModel}
     */
    ListenerModel getListener();
    
    /**
     * set Listener model.
     * 
     * @param listener {@link ListenerModel} to set
     * @return {@link InboundInteractionModel} to support method chaining
     */
    InboundInteractionModel setListener(final ListenerModel listener);
    
    /**
     * get Endpoint model.
     * 
     * @return {@link EndpointModel}
     */
    EndpointModel getEndpoint();
    
    /**
     * set Endpoint model.
     * 
     * @param endpoint {@link EndpointModel} to set
     * @return {@link InboundInteractionModel} to support method chaining
     */
    InboundInteractionModel setEndpoint(final EndpointModel endpoint);

    /**
     * get transacted.
     * 
     * @return true if transacted
     */
    boolean isTransacted();
    
    /**
     * set transacted.
     * 
     * @param transacted true if transacted
     * @return {@link InboundInteractionModel} to suport method chaining
     */
    InboundInteractionModel setTransacted(boolean transacted);
}
