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

import org.apache.log4j.Logger;
import org.switchyard.common.type.Classes;
import org.switchyard.component.bean.deploy.BeanDeploymentMetaData;
import org.switchyard.component.bean.deploy.BeanDeploymentMetaDataCDIBean;
import org.switchyard.component.bean.deploy.CDIBean;
import org.switchyard.component.bean.deploy.CDIBeanServiceDescriptor;
import org.switchyard.component.bean.internal.context.ContextBean;

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
     * {@link javax.enterprise.inject.spi.BeforeBeanDiscovery} CDI event observer.
     *
     * @param beforeEvent CDI Event instance.
     * @param beanManager CDI Bean Manager instance.
     */
    public void beforeBeanDiscovery(@Observes BeforeBeanDiscovery beforeEvent, BeanManager beanManager) {
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
                            addInjectableClientProxyBean((Field) member, (Reference) qualifier, injectionPoint.getQualifiers(), beanManager);
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

        afterEvent.addBean(new BeanDeploymentMetaDataCDIBean(_beanDeploymentMetaData));
        afterEvent.addBean(new ContextBean());
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

    private boolean isServiceBean(Bean<?> bean) {
        Class<?> beanClass = bean.getBeanClass();
        return (Modifier.isPublic(beanClass.getModifiers()) && beanClass.isAnnotationPresent(Service.class));
    }

}
