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
 
package org.switchyard.component.http.composer;

import org.switchyard.component.common.composer.Composition;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.http.config.model.HttpBindingModel;
import org.switchyard.config.model.composer.ContextMapperModel;
import org.switchyard.config.model.composer.MessageComposerModel;

/**
 * Utility class for HTTP-specific Composition.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public final class HttpComposition {

    /** The "http_header" context property label. */
    public static final String HTTP_HEADER = "http_header";

    /**
     * Uses the {@link Composition} class to create a HTTP-specific MessageComposer.
     * @return the MessageComposer
     */
    public static MessageComposer<HttpBindingData> getMessageComposer() {
        return Composition.getMessageComposer(HttpBindingData.class);
    }

    /**
     * Uses the {@link Composition} class to create a HTTP-specific MessageComposer.
     * @param hbm a HttpBindingModel to get configuration details from
     * @return the MessageComposer
     */
    public static MessageComposer<HttpBindingData> getMessageComposer(HttpBindingModel hbm) {
        ContextMapperModel cmm = hbm != null ? hbm.getContextMapper() : null;
        MessageComposerModel mcm = hbm != null ? hbm.getMessageComposer() : null;
        MessageComposer<HttpBindingData> mc = Composition.getMessageComposer(HttpBindingData.class, cmm, mcm);
        if (mc instanceof HttpMessageComposer && mcm != null) {
            HttpMessageComposer smc = (HttpMessageComposer)mc;
            smc.setComposerConfig(mcm);
        }
        return mc;
    }

    private HttpComposition() {}

}
