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

import org.apache.camel.Message;
import org.switchyard.component.common.composer.BindingData;

/**
 * Camel binding data.
 * 
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class CamelBindingData implements BindingData {

    private final Message _message;

    /**
     * Constructs a new Camel binding data with the specified message.
     * @param message the specified message
     */
    public CamelBindingData(Message message) {
        _message = message;
    }

    /**
     * Gets the message.
     * @return the message
     */
    public Message getMessage() {
        return _message;
    }

}
