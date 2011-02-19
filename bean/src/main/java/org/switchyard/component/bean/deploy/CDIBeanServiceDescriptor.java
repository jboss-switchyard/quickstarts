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

    /**
     * Public constructor.
     * @param bean The CDI bean instance.
     * @param beanManager The CDI BeanManager.
     */
    public CDIBeanServiceDescriptor(Bean bean, BeanManager beanManager) {
        this._bean = bean;
        this._beanManager = beanManager;
        this._serviceName = new QName(getServiceInterface(bean).getSimpleName());
        this._serviceMetadata = new BeanServiceMetadata(getServiceInterface(_bean));
    }

    @Override
    public QName getServiceName() {
        return _serviceName;
    }

    @Override
    public ExchangeHandler getHandler() {
        CreationalContext creationalContext = _beanManager.createCreationalContext(_bean);
        Object beanRef = _beanManager.getReference(_bean, Object.class, creationalContext);

        return new ServiceProxyHandler(beanRef, _serviceMetadata);
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
