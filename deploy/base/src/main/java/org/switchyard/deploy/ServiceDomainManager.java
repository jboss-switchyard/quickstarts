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

package org.switchyard.deploy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

import javax.xml.namespace.QName;

import org.switchyard.ExchangeHandler;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.common.type.Classes;
import org.switchyard.config.model.domain.HandlerModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.internal.DefaultServiceRegistry;
import org.switchyard.internal.DomainImpl;
import org.switchyard.internal.LocalExchangeBus;
import org.switchyard.internal.transform.BaseTransformerRegistry;
import org.switchyard.internal.validate.BaseValidatorRegistry;
import org.switchyard.spi.ExchangeBus;
import org.switchyard.spi.ServiceRegistry;

/**
 * {@link org.switchyard.ServiceDomain} manager class.
 * <p/>
 * Currently supports a flat ServiceDomain model with a ServiceDomain per application/deployment,
 * all managed from this container level bean.  Deployments are supplied with a {@link DomainProxy}
 * instance which can first delegate service lookup to the application's own domain, but on lookup
 * failure, can then delegate to this class in order to continue the lookup across all application
 * ServiceDomain's managed by the container.
 * <p/>
 * This model does not yet support the notion of multiple isolated ServiceDomains.  This class will
 * change or go away.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ServiceDomainManager {

    /**
     * Root domain property.
     */
    public static final QName ROOT_DOMAIN = new QName("org.switchyard.domains.root");
    /**
     * Exchange Bus provider class name key.
     */
    public static final String BUS_CLASS_NAME = "busProvider";
    /**
     * Registry class name property.
     */
    public static final String REGISTRY_CLASS_NAME = "registryProvider";

    private List<ServiceDomain> _activeApplicationServiceDomains = Collections.synchronizedList(new ArrayList<ServiceDomain>());

    /**
     * Find the named service across all service being managed by this manager instance.
     * @param serviceName The service name.
     * @param excludeDomain The domain to be excluded from the search.
     * @return The service reference instance, or null if the service was not located.
     */
    public ServiceReference findService(QName serviceName, ServiceDomain excludeDomain) {
        if (excludeDomain instanceof DomainProxy) {
            excludeDomain = ((DomainProxy) excludeDomain).getDomain();
        }

        for (ServiceDomain domain : _activeApplicationServiceDomains) {
            if (domain != excludeDomain) {
                ServiceReference service = domain.getService(serviceName);
                if (service != null) {
                    return service;
                }
            }
        }
        return null;
    }

    /**
     * Create a ServiceDomain instance.
     * <p/>
     * Uses {@link #ROOT_DOMAIN} as the domain name.
     * @return The ServiceDomain instance.
     */
    public static ServiceDomain createDomain() {
        return createDomain(ROOT_DOMAIN, null);
    }

    /**
     * Create a ServiceDomain instance.
     * @param domainName The domain name.
     * @param switchyardConfig The SwitchYard configuration.
     * @return The ServiceDomain instance.
     */
    public static ServiceDomain createDomain(QName domainName, SwitchYardModel switchyardConfig) {
        ServiceRegistry registry = getRegistry(DefaultServiceRegistry.class.getName());
        ExchangeBus endpointProvider = getEndpointProvider(LocalExchangeBus.class.getName());
        BaseTransformerRegistry transformerRegistry = new BaseTransformerRegistry();
        BaseValidatorRegistry validatorRegistry = new BaseValidatorRegistry();
        
        DomainImpl domain = new DomainImpl(domainName, registry, endpointProvider, transformerRegistry, validatorRegistry);
        // add appropriate domain config
        if (switchyardConfig != null) {
            addHandlersToDomain(domain, switchyardConfig);
        }

        return domain;
    }
    
    /**
     * Add a new ServiceDomain for the specified application.
     * @param applicationName The application name.
     * @return The ServiceDomain for the application.
     */
    public ServiceDomain addApplicationServiceDomain(QName applicationName) {
        return addApplicationServiceDomain(applicationName, null);
    }

    /**
     * Add a new ServiceDomain for the specified application.
     * @param applicationName The application name.
     * @param switchyardConfig The SwitchYard configuration.
     * @return The ServiceDomain for the application.
     */
    public ServiceDomain addApplicationServiceDomain(QName applicationName, SwitchYardModel switchyardConfig) {
        ServiceDomain serviceDomain = createDomain(applicationName, switchyardConfig);
        _activeApplicationServiceDomains.add(serviceDomain);
        return new DomainProxy(serviceDomain, this);
    }

    /**
     * Remove the specified application ServiceDomain.
     * @param applicationDomain The ServiceDomain for the application.
     */
    public void removeApplicationServiceDomain(ServiceDomain applicationDomain) {
        if (applicationDomain instanceof DomainProxy) {
            _activeApplicationServiceDomains.remove(((DomainProxy) applicationDomain).getDomain());
        } else {
            _activeApplicationServiceDomains.remove(applicationDomain);
        }
    }

    /**
     * Returns an instance of the ServiceRegistry.
     * @param registryClass class name of the serviceregistry
     * @return ServiceRegistry
     */
    private static ServiceRegistry getRegistry(final String registryClass) {
        ServiceRegistry registry = null;
        ServiceLoader<ServiceRegistry> registryServices = ServiceLoader.load(ServiceRegistry.class);
        
        for (ServiceRegistry serviceRegistry : registryServices) {
            if (registryClass.equals(serviceRegistry.getClass().getName())) {
                registry = serviceRegistry;
                break;
            }
        }
        
        if (registry != null) {
            return registry;
        } else {
            throw new SwitchYardException("Unable to load registry provider: " + registryClass);
        }
    }

    /**
     * Returns an instance of the EndpointProvider.
     * @param providerClass class name of the endpointprovider implementation
     * @return EndpointProvider
     */
    private static ExchangeBus getEndpointProvider(final String providerClass) {
        ServiceLoader<ExchangeBus> providerServices = ServiceLoader.load(ExchangeBus.class);
        ExchangeBus bus = null;
        
        for (ExchangeBus provider : providerServices) {
            if (providerClass.equals(provider.getClass().getName())) {
                return provider;
            }
        }
        
        if (bus != null) {
            return bus;
        } else {
            throw new SwitchYardException("Unable to load exchange bus provider: " + providerClass);
        }
    }
    
    /**
     * Looks for handler definitions in the switchyard config and attempts to 
     * create and add them to the domain's global handler chain.
     */
    private static void addHandlersToDomain(ServiceDomain domain, SwitchYardModel config) {
        if (config.getDomain() != null && config.getDomain().getHandlers() != null) {
            for (HandlerModel handlerConfig : config.getDomain().getHandlers().getHandlers()) {
                Class<?> handlerClass = Classes.forName(handlerConfig.getClassName());
                if (handlerClass == null) {
                    throw new SwitchYardException("Handler class not found " + handlerConfig.getClassName());
                }
                if (!ExchangeHandler.class.isAssignableFrom(handlerClass)) {
                    throw new SwitchYardException("Handler " + handlerConfig.getName() 
                            + " is not an instance of " + ExchangeHandler.class.getName());
                }
                try {
                    ExchangeHandler handler = (ExchangeHandler)handlerClass.newInstance();
                    domain.getHandlerChain().addFirst(handlerConfig.getName(), handler);
                } catch (Exception ex) {
                    throw new SwitchYardException("Failed to initialize handler class " + handlerClass.getName(), ex);
                }
            }
        }
    }
}
