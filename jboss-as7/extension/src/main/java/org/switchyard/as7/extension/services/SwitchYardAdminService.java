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

import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.switchyard.admin.SwitchYard;
import org.switchyard.admin.base.BaseSwitchYard;

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
    
    private final String _version;
    private SwitchYard _switchYard;

    /**
     * Create a new SwitchYardAdminService.
     * 
     * @param version the version of the SwitchYard runtime.
     */
    public SwitchYardAdminService(String version) {
        _version = version;
    }

    @Override
    public SwitchYard getValue() throws IllegalStateException, IllegalArgumentException {
        return _switchYard;
    }

    @Override
    public void start(StartContext context) throws StartException {
        _switchYard = new BaseSwitchYard(_version);
    }

    @Override
    public void stop(StopContext context) {
        _switchYard = null;
    }

}
