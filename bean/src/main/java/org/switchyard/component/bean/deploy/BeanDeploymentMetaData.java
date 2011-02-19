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

import org.switchyard.component.bean.ClientProxyBean;
import org.switchyard.transform.Transformer;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bean Deployment Meta Data.
 * <p/>
 * All the CDI bean info for a specific deployment.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class BeanDeploymentMetaData implements Serializable {

    private static final String JAVA_COMP_SWITCHYARD_SERVICE_DESCRIPTOR_SET = "cn=SwitchyardApplicationServiceDescriptorSet";

    private List<ServiceDescriptor> _serviceDescriptors = new ArrayList<ServiceDescriptor>();
    private List<ClientProxyBean> _clientProxies = new ArrayList<ClientProxyBean>();
    private List<Transformer> _transformers = new ArrayList<Transformer>();

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
     * Add a {@link Transformer}.
     * @param transformer The transformer instance.
     */
    public void addTransformer(Transformer transformer) {
        _transformers.add(transformer);
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
     * Add a list of all the {@link Transformer Transformers}.
     * @return The list of all the {@link Transformer Transformers}.
     */
    public List<Transformer> getTransformers() {
        return Collections.unmodifiableList(_transformers);
    }

    /**
     * Bind a new {@link BeanDeploymentMetaData} instance to the JNDI Context.
     * <p/>
     * The instance is associated with the Context ClassLoader.
     *
     * @return The new {@link BeanDeploymentMetaData}.
     */
    public static BeanDeploymentMetaData bind() {
        Map<ClassLoader, BeanDeploymentMetaData> metaDataMap = getBeanDeploymentMetaDataMap();
        BeanDeploymentMetaData deploymentMetaData = new BeanDeploymentMetaData();

        metaDataMap.put(Thread.currentThread().getContextClassLoader(), deploymentMetaData);

        return deploymentMetaData;
    }

    /**
     * Lookup the {@link BeanDeploymentMetaData} associated with the callers Context ClassLoader.
     * @return The {@link BeanDeploymentMetaData}.
     */
    public static BeanDeploymentMetaData lookup() {
        return getBeanDeploymentMetaDataMap().get(Thread.currentThread().getContextClassLoader());
    }

    /**
     * Unbind the {@link BeanDeploymentMetaData} associated with the callers Context ClassLoader.
     */
    public static void unbind() {
        getBeanDeploymentMetaDataMap().remove(Thread.currentThread().getContextClassLoader());
    }

    private synchronized static Map<ClassLoader, BeanDeploymentMetaData> getBeanDeploymentMetaDataMap() {
        try {
            Context jndiContext = new InitialContext();

            try {
                Map<ClassLoader, BeanDeploymentMetaData> descriptorMap = (Map<ClassLoader, BeanDeploymentMetaData>)
                            jndiContext.lookup(JAVA_COMP_SWITCHYARD_SERVICE_DESCRIPTOR_SET);

                return descriptorMap;
            } finally {
                jndiContext.close();
            }
        } catch (NamingException e1) {
            try {
                Context jndiContext = new InitialContext();

                try {
                    Map<ClassLoader, BeanDeploymentMetaData> descriptorMap =
                            new ConcurrentHashMap<ClassLoader, BeanDeploymentMetaData>();
                    jndiContext.bind(JAVA_COMP_SWITCHYARD_SERVICE_DESCRIPTOR_SET, descriptorMap);

                    return descriptorMap;
                } finally {
                    jndiContext.close();
                }
            } catch (NamingException e2) {
                throw new IllegalStateException("Unexpected NamingException getting JNDI Context.", e2);
            }
        }
    }
}
