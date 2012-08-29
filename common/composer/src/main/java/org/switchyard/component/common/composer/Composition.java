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
            contextMapperModel = new V1ContextMapperModel().setExcludes(".*");
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
