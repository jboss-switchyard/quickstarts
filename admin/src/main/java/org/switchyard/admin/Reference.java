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
 * Reference
 * 
 * Represents an application dependency exported through the SwitchYard runtime.
 */
public interface Reference extends MessageMetricsAware {

    /**
     * @return the name of this reference.
     */
    QName getName();

    /**
     * @return the component reference promoted by this service.
     */
    String getPromotedReference();

    /**
     * @return the gateway bindings for this reference.
     */
    List<Binding> getGateways();

    /**
     * @return the interface used by this reference.
     */
    String getInterface();

    /**
     * @return the operations exposed by this service.
     */
    List<ServiceOperation> getServiceOperations();

    /**
     * Gets service operation by name.
     * @param operation the name of a operation provided by this service.
     * @return the requested operation, may be null
     */
    ServiceOperation getServiceOperation(String operation);

    /**
     * @return the application which exports this reference.
     */
    Application getApplication();

    /**
     * @param gatewayName the name of a gateway on this service.
     * @return the named gateway
     */
    Binding getGateway(String gatewayName);
}
