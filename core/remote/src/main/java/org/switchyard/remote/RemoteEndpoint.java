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
package org.switchyard.remote;

import javax.xml.namespace.QName;

import org.switchyard.Service;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.serial.graph.AccessType;
import org.switchyard.serial.graph.Strategy;

/**
 * Represents a service endpoint registered in a distributed registry.
 */
@Strategy(access=AccessType.FIELD)
public class RemoteEndpoint {

    private QName _serviceName;
    private QName _domainName;
    private String _endpoint;
    private String _node;
    private ServiceInterface _contract;
    
    /**
     * Creates a new, empty RemoteEndpoint.
     */
    public RemoteEndpoint() {
        
    }
    
    /**
     * Creates a new RemoteEndpoint with the specified attributes.
     * @param serviceName the service name
     * @param domainName name of the domain where the service is regsitered
     * @param endpoint endpoint remote access URL
     * @param node the cluster node name where the service resides
     * @param contract the contract for the service
     */
    public RemoteEndpoint(QName serviceName, 
            QName domainName, 
            String endpoint, 
            String node,
            ServiceInterface contract) {
        
        _serviceName = serviceName;
        _domainName = domainName;
        _endpoint = endpoint;
        _node = node;
        _contract = contract;
    }
    
    /**
     * Returns the name of the service.
     * @return service name
     */
    public QName getServiceName() {
        return _serviceName;
    }
    
    /**
     * Specifies the service name for the endpoint.
     * @param serviceName service name
     * @return reference to this RemoteEndpoint
     */
    public RemoteEndpoint setServiceName(QName serviceName) {
        _serviceName = serviceName;
        return this;
    }
    
    /**
     * Returns the domain name.
     * @return domain name.
     */
    public QName getDomainName() {
        return _domainName;
    }
    
    /**
     * Specifies the domain name.
     * @param domainName domain name
     * @return reference to this RemoteEndpoint
     */
    public RemoteEndpoint setDomainName(QName domainName) {
        _domainName = domainName;
        return this;
    }
    
    /**
     * Returns the endpoint address.
     * @return endpoint address
     */
    public String getEndpoint() {
        return _endpoint;
    }
    
    /**
     * Specifies the endpoint address.
     * @param endpoint endpoint address
     * @return reference to this RemoteEndpoint
     */
    public RemoteEndpoint setEndpoint(String endpoint) {
        _endpoint = endpoint;
        return this;
    }
    
    /**
     * Returns the service contract.
     * @return service contract
     */
    public ServiceInterface getContract() {
        return _contract;
    }
    
    /**
     * Specifies the service contract.
     * @param contract service contract
     * @return reference to this RemoteEndpoint
     */
    public RemoteEndpoint setContract(ServiceInterface contract) {
        _contract = contract;
        return this;
    }
    
    /**
     * Returns the node name.
     * @return node name.
     */
    public String getNode() {
        return _node;
    }
    
    /**
     * Specifies the domain name.
     * @param node domain name
     * @return reference to this RemoteEndpoint
     */
    public RemoteEndpoint setNode(String node) {
        _node = node;
        return this;
    }
    
    /**
     * Create a RemoteEndpoint representation from a registered service.
     * @param service registered service
     * @return reference to this RemoteEndpoint
     */
    public static RemoteEndpoint fromService(Service service) {
        return new RemoteEndpoint(service.getName(), 
                service.getDomain().getName(), 
                null, 
                null,
                RemoteInterface.fromInterface(service.getInterface()));
    }
}
