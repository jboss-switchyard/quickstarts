/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.admin.mbean.internal;

import java.util.EventObject;
import java.util.List;

import org.switchyard.deploy.ServiceDomainManager;
import org.switchyard.event.EventObserver;

/**
 * This is a local, in-memory management interface for a SwitchYard runtime.  The methods on 
 * this management interface are not intended for use by remote JMX clients outside the VM.
 */
public class LocalManagement implements LocalManagementMBean {
    
    private ServiceDomainManager _domainManager;
    
    /**
     * Create a new LocalManagement instance.
     * @param domainManager the SY ServiceDomainManager
     */
    public LocalManagement(ServiceDomainManager domainManager) {
        _domainManager = domainManager;
    }

    @Override
    public void addObserver(EventObserver observer, Class<? extends EventObject> event) {
        _domainManager.getEventManager().addObserver(observer, event);
    }

    @Override
    public void addObserver(EventObserver observer, List<Class<? extends EventObject>> events) {
        if (events != null) {
            for (Class<? extends EventObject> event : events) {
                addObserver(observer, event);
            }
        }
    }

    @Override
    public void removeObserver(EventObserver observer) {
        _domainManager.getEventManager().removeObserver(observer);
    }


}
