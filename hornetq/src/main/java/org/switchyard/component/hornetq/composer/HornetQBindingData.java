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
package org.switchyard.component.hornetq.composer;

import org.hornetq.api.core.client.ClientMessage;
import org.switchyard.component.common.composer.BindingData;

/**
 * HornetQ binding data.
 * 
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class HornetQBindingData implements BindingData {

    private final ClientMessage _clientMessage;

    /**
     * Constructs a new HornetQ binding data with the specified client message.
     * @param clientMessage the specified client message
     */
    public HornetQBindingData(ClientMessage clientMessage) {
        _clientMessage = clientMessage;
    }

    /**
     * Gets the client message.
     * @return the client message
     */
    public ClientMessage getClientMessage() {
        return _clientMessage;
    }

}
