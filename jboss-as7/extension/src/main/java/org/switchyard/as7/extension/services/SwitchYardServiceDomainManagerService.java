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

package org.switchyard.as7.extension.services;

import org.infinispan.Cache;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;
import org.switchyard.deploy.ServiceDomainManager;

/**
 * ServiceDomainManager Service for AS7 deployments.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SwitchYardServiceDomainManagerService implements Service<ServiceDomainManager> {

    /**
     * The name used to resolve the ServiceDomainManager.
     */
    public final static ServiceName SERVICE_NAME = ServiceName.of(SwitchYardServiceDomainManagerService.class.getSimpleName());

    private ServiceDomainManager _domainManager;

    private final InjectedValue<Cache> _cache = new InjectedValue<Cache>();

    @Override
    public void start(StartContext startContext) throws StartException {
        _domainManager = new ServiceDomainManager();
    }

    @Override
    public void stop(StopContext stopContext) {
        //endpoint.stop();    
    }

    @Override
    public ServiceDomainManager getValue() throws IllegalStateException, IllegalArgumentException {
        return _domainManager;
    }

    /**
     * Cache injection point.
     * 
     * @return injected Cache
     */
    public InjectedValue<Cache> getCache() {
        return _cache;
    }
}
