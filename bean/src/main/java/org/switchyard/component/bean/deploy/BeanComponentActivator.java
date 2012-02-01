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

import javax.xml.namespace.QName;

import org.switchyard.component.bean.ServiceProxyHandler;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.deploy.BaseActivator;
import org.switchyard.deploy.ServiceHandler;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.metadata.ServiceInterface;

/**
 * The Bean Component Activator.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class BeanComponentActivator extends BaseActivator {

    /**
     * Bean component activator type name.
     */
    public static final String BEAN_TYPE = "bean";
    
    private BeanDeploymentMetaData _beanDeploymentMetaData;

    /**
     * Public constructor.
     */
    public BeanComponentActivator() {
        super(BEAN_TYPE);
    }
    
    
    @Override
    public ServiceHandler activateService(QName serviceName, ComponentModel config) {
        lookupBeanMetaData();
        for (ServiceDescriptor descriptor : _beanDeploymentMetaData.getServiceDescriptors()) {
            if (descriptor.getServiceName().equals(serviceName.getLocalPart())) {
                ServiceProxyHandler handler = descriptor.getHandler();
                for (ComponentReferenceModel reference : config.getReferences()) {
                    handler.addReference(getServiceDomain().getServiceReference(reference.getQName()));
                }
                return handler;
            }
        }
        // bean discovery did not find a bean providing this service
        throw new SwitchYardException("Unknown Service name '" + serviceName + "'.");
    }

    @Override
    public void deactivateService(QName name, ServiceHandler handler) {
        // NOP - CDI subsystem will pull down the CDI bits and pieces
    }

    /**
     * Looks up Bean meta data. 
     */
    public void lookupBeanMetaData() {
        _beanDeploymentMetaData = BeanDeploymentMetaData.lookupBeanDeploymentMetaData();
    }

    /**
     * Create a ServiceInterface instance for the named Service.
     * @param name The Service Name.
     * @return The ServiceInterface instance.
     */
    public ServiceInterface buildServiceInterface(String name) {
        for (ServiceDescriptor descriptor : _beanDeploymentMetaData.getServiceDescriptors()) {
            if (descriptor.getServiceName().equals(name)) {
                return descriptor.getInterface();
            }
        }
        // bean discovery did not find a bean providing this service
        throw new SwitchYardException("Unknown Service name '" + name + "'.");
    }

}
