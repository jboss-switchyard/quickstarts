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
package org.switchyard.component.sca.deploy;

import java.util.ServiceLoader;

import org.switchyard.ServiceDomain;
import org.switchyard.component.sca.RemoteEndpointPublisher;
import org.switchyard.deploy.Activator;
import org.switchyard.deploy.BaseComponent;
import org.switchyard.component.sca.SCALogger;

/**
 * The "remote" component used in SwitchYard for providing and consuming clustered endpoints.
 */
public class SCAComponent extends BaseComponent {
    
    private static final String CONTEXT_PATH = "switchyard-remote";
    private RemoteEndpointPublisher _endpointPublisher;

    /**
     * Default constructor.
     */
    public SCAComponent() {
        super(SCAActivator.TYPES);
        setName("RemoteComponent");
        try {
            _endpointPublisher = ServiceLoader.load(RemoteEndpointPublisher.class).iterator().next();
            _endpointPublisher.init(CONTEXT_PATH);
        } catch (Exception ex) {
            SCALogger.ROOT_LOGGER.failedToInitializeRemoteEndpointPublisher(ex);
        }
    }
    
    @Override
    public Activator createActivator(ServiceDomain domain) {
        SCAActivator activator = new SCAActivator(getConfig());
        activator.setServiceDomain(domain);
        activator.setEndpointPublisher(_endpointPublisher);
        return activator;
    }

    @Override
    public synchronized void destroy() {
        try {
            _endpointPublisher.stop();
        } catch (Exception ex) {
            SCALogger.ROOT_LOGGER.failedToDestroyRemoteEndpointPublisher(ex);
        }
    }
}
