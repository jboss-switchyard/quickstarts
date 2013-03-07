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
package org.switchyard.component.resteasy.composer;

import org.switchyard.component.common.composer.Composition;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.resteasy.config.model.RESTEasyBindingModel;
import org.switchyard.config.model.composer.ContextMapperModel;
import org.switchyard.config.model.composer.MessageComposerModel;

/**
 * Utility class for RESTEasy-specific Composition.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class RESTEasyComposition {

    /**
     * Uses the {@link Composition} class to create a RESTEasy-specific MessageComposer.
     * @return the MessageComposer
     */
    public static MessageComposer<RESTEasyBindingData> getMessageComposer() {
        return Composition.getMessageComposer(RESTEasyBindingData.class);
    }

    /**
     * Uses the {@link Composition} class to create a RESTEasy-specific MessageComposer.
     * @param rebm a RESTEasyBindingModel to get configuration details from
     * @return the MessageComposer
     */
    public static MessageComposer<RESTEasyBindingData> getMessageComposer(RESTEasyBindingModel rebm) {
        ContextMapperModel cmm = rebm != null ? rebm.getContextMapper() : null;
        MessageComposerModel mcm = rebm != null ? rebm.getMessageComposer() : null;
        return Composition.getMessageComposer(RESTEasyBindingData.class, cmm, mcm);
    }

    private RESTEasyComposition() {}

}
