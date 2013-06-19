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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.switchyard.ProviderRegistry;
import org.switchyard.common.type.Classes;
import org.switchyard.component.common.CommonCommonLogger;
import org.switchyard.config.model.composer.MessageComposerModel;

/**
 * Utility AND base class making it easy for Component developers to specify their own MessageComposer implementations.
 *
 * @param <D> the type of binding data
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public abstract class MessageComposerFactory<D extends BindingData> {

    /**
     * Component developer should implement this message to specify the type of source/target object.
     * @return the type of source/target object
     */
    public abstract Class<D> getBindingDataClass();

    /**
     * Component developer should implement this message to provide their default/fallback implementation
     * if the MessageComposerInfo passed into {@link MessageComposerFactory#newMessageComposer(MessageComposerInfo)}
     * doesn't specify (or specifies a bad) message composer class to use.
     * @return the default/fallback message composer implementation
     */
    public abstract MessageComposer<D> newMessageComposerDefault();

    /**
     * Will create a new MessageComposer based on the specifications of the passed in MessageComposerInfo, or if
     * a class it not specified, will apply the rest of the info properties on the default/fallback implementation.
     * @param model contains the config details
     * @return the new MessageComposer instance
     */
    @SuppressWarnings("unchecked")
    public final MessageComposer<D> newMessageComposer(MessageComposerModel model) {
        MessageComposer<D> messageComposer = null;
        MessageComposerFactory<D> messageComposerFactory = MessageComposerFactory.getMessageComposerFactory(getBindingDataClass());
        if (model != null) {
            messageComposer = messageComposerFactory.newMessageComposer((Class<MessageComposer<D>>)Classes.forName(model.getClazz()));
        } else {
            messageComposer = messageComposerFactory.newMessageComposerDefault();
        }
        return messageComposer;
    }

    /**
     * Will create a new custom MessageComposer based on the specified class, or if it can't, will use the
     * default/fallback implementation.
     * @param custom the custom MessageComposer class
     * @return the new MessageComposer instance
     */
    public final MessageComposer<D> newMessageComposer(Class<? extends MessageComposer<D>> custom) {
        MessageComposer<D> messageComposer = null;
        if (custom != null) {
            try {
                messageComposer = custom.newInstance();
            } catch (Exception e) {
                CommonCommonLogger.ROOT_LOGGER.couldNotInstantiateMessageComposer(custom.getClass().getName(), e.getMessage());
            }
        }
        if (messageComposer == null) {
            messageComposer = newMessageComposerDefault();
        }
        return messageComposer;
    }

    /**
     * Constructs a new MessageComposerFactory that is known to be able to construct MessageComposers
     * of the specified type.
     * @param <F> the type of binding data
     * @param targetClass the target MessageComposer class
     * @return the new MessageComposerFactory instance
     */
    @SuppressWarnings("unchecked")
    public static final <F extends BindingData> MessageComposerFactory<F> getMessageComposerFactory(Class<F> targetClass) {
        MessageComposerFactory<F> factory = (MessageComposerFactory<F>)getMessageComposerFactories().get(targetClass);
        if (factory == null) {
            throw new IllegalStateException("Unable to find composer factory for " + targetClass.getName());
        }
        return factory;
    }

    /**
     * Gets a map of all known MessageComposerFactories, keyed by their supported source/target object type.
     * @return the MessageComposerFactories map
     */
    @SuppressWarnings("rawtypes")
    public static final Map<Class, MessageComposerFactory> getMessageComposerFactories() {
        Map<Class, MessageComposerFactory> factories = new HashMap<Class, MessageComposerFactory>();
        List<MessageComposerFactory> services = ProviderRegistry.getProviders(MessageComposerFactory.class);
        for (MessageComposerFactory factory : services) {
            factories.put(factory.getBindingDataClass(), factory);
        }
        return factories;
    }

}
