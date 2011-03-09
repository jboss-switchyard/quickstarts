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

package org.switchyard.component.bean.deploy;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.switchyard.ExchangeHandler;
import org.switchyard.ServiceReference;
import org.switchyard.component.bean.ClientProxyBean;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.deploy.Activator;
import org.switchyard.metadata.ServiceInterface;

/**
 * The Bean Component {@link Activator}.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class BeanComponentActivator implements Activator {

    private BeanDeploymentMetaData _beanDeploymentMetaData;
    private Map<QName, ComponentReferenceModel> _references = new HashMap<QName, ComponentReferenceModel>();

    /**
     * Public constructor.
     */
    public BeanComponentActivator() {
        _beanDeploymentMetaData = BeanDeploymentMetaData.lookup(Thread.currentThread().getContextClassLoader());
    }


    @Override
    public ExchangeHandler init(QName name, Model config) {
        if (config instanceof ComponentReferenceModel) {
            // policy and configuration validation can be performed here - 
            // nothing to do for now
            _references.put(name, (ComponentReferenceModel)config);
            return null;
        } else if (config instanceof ComponentServiceModel) {
            // lookup the handler for the initialized service
            for (ServiceDescriptor descriptor : _beanDeploymentMetaData.getServiceDescriptors()) {
                if (descriptor.getServiceName().equals(name)) {
                    return descriptor.getHandler();
                }
            }
        }
        // bean discovery did not find a bean providing this service
        throw new RuntimeException("Unknown Service name '" + name + "'.");
    }

    /**
     * Create a ServiceInterface instance for the named Service.
     * @param name The Service Name.
     * @return The ServiceInterface instance.
     */
    public ServiceInterface buildServiceInterface(QName name) {
        for (ServiceDescriptor descriptor : _beanDeploymentMetaData.getServiceDescriptors()) {
            if (descriptor.getServiceName().equals(name)) {
                return descriptor.getInterface();
            }
        }
        // bean discovery did not find a bean providing this service
        throw new RuntimeException("Unknown Service name '" + name + "'.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(ServiceReference service) {
        // Initialise any client proxies to the started service...
        for (ClientProxyBean proxyBean : _beanDeploymentMetaData.getClientProxies()) {
            if (proxyBean.getServiceQName().equals(service.getName())) {
                proxyBean.setService(service);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(ServiceReference service) {
        // not sure this is significant for bean component
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy(ServiceReference service) {
        _references.remove(service.getName());
    }


}
