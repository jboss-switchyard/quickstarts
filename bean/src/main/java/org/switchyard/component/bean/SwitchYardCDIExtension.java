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

package org.switchyard.component.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.util.AnnotationLiteral;
import javax.xml.namespace.QName;

import org.switchyard.internal.DefaultHandlerChain;
import org.switchyard.internal.ServiceDomains;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@ApplicationScoped
public class SwitchYardCDIExtension implements Extension {

    private List<ClientProxyBean> createdProxyBeans = new ArrayList<ClientProxyBean>();

    public void afterBeanDiscovery(@Observes AfterBeanDiscovery abd, BeanManager beanManager) {
        Set<Bean<?>> allBeans = beanManager.getBeans(Object.class, new AnnotationLiteral<Any>() {});

        for(Bean<?> bean : allBeans) {
            Set<InjectionPoint> injectionPoints = bean.getInjectionPoints();

            // Create proxies for the relevant injection points...
            for(InjectionPoint injectionPoint : injectionPoints) {
                for(Annotation qualifier : injectionPoint.getQualifiers()) {
                    if(Reference.class.isAssignableFrom(qualifier.annotationType())) {
                        Member member = injectionPoint.getMember();
                        if(member instanceof Field) {
                            Class<?> memberType = ((Field) member).getType();
                            if(memberType.isInterface()) {
                                addInjectableClientProxyBean((Field) member, (Reference) qualifier, injectionPoint.getQualifiers(), beanManager, abd);
                            }
                        }
                    }
                }
            }

            // Create Service Proxy ExchangeHandlers and register them as Services, for all @Service beans...
            if(isServiceBean(bean)) {
                Class<?> serviceType = bean.getBeanClass();
                Service serviceAnnotation = serviceType.getAnnotation(Service.class);

                registerESBServiceProxyHandler(bean, serviceType, serviceAnnotation, beanManager);
                if(serviceType.isInterface()) {
                    addInjectableClientProxyBean(bean, serviceType, serviceAnnotation, beanManager, abd);
                }
            }

        }
    }

    private void registerESBServiceProxyHandler(Bean<?> serviceBean, Class<?> serviceType, Service serviceAnnotation, BeanManager beanManager) {
        CreationalContext creationalContext = beanManager.createCreationalContext(serviceBean);
        Object beanRef = beanManager.getReference(serviceBean, Object.class, creationalContext);
        BeanServiceMetadata serviceMetadata = new BeanServiceMetadata(serviceType);
        ServiceProxyHandler proxyHandler = new ServiceProxyHandler(beanRef, serviceMetadata);
        Class<?>[] serviceInterfaces = serviceAnnotation.value();

        if(serviceInterfaces == null || serviceInterfaces.length == 0) {
            registerESBServiceProxyHandler(proxyHandler, beanRef.getClass());
        } else {
            for(Class<?> serviceInterface : serviceInterfaces) {
                registerESBServiceProxyHandler(proxyHandler, serviceInterface);
            }
        }
    }

    private void registerESBServiceProxyHandler(ServiceProxyHandler proxyHandler, Class<?> serviceType) {
        QName serviceQName = toServiceQName(serviceType);

        // Register the Service in the ESB domain...
        DefaultHandlerChain handlerChain = new DefaultHandlerChain();
        handlerChain.addLast("serviceProxy", proxyHandler);
        ServiceDomains.getDomain().registerService(serviceQName, handlerChain);
    }

    private void addInjectableClientProxyBean(Bean<?> serviceBean, Class<?> serviceType, Service serviceAnnotation, BeanManager beanManager, AfterBeanDiscovery abd) {
        QName serviceQName = toServiceQName(serviceType);
        addClientProxyBean(serviceQName, serviceType, null, abd);
    }

    private void addInjectableClientProxyBean(Field injectionPointField, Reference serviceReference, Set<Annotation> qualifiers, BeanManager beanManager, AfterBeanDiscovery abd) {
        QName serviceQName = toServiceQName(injectionPointField.getType());

        addClientProxyBean(serviceQName, injectionPointField.getType(), qualifiers, abd);
    }

    private void addClientProxyBean(QName serviceQName, Class<?> beanClass, Set<Annotation> qualifiers, AfterBeanDiscovery abd) {
        // Check do we already have a proxy for this service interface...
        for(ClientProxyBean clientProxyBean : createdProxyBeans) {
            if(serviceQName.equals(clientProxyBean.getServiceQName()) && beanClass == clientProxyBean.getBeanClass()) {
                // ignore... we already have a proxy ...
                return;
            }
        }

        ClientProxyBean clientProxyBean = new ClientProxyBean(serviceQName, beanClass, qualifiers);
        createdProxyBeans.add(clientProxyBean);
        abd.addBean(clientProxyBean);
    }

    private boolean isServiceBean(Bean<?> bean) {
        return bean.getBeanClass().isAnnotationPresent(Service.class);
    }

    private QName toServiceQName(Class<?> serviceType) {
        // TODO: Could use the bean class package name as the namespace component of the Service QName
        return new QName(serviceType.getSimpleName());
    }
}
