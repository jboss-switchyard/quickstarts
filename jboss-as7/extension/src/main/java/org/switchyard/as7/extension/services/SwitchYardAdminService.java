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

import java.util.Map;

import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;
import org.switchyard.admin.SwitchYard;
import org.switchyard.admin.base.BaseSwitchYard;
import org.switchyard.admin.base.SwitchYardBuilder;
import org.switchyard.deploy.ServiceDomainManager;
import org.switchyard.deploy.event.ApplicationDeployedEvent;
import org.switchyard.deploy.event.ApplicationUndeployedEvent;
import org.switchyard.runtime.event.ExchangeCompletionEvent;

/**
 * SwitchYardAdminService
 * 
 * Provides a {@link SwitchYard} instance as an AS7 {@link Service}.
 * 
 * @author Rob Cernich
 */
public class SwitchYardAdminService implements Service<SwitchYard> {

    /**
     * The name used to resolve the SwitchYard administration service.
     */
    public final static ServiceName SERVICE_NAME = ServiceName.of("SwitchYardAdminService");

    @SuppressWarnings("rawtypes")
    private final InjectedValue<Map> _socketBindings = new InjectedValue<Map>();
    private final InjectedValue<ServiceDomainManager> _serviceDomainManager = new InjectedValue<ServiceDomainManager>();
    private SwitchYard _switchYard;
    private SwitchYardBuilder _syBuilder;

    /**
     * Create a new SwitchYardAdminService.
     */
    public SwitchYardAdminService() {}

    @Override
    public SwitchYard getValue() throws IllegalStateException, IllegalArgumentException {
        return _switchYard;
    }

    @Override
    public void start(StartContext context) throws StartException {
        _syBuilder = new SwitchYardBuilder();
        _syBuilder.init(_serviceDomainManager.getValue());
        _switchYard = _syBuilder.getSwitchYard();

        // add in the configured socket bindings
        ((BaseSwitchYard)_switchYard).addSocketBindingNames(_socketBindings.getValue().keySet());

        // TODO: add in configured properties
        // _switchYard.addProperties(properties);
    }

    @Override
    public void stop(StopContext context) {
        _syBuilder.destroy();
        _switchYard = null;
    }

    /**
     * Injection point for SwitchYard socket bindings.
     * 
     * @return injected socket bindings map.
     * @see SwitchYardInjectorService
     */
    @SuppressWarnings("rawtypes")
    public final InjectedValue<Map> getSocketBindings() {
        return _socketBindings;
    }
    
    /**
     * Injection point for ServiceDomainManager.
     * 
     * @return the ServiceDomainManager
     */
    public InjectedValue<ServiceDomainManager> getServiceDomainManager() {
        return _serviceDomainManager;
    }
}
