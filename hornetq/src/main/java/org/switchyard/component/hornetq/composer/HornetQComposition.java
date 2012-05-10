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
package org.switchyard.component.hornetq.composer;

import org.hornetq.api.core.client.ClientMessage;
import org.switchyard.component.common.composer.Composition;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.hornetq.config.model.HornetQBindingModel;
import org.switchyard.config.model.composer.ContextMapperModel;
import org.switchyard.config.model.composer.MessageComposerModel;

/**
 * Utility class for HornetQ-specific Composition.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class HornetQComposition {

    /** The "hornetq_message_property" context property label. */
    public static final String HORNETQ_MESSAGE_PROPERTY = "hornetq_message_property";

    /**
     * Uses the {@link Composition} class to create a HornetQ-specific MessageComposer.
     * @return the MessageComposer
     */
    public static MessageComposer<ClientMessage> getMessageComposer() {
        return Composition.getMessageComposer(ClientMessage.class);
    }

    /**
     * Uses the {@link Composition} class to create a HornetQ-specific MessageComposer.
     * @param hqbm a HornetQBindingModel to get configuration details from
     * @return the MessageComposer
     */
    public static MessageComposer<ClientMessage> getMessageComposer(HornetQBindingModel hqbm) {
        ContextMapperModel cmm = hqbm != null ? hqbm.getContextMapper() : null;
        MessageComposerModel mcm = hqbm != null ? hqbm.getMessageComposer() : null;
        return Composition.getMessageComposer(ClientMessage.class, cmm, mcm);
    }

    private HornetQComposition() {}

}
