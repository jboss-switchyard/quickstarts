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

package org.switchyard.admin;

import java.util.Map;
import java.util.Set;

/**
 * Component
 * 
 * Represents a SwitchYard component registered with the SwitchYard runtime
 * (e.g. camel).
 */
public interface Component {
    /**
     * @return the name of this component.
     */
    String getName();

    /**
     * @return supported activation types, e.g. bean, soap, etc.
     */
    Set<String> getTypes();

    /**
     * @return component properties.
     */
    Map<String,String> getProperties();

}
