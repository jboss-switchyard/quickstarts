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

package org.switchyard.config.model.composer;

import org.switchyard.config.model.Model;

/**
 * The "messageComposer" configuration model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface MessageComposerModel extends Model {

    /** messageComposer variable. */
    public static final String MESSAGE_COMPOSER = "messageComposer";

    /**
     * Gets the fully qualified class name of ContextMapper.
     * @return the fully qualified class name of ContextMapper
     */
    public String getClazz();

    /**
     * Sets the fully qualified class name of ContextMapper.
     * @param clazz fully qualified class name of ContextMapper
     * @return this instance (useful for chaining)
     */
    public MessageComposerModel setClazz(String clazz);

}
