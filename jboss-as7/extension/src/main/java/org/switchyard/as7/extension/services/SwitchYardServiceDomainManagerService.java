/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
    
    public InjectedValue<Cache> getCache() {
        return _cache;
    }
}
