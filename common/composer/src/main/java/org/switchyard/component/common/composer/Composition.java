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
package org.switchyard.component.common.composer;

import org.switchyard.config.model.composer.ContextMapperModel;
import org.switchyard.config.model.composer.MessageComposerModel;
import org.switchyard.config.model.composer.v1.V1ContextMapperModel;

/**
 * Utility class for Component-specific Composition classes.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class Composition {

    /**
     * Gets a ContextMapper instance based on the supported target class.
     * @param <T> the type of target class
     * @param targetClass the target class
     * @return the ContextMapper instance
     */
    public static final <T> ContextMapper<T> getContextMapper(Class<T> targetClass) {
        return ContextMapperFactory.getContextMapperFactory(targetClass).newContextMapperDefault();
    }

    /**
     * Gets a ContextMapper instance based on the supported target class.
     * @param <T> the type of target class
     * @param targetClass the target class
     * @param contextMapperModel specific context mapper details
     * @return the ContextMapper instance
     */
    public static final <T> ContextMapper<T> getContextMapper(Class<T> targetClass, ContextMapperModel contextMapperModel) {
        if (contextMapperModel == null) {
            // If you don't specify a ContexMapperInfo, your ContextMapper will not match anything!
            contextMapperModel = new V1ContextMapperModel().setExcludes(".*");
        }
        return ContextMapperFactory.getContextMapperFactory(targetClass).newContextMapper(contextMapperModel);
    }

    /**
     * Gets a MessageComposer instance based on the supported target class.
     * @param <T> the type of target class
     * @param targetClass the target class
     * @return the MessageComposer instance
     */
    public static final <T> MessageComposer<T> getMessageComposer(Class<T> targetClass) {
        MessageComposer<T> messageComposer = MessageComposerFactory.getMessageComposerFactory(targetClass).newMessageComposerDefault();
        messageComposer.setContextMapper(getContextMapper(targetClass));
        return messageComposer;
    }

    /**
     * Gets a MessageComposer instance based on the supported target class.
     * @param <T> the type of target class
     * @param targetClass the target class
     * @param contextMapperModel specific context mapper details
     * @param messageComposerModel message composer details
     * @return the MessageComposer instance
     */
    public static final <T> MessageComposer<T> getMessageComposer(Class<T> targetClass, ContextMapperModel contextMapperModel, MessageComposerModel messageComposerModel) {
        MessageComposer<T> messageComposer = MessageComposerFactory.getMessageComposerFactory(targetClass).newMessageComposer(messageComposerModel);
        messageComposer.setContextMapper(getContextMapper(targetClass, contextMapperModel));
        return messageComposer;
    }

    private Composition() {}

}
