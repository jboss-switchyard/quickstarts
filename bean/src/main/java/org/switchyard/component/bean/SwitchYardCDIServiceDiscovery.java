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

package org.switchyard.component.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ProcessBean;

import org.jboss.logging.Logger;
import org.switchyard.common.type.Classes;
import org.switchyard.component.bean.deploy.BeanDeploymentMetaData;
import org.switchyard.component.bean.deploy.BeanDeploymentMetaDataCDIBean;
import org.switchyard.component.bean.deploy.CDIBean;
import org.switchyard.component.bean.deploy.CDIBeanServiceDescriptor;
import org.switchyard.component.bean.internal.ReferenceInvokerBean;
import org.switchyard.component.bean.internal.context.ContextBean;
import org.switchyard.component.bean.internal.message.MessageBean;

/**
 * Portable CDI extension for SwitchYard.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@ApplicationScoped
public class SwitchYardCDIServiceDiscovery implements Extension {

    /**
     * Logger
     */
    private static Logger _logger = Logger.getLogger(SwitchYardCDIServiceDiscovery.class);
    /**
     * Bean deployment metadata.
     */
    private BeanDeploymentMetaData _beanDeploymentMetaData;
    /**
     * List of created {@link ClientProxyBean} instances.
     */
    private List<ClientProxyBean> _createdProxyBeans = new ArrayList<ClientProxyBean>();
    
    /**
     * List of created ReferenceInvokerBean instances.
     */
    private List<ReferenceInvokerBean> _createdInvokerBeans = new ArrayList<ReferenceInvokerBean>();

    /**
     * {@link javax.enterprise.inject.spi.BeforeBeanDiscovery} CDI event observer.
     *
     * @param beforeEvent CDI Event instance.
     * @param beanManager CDI Bean Manager instance.
     */
    public void beforeBeanDiscovery(@Observes BeforeBeanDiscovery beforeEvent, BeanManager beanManager) {
        _logger.debug("CDI Bean discovery process started.");

        _beanDeploymentMetaData = new BeanDeploymentMetaData();
        _beanDeploymentMetaData.setBeanManager(beanManager);
        _beanDeploymentMetaData.setDeploymentClassLoader(Classes.getTCCL());
    }

    /**
     * {@link javax.enterprise.inject.spi.ProcessBean} CDI event observer.
     *
     * @param processBean CDI Event instance.
     * @param beanManager CDI Bean Manager instance.
     */
    public void processBean(@Observes ProcessBean processBean, BeanManager beanManager) {
        Bean<?> bean = processBean.getBean();
        Set<InjectionPoint> injectionPoints = bean.getInjectionPoints();

        // Create proxies for the relevant injection points...
        for (InjectionPoint injectionPoint : injectionPoints) {
            for (Annotation qualifier : injectionPoint.getQualifiers()) {
                if (Reference.class.isAssignableFrom(qualifier.annotationType())) {
                    Member member = injectionPoint.getMember();
                    if (member instanceof Field) {
                        Class<?> memberType = ((Field) member).getType();
                        if (memberType.isInterface()) {
                            if (memberType.equals(ReferenceInvoker.class)) {
                                addInvokerBean((Reference)qualifier, injectionPoint.getQualifiers());
                            } else {
                                addInjectableClientProxyBean((Field) member, (Reference) qualifier, injectionPoint.getQualifiers(), beanManager);
                            }
                        }
                    }
                }
            }
        }

        CDIBean cdiBean = new CDIBean(bean, beanManager);

        // Create Service Proxy ExchangeHandlers and register them as Services, for all @Service beans...
        if (isServiceBean(bean)) {
            _logger.debug("Adding ServiceDescriptor for bean " + bean.getBeanClass().getName());
            _beanDeploymentMetaData.addServiceDescriptor(new CDIBeanServiceDescriptor(cdiBean, _beanDeploymentMetaData));
        }

        // Register all beans in the deployment...
        _beanDeploymentMetaData.addDeploymentBean(cdiBean);
    }

    /**
     * {@link javax.enterprise.inject.spi.ProcessBean} CDI event observer.
     *
     * @param afterEvent  CDI Event instance.
     */
    public void afterBeanDiscovery(@Observes AfterBeanDiscovery afterEvent) {
        for (ClientProxyBean proxyBean : _createdProxyBeans) {
            _logger.debug("Adding ClientProxyBean for bean Service " + proxyBean.getServiceName() + ".  Service Interface type is " + proxyBean.getServiceInterface().getName());
            afterEvent.addBean(proxyBean);
            _beanDeploymentMetaData.addClientProxy(proxyBean);
        }
        
        for (ReferenceInvokerBean invokerBean : _createdInvokerBeans) {
            _logger.debug("Adding ReferenceInvokerBean for bean Service " + invokerBean.getServiceName());
            afterEvent.addBean(invokerBean);
            _beanDeploymentMetaData.addReferenceInvoker(invokerBean);
        }

        afterEvent.addBean(new BeanDeploymentMetaDataCDIBean(_beanDeploymentMetaData));
        afterEvent.addBean(new ContextBean());
        afterEvent.addBean(new MessageBean());

        _logger.debug("CDI Bean discovery process completed.");
    }

    private void addInjectableClientProxyBean(Field injectionPointField, Reference serviceReference, Set<Annotation> qualifiers, BeanManager beanManager) {
        final String serviceName;

        if (serviceReference.value().length() > 0) {
            serviceName = serviceReference.value();
        } else {
            serviceName = injectionPointField.getType().getSimpleName();
        }

        addClientProxyBean(serviceName, injectionPointField.getType(), qualifiers);
    }

    private void addClientProxyBean(String serviceName, Class<?> beanClass, Set<Annotation> qualifiers) {
        // Check do we already have a proxy for this service interface...
        for (ClientProxyBean clientProxyBean : _createdProxyBeans) {
            if (serviceName.equals(clientProxyBean.getServiceName()) && beanClass == clientProxyBean.getBeanClass()) {
                // ignore... we already have a proxy ...
                return;
            }
        }

        ClientProxyBean clientProxyBean = new ClientProxyBean(serviceName, beanClass, qualifiers, _beanDeploymentMetaData);
        _createdProxyBeans.add(clientProxyBean);
    }
    
    private void addInvokerBean(Reference serviceReference, Set<Annotation> qualifiers) {
        // Value of Reference annotation is required for ReferenceInvoker
        if (serviceReference.value().length() == 0) {
            _logger.debug("Unable to create reference invoker for @Reference with missing value");
        }
        
        String serviceName = serviceReference.value();
        // Check do we already have an invoker for this service reference ...
        for (ReferenceInvokerBean invokerBean : _createdInvokerBeans) {
            if (serviceName.equals(invokerBean.getServiceName())) {
                // ignore... we already have a proxy ...
                return;
            }
        }

        ReferenceInvokerBean invokerBean = new ReferenceInvokerBean(serviceName, qualifiers);
        _createdInvokerBeans.add(invokerBean);
    }

    private boolean isServiceBean(Bean<?> bean) {
        Class<?> beanClass = bean.getBeanClass();
        return (Modifier.isPublic(beanClass.getModifiers()) && beanClass.isAnnotationPresent(Service.class));
    }

}
