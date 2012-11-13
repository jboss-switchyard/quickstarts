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

import org.switchyard.config.model.composite.BindingModel;

/**
 * A binding definition for JCA gateway.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public interface JCABindingModel extends BindingModel {
    
    /**
     * get OutboundConnection model.
     * 
     * @return {@link OutboundConnectionModel}
     */
    OutboundConnectionModel getOutboundConnection();
    
    /**
     * set OutboundConnection model.
     * 
     * @param config {@link OutboundConnectionModel} to set
     * @return {@JCABindingModel} to support method chaining
     */
    JCABindingModel setOutboundConnection(final OutboundConnectionModel config);
    
    /**
     * get InboundConnection model.
     * 
     * @return {@link InboundConnectionModel}
     */
    InboundConnectionModel getInboundConnection();
    
    /**
     * set InboundConnection model.
     * 
     * @param config {@link InboundConnectionModel} to set
     * @return {@link JCABindingModel} to support method chaining
     */
    JCABindingModel setInboundConnection(final InboundConnectionModel config);
    
    /**
     * get OutboundInteraction model.
     * 
     * @return {@link OutboundInteractionModel}
     */
    OutboundInteractionModel getOutboundInteraction();
    
    /**
     * set OutboundInteraction model.
     * 
     * @param config {@link OutboundInteractionModel} to set
     * @return {@link JCABindingModel} to support method chaining
     */
    JCABindingModel setOutboundInteraction(final OutboundInteractionModel config);
    
    /**
     * get InboundInteraction model.
     * 
     * @return {@link InboundInteractionModel}
     */
    InboundInteractionModel getInboundInteraction();
    
    /**
     * set InboundInteraction model.
     * 
     * @param config {@link InboundInteractionModel} to set
     * @return {@link JCABinding} to support method chaining
     */
    JCABindingModel setInboundInteraction(final InboundInteractionModel config);
    
}
