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
package org.switchyard.component.common.knowledge.config.model;

import org.switchyard.config.model.NamedModel;

/**
 * A Channel Model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public interface ChannelModel extends NamedModel {

    /**
     * The channel XML element.
     */
    public static final String CHANNEL = "channel";

    /**
     * Gets the Channel class.
     * @param loader the ClassLoader to use
     * @return the Channel class
     */
    public Class<?> getClazz(ClassLoader loader);

    /**
     * Sets the Channel class.
     * @param clazz the Channel class
     * @return this ChannelModel (useful for chaining)
     */
    public ChannelModel setClazz(Class<?> clazz);

    /**
     * Gets the operation attribute.
     * @return the operation attribute
     */
    public String getOperation();

    /**
     * Sets the operation attribute.
     * @param operation the operation attribute
     * @return this ChannelModel (useful for chaining)
     */
    public ChannelModel setOperation(String operation);

    /**
     * Gets the reference attribute.
     * @return the reference attribute
     */
    public String getReference();

    /**
     * Sets the reference attribute.
     * @param reference the reference attribute
     * @return this ChannelModel (useful for chaining)
     */
    public ChannelModel setReference(String reference);

}
