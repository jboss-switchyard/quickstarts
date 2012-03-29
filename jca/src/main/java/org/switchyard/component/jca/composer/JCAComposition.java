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
package org.switchyard.component.jca.composer;

import org.switchyard.component.common.composer.Composition;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.jca.config.model.JCABindingModel;
import org.switchyard.config.model.composer.ContextMapperModel;
import org.switchyard.config.model.composer.MessageComposerModel;

/**
 * Utility class for JCA-specific Composition.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class JCAComposition {

    /** The "jca_message_property" context property label. */
    public static final String JCA_MESSAGE_PROPERTY = "jca_message_property";

    /**
     * Uses the {@link Composition} class to create a JCA-specific MessageComposer.
     * @param type {@link Class} for message type
     * @param <T> message type
     * @return the MessageComposer
     */
    public static <T> MessageComposer<T> getMessageComposer(Class<T> type) {
        return Composition.getMessageComposer(type);
    }

    /**
     * Uses the {@link Composition} class to create a JCA-specific MessageComposer.
     * @param jcabm a JCABindingModel to get configuration details from
     * @param type {@link Class} for message type
     * @param <T> message type
     * @return the MessageComposer
     */
    public static <T> MessageComposer<T> getMessageComposer(JCABindingModel jcabm, Class<T> type) {
        ContextMapperModel cmm = jcabm != null ? jcabm.getContextMapper() : null;
        MessageComposerModel mcm = jcabm != null ? jcabm.getMessageComposer() : null;
        return Composition.getMessageComposer(type, cmm, mcm);
    }

    private JCAComposition() {}

}
