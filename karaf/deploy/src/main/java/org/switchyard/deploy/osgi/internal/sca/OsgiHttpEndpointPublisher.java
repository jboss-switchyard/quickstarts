/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.deploy.osgi.internal.sca;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;

import org.osgi.service.http.HttpService;
import org.switchyard.ServiceDomain;
import org.switchyard.component.sca.RemoteEndpointPublisher;
import org.switchyard.component.sca.SwitchYardRemotingServlet;

/**
 * Instance of SCA RemoteEndpointPublisher which integrates SwitchYardRemotingServlet
 * with the OSGi HTTP Service.
 */
public class OsgiHttpEndpointPublisher implements RemoteEndpointPublisher {

    private HttpService _httpService;
    private boolean _started;
    private String _contextName;
    private Map<QName, ServiceDomain> _services = new ConcurrentHashMap<QName, ServiceDomain>();
    
    /**
     * Used by blueprint to set the HttpService reference.
     * @param httpService
     */
    public void setHttpService(HttpService httpService) {
        _httpService = httpService;
    }
    
    @Override
    public void init(String context) {
        _contextName = context;
    }

    @Override
    public synchronized void start() throws Exception {
        // If the remote listener is already started, just return.
        if (_started) {
            return;
        }
        
        SwitchYardRemotingServlet servlet = new SwitchYardRemotingServlet();
        servlet.setEndpointPublisher(this);
        _httpService.registerServlet("/" + _contextName, servlet, null, null);
        _started = true;
    }

    @Override
    public synchronized void stop() throws Exception {
        _started = false;
    }

    @Override
    public void addService(QName serviceName, ServiceDomain domain) {
        _services.put(serviceName, domain);
    }

    @Override
    public void removeService(QName serviceName, ServiceDomain domain) {
        _services.remove(serviceName);
    }
    
    @Override
    public ServiceDomain getDomain(QName serviceName) {
        return _services.get(serviceName);
    }

    @Override
    public String getAddress() {
        // TODO Auto-generated method stub
        return null;
    }

}
