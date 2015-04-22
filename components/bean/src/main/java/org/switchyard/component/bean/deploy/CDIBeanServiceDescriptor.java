/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.component.bean.deploy;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.BeanManager;

import org.switchyard.common.lang.Strings;
import org.switchyard.component.bean.BeanServiceMetadata;
import org.switchyard.component.bean.BeanMessages;
import org.switchyard.component.bean.Service;
import org.switchyard.component.bean.ServiceProxyHandler;
import org.switchyard.extensions.java.JavaService;
import org.switchyard.metadata.ServiceInterface;

/**
 * SwitchYard CDI bean Service Descriptor.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class CDIBeanServiceDescriptor implements ServiceDescriptor {

    private final CDIBean _cdiBean;
    private final BeanDeploymentMetaData _beanDeploymentMetaData;
    private final String _serviceName;
    private final BeanServiceMetadata _serviceMetadata;

    /**
     * Public constructor.
     * @param cdiBean The CDI bean instance.
     * @param beanDeploymentMetaData bean deployment info
     */
    public CDIBeanServiceDescriptor(CDIBean cdiBean, BeanDeploymentMetaData beanDeploymentMetaData) {
        this._cdiBean = cdiBean;
        this._beanDeploymentMetaData = beanDeploymentMetaData;
        final Class<?> beanClass = cdiBean.getBean().getBeanClass();
        this._serviceName = getServiceName(beanClass);
        this._serviceMetadata = new BeanServiceMetadata(getServiceInterface(beanClass));
    }

    /**
     * Get the CDI bean.
     * @return The CDI bean.
     */
    public CDIBean getCDIBean() {
        return _cdiBean;
    }

    @Override
    public String getServiceName() {
        return _serviceName;
    }

    /**
     * Get the service metadata.
     * @return The service metadata.
     */
    public BeanServiceMetadata getServiceMetadata() {
        return _serviceMetadata;
    }

    @Override
    public ServiceProxyHandler getHandler() {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(_beanDeploymentMetaData.getDeploymentClassLoader());

            BeanManager beanManager = _cdiBean.getBeanManager();
            CreationalContext creationalContext = beanManager.createCreationalContext(_cdiBean.getBean());
            Object beanRef = beanManager.getReference(_cdiBean.getBean(), Object.class, creationalContext);

            return new ServiceProxyHandler(beanRef, _serviceMetadata, _beanDeploymentMetaData);
        } finally {
            Thread.currentThread().setContextClassLoader(tccl);
        }
    }

    @Override
    public ServiceInterface getInterface() {
        return JavaService.fromClass(_serviceMetadata.getServiceClass());
    }

    private String getServiceName(Class<?> beanClass) {
        Service service = beanClass.getAnnotation(Service.class);
        String name = Strings.trimToNull(service.name());
        if (name == null) {
            name = getServiceInterface(beanClass).getSimpleName();
        }
        return name;
    }

    /**
     * Get the service interface defined by a service bean class.
     * @param beanClass The bean class.
     * @return The Service Interface type.
     */
    protected static Class<?> getServiceInterface(Class<?> beanClass) {
        Service serviceAnnotation = beanClass.getAnnotation(Service.class);
        Class<?> serviceInterface = serviceAnnotation.value();

        if (serviceInterface == Service.class) {
            Class<?>[] interfaces = beanClass.getInterfaces();
            if (interfaces.length == 1) {
                serviceInterface = interfaces[0];
            } else {
                throw BeanMessages.MESSAGES.unexpectedExceptionTheServiceAnnotationHasNoValueItCannotBeOmmittedUnlessTheBeanImplementsExactlyOneInterface();
            }
        } else if (!serviceInterface.isInterface()) {
            throw BeanMessages.MESSAGES.invalidServiceSpecificationService(serviceInterface.getName());
        }

        return serviceInterface;
    }
}
