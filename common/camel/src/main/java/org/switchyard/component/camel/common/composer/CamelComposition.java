/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.component.camel.common.composer;

import org.switchyard.component.camel.common.model.CamelBindingModel;
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
