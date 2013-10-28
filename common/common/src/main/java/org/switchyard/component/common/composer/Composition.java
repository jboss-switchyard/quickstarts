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
package org.switchyard.component.common.composer;

import org.switchyard.config.model.composer.ContextMapperModel;
import org.switchyard.config.model.composer.MessageComposerModel;
import org.switchyard.config.model.composer.v1.V1ContextMapperModel;
import org.switchyard.config.model.switchyard.SwitchYardNamespace;

/**
 * Utility class for Component-specific Composition classes.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class Composition {

    /**
     * Gets a ContextMapper instance based on the supported binding data type.
     * @param <D> the type of binding data
     * @param bindingDataType the binding data type
     * @return the ContextMapper instance
     */
    public static final <D extends BindingData> ContextMapper<D> getContextMapper(Class<D> bindingDataType) {
        return ContextMapperFactory.getContextMapperFactory(bindingDataType).newContextMapperDefault();
    }

    /**
     * Gets a ContextMapper instance based on the supported binding data type.
     * @param <D> the type of binding data
     * @param bindingDataType the binding data type
     * @param contextMapperModel specific context mapper details
     * @return the ContextMapper instance
     */
    public static final <D extends BindingData> ContextMapper<D> getContextMapper(Class<D> bindingDataType, ContextMapperModel contextMapperModel) {
        if (contextMapperModel == null) {
            // If you don't specify a ContexMapperInfo, your ContextMapper will not match anything!
            contextMapperModel = new V1ContextMapperModel(SwitchYardNamespace.DEFAULT.uri()).setExcludes(".*");
        }
        return ContextMapperFactory.getContextMapperFactory(bindingDataType).newContextMapper(contextMapperModel);
    }

    /**
     * Gets a MessageComposer instance based on the supported binding data type.
     * @param <D> the type of binding data
     * @param bindingDataType the binding data type
     * @return the MessageComposer instance
     */
    public static final <D extends BindingData> MessageComposer<D> getMessageComposer(Class<D> bindingDataType) {
        MessageComposer<D> messageComposer = MessageComposerFactory.getMessageComposerFactory(bindingDataType).newMessageComposerDefault();
        messageComposer.setContextMapper(getContextMapper(bindingDataType));
        return messageComposer;
    }

    /**
     * Gets a MessageComposer instance based on the supported binding data type.
     * @param <D> the type of binding data
     * @param bindingDataType the binding data type
     * @param contextMapperModel specific context mapper details
     * @param messageComposerModel message composer details
     * @return the MessageComposer instance
     */
    public static final <D extends BindingData> MessageComposer<D> getMessageComposer(Class<D> bindingDataType, ContextMapperModel contextMapperModel, MessageComposerModel messageComposerModel) {
        MessageComposer<D> messageComposer = MessageComposerFactory.getMessageComposerFactory(bindingDataType).newMessageComposer(messageComposerModel);
        messageComposer.setContextMapper(getContextMapper(bindingDataType, contextMapperModel));
        return messageComposer;
    }

    private Composition() {}

}
