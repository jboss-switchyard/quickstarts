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

import org.switchyard.component.camel.config.model.CamelBindingModel;
import org.switchyard.component.common.composer.Composition;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.config.model.composer.ContextMapperModel;
import org.switchyard.config.model.composer.MessageComposerModel;

/**
 * Utility class for Camel-specific Composition.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class CamelComposition {

    /** The "camel_exchange_property" context property label. */
    public static final String CAMEL_EXCHANGE_PROPERTY = "camel_exchange_property";

    /** The "camel_message_header" context property label. */
    public static final String CAMEL_MESSAGE_HEADER = "camel_message_header";

    /**
     * Uses the {@link Composition} class to create a Camel-specific MessageComposer.
     * @return the MessageComposer
     */
    public static MessageComposer<CamelBindingData> getMessageComposer() {
        return Composition.getMessageComposer(CamelBindingData.class);
    }

    /**
     * Uses the {@link Composition} class to create a Camel-specific MessageComposer.
     * @param cbm a CamelBindingModel to get configuration details from
     * @return the MessageComposer
     */
    public static MessageComposer<CamelBindingData> getMessageComposer(CamelBindingModel cbm) {
        ContextMapperModel cmm = cbm != null ? cbm.getContextMapper() : null;
        MessageComposerModel mcm = cbm != null ? cbm.getMessageComposer() : null;
        return Composition.getMessageComposer(CamelBindingData.class, cmm, mcm);
    }

    private CamelComposition() {}

}
