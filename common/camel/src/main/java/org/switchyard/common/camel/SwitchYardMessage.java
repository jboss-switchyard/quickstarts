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
package org.switchyard.common.camel;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.impl.DefaultMessage;

/**
 * An extension of camel default message implementation which uses traditional
 * case sensitive hash map for storing headers.
 */
public class SwitchYardMessage extends DefaultMessage {

    @Override
    protected Map<String, Object> createHeaders() {
        return new HashMap<String, Object>();
    }

    @Override
    public DefaultMessage newInstance() {
        return new SwitchYardMessage();
    }

}
