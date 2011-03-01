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

import org.switchyard.ExchangeHandler;
import org.switchyard.component.bean.BeanServiceMetadata;
import org.switchyard.component.bean.Service;
import org.switchyard.component.bean.ServiceProxyHandler;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.metadata.java.JavaService;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class CDIBeanServiceDescriptor implements ServiceDescriptor {

    private QName _serviceName;
    private Bean _bean;
    private BeanServiceMetadata _serviceMetadata;
    private BeanManager _beanManager;
    private BeanDeploymentMetaData _beanDeploymentMetaData;

    /**
     * Public constructor.
     * @param bean The CDI bean instance.
     * @param beanManager The CDI BeanManager.
     * @param beanDeploymentMetaData
     */
    public CDIBeanServiceDescriptor(Bean bean, BeanManager beanManager, BeanDeploymentMetaData beanDeploymentMetaData) {
        this._bean = bean;
        this._beanManager = beanManager;
        this._serviceName = new QName(getServiceInterface(bean).getSimpleName());
        this._serviceMetadata = new BeanServiceMetadata(getServiceInterface(_bean));
        this._beanDeploymentMetaData = beanDeploymentMetaData;
    }

    @Override
    public QName getServiceName() {
        return _serviceName;
    }

    @Override
    public ExchangeHandler getHandler() {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(_beanDeploymentMetaData.getDeploymentClassLoader());

            CreationalContext creationalContext = _beanManager.createCreationalContext(_bean);
            Object beanRef = _beanManager.getReference(_bean, Object.class, creationalContext);

            return new ServiceProxyHandler(beanRef, _serviceMetadata, _beanDeploymentMetaData);
        } finally {
            Thread.currentThread().setContextClassLoader(tccl);
        }
    }

    @Override
    public ServiceInterface getInterface() {
        return JavaService.fromClass(_serviceMetadata.getServiceClass());
    }

    private Class<?> getServiceInterface(Bean bean) {
        Class<?> beanClass = bean.getBeanClass();
        Service serviceAnnotation = beanClass.getAnnotation(Service.class);
        Class<?>[] interfaces = serviceAnnotation.value();

        if (interfaces == null || interfaces.length != 1) {
            // TODO: This might change... perhaps a service interface should be mandatory??
            return beanClass;
        }

        return interfaces[0];
    }
}
