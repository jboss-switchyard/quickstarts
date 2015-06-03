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

import java.util.List;

import javax.xml.namespace.QName;

/**
 * Service
 * 
 * Represents an application service exported through the SwitchYard runtime.
 */
public interface Service extends MessageMetricsAware {

    /**
     * @return the name of this service.
     */
    QName getName();

    /**
     * @return the component service promoted by this service.
     */
    ComponentService getPromotedService();

    /**
     * @return the gateway bindings for this service.
     */
    List<Binding> getGateways();

    /**
     * @return the interface implemented by this service.
     */
    String getInterface();

    /**
     * @return the application which exports this service.
     */
    Application getApplication();

    /**
     * @param gatewayName the name of a gateway on this service.
     * @return the named gateway
     */
    Binding getGateway(String gatewayName);

    /**
     * @return throttling details associated with this service.
     */
    Throttling getThrottling();
}
