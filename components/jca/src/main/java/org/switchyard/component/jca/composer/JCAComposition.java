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

    /**
     * Uses the {@link Composition} class to create a JCA-specific MessageComposer.
     * @param bindingDataType {@link Class} for binding data type
     * @param <D> jca binding data type
     * @return the MessageComposer
     */
    public static <D extends JCABindingData> MessageComposer<D> getMessageComposer(Class<D> bindingDataType) {
        return Composition.getMessageComposer(bindingDataType);
    }

    /**
     * Uses the {@link Composition} class to create a JCA-specific MessageComposer.
     * @param jcabm a JCABindingModel to get configuration details from
     * @param bindingDataType {@link Class} for binding data type
     * @param <D> jca binding data type
     * @return the MessageComposer
     */
    public static <D extends JCABindingData> MessageComposer<D> getMessageComposer(JCABindingModel jcabm, Class<D> bindingDataType) {
        ContextMapperModel cmm = jcabm != null ? jcabm.getContextMapper() : null;
        MessageComposerModel mcm = jcabm != null ? jcabm.getMessageComposer() : null;
        return Composition.getMessageComposer(bindingDataType, cmm, mcm);
    }

    private JCAComposition() {}

}
