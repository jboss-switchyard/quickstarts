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


import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.switchyard.common.cdi.CDIUtil;
import org.switchyard.component.bean.ClientProxyBean;
import org.switchyard.component.bean.BeanMessages;
import org.switchyard.component.bean.internal.ReferenceInvokerBean;

/**
 * Bean Deployment Meta Data.
 * <p/>
 * All the CDI bean info for a specific deployment.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class BeanDeploymentMetaData {

    private BeanManager _beanManager;
    private ClassLoader _deploymentClassLoader;
    private List<ServiceDescriptor> _serviceDescriptors = new ArrayList<ServiceDescriptor>();
    private List<ClientProxyBean> _clientProxies = new ArrayList<ClientProxyBean>();
    private List<ReferenceInvokerBean> _referenceInvokers = new ArrayList<ReferenceInvokerBean>();
    private List<CDIBean> _deploymentBeans = new ArrayList<CDIBean>();

    /**
     * Default no-arg constructor.
     */
    public BeanDeploymentMetaData() {}

    /**
     * Set the deployment CDI BeanManager.
     * @param beanManager The bean manager.
     * @return this instance.
     */
    public BeanDeploymentMetaData setBeanManager(BeanManager beanManager) {
        _beanManager = beanManager;
        return this;
    }

    /**
     * Get the deployment CDI BeanManager.
     * @return The bean manager.
     */
    public BeanManager getBeanManager() {
        return _beanManager;
    }

    /**
     * Set the deployment ClassLoader.
     * @param deploymentClassLoader The deployment ClassLoader.
     * @return this instance.
     */
    public BeanDeploymentMetaData setDeploymentClassLoader(ClassLoader deploymentClassLoader) {
        _deploymentClassLoader = deploymentClassLoader;
        return this;
    }

    /**
     * Get the deployment ClassLoader.
     * @return The deployment ClassLoader.
     */
    public ClassLoader getDeploymentClassLoader() {
        return _deploymentClassLoader;
    }

    /**
     * Add a {@link ServiceDescriptor}.
     * @param serviceDescriptor The descriptor instance.
     */
    public void addServiceDescriptor(ServiceDescriptor serviceDescriptor) {
        _serviceDescriptors.add(serviceDescriptor);
    }

    /**
     * Add a {@link ClientProxyBean}.
     * @param proxy The proxy instance.
     */
    public void addClientProxy(ClientProxyBean proxy) {
        _clientProxies.add(proxy);
    }
    
    /**
     * Add a ReferenceInvokerBean.
     * @param invoker The invoker bean.
     */
    public void addReferenceInvoker(ReferenceInvokerBean invoker) {
        _referenceInvokers.add(invoker);
    }

    /**
     * Add a deployment CDI bean.
     * @param bean The CDI bean instance.
     */
    public void addDeploymentBean(CDIBean bean) {
        _deploymentBeans.add(bean);
    }

    /**
     * Add a list of all the {@link ServiceDescriptor ServiceDescriptors}.
     * @return The list of all the {@link ServiceDescriptor ServiceDescriptors}.
     */
    public List<ServiceDescriptor> getServiceDescriptors() {
        return Collections.unmodifiableList(_serviceDescriptors);
    }

    /**
     * Get a list of all the {@link ClientProxyBean ClientProxyBeans}.
     * @return The list of all the {@link ClientProxyBean ClientProxyBeans}.
     */
    public List<ClientProxyBean> getClientProxies() {
        return Collections.unmodifiableList(_clientProxies);
    }
    
    /**
     * Get a list of all the ReferenceInvokerBeans.
     * @return The list of all the ReferenceInvokerBeans.
     */
    public List<ReferenceInvokerBean> getReferenceInvokers() {
        return Collections.unmodifiableList(_referenceInvokers);
    }

    /**
     * Get a list of all beans in the deployment.
     * @return The list of all beans in the deployment.
     */
    public List<CDIBean> getDeploymentBeans() {
        return Collections.unmodifiableList(_deploymentBeans);
    }

    /**
     * Lookup the BeanDeploymentMetaData for the current deployment.
     * @return The BeanDeploymentMetaData.
     */
    public static BeanDeploymentMetaData lookupBeanDeploymentMetaData() {
        BeanManager beanManager = CDIUtil.lookupBeanManager();
        if (beanManager == null) {
            throw BeanMessages.MESSAGES.nameBeanManagerIsNotBoundInThisContext();
        }
        
        Set<Bean<?>> beans = beanManager.getBeans(BeanDeploymentMetaData.class);
        if (beans.isEmpty()) {
            throw BeanMessages.MESSAGES.failedToLookupBeanDeploymentMetaDataFromBeanManagerMustBeBoundIntoBeanManagerPerhapsSwitchYardCDIExtensionsNotProperlyInstalledInContainer();
        }
        if (beans.size() > 1) {
            throw BeanMessages.MESSAGES.failedToLookupBeanDeploymentMetaDataFromBeanManagerMultipleBeansResolvedForType(BeanDeploymentMetaData.class.getName());
        }

        BeanDeploymentMetaDataCDIBean bean = (BeanDeploymentMetaDataCDIBean) beans.iterator().next();
        return bean.getBeanMetaData();
    }
}
