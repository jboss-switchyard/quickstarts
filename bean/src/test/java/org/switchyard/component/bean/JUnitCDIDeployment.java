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

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.junit.Assert;
import org.switchyard.*;
import org.switchyard.Service;
import org.switchyard.component.bean.deploy.BeanComponentActivator;
import org.switchyard.component.bean.deploy.BeanDeploymentMetaData;
import org.switchyard.component.bean.deploy.ServiceDescriptor;
import org.switchyard.deploy.internal.AbstractDeployment;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.transform.Transformer;
import org.switchyard.transform.TransformerRegistry;

import javax.xml.namespace.QName;

/**
 * JUnit Application deployment.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
class JUnitCDIDeployment extends AbstractDeployment {

    public void init() {
        deployWeldContainer();

        super.init();

        BeanDeploymentMetaData beanDeploymentMetaData = BeanDeploymentMetaData.lookup();
        deployTransformers(beanDeploymentMetaData);
        deployServicesAndProxies(beanDeploymentMetaData);
    }

    @Override
    public void destroy() {

    }

    private void deployWeldContainer() {
        WeldContainer weld = new Weld().initialize();
        weld.event().select(ContainerInitialized.class).fire(new ContainerInitialized());
    }

    private void deployTransformers(BeanDeploymentMetaData beanDeploymentMetaData) {
        TransformerRegistry transformerRegistry = getDomain().getTransformerRegistry();

        for (Transformer transformer : beanDeploymentMetaData.getTransformers()) {
            transformerRegistry.addTransformer(transformer);
        }
    }

    private void deployServicesAndProxies(BeanDeploymentMetaData beanDeploymentMetaData) {
        if(beanDeploymentMetaData == null) {
            Assert.fail("Failed to lookup BeanDeploymentMetaData from Naming Context.");
        }

        BeanComponentActivator activator = new BeanComponentActivator();

        for (ServiceDescriptor serviceDescriptor : beanDeploymentMetaData.getServiceDescriptors()) {
            QName serviceName = serviceDescriptor.getServiceName();
            ExchangeHandler handler = serviceDescriptor.getHandler();
            ServiceInterface serviceInterface;
            Service service;

            serviceInterface = activator.buildServiceInterface(serviceName);
            service = getDomain().registerService(serviceName, handler, serviceInterface);
            activator.start(service);
        }
    }
}
