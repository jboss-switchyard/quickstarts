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
package org.switchyard.composer;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.apache.log4j.Logger;
import org.switchyard.common.composer.MessageComposerInfo;

/**
 * Utility AND base class making it easy for Component developers to specify their own MessageComposer implementations.
 *
 * @param <T> the type of source/target object
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public abstract class MessageComposerFactory<T> {

    private static final Logger LOGGER = Logger.getLogger(MessageComposerFactory.class);

    /**
     * Component developer should implement this message to specify the type of source/target object.
     * @return the type of source/target object
     */
    public abstract Class<T> getTargetClass();

    /**
     * Component developer should implement this message to provide their default/fallback implementation
     * if the MessageComposerInfo passed into {@link MessageComposerFactory#newMessageComposer(MessageComposerInfo)}
     * doesn't specify (or specifies a bad) message composer class to use.
     * @return the default/fallback message composer implementation
     */
    public abstract MessageComposer<T> newMessageComposerDefault();

    /**
     * Will create a new MessageComposer based on the specifications of the passed in MessageComposerInfo, or if
     * a class it not specified, will apply the rest of the info properties on the default/fallback implementation.
     * @param info contains the config details
     * @return the new MessageComposer instance
     */
    @SuppressWarnings("unchecked")
    public final MessageComposer<T> newMessageComposer(MessageComposerInfo info) {
        MessageComposer<T> messageComposer = null;
        MessageComposerFactory<T> messageComposerFactory = MessageComposerFactory.getMessageComposerFactory(getTargetClass());
        if (info != null) {
            messageComposer = messageComposerFactory.newMessageComposer((Class<MessageComposer<T>>)info.getClazz());
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
    public final MessageComposer<T> newMessageComposer(Class<? extends MessageComposer<T>> custom) {
        MessageComposer<T> messageComposer = null;
        if (custom != null) {
            try {
                messageComposer = custom.newInstance();
            } catch (Exception e) {
                LOGGER.error("Could not instantiate MessageComposer: " + custom.getClass().getName() + " - " + e.getMessage());
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
     * @param <F> the type of source/target object
     * @param targetClass the target MessageComposer class
     * @return the new MessageComposerFactory instance
     */
    @SuppressWarnings("unchecked")
    public static final <F> MessageComposerFactory<F> getMessageComposerFactory(Class<F> targetClass) {
        return (MessageComposerFactory<F>)getMessageComposerFactories().get(targetClass);
    }

    /**
     * Gets a map of all known MessageComposerFactories, keyed by their supported source/target object type.
     * @return the MessageComposerFactories map
     */
    @SuppressWarnings("rawtypes")
    public static final Map<Class, MessageComposerFactory> getMessageComposerFactories() {
        Map<Class, MessageComposerFactory> factories = new HashMap<Class, MessageComposerFactory>();
        ServiceLoader<MessageComposerFactory> services = ServiceLoader.load(MessageComposerFactory.class);
        for (MessageComposerFactory factory : services) {
            factories.put(factory.getTargetClass(), factory);
        }
        return factories;
    }

}
