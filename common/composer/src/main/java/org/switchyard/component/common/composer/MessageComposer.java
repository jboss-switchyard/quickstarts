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
package org.switchyard.component.common.composer;

import org.switchyard.Exchange;
import org.switchyard.Message;

/**
 * Composes or decomposes SwitchYard Messages (via their Exchange) to and from a source or target object.
 *
 * @param <D> the type of binding data
 * 
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface MessageComposer<D extends BindingData> {

    /**
     * Gets this message composer's associated context mapper.
     * @return the context mapper
     */
    public ContextMapper<D> getContextMapper();

    /**
     * Sets this message composer's associated context mapper.
     * @param contextMapper the context mapper
     * @return this message composer (useful for chaining)
     */
    public MessageComposer<D> setContextMapper(ContextMapper<D> contextMapper);

    /**
     * Takes the data from the passed in source object and composes a SwithYardMessage based on the specified Exchange.
     * @param source the source object
     * @param exchange the exchange to use
     * @param create whether or not we ask the exchange to create a new message, or just get the existing one
     * @return the composed message
     * @throws Exception if a problem happens
     */
    public Message compose(D source, Exchange exchange, boolean create) throws Exception;

    /**
     * Takes the data from the SwitchYardMessage in the specified Exchange and decomposes it into the target object.
     * @param exchange the exchange to use
     * @param target the target object
     * @return the new target object (could be the same or different than the original target)
     * @throws Exception if a problem happens
     */
    public D decompose(Exchange exchange, D target) throws Exception;

}
