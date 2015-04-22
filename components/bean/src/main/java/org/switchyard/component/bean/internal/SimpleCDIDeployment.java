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

package org.switchyard.component.bean.internal;

import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.bean.BeanMessages;
import org.switchyard.component.bean.ServiceProxyHandler;
import org.switchyard.component.bean.deploy.BeanComponentActivator;
import org.switchyard.component.bean.deploy.BeanDeploymentMetaData;
import org.switchyard.component.bean.deploy.CDIBean;
import org.switchyard.component.bean.deploy.ServiceDescriptor;
import org.switchyard.deploy.Activator;
import org.switchyard.deploy.Lifecycle;
import org.switchyard.deploy.internal.AbstractDeployment;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.transform.TransformerRegistry;
import org.switchyard.transform.internal.TransformerTypes;
import org.switchyard.transform.internal.TransformerUtil;

/**
 * Simple CDI deployment.
 * <p/>
 * For internal use only with tests etc.  Does not initialize/deploy the CDI container.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SimpleCDIDeployment extends AbstractDeployment {

    private boolean _activateBeans = false;

    /**
     * Creates a new CDI deployment with no configuration.
     */
    public SimpleCDIDeployment() {
        super(null);
    }

    @Override
    protected void doInit(List<Activator> activators) {
        for (Activator activator : activators) {
            if (activator.getActivationTypes().contains(BeanComponentActivator.BEAN_TYPE)) {
                _activateBeans = true;
                return;
            }
        }
    }

    @Override
    public void start() {
        BeanDeploymentMetaData beanDeploymentMetaData = BeanDeploymentMetaData.lookupBeanDeploymentMetaData();
        deployTransformers(beanDeploymentMetaData, getDomain());
        deployServicesAndProxies(beanDeploymentMetaData, getDomain());
    }

    @Override
    public void stop() {
    }

    @Override
    public void destroy() {
    }

    private void deployTransformers(BeanDeploymentMetaData beanDeploymentMetaData, ServiceDomain domain) {
        TransformerRegistry transformerRegistry = domain.getTransformerRegistry();

        for (CDIBean deploymentBean : beanDeploymentMetaData.getDeploymentBeans()) {
            Class<?> beanClass = deploymentBean.getBean().getBeanClass();

            if (TransformerUtil.isTransformer(beanClass)) {
                List<TransformerTypes> transformers = TransformerUtil.listTransformations(beanClass);
                for (TransformerTypes transformer : transformers) {
                    transformerRegistry.addTransformer(TransformerUtil.newTransformer(beanClass,
                            transformer.getFrom(), transformer.getTo()));
                }
            }
        }
    }

    private void deployServicesAndProxies(BeanDeploymentMetaData beanDeploymentMetaData, ServiceDomain domain) {
        if (!_activateBeans) {
            return;
        }

        if (beanDeploymentMetaData == null) {
            throw BeanMessages.MESSAGES.failedToLookupBeanDeploymentMetaDataFromNamingContext();
        }

        BeanComponentActivator activator = new BeanComponentActivator();

        for (ServiceDescriptor serviceDescriptor : beanDeploymentMetaData.getServiceDescriptors()) {
            String serviceName = serviceDescriptor.getServiceName();
            ServiceProxyHandler handler = serviceDescriptor.getHandler();
            ServiceInterface serviceInterface;
            ServiceReference service;

            activator.lookupBeanMetaData();
            serviceInterface = activator.buildServiceInterface(serviceName);
            QName serviceQName = XMLHelper.createQName(domain.getName().getNamespaceURI(), serviceName);
            domain.registerService(serviceQName, serviceInterface, handler);
            service = domain.registerServiceReference(serviceQName, serviceInterface);
            handler.addReference(service);
            handler.start();

        }
    }

    @Override
    public Lifecycle getGatwayLifecycle(QName serviceName, String bindingName) {
        return null;
    }
}
