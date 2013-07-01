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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.switchyard.SwitchYardException;
import org.switchyard.component.bean.ClientProxyBean;

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
     * Add a list of all the {@link ClientProxyBean ClientProxyBeans}.
     * @return The list of all the {@link ClientProxyBean ClientProxyBeans}.
     */
    public List<ClientProxyBean> getClientProxies() {
        return Collections.unmodifiableList(_clientProxies);
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
        try {
            BeanManager beanManager = getCDIBeanManager();

            Set<Bean<?>> beans = beanManager.getBeans(BeanDeploymentMetaData.class);
            if (beans.isEmpty()) {
                throw new SwitchYardException("Failed to lookup BeanDeploymentMetaData from BeanManager.  Must be bound into BeanManager.  Perhaps SwitchYard CDI Extensions not properly installed in container.");
            }
            if (beans.size() > 1) {
                throw new SwitchYardException("Failed to lookup BeanDeploymentMetaData from BeanManager.  Multiple beans resolved for type '" + BeanDeploymentMetaData.class.getName() + "'.");
            }

            BeanDeploymentMetaDataCDIBean bean = (BeanDeploymentMetaDataCDIBean) beans.iterator().next();

            return bean.getBeanMetaData();
        } catch (NamingException e) {
            throw new SwitchYardException("Failed to lookup BeanManager.  Must be bound into java:comp as per CDI specification.", e);
        }
    }

    /**
     * Get the CDI BeanManager for the current context.
     * @return The CDI BeanManager for the current context.
     * @throws NamingException Error looking up BeanManager instance.
     */
    public static BeanManager getCDIBeanManager() throws NamingException {
        BeanManager beanManager = getCDIBeanManager("java:comp");

        if (beanManager == null) {
            beanManager = getCDIBeanManager("java:comp/env");
            if (beanManager == null) {
                throw new NameNotFoundException("Name BeanManager is not bound in this Context");
            }
        }

        return beanManager;
    }

    private static BeanManager getCDIBeanManager(String jndiLocation) {
        Context javaComp = getJavaComp(jndiLocation);

        if (javaComp != null) {
            try {
                return (BeanManager) javaComp.lookup("BeanManager");
            } catch (NamingException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    private static Context getJavaComp(String jndiName) {
        InitialContext initialContext = null;

        try {
            initialContext = new InitialContext();
            return (Context) initialContext.lookup(jndiName);
        } catch (NamingException e) {
            return null;
        } catch (Exception e) {
            throw new SwitchYardException("Unexpected Exception retrieving '" + jndiName + "' from JNDI namespace.", e);
        } finally {
            if (initialContext != null) {
                try {
                    initialContext.close();
                } catch (NamingException e) {
                    throw new SwitchYardException("Unexpected error closing InitialContext.", e);
                }
            }
        }
    }
}
