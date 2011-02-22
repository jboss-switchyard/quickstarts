/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.switchyard.deploy.internal;

import java.util.ServiceLoader;

import javax.xml.namespace.QName;

import org.switchyard.ServiceDomain;
import org.switchyard.internal.DefaultEndpointProvider;
import org.switchyard.internal.DefaultServiceRegistry;
import org.switchyard.internal.DomainImpl;
import org.switchyard.internal.transform.BaseTransformerRegistry;
import org.switchyard.spi.EndpointProvider;
import org.switchyard.spi.ServiceRegistry;

/**
 * Abstract SwitchYard application deployment.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public abstract class AbstractDeployment {
    /**
     * Root domain property.
     */
    public static final QName ROOT_DOMAIN = new QName("org.switchyard.domains.root");
    /**
     * Endpoint provider class name key.
     */
    public static final String ENDPOINT_PROVIDER_CLASS_NAME
        = "org.switchyard.endpoint.provider.class.name";
    /**
     * Registry class name property.
     */
    public static final String REGISTRY_CLASS_NAME
        = "org.switchyard.registry.class.name";

    private ServiceDomain _serviceDomain;

    /**
     * Initialise the deployment.
     */
    public void init() {
        createDomain();
    }

    /**
     * Destroy the deployment.
     */
    public abstract void destroy();

    /**
     * Get the {@link ServiceDomain} associated with the deployment.
     * @return The domain instance.
     */
    public ServiceDomain getDomain() {
        return _serviceDomain;
    }

    private void createDomain() {
        String registryClassName = System.getProperty(REGISTRY_CLASS_NAME, DefaultServiceRegistry.class.getName());
        String endpointProviderClassName = System.getProperty(ENDPOINT_PROVIDER_CLASS_NAME, DefaultEndpointProvider.class.getName());

        try {
            ServiceRegistry registry = getRegistry(registryClassName);
            EndpointProvider endpointProvider = getEndpointProvider(endpointProviderClassName);
            BaseTransformerRegistry transformerRegistry = new BaseTransformerRegistry();

            _serviceDomain = new DomainImpl(ROOT_DOMAIN, registry, endpointProvider, transformerRegistry);
        } catch (NullPointerException npe) {
            throw new RuntimeException(npe);
        }

    }

    /**
     * Returns an instance of the ServiceRegistry.
     * @param registryClass class name of the serviceregistry
     * @return ServiceRegistry
     */
    private static ServiceRegistry getRegistry(final String registryClass) {
        ServiceLoader<ServiceRegistry> registryServices
                = ServiceLoader.load(ServiceRegistry.class);
        for (ServiceRegistry serviceRegistry : registryServices) {
            if (registryClass.equals(serviceRegistry.getClass().getName())) {
                return serviceRegistry;
            }
        }
        return null;
    }

    /**
     * Returns an instance of the EndpointProvider.
     * @param providerClass class name of the endpointprovider implementation
     * @return EndpointProvider
     */
    private static EndpointProvider
    getEndpointProvider(final String providerClass) {
        ServiceLoader<EndpointProvider> providerServices
                = ServiceLoader.load(EndpointProvider.class);
        for (EndpointProvider provider : providerServices) {
            if (providerClass.equals(provider.getClass().getName())) {
                return provider;
            }
        }
        return null;
    }
}
