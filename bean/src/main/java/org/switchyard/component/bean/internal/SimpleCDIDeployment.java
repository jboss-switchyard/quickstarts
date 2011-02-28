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

package org.switchyard.component.bean.internal;

import javax.xml.namespace.QName;

import org.switchyard.ExchangeHandler;
import org.switchyard.ServiceReference;
import org.switchyard.component.bean.deploy.BeanComponentActivator;
import org.switchyard.component.bean.deploy.BeanDeploymentMetaData;
import org.switchyard.component.bean.deploy.ServiceDescriptor;
import org.switchyard.deploy.internal.AbstractDeployment;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.transform.Transformer;
import org.switchyard.transform.TransformerRegistry;

/**
 * Simple CDI deployment.
 * <p/>
 * For internal use only with tests etc.  Does not initialize/deploy the CDI container.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SimpleCDIDeployment extends AbstractDeployment {

    /**
     * Initialize the deployment.
     */
    public void init() {
        super.init();

        BeanDeploymentMetaData beanDeploymentMetaData = BeanDeploymentMetaData.lookup();
        deployTransformers(beanDeploymentMetaData);
        deployServicesAndProxies(beanDeploymentMetaData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {

    }

    private void deployTransformers(BeanDeploymentMetaData beanDeploymentMetaData) {
        TransformerRegistry transformerRegistry = getDomain().getTransformerRegistry();

        for (Transformer transformer : beanDeploymentMetaData.getTransformers()) {
            transformerRegistry.addTransformer(transformer);
        }
    }

    private void deployServicesAndProxies(BeanDeploymentMetaData beanDeploymentMetaData) {
        if (beanDeploymentMetaData == null) {
            throw new RuntimeException("Failed to lookup BeanDeploymentMetaData from Naming Context.");
        }

        BeanComponentActivator activator = new BeanComponentActivator();

        for (ServiceDescriptor serviceDescriptor : beanDeploymentMetaData.getServiceDescriptors()) {
            QName serviceName = serviceDescriptor.getServiceName();
            ExchangeHandler handler = serviceDescriptor.getHandler();
            ServiceInterface serviceInterface;
            ServiceReference service;

            serviceInterface = activator.buildServiceInterface(serviceName);
            service = getDomain().registerService(serviceName, handler, serviceInterface);
            activator.start(service);
        }
    }
}
