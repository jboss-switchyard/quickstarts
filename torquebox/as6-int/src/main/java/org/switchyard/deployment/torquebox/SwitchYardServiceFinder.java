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

package org.switchyard.deployment.torquebox;

import org.switchyard.ServiceReference;
import org.switchyard.deploy.ServiceDomainManager;

import javax.xml.namespace.QName;

/**
 * Service Finder.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SwitchYardServiceFinder {

    private ServiceDomainManager _domainManager;

    /**
     * Set the {@link ServiceDomainManager}.
     * @param domainManager The domain manager.
     */
    public void setDomainManager(ServiceDomainManager domainManager) {
        this._domainManager = domainManager;
    }

    /**
     * Get a ServiceInvoker for the specified service name.
     * @param serviceName The service name.
     * @return The Service Invoker instance.
     */
    public ServiceInvoker getServiceInvoker(String serviceName) {
        if (serviceName == null) {
            throw new IllegalArgumentException("null 'serviceName' arg in method call.");
        }

        QName serviceQName = QName.valueOf(serviceName);
        ServiceReference serviceReference = _domainManager.findService(serviceQName, null);

        if (serviceReference == null) {
            throw new IllegalArgumentException("Unable to find service '" + serviceName + "'.");
        }

        return new ServiceInvoker(serviceReference);
    }

    /**
     * Create a finder bean name for the specified application name.
     * @param applicationName The application name.
     * @return The finder bean name.
     */
    public static String beanName(String applicationName) {
        return "switchyard." + SwitchYardServiceFinder.class.getSimpleName() + "." + applicationName;
    }
}
