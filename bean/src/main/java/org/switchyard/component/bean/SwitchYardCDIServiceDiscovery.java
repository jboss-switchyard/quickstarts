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

package org.switchyard.component.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.BeforeShutdown;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ProcessBean;
import javax.xml.namespace.QName;

import org.switchyard.component.bean.deploy.BeanDeploymentMetaData;
import org.switchyard.component.bean.deploy.CDIBeanServiceDescriptor;
import org.switchyard.transform.Transformer;

/**
 * Portable CDI extension for SwitchYard.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@ApplicationScoped
public class SwitchYardCDIServiceDiscovery implements Extension {

    /**
     * Bean deployment metadata.
     */
    private BeanDeploymentMetaData _beanDeploymentMetaData;
    /**
     * List of created {@link ClientProxyBean} instances.
     */
    private List<ClientProxyBean> _createdProxyBeans = new ArrayList<ClientProxyBean>();

    /**
     * {@link javax.enterprise.inject.spi.BeforeBeanDiscovery} CDI event observer.
     *
     * @param beforeEvent CDI Event instance.
     */
    public void beforeBeanDiscovery(@Observes BeforeBeanDiscovery beforeEvent) {
        _beanDeploymentMetaData = BeanDeploymentMetaData.bind();
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
                            addInjectableClientProxyBean((Field) member, (Reference) qualifier, injectionPoint.getQualifiers(), beanManager);
                        }
                    }
                }
            }
        }

        // Create Service Proxy ExchangeHandlers and register them as Services, for all @Service beans...
        if (isServiceBean(bean)) {
            Class<?> serviceType = bean.getBeanClass();
            Service serviceAnnotation = serviceType.getAnnotation(Service.class);

            _beanDeploymentMetaData.addServiceDescriptor(new CDIBeanServiceDescriptor(bean, beanManager, _beanDeploymentMetaData));
            if (serviceType.isInterface()) {
                addInjectableClientProxyBean(bean, serviceType, serviceAnnotation, beanManager);
            }
        }

        // Register all transformers we can find...
        if (Transformer.class.isAssignableFrom(bean.getBeanClass())) {
            Class<?> transformerRT = bean.getBeanClass();

            try {
                _beanDeploymentMetaData.addTransformer((Transformer) transformerRT.newInstance());
            } catch (InstantiationException e) {
                throw new IllegalStateException("Invalid Transformer implementation '" + transformerRT.getName() + "'.", e);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Invalid Transformer implementation '" + transformerRT.getName() + "'.", e);
            }
        }
    }

    /**
     * {@link javax.enterprise.inject.spi.ProcessBean} CDI event observer.
     *
     * @param afterEvent  CDI Event instance.
     */
    public void afterBeanDiscovery(@Observes AfterBeanDiscovery afterEvent) {
        for (ClientProxyBean proxyBean : _createdProxyBeans) {
            afterEvent.addBean(proxyBean);
            _beanDeploymentMetaData.addClientProxy(proxyBean);
        }
    }

    /**
     * {@link javax.enterprise.inject.spi.BeforeShutdown} CDI event observer.
     *
     * @param event       CDI Event instance.
     * @param beanManager CDI Bean Manager instance.
     */
    public void beforeShutdown(@Observes BeforeShutdown event, BeanManager beanManager) {
        BeanDeploymentMetaData.unbind();
    }

    private void addInjectableClientProxyBean(Bean<?> serviceBean, Class<?> serviceType, Service serviceAnnotation, BeanManager beanManager) {
        QName serviceQName = toServiceQName(serviceType);
        addClientProxyBean(serviceQName, serviceType, null);
    }

    private void addInjectableClientProxyBean(Field injectionPointField, Reference serviceReference, Set<Annotation> qualifiers, BeanManager beanManager) {
        QName serviceQName = toServiceQName(injectionPointField.getType());

        addClientProxyBean(serviceQName, injectionPointField.getType(), qualifiers);
    }

    private void addClientProxyBean(QName serviceQName, Class<?> beanClass, Set<Annotation> qualifiers) {
        // Check do we already have a proxy for this service interface...
        for (ClientProxyBean clientProxyBean : _createdProxyBeans) {
            if (serviceQName.equals(clientProxyBean.getServiceQName()) && beanClass == clientProxyBean.getBeanClass()) {
                // ignore... we already have a proxy ...
                return;
            }
        }

        ClientProxyBean clientProxyBean = new ClientProxyBean(serviceQName, beanClass, qualifiers, _beanDeploymentMetaData);
        _createdProxyBeans.add(clientProxyBean);
    }

    private boolean isServiceBean(Bean<?> bean) {
        return bean.getBeanClass().isAnnotationPresent(Service.class);
    }

    private QName toServiceQName(Class<?> serviceType) {
        return new QName(serviceType.getSimpleName());
    }
}
