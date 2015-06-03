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
package org.switchyard.admin.mbean;

/**
 * Management interface for a gateway binding defined on a service or reference.
 */
public interface BindingMXBean extends LifecycleMXBean, MessageMetricsMXBean {
    
    /**
     * The binding type.
     * @return the type of binding (e.g. soap)
     */
    public String getType();

    /**
     * The binding's name.
     * @return binding name.
     */
    public String getName();
    
    
    /**
     * The config descriptor for the binding as a string.
     * @return the raw configuration details
     */
    public String getConfiguration();

}
