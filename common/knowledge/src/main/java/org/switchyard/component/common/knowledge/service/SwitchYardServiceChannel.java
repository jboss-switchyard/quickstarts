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
package org.switchyard.component.common.knowledge.service;

import javax.xml.namespace.QName;

import org.kie.api.runtime.Channel;

/**
 * SwitchYardServiceChannel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public class SwitchYardServiceChannel implements Channel {

    /** service. */
    public static final String SERVICE = "service";

    private String _name;
    private QName _serviceName;
    private String _operationName;
    private SwitchYardServiceInvoker _invoker;

    /**
     * Constructs a new SwitchYardServiceChannel with the name "service".
     */
    public SwitchYardServiceChannel() {
        setName(SERVICE);
    }

    /**
     * Gets the name.
     * @return the name
     */
    public String getName() {
        return _name;
    }

    /**
     * Sets the name.
     * @param name the name
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * Gets the service name.
     * @return the service name
     */
    public QName getServiceName() {
        return _serviceName;
    }

    /**
     * Sets the service name.
     * @param serviceName the service name
     */
    public void setServiceName(QName serviceName) {
        _serviceName = serviceName;
    }

    /**
     * Gets the operation name.
     * @return the operation name
     */
    public String getOperationName() {
        return _operationName;
    }

    /**
     * Sets the operation name.
     * @param operationName the operation name
     */
    public void setOperationName(String operationName) {
        _operationName = operationName;
    }

    /**
     * Gets the invoker.
     * @return the invoker
     */
    public SwitchYardServiceInvoker getInvoker() {
        return _invoker;
    }

    /**
     * Sets the invoker.
     * @param invoker the invoker
     */
    public void setInvoker(SwitchYardServiceInvoker invoker) {
        _invoker = invoker;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(Object object) {
        SwitchYardServiceRequest request = new SwitchYardServiceRequest(getServiceName(), getOperationName(), object);
        SwitchYardServiceResponse response = getInvoker().invoke(request);
        if (response.hasFault()) {
            response.logFaultMessage();
        }
    }

}
