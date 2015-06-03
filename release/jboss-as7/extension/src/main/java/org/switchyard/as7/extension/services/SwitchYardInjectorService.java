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

import java.util.HashMap;
import java.util.Map;
import org.jboss.as.network.SocketBinding;
import org.jboss.logging.Logger;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;
import org.switchyard.common.net.SocketAddr;

/**
 * The SwitchYard Dependencies Injector service.
 * 
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class SwitchYardInjectorService implements Service<Map<String, String>> {

    private static final Logger LOG = Logger.getLogger("org.switchyard");

    /**
     * Represents a SwitchYard Dependencies Injector service name.
     */
    public static final ServiceName SERVICE_NAME = ServiceName.of("SwitchYardInjectorService");

    private final Map<String, String> _injectedValues = new HashMap<String, String>();
    private final Map<String, InjectedValue<SocketBinding>> _socketBindings = new HashMap<String, InjectedValue<SocketBinding>>();

    /**
     * Constructs a SwitchYard Dependencies Injector service.
     */
    public SwitchYardInjectorService() {
    }

    @Override
    public Map<String, String> getValue() throws IllegalStateException,
            IllegalArgumentException {
        return _injectedValues;
    }

    @Override
    public void start(StartContext context) throws StartException {
        for (String key : _socketBindings.keySet()) {
            SocketBinding binding = _socketBindings.get(key).getValue();
            SocketAddr addr = new SocketAddr(binding.getAddress().getHostAddress(), binding.getPort());
            LOG.trace("Injecting socket binding '" + addr + "'");
            _injectedValues.put(key, addr.toString());
        }
    }

    @Override
    public void stop(StopContext context) {

    }

    /**
     * Injection point for SocketBindings.
     * 
     * @param name the name of the SocketBinding
     * @return the SocketBinding
     */
    public InjectedValue<SocketBinding> getSocketBinding(String name) {
        InjectedValue<SocketBinding> binding = _socketBindings.get(name);
        if (binding == null) {
            binding = new InjectedValue<SocketBinding>();
            _socketBindings.put(name, binding);
        }
        return binding;
    }

}
