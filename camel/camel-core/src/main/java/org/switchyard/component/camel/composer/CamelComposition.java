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

import org.apache.camel.Message;
import org.switchyard.composer.Composition;
import org.switchyard.composer.MessageComposer;
import org.switchyard.config.model.composer.ContextMapperModel;
import org.switchyard.config.model.composer.MessageComposerModel;
import org.switchyard.config.model.composite.BindingModel;

/**
 * Utility class for Camel-specific Composition.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class CamelComposition {

    /**
     * Uses the {@link Composition} class to create a Camel-specific MessageComposer.
     * @return the MessageComposer
     */
    public static MessageComposer<Message> getMessageComposer() {
        return Composition.getMessageComposer(Message.class);
    }

    /**
     * Uses the {@link Composition} class to create a Camel-specific MessageComposer.
     * @param bm a BidingModel to get configuration details from
     * @return the MessageComposer
     */
    public static MessageComposer<Message> getMessageComposer(BindingModel bm) {
        ContextMapperModel cmm = bm != null ? bm.getContextMapper() : null;
        MessageComposerModel mcm = bm != null ? bm.getMessageComposer() : null;
        return Composition.getMessageComposer(Message.class, cmm, mcm);
    }

    private CamelComposition() {}

}
