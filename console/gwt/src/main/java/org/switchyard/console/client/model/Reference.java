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
package org.switchyard.console.client.model;

import java.util.List;

/**
 * Reference
 * 
 * Represents a SwitchYard reference.
 */
public interface Reference extends HasQName {

    /**
     * @return the interface name
     */
    public String getInterface();

    /**
     * @param interfaceName the interface name
     */
    public void setInterface(String interfaceName);

    /**
     * @return the gateways
     */
    List<Binding> getGateways();

    /**
     * @param gateways the gateways
     */
    public void setGateways(List<Binding> gateways);

    /**
     * @return the application name
     */
    public String getApplication();

    /**
     * @param application the application name
     */
    public void setApplication(String application);

}
