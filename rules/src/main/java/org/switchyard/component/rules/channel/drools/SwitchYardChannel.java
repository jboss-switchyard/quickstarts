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
package org.switchyard.component.rules.channel.drools;

import org.drools.runtime.Channel;
import org.switchyard.ServiceReference;
import org.switchyard.component.rules.config.model.ChannelModel;

/**
 * A Drools Channel that is configurable via a ChannelModel and ServiceReference.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface SwitchYardChannel extends Channel {

    /**
     * Gets the ChannelModel.
     * @return the ChannelModel
     */
    public ChannelModel getModel();

    /**
     * Sets the ChannelModel.
     * @param model the ChannelModel
     * @return this SwitchYardChannel (useful for chaining)
     */
    public SwitchYardChannel setModel(ChannelModel model);

    /**
     * Gets the ServiceReference.
     * @return the ServiceReference
     */
    public ServiceReference getReference();

    /**
     * Sets the ServiceReference.
     * @param reference the ServiceReference
     * @return this SwitchYardChannel (useful for chaining)
     */
    public SwitchYardChannel setReference(ServiceReference reference);

}
