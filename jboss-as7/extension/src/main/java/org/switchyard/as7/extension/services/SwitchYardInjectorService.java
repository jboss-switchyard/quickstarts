/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
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
